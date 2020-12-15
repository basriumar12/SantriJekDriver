package com.santrijek.driver.activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.santrijek.driver.model.BarangBelanja;
import com.santrijek.driver.model.DestinasiMbox;
import com.santrijek.driver.model.LaundryBelanja;
import com.santrijek.driver.model.MakananBelanja;
import com.santrijek.driver.model.StoreBelanja;
import com.santrijek.driver.network.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bcgdv.asia.lib.ticktock.TickTockView;
import com.santrijek.driver.R;

import com.santrijek.driver.MainActivity;
import com.santrijek.driver.database.DBHandler;
import com.santrijek.driver.database.Queries;
import com.santrijek.driver.model.AksiOrder;
import com.santrijek.driver.model.Content;
import com.santrijek.driver.model.Driver;
import com.santrijek.driver.model.Transaksi;
import com.santrijek.driver.network.AsyncTaskHelper;
import com.santrijek.driver.network.HTTPHelper;
import com.santrijek.driver.network.NetworkActionResult;
import com.santrijek.driver.preference.SettingPreference;
//import driver.pacekurir.drivermangjek.preference.UserPreference;
import com.santrijek.driver.service.MyConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {

    NotificationActivity activity;
    Bundle dataOrder;
    TickTockView countDown;
    Driver driver;
    Transaksi mCar;
    int status = -1;
    boolean timesUp, hasCek = false;
    long timeNow;
    ArrayList<BarangBelanja> arrBarang;
    ArrayList<MakananBelanja> arrMakanan;
    ArrayList<StoreBelanja> arrStore;
    ArrayList<LaundryBelanja> arrLaundry;


    ArrayList<DestinasiMbox> arrDestinasi;
    Queries que;
    MediaPlayer BG;
    Vibrator v;
    int maxTry = 4;
    int maxTry2 = 4;
    ProgressDialog pdGetData, pdResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_notification);

        activity = NotificationActivity.this;
        mCar = new Transaksi();
        que = new Queries(new DBHandler(activity));
        driver = que.getDriver();
        que.updateStatus(5);

        dataOrder = getIntent().getExtras();
        if(dataOrder != null){
            mCar = (Transaksi) dataOrder.getSerializable("data_order");
            Log.d("Notif Activity","DATA ORDER : " + "resto : " +mCar.nama_laundry + "NAMA TOKO : "+mCar.nama_laundry);
        }

        JSONObject jTrans = new JSONObject();
        try {
            jTrans.put("id_transaksi", mCar.id_transaksi);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(dataOrder.getInt("order_fitur") == 4){
            pdGetData = showLoading();
            get_data_transaksi_mmart(jTrans);
        }else if(dataOrder.getInt("order_fitur") == 7){
            pdGetData = showLoading();
            get_data_transaksi_mbox(jTrans);
        }else if(dataOrder.getInt("order_fitur") == 3) {
            pdGetData = showLoading();
            get_data_transaksi_mfood(jTrans);
        }else if(dataOrder.getInt("order_fitur") == 9) {
            pdGetData = showLoading();
            get_data_transaksi_mstore(jTrans);
        }else if(dataOrder.getInt("order_fitur") == 10){
            pdGetData = showLoading();
            get_data_transaksi_mlaundry(jTrans);
        }else{
            initView();
        }

        removeNotif();
    }

    private void checkAutoBid(){
        if(new SettingPreference(this).getSetting()[0].equals("OFF")){
        }else{
            countDown.stop();
            pdResponse = showLoading();
            sendMyResponse(1);
        }
    }

    private void initView(){
        Button ambilBooking = (Button) findViewById(R.id.ambilBooking);
        Button tolakBooking = (Button) findViewById(R.id.tolakBooking);

        countDown = (TickTockView) findViewById(R.id.tickTock);

        ambilBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDown.stop();
                if(timesUp){
                    Toast.makeText(activity, "When the order has expired!", Toast.LENGTH_SHORT).show();
//                    finish();
                    intentToMain();
                }else{
                    if(BG.isPlaying()){
                        BG.stop();
                        v.cancel();
                    }
//                    Log.d("Time_accept", mCar.time_accept+" "+"Time now"+ (new Date().getTime()));
////                    Toast.makeText(activity, "Action Accept", Toast.LENGTH_SHORT).show();
//                    if (Long.parseLong(mCar.time_accept) + 20000 >= new Date().getTime()){
                    pdResponse = showLoading();
                    sendMyResponse(1);
//                    }else{
//                        Toast.makeText(activity, "Waktu pemesanan telah habis!", Toast.LENGTH_LONG).show();
//                        finish();
//                        intentToMain();
//                    }
//                    sendMyResponse(1);
                }
            }
        });

        tolakBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timesUp){
                    intentToMain();
//                    finish();
                }else{
                    if(BG.isPlaying()){
                        BG.stop();
                        v.cancel();
                    }
                    pdResponse = showLoading();
                    sendMyResponse(0);
                }
            }
        });
        if (countDown != null) {
            countDown.setOnTickListener(new TickTockView.OnTickListener() {
                @Override
                public String getText(long timeRemaining) {
                    int seconds = (int) (timeRemaining / 1000) % 60;
                    if (seconds==0){
                        countDown.stop();
                        timesUp = true;
                        timesUp();
                    }
                    return String.valueOf(seconds);
                }

            });
        }
        initTime();
        checkAutoBid();
        playSound();
        TextView textBiaya, textJarak, textAlamatAsal, textAlamatTujuan, judulOrder,pelanggan;
        TextView headerJarak, headerJemput, headerAntar, textTotalBiaya, textKE;

        FrameLayout fl = (FrameLayout) findViewById(R.id.frameAlamatAntar);
        ImageView logoOrder = (ImageView) findViewById(R.id.logoOrder);
        ImageView iconTagAntar = (ImageView) findViewById(R.id.iconTagAntar);
        textBiaya = (TextView) findViewById(R.id.textBiaya);
        pelanggan = (TextView) findViewById(R.id.namaPelanggan);
        textJarak = (TextView) findViewById(R.id.textKM);
        textAlamatAsal = (TextView) findViewById(R.id.alamatJemput);
        textAlamatTujuan = (TextView) findViewById(R.id.alamatAntar);
        judulOrder = (TextView) findViewById(R.id.judulOrder);
        headerJarak = (TextView) findViewById(R.id.headerJarak);
        headerJemput = (TextView) findViewById(R.id.headerJemput);
        headerAntar = (TextView) findViewById(R.id.headerAntar);
        textTotalBiaya = (TextView) findViewById(R.id.textTotalBiaya);
        textKE = (TextView) findViewById(R.id.textKE);

//        if(mCar.kredit_promo.equals("0")){
//            textBiaya.setText(amountAdapter(Integer.parseInt(mCar.harga)));
//        }else{
            textBiaya.setText(amountAdapter(Integer.parseInt(mCar.biaya_akhir)));
//        }

        textJarak.setText(convertJarak(Double.parseDouble(mCar.jarak))+" KM");
        if(mCar.order_fitur.equals("3")||mCar.order_fitur.equals("9")){
            textAlamatAsal.setText(mCar.alamat_tujuan);
            textAlamatTujuan.setText(mCar.alamat_asal);
        }else{
            textAlamatAsal.setText(mCar.alamat_asal);
            textAlamatTujuan.setText(mCar.alamat_tujuan);
        }


        switch (mCar.order_fitur){
            case "1":
                logoOrder.setImageResource(R.drawable.ic_e_ride_on);
                judulOrder.setText("Order Ride");
                pelanggan.setText(mCar.nama_pelanggan);
                break;
            case "2":
                pelanggan.setText(mCar.nama_pelanggan);
                logoOrder.setImageResource(R.mipmap.ic_fitur_mcar);
                judulOrder.setText("Order Taxi");
                break;
            case "3":
                pelanggan.setText(mCar.nama_pelanggan);
                headerJemput.setText(mCar.nama_resto);
                headerAntar.setText("Delivery");
                logoOrder.setImageResource(R.drawable.ic_e_food);
                judulOrder.setText("Order Food");
                textTotalBiaya.setText("Estimated costs");
                textBiaya.setText(amountAdapter(mCar.total_biaya));
                break;
            case "4":
                pelanggan.setText(mCar.nama_pelanggan);
                logoOrder.setImageResource(R.mipmap.ic_fitur_mmart);
                judulOrder.setText("Shopping Express");
                break;
            case "5":
                pelanggan.setText(mCar.nama_pelanggan);
                logoOrder.setImageResource(R.mipmap.ic_fitur_msend);
                judulOrder.setText("Order Courier");
                break;
            case "6":
                pelanggan.setText(mCar.nama_pelanggan);
                logoOrder.setImageResource(R.mipmap.ic_fitur_mmassage);
                judulOrder.setText("Pijat Reflexi");
                textKE.setVisibility(View.GONE);
                iconTagAntar.setImageResource(R.mipmap.ic_fitur_mmassage);
                headerJarak.setText("Waktu");
                headerJemput.setText("Lokasi");
                textTotalBiaya.setText("Total Cost");
                headerAntar.setText("Layanan");
                textJarak.setText(mCar.lama_pelayanan+" menit\n"+mCar.jam_pelayanan);
                textBiaya.setText(amountAdapter(Integer.parseInt(mCar.harga)));
                textAlamatTujuan.setText(mCar.massage_menu);
                break;
            case "7":
                pelanggan.setText(mCar.nama_pelanggan);
                logoOrder.setImageResource(R.mipmap.ic_fitur_mbox);
                judulOrder.setText("Order Truck");
                TextView addInfo = (TextView) findViewById(R.id.addInfo);
                addInfo.setVisibility(View.VISIBLE);
                addInfo.setText(mCar.shipper+" Additional Servis");
                break;
            case "8":
                pelanggan.setText(mCar.nama_pelanggan);
                logoOrder.setImageResource(R.mipmap.ic_fitur_mservice);
                judulOrder.setText("Jasa Servis");
                headerJarak.setText("Layanan");
                headerJemput.setText("Lokasi");
                textTotalBiaya.setText("Total Biaya");
                textJarak.setText(mCar.quantity+" "+mCar.jenis_service.toUpperCase());
                textBiaya.setText(amountAdapter(Integer.parseInt(mCar.harga)));
                fl.setVisibility(View.GONE);
                textKE.setVisibility(View.GONE);
                textAlamatAsal.setGravity(View.TEXT_ALIGNMENT_CENTER);
                break;
            case "9" :
                pelanggan.setText(mCar.nama_pelanggan);
                headerJemput.setText(mCar.nama_toko);
                logoOrder.setImageResource(R.drawable.ic_e_electronic);
                judulOrder.setText("Market");
                textTotalBiaya.setText("Estimasi Biaya");
                textBiaya.setText(amountAdapter(mCar.total_biaya));
                break;

            case "10" :
                pelanggan.setText(mCar.nama_pelanggan);
                headerAntar.setText(mCar.nama_laundry);
                headerJemput.setText("Buy Here");
                logoOrder.setImageResource(R.drawable.ic_e_food);
                judulOrder.setText("Grocery");
                textTotalBiaya.setText("Estimated costs");
                textBiaya.setText(amountAdapter(mCar.total_biaya));
                break;


            default:
                break;
        }
    }

    private String amountAdapter(int amo){
        return "$."+NumberFormat.getNumberInstance(Locale.GERMANY).format(amo)+".00";
    }

    private String convertJarak(Double jarak){
        int range = (int)(jarak*10);
        jarak = (double)range/10;
        return String.valueOf(jarak);
    }

    private void timesUp(){
        if(timesUp && !hasCek){
            Toast.makeText(activity, "Time's up!", Toast.LENGTH_SHORT).show();
            hasCek = true;
            pdResponse = showLoading();
            sendMyResponse(0);
            if(BG.isPlaying()){
                BG.stop();
                v.cancel();
            }
        }
    }

//    10:10 ---> Open_in 10:11 ---> 20 ---> 10:31
//    10:10 ---> Open_in 10:20 ---> 10 ---> 10:30

    private void initTime(){

//        int ending = (int)(20 - (new Date().getTime() - Long.parseLong(mCar.time_accept)));
        Calendar end = Calendar.getInstance();

        end.add(Calendar.SECOND, 20);

        Calendar start = Calendar.getInstance();
        end.add(Calendar.SECOND, 0);

        countDown.start(start, end);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDown != null)
            countDown.stop();

        que.closeDatabase();
        if(BG.isPlaying()){
            BG.stop();
            v.cancel();
        }
    }

    private void sendMyResponse(final int acc){
        AksiOrder ao = new AksiOrder(driver.id, mCar.id_transaksi);
        Log.d("JSON_aksiOrder", ao.getAksi().toString());
        if (acc == 1) {
            HTTPHelper.getInstance(activity).acceptOrder(ao.getAksi(), new NetworkActionResult() {
                @Override
                public void onSuccess(JSONObject obj) {
                    pdResponse.dismiss();
                    Log.d(MyConfig.TAG_response + " acc_order", obj.toString());
                    try {
                        if (obj.getString("message").equals("success")){
                            que.insertInProgressTransaksi(mCar);
                            if(mCar.order_fitur.equals("4")){
                                que.insertBarangBelanja(arrBarang);
                            }else if(mCar.order_fitur.equals("7")){
                                que.insertDestinasiMbox(arrDestinasi);
                            }else if(mCar.order_fitur.equals("3")){
                                que.insertMakananBelanja(arrMakanan);
                            }else if (mCar.order_fitur.equals("9")){
                                que.insertStoreBelanja(arrStore);
                            }else if (mCar.order_fitur.equals("10")){
                                que.insertLaundryBelanja(arrLaundry);
                            }
                            announceToUser(mCar.reg_id_pelanggan, mCar.id_transaksi, acc);
                        } else {
                            Toast.makeText(activity, "Order not available", Toast.LENGTH_SHORT).show();
                            intentToMain();
//                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String message) {
                }

                @Override
                public void onError(String message) {
                    if(maxTry2 == 0){
                        Toast.makeText(activity, "Failed to connect to the server, please press the Retrieve button.", Toast.LENGTH_LONG).show();
                        pdResponse.dismiss();
                        maxTry2 = 4;
                    }else{
                        sendMyResponse(acc);
                        Log.d("try_ke_data_response", String.valueOf(maxTry2));
                        maxTry2--;
                    }
                }
            });
        }else{
            HTTPHelper.getInstance(activity).rejectOrder(ao.getAksi(), new NetworkActionResult() {
                @Override
                public void onSuccess(JSONObject obj){
                    pdResponse.dismiss();
                    Log.d(MyConfig.TAG_response+" acc_order", obj.toString());
                    try {
                        if(obj.getString("message").equals("reject success")){
                            announceToUser(mCar.reg_id_pelanggan, mCar.id_transaksi, acc);
                        }else{
                            intentToMain();
                            Toast.makeText(activity, "Order not available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String message) {
                }

                @Override
                public void onError(String message) {
//                    Log.d(MyConfig.TAG_error+" acc_order", message);
                    if(maxTry2 == 0){
                        intentToMain();
                        pdResponse.dismiss();
                        maxTry2 = 4;
                    }else{
                        sendMyResponse(acc);
                        Log.d("try_ke_data_response", String.valueOf(maxTry2));
                        maxTry2--;
                    }
                    Log.d(MyConfig.TAG_error + " acc_order", message);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void removeNotif(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    private void announceToUser(String userRegID, String id_trans, int acc){
        Content content = new Content();
        content.addRegId(userRegID);
        content.createDataOrder(driver.id, id_trans, String.valueOf(acc), mCar.order_fitur);
        sendResponseToPelanggan(content, acc);
    }

    private void sendResponseToPelanggan(final Content content, final int acc){

        AsyncTaskHelper asyncTask = new AsyncTaskHelper(activity, true);
        asyncTask.setAsyncTaskListener(new AsyncTaskHelper.OnAsyncTaskListener() {
            @Override
            public void onAsyncTaskDoInBackground(AsyncTaskHelper asyncTask) {
                status = HTTPHelper.sendToGCMServer(content);
            }

            @Override
            public void onAsyncTaskProgressUpdate(AsyncTaskHelper asyncTask) {
            }

            @Override
            public void onAsyncTaskPostExecute(AsyncTaskHelper asyncTask) {
                if(acc == 0){
                    if(status == 1){

                    }else{
                        Toast.makeText(activity, "Message sending failed", Toast.LENGTH_SHORT).show();
                    }
                    intentToMain();
//                    finish();
                }else{
                    if(status == 1){
//                        new UserPreference(activity).updateStatus(2);
                        if(mCar.order_fitur.equalsIgnoreCase("1")){
                            Toast.makeText(activity, "Pickup of his customers", Toast.LENGTH_SHORT).show();

                        }

//                        Intent toMaps = new Intent(activity, MainActivity.class);
//                        dataOrder.putString("SOURCE", MyConfig.orderFragment);
//                        toMaps.putExtras(dataOrder);
//                        startActivity(toMaps);
//                        finish();
                    }else{
                        Toast.makeText(activity, "Message Failed", Toast.LENGTH_SHORT).show();
                    }
                    que.updateStatus(2);
                    Intent toMaps = new Intent(activity, MainActivity.class);
                    dataOrder.putString("SOURCE", MyConfig.orderFragment);
                    toMaps.putExtras(dataOrder);
                    startActivity(toMaps);
                    finish();
                }
            }
            @Override
            public void onAsyncTaskPreExecute(AsyncTaskHelper asyncTask) {

            }
        });
        asyncTask.execute();
    }

    private void doNothing(){
    }

    private void intentToMain(){
        que.updateStatus(1);
        Intent toMain = new Intent(activity, MainActivity.class);
        startActivity(toMain);
        finish();
    }

    private void get_data_transaksi_mmart(final JSONObject jTrans){

        HTTPHelper.getInstance(this).getTransaksiMmart(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {

                Log.e("berhasil rsponse Notif",obj+"");
                Log.e("berhasil rsponse Notif",obj+""+"transaksi id"+jTrans);
                pdGetData.dismiss();
                mCar = HTTPHelper.parseDataMmart(mCar, obj);
                arrBarang = HTTPHelper.parseBarangBelanja(obj);
                initView();
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if(maxTry == 0){
                    intentToMain();
                    Toast.makeText(activity, "Retrieving Data Null", Toast.LENGTH_SHORT).show();
                    pdGetData.dismiss();
                    maxTry = 4;
                }else{
                    get_data_transaksi_mmart(jTrans);
                    Log.d("try_ke_data_mart", String.valueOf(maxTry));
                    maxTry--;
                }
            }
        });
    }

    private void get_data_transaksi_mfood(final JSONObject jTrans){
        HTTPHelper.getInstance(this).getTransaksiMfood(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                Log.e("berhasil rsponse Notif",obj+"");
                Log.e("berhasil rsponse Notif",obj+""+"transaksi id"+jTrans);
                pdGetData.dismiss();
                mCar = HTTPHelper.parseDataMfood(mCar, obj);
                arrMakanan = HTTPHelper.parseMakananBelanja(obj);
                initView();
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if(maxTry == 0){
                    intentToMain();
                    Toast.makeText(activity, "Retrieving Data Null", Toast.LENGTH_SHORT).show();
                    pdGetData.dismiss();
                    maxTry = 4;
                }else{
                    get_data_transaksi_mfood(jTrans);
                    Log.d("try_ke_data_mfood", String.valueOf(maxTry));
                    maxTry--;
                }
            }
        });
    }

    private void get_data_transaksi_mstore(final JSONObject jTrans){
        HTTPHelper.getInstance(this).getTransaksiMstore(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                Log.e("berhasil rsponse Notif",obj+"");
                Log.e("berhasil rsponse Notif",obj+""+"transaksi id"+jTrans);
                pdGetData.dismiss();
                mCar = HTTPHelper.parseDataMstore(mCar, obj);
                arrStore = HTTPHelper.parseStoreBelanja(obj);
                initView();
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if(maxTry == 0){
                    intentToMain();
                    Toast.makeText(activity, "Retrieving Data Null", Toast.LENGTH_SHORT).show();
                    pdGetData.dismiss();
                    maxTry = 4;
                }else{
                    get_data_transaksi_mstore(jTrans);
                    Log.d("try_ke_data_mStore", String.valueOf(maxTry));
                    maxTry--;
                }
            }
        });
    }

    private void get_data_transaksi_mlaundry(final JSONObject jTrans){
        HTTPHelper.getInstance(this).getTransaksiMlaundry(jTrans, new NetworkActionResult() {

            @Override
            public void onSuccess(JSONObject obj) {
                pdGetData.dismiss();
                mCar = HTTPHelper.parseDataMlaundry(mCar, obj);
                arrLaundry = HTTPHelper.parseLaundryBelanja(obj);
                Log.e("noti diterima",obj+""+"transaksi id"+jTrans);
                initView();
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if(maxTry == 0){
                    intentToMain();
                    Toast.makeText(activity, "Retrieving Data Null", Toast.LENGTH_SHORT).show();
                    pdGetData.dismiss();
                    maxTry = 4;
                }else{
                    get_data_transaksi_mlaundry(jTrans);
                    Log.d("try_ke_data_mStore", String.valueOf(maxTry));
                    maxTry--;
                }
            }
        });
    }

    private void get_data_transaksi_mbox(final JSONObject jTrans){
        HTTPHelper.getInstance(this).getTransaksiMbox(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                pdGetData.dismiss();
                mCar = HTTPHelper.parseDataMbox(mCar, obj);
                arrDestinasi = HTTPHelper.parseDestinasiMbox(obj);
                Log.d("isi_arr_destinasi", arrDestinasi.get(0).latitude);
                initView();
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if(maxTry == 0){
                    intentToMain();
                    Toast.makeText(activity, "Retrieving Data Null", Toast.LENGTH_SHORT).show();
                    pdGetData.dismiss();
                    maxTry = 4;
                }else{
                    get_data_transaksi_mbox(jTrans);
                    Log.d("try_ke_data_mbox", String.valueOf(maxTry));
                    maxTry--;
                }
            }
        });
    }

    private ProgressDialog showLoading(){
        ProgressDialog ad = ProgressDialog.show(activity, "", "Loading...", true);
        return ad;
    }

    private void playSound(){
        v = (Vibrator) this.getSystemService(activity.VIBRATOR_SERVICE);
        long[] pattern = { 0, 100, 700};
        v.vibrate(pattern, 0);
//        v.vibrate(20000);

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            BG = MediaPlayer.create(getBaseContext(), notification);
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//            r.play();
        } catch (Exception e) {
            BG = MediaPlayer.create(getBaseContext(), R.raw.morningsunshine);
            e.printStackTrace();
        }

        BG.setLooping(true);
        BG.setVolume(100, 100);
        BG.start();
    }

}
