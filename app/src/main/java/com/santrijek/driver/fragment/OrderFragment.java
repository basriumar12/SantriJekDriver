package com.santrijek.driver.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.DisplayMetrics;

import com.santrijek.driver.activity.DestinasiMboxActivity;
import com.santrijek.driver.activity.DetailPengirimanActivity;
import com.santrijek.driver.activity.KonfirmasiBarangActivity;
import com.santrijek.driver.activity.ListBarangActivity;
import com.santrijek.driver.activity.ListLaundryActivity;
import com.santrijek.driver.activity.ListMakananActivity;
import com.santrijek.driver.model.DestinasiMbox;
import com.santrijek.driver.network.AsyncTaskHelperNoLoad;
import com.santrijek.driver.network.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.santrijek.driver.R;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import com.santrijek.driver.MainActivity;
import com.santrijek.driver.activity.ChatActivity;
import com.santrijek.driver.activity.RatingUserActivity;
import com.santrijek.driver.database.DBHandler;
import com.santrijek.driver.database.Queries;
import com.santrijek.driver.gmaps.GMapDirection;
import com.santrijek.driver.gmaps.directions.Directions;
import com.santrijek.driver.gmaps.directions.Leg;
import com.santrijek.driver.gmaps.directions.Route;
import com.santrijek.driver.gmaps.directions.Step;
import com.santrijek.driver.model.Content;
import com.santrijek.driver.model.Driver;
import com.santrijek.driver.model.Transaksi;
import com.santrijek.driver.network.AsyncTaskHelper;
import com.santrijek.driver.network.HTTPHelper;
import com.santrijek.driver.network.NetworkActionResult;
//import driver.pacekurir.drivermangjek.preference.UserPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class OrderFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    private static final String TAG = OrderFragment.class.getSimpleName();
    private View rootView;
    MainActivity activity;
    boolean isOn = false;
    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private Marker jakCar;
    private static final LatLng NUSAINDAH = new LatLng(4.0096749,122.5117972);
    private static final LatLng SENOPATI = new LatLng(-6.2331678, 106.8121171);
    Transaksi myTrans;
    Driver driver;
    RelativeLayout cancelOrder,barang_kurir;
    LinearLayout pickUpPelanggan;
    ImageView pickUpPelanggan1;
    int status;
    TextView textStatus, namaBarang, listBarang, detailOrder,txtpick,namamarket,namaPelangganOjek,namapenerima;

    Location myLocation, lastKnownLocation;
    Marker myMarker;
    ImageView gbr;
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    private static final int REQUEST_PERMISSION_CALL = 992;
    private boolean running = true;
    int maxRetrySP = 4;
    private GoogleApiClient googleApiClient;
    int maxRetry = 4;
    int maxRetry1 = 4;
    int maxRetry2 = 4;



    public OrderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("OnCreateView", "Run");
        activity = (MainActivity) getActivity();

        Queries que = new Queries(new DBHandler(activity));
        driver = que.getDriver();
        myTrans = que.getInProgressTransaksi();


        syncronizingAccount();

        if(myTrans.id_transaksi == null){
            backToHome();
        }else{
            selectionFitur(inflater, container);
            activity.ordering = true;
            initView();


        }
        que.closeDatabase();
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                setMyLocationLayerEnabled();
            } else {
                // TODO: 10/15/2016 Tell user to use GPS
            }
        }

        if(requestCode == REQUEST_PERMISSION_CALL){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "Call permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Call permission restricted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.mapOrder);
        mapFragment.getMapAsync(this);
        syncronizingAccount();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
//        activity.registerReceiver(broadcastReceiver, new IntentFilter(MyFirebaseMessagingService.BROADCAST_ACTION_ORDER));
        running = true;
        cekStatus();
        Log.d("Onresume", "Run");
    }

    @Override
    public void onPause(){
        super.onPause();
//        activity.unregisterReceiver(broadcastReceiver);
        running = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        running = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
    }

    private void selectionFitur(LayoutInflater inflater, ViewGroup container){
        ImageView logoOrder;
      final TextView namabr;

           final Transaksi myTrans1;

//        myTrans = (Transaksi) receiver.getSerializable("data_order");
        switch (Integer.parseInt(myTrans.order_fitur)){
            case 1: {
                rootView = inflater.inflate(R.layout.content_order_mride, container, false);
                setHasOptionsMenu(true);
                namaPelangganOjek = (TextView) rootView.findViewById(R.id.namaPelanggan1);
                logoOrder = (ImageView) rootView.findViewById(R.id.logoOrder);
                logoOrder.setImageResource(R.drawable.e_ride_on);
                txtpick = (TextView)rootView.findViewById(R.id.txt_pick) ;
                pickUpPelanggan = (LinearLayout) rootView.findViewById(R.id.pickUpPelanggan);
                namaPelangganOjek.setText(myTrans.nama_pelanggan);
                break;
            }
            case 2: {
                rootView = inflater.inflate(R.layout.content_order_mride, container, false);
                setHasOptionsMenu(true);
                namaPelangganOjek = (TextView) rootView.findViewById(R.id.namaPelanggan1);
                logoOrder = (ImageView) rootView.findViewById(R.id.logoOrder);
                logoOrder.setImageResource(R.mipmap.ic_fitur_mcar);
                pickUpPelanggan = (LinearLayout) rootView.findViewById(R.id.pickUpPelanggan);
                namaPelangganOjek.setText(myTrans.nama_pelanggan);

                break;
            }
            case 3:{
                rootView = inflater.inflate(R.layout.content_order_efood, container, false);
                setHasOptionsMenu(true);
                logoOrder = (ImageView) rootView.findViewById(R.id.logoOrder);
                pickUpPelanggan = (LinearLayout) rootView.findViewById(R.id.pickUpPelanggan);
                //pickUpPelanggan.setImageResource(R.drawable.ic_select_start);
                barang_kurir = (RelativeLayout) rootView.findViewById(R.id.listBarang);
                TextView namaRestoran = (TextView) rootView.findViewById(R.id.namaResto);
                namaRestoran.setText(myTrans.nama_resto);
                txtpick = (TextView)rootView.findViewById(R.id.txt_pick) ;
                barang_kurir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent toList = new Intent(activity, ListMakananActivity.class);
                        toList.putExtra("estimasi_biaya", myTrans.total_biaya);
                        toList.putExtra("nama_resto", myTrans.nama_resto);
                        toList.putExtra("kontak_telepon", myTrans.telepon_resto);
                        startActivity(toList);
                    }
                });
                Log.d("estimasi_biaya", myTrans.estimasi_biaya+"");
                logoOrder.setImageResource(R.drawable.ic_e_food);
                break;
            }
            case 4: {
                rootView = inflater.inflate(R.layout.content_order_msend, container, false);
                setHasOptionsMenu(true);
                logoOrder = (ImageView) rootView.findViewById(R.id.logoOrder);
                barang_kurir = (RelativeLayout) rootView.findViewById(R.id.listBarang);

                txtpick = (TextView)rootView.findViewById(R.id.txt_pick) ;
                barang_kurir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent toList = new Intent(activity, ListBarangActivity.class);
                        toList.putExtra("estimasi_biaya", myTrans.estimasi_biaya);
                        toList.putExtra("nama_toko", myTrans.nama_toko);
                        startActivity(toList);
                    }
                });
                pickUpPelanggan = (LinearLayout) rootView.findViewById(R.id.pickUpPelanggan);
            //  pickUpPelanggan.setImageResource(R.drawable.ic_select_start);
                //Log.d("estimasi_biaya", myTrans.estimasi_biaya+"");
                logoOrder.setImageResource(R.mipmap.ic_fitur_mmart);
                break;
            }
            case 5: {
                rootView = inflater.inflate(R.layout.content_order_msend, container, false);
                setHasOptionsMenu(true);
                logoOrder = (ImageView) rootView.findViewById(R.id.logoOrder);
                namaPelangganOjek = (TextView) rootView.findViewById(R.id.namaPelanggan1);
                namapenerima = (TextView) rootView.findViewById(R.id.namaPelanggan);
                txtpick = (TextView)rootView.findViewById(R.id.txt_pick) ;
                namaPelangganOjek.setText(myTrans.nama_penerima);
                namaPelangganOjek.setText(myTrans.nama_pengirim);
                barang_kurir = (RelativeLayout) rootView.findViewById(R.id.listBarang);
                barang_kurir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                     Intent toDet = new Intent(activity, DetailPengirimanActivity.class);
                     toDet.putExtra("data_order", myTrans);
                     startActivity(toDet);
                    }
                });
                pickUpPelanggan = (LinearLayout) rootView.findViewById(R.id.pickUpPelanggan);
                logoOrder.setImageResource(R.mipmap.ic_fitur_msend);
                break;
            }
            case 6:{
                rootView = inflater.inflate(R.layout.content_order_mservice, container, false);
                setHasOptionsMenu(true);
                logoOrder = (ImageView) rootView.findViewById(R.id.logoOrder);
                logoOrder.setImageResource(R.mipmap.ic_fitur_mservice);
                pickUpPelanggan = (LinearLayout) rootView.findViewById(R.id.pickUpPelanggan);
               // pickUpPelanggan.setImageResource(R.drawable.ic_select_start);
                txtpick.setText("Mulai Perjalanan");
                TextView layanan, problem, residentialType, quantityAcType;
                layanan = (TextView) rootView.findViewById(R.id.layanan);
                problem = (TextView) rootView.findViewById(R.id.problem);
                residentialType = (TextView) rootView.findViewById(R.id.residentialType);
                quantityAcType = (TextView) rootView.findViewById(R.id.quantityAcType);
                layanan.setText(myTrans.massage_menu);
                problem.setText(myTrans.lama_pelayanan+" menit");
                residentialType.setText(myTrans.jam_pelayanan);
                quantityAcType.setText(myTrans.catatan_tambahan);
                break;
            }
            case 7:{
                rootView = inflater.inflate(R.layout.content_order_obbox, container, false);
                setHasOptionsMenu(true);
                logoOrder = (ImageView) rootView.findViewById(R.id.logoOrder);
          //      TextView shpper = (TextView) rootView.findViewById(R.id.shipper);
                barang_kurir = (RelativeLayout) rootView.findViewById(R.id.listBarang);
                txtpick = (TextView)rootView.findViewById(R.id.txt_pick) ;
                barang_kurir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent toDet = new Intent(activity, DestinasiMboxActivity.class);
                        toDet.putExtra("transaksi", myTrans);
                        startActivity(toDet);
                    }
                });
                pickUpPelanggan = (LinearLayout) rootView.findViewById(R.id.pickUpPelanggan);
              //  pickUpPelanggan.setImageResource(R.drawable.ic_select_start);
                txtpick.setText("Start Trip");
                logoOrder.setImageResource(R.mipmap.ic_fitur_mbox);
                break;
            }
            case 8:{
                rootView = inflater.inflate(R.layout.content_order_mservice, container, false);
                setHasOptionsMenu(true);
                logoOrder = (ImageView) rootView.findViewById(R.id.logoOrder);
                logoOrder.setImageResource(R.mipmap.ic_fitur_mservice);
                pickUpPelanggan = (LinearLayout) rootView.findViewById(R.id.pickUpPelanggan);
               // pickUpPelanggan.setImageResource(R.drawable.ic_select_start);
                txtpick.setText("Start Trip");
                TextView layanan, problem, residentialType, quantityAcType;
                layanan = (TextView) rootView.findViewById(R.id.layanan);
                problem = (TextView) rootView.findViewById(R.id.problem);
                residentialType = (TextView) rootView.findViewById(R.id.residentialType);
                quantityAcType = (TextView) rootView.findViewById(R.id.quantityAcType);
                layanan.setText(myTrans.jenis_service);
                problem.setText(myTrans.problem);
                residentialType.setText(myTrans.residential_type);
                quantityAcType.setText(myTrans.quantity+" "+myTrans.ac_type);
                break;
            }

            case 9:{
                rootView = inflater.inflate(R.layout.content_order_efood, container, false);
                setHasOptionsMenu(true);
                logoOrder = (ImageView) rootView.findViewById(R.id.logoOrder);
                namaBarang = (TextView) rootView.findViewById(R.id.namaBarang);
                namaBarang.setVisibility(View.GONE);
                pickUpPelanggan = (LinearLayout) rootView.findViewById(R.id.pickUpPelanggan);
                //pickUpPelanggan.setImageResource(R.drawable.ic_select_start);
                txtpick.setText("Start Trip");
                listBarang = (TextView) rootView.findViewById(R.id.listBarang);
                listBarang.setText("DAFTAR BARANG");
                TextView namaRestoran = (TextView) rootView.findViewById(R.id.namaResto);
                namaRestoran.setText(myTrans.nama_toko);

                listBarang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent toList = new Intent(activity, ListBarangActivity.class);
                        toList.putExtra("estimasi_biaya", myTrans.total_biaya);
                        toList.putExtra("nama_toko", myTrans.nama_toko);
                        toList.putExtra("kontak_telepon", myTrans.telepon_toko);
                        Log.d("Estimasi biaya : ","" + myTrans.total_biaya + " nama_toko : " + myTrans.nama_toko + " telepon_toko : "+ myTrans.telepon_toko);
                        startActivity(toList);
                    }
                });
                Log.d("estimasi_biaya", myTrans.estimasi_biaya+"");
                logoOrder.setImageResource(R.drawable.ic_e_electronic);
                break;
            }

            case 10:{
                rootView = inflater.inflate(R.layout.conten_grocery, container, false);
                setHasOptionsMenu(true);
                logoOrder = (ImageView) rootView.findViewById(R.id.logoOrder);
                barang_kurir = (RelativeLayout) rootView.findViewById(R.id.listBarang);

                txtpick = (TextView)rootView.findViewById(R.id.txt_pick) ;
                namamarket = (TextView)rootView.findViewById(R.id.namaResto) ;
                namamarket.setText(myTrans.nama_laundry);
                barang_kurir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent toList = new Intent(activity, ListLaundryActivity.class);
                        toList.putExtra("estimasi_biaya", myTrans.total_biaya);
                        toList.putExtra("nama_laundry", myTrans.nama_laundry);
                        toList.putExtra("kontak_telepon", myTrans.telepon_laundry);
                  //      Log.d("Estimasi biaya : ","" + myTrans.total_biaya + " nama_laundry : " + myTrans.nama_laundry + " telepon_toko : "+ myTrans.telepon_laundry);
                        startActivity(toList);
                    }
                });
                pickUpPelanggan = (LinearLayout) rootView.findViewById(R.id.pickUpPelanggan);
                //  pickUpPelanggan.setImageResource(R.drawable.ic_select_start);
                //Log.d("estimasi_biaya", myTrans.estimasi_biaya+"");
                logoOrder.setImageResource(R.mipmap.ic_fitur_mfood);
                break;
            }


            default:
                break;
        }
    }

    private void cekStatus(){
        final Handler handlerTimer = new Handler();
        handlerTimer.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (running) {
                    Log.d("run_checking", "yes");
                    final JSONObject dataTrans = new JSONObject();
                    try {
                        dataTrans.put("id_transaksi", myTrans.id_transaksi);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    handlerTimer.postDelayed(this, 60000);
                    checkingStatus(dataTrans);
                }
            }
        }, 60000);
    }

    private void initView() {
          final TextView namaPelanggan,namaPelangganOjek, textTunai, textCredit, alamatJemput, alamatAntar, idOrder;
        final ImageView callPelanggan,  chatPelanggan,img_rute,img_rute1,cancelOrder1;

        final RelativeLayout rela_asal,rela_tujuan,alamatdesk;



        rela_asal = (RelativeLayout)rootView.findViewById(R.id.btn_asal);
        rela_tujuan = (RelativeLayout)rootView.findViewById(R.id.btn_tujuan);
        alamatdesk = (RelativeLayout)rootView.findViewById(R.id.alamat_desk);


        textStatus = (TextView) rootView.findViewById(R.id.textStatus);
        namaPelanggan = (TextView) rootView.findViewById(R.id.namaPelanggan);
        namaPelangganOjek = (TextView) rootView.findViewById(R.id.namaPelanggan1);
        textTunai = (TextView) rootView.findViewById(R.id.textTunai);
        textCredit = (TextView) rootView.findViewById(R.id.textCredit);
        alamatJemput = (TextView) rootView.findViewById(R.id.alamatJemput);

        alamatAntar = (TextView) rootView.findViewById(R.id.alamatAntar);

        chatPelanggan = (ImageView) rootView.findViewById(R.id.chatPelanggan);
        callPelanggan = (ImageView) rootView.findViewById(R.id.callPelanggan);
         cancelOrder = (RelativeLayout) rootView.findViewById(R.id.cancelOrder);
        img_rute = (ImageView) rootView.findViewById(R.id.ruteasal);
        img_rute1 = (ImageView) rootView.findViewById(R.id.rutetujuan);
        idOrder = (TextView) rootView.findViewById(R.id.idOrder);

        if(!myTrans.order_fitur.equals("8") && !myTrans.order_fitur.equals("6")){
            alamatAntar.setText(myTrans.alamat_tujuan);
        }
        if(myTrans.order_fitur.equals("3")){
            alamatAntar.setText(myTrans.alamat_asal);
        }

        namaPelanggan.setText(myTrans.nama_pelanggan);
       // namaPelangganOjek.setText(myTrans.nama_pelanggan);
        int hargaAkhir = 0;

        if (myTrans.pakai_mpay.equals("0")){
            hargaAkhir = Integer.parseInt(myTrans.kredit_promo) - (Integer.parseInt(myTrans.biaya_akhir)+myTrans.total_biaya);
            if(hargaAkhir >= 0){
                textTunai.setText(amountAdapter(0));
            }else{
                textTunai.setText(amountAdapter(-1* hargaAkhir));
            }
            textCredit.setText("("+amountAdapter(Integer.parseInt(myTrans.kredit_promo))+")");
        }else{
            textTunai.setText(amountAdapter(0));
            textCredit.setText("("+amountAdapter(Integer.parseInt(myTrans.biaya_akhir)+myTrans.total_biaya)+")");
        }

        idOrder.setText("ID "+myTrans.id_transaksi);

        //alamatJemput.setText(myTrans.alamat_asal);
        if(myTrans.order_fitur.equals("3")){
            alamatJemput.setText(myTrans.alamat_tujuan);

        }else{
            alamatJemput.setText(myTrans.alamat_asal);
        }
        rela_asal.setBackgroundResource(R.color.greyText);
        rela_tujuan.setBackgroundResource(R.color.textGrey);
        rela_asal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alamatJemput.setVisibility(View.VISIBLE);
                alamatAntar.setVisibility(View.GONE);
                rela_asal.setBackgroundResource(R.color.greyText);
                rela_tujuan.setBackgroundResource(R.color.textGrey);
                img_rute1.setVisibility(View.GONE);
                img_rute.setVisibility(View.VISIBLE);

            }
        });
        rela_tujuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alamatJemput.setVisibility(View.GONE);
                alamatAntar.setVisibility(View.VISIBLE);
                rela_asal.setBackgroundResource(R.color.textGrey);
                rela_tujuan.setBackgroundResource(R.color.greyText);
                img_rute1.setVisibility(View.VISIBLE);
                img_rute.setVisibility(View.GONE);




            }
        });
        img_rute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asal();


            }
        });
        img_rute1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tuujuan();


            }
        });


        callPelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL);
                    return;
                }
                showWarningCall();
            }
        });
        chatPelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toChat = new Intent(activity, ChatActivity.class);
                toChat.putExtra("reg_id", myTrans.reg_id_pelanggan);
                startActivity(toChat);
            }
        });

        if(driver.status == 2){
        //    pickUpPelanggan.setImageResource(R.drawable.ic_select_start);
            if(myTrans.order_fitur.equals("3")|| myTrans.order_fitur.equals("2")|| myTrans.order_fitur.equals("1")|| myTrans.order_fitur.equals("5")|| myTrans.order_fitur.equals("10")|| myTrans.order_fitur.equals("7")){
                txtpick.setText("Start Trip");
            }
        }

        if(driver.status == 3){
           // pickUpPelanggan.setImageResource(R.drawable.ic_select_finish);

            if(myTrans.order_fitur.equals("3")|| myTrans.order_fitur.equals("2")|| myTrans.order_fitur.equals("1")|| myTrans.order_fitur.equals("5")|| myTrans.order_fitur.equals("10")|| myTrans.order_fitur.equals("7")){
                txtpick.setText("Finish Trip");
            }
        }

        final JSONObject dataOrder = new JSONObject();
        try {
            dataOrder.put("id", driver.id);
            dataOrder.put("id_transaksi", myTrans.id_transaksi);
        } catch (JSONException e){
            e.printStackTrace();
        }

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View view) {
             if(driver.status == 2){
                  showWarningCancel(dataOrder);
               }
           }
       });

        pickUpPelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Queries que = new Queries(new DBHandler(activity));
                driver = que.getDriver();
                que.closeDatabase();

                if(driver.status == 2){
                    startPerjalanan(dataOrder);
                    txtpick.setText("Start Trip");
                    Log.d("START","");
                }else if(driver.status == 3){
                    showWarningFinish(dataOrder);
                    txtpick.setText("Finish Trip");
                    Log.d("WORKING","");

                }else if(driver.status == 1){
                    Intent toRate = new Intent(getContext(), RatingUserActivity.class);
                    toRate.putExtra("id_transaksi", myTrans.id_transaksi);
                    toRate.putExtra("waktu_order", myTrans.waktu_order);
                    toRate.putExtra("harga", myTrans.harga);
                    toRate.putExtra("id_pelanggan", myTrans.id_pelanggan);
                    toRate.putExtra("order_fitur", myTrans.order_fitur);
                    toRate.putExtra("id_driver", driver.id);
                    startActivity(toRate);
                    Log.d("COMPLETED","");

                }else{
                    //Toast.makeText(activity, String.valueOf(driver.status), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkingStatus(JSONObject dataOrder){
        Log.d("isi_data_check", dataOrder.toString());
//        final ProgressDialog startProgess = showLoading();
        HTTPHelper.getInstance(activity).checkStatusTransaksi(dataOrder, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
//                startProgess.dismiss();
                try {
                    if(obj.getString("message").equals("check status")){
                        JSONArray jArray = obj.getJSONArray("data");
                        JSONObject oneObject = jArray.getJSONObject(0);
                        if(oneObject.getString("status").equals("5")){
                            backToHome();
                            Toast.makeText(activity, "Transaksi dibatalkan", Toast.LENGTH_LONG).show();
                            running = false;
                            Log.d("cek_status", "Cancel and Free");
                        }else{
                            Log.d("cek_status", "Still Oke");
                        }
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
//                startProgess.dismiss();
            }
        });
    }
    private ProgressDialog showLoading(){
        ProgressDialog ad = ProgressDialog.show(activity, "", "Loading...", true);
        return ad;
    }

    private void cancelPerjalanan(JSONObject dataOrder){
//        Log.d("isi_data_order", dataOrder.toString());
        final ProgressDialog startProgess = showLoading();
        HTTPHelper.getInstance(activity).cancelOrder(dataOrder, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                try {
                    if(obj.getString("message").equals("success")){
                        Toast.makeText(activity, "Order Canceled", Toast.LENGTH_SHORT).show();
                        announceToUser(myTrans.reg_id_pelanggan, myTrans.id_transaksi, 2);
                    }else{
                        Toast.makeText(activity, "Gagal melakukan cancel order", Toast.LENGTH_LONG).show();
                    }
                    startProgess.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String message) {

            }

            @Override
            public void onError(String message) {
//                Log.e(TAG, message);
                startProgess.dismiss();
            }
        });
    }

    private void startPerjalanan(final JSONObject dataOrder){
//        Log.d("isi_data_order", dataOrder.toString());
        final ProgressDialog startProgess = showLoading();
        HTTPHelper.getInstance(activity).startOrder(dataOrder, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                try {
                    if(obj.getString("message").equals("success")){
                        Toast.makeText(activity, "Let's Go!", Toast.LENGTH_SHORT).show();
                        announceToUser(myTrans.reg_id_pelanggan, myTrans.id_transaksi, 3);

                      //  pickUpPelanggan.setImageResource(R.drawable.ic_select_finish);

                        cancelOrder.setVisibility(View.GONE);

                        if(myTrans.order_fitur.equals("3")|| myTrans.order_fitur.equals("2")|| myTrans.order_fitur.equals("1")|| myTrans.order_fitur.equals("5")|| myTrans.order_fitur.equals("10")|| myTrans.order_fitur.equals("7")){
                            txtpick.setText("Finish Trip");
                        }
                    }else{
                        backToHome();
                        Toast.makeText(activity, "Transaksi dibatalkan", Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startProgess.dismiss();
                maxRetrySP = 4;
            }

            @Override
            public void onFailure(String message) {

            }

            @Override
            public void onError(String message) {
                if(maxRetrySP == 0){
                    startProgess.dismiss();
                    Toast.makeText(activity, "Koneksi bermasalah..", Toast.LENGTH_SHORT).show();
                    maxRetrySP = 4;
                }else{
                    startPerjalanan(dataOrder);
                    maxRetrySP--;
                    Log.d("Try_ke_start_perjaanan", String.valueOf(maxRetrySP));
                    startProgess.dismiss();
                }
            }
        });
    }

    private void backToHome(){
        activity.ordering = false;
        Queries que = new Queries(new DBHandler(activity));
        que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
        que.truncate(DBHandler.TABLE_BARANG_BELANJA);
        que.truncate(DBHandler.TABLE_MAKANAN_BELANJA);
        que.truncate(DBHandler.TABLE_LAUNDRY_BELANJA);
        que.truncate(DBHandler.TABLE_DESTINASI_MBOX);
        que.truncate(DBHandler.TABLE_CHAT);
        que.updateStatus(1);
        que.closeDatabase();
        //Toast.makeText(activity, "Transaksi dibatalkan", Toast.LENGTH_LONG).show();
        changeFragment(new DashboardFragment(), false);
    }

    private void announceToUser(String userRegID, String id_trans, int acc){
        Content content = new Content();
        content.addRegId(userRegID);

        if(acc == 4){
            content.createDataOrderFins(driver.id, id_trans, String.valueOf(acc), myTrans.order_fitur, myTrans.id_pelanggan, driver.image);
        }else{
            content.createDataOrder(driver.id, id_trans, String.valueOf(acc), myTrans.order_fitur);
        }
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
                Queries que = new Queries(new DBHandler(activity));
                if (status == 1){
                    if(acc == 3){
                        que.updateStatus(3);
                        que.closeDatabase();
                    }else if(acc == 2){
                        que.truncate(DBHandler.TABLE_CHAT);
                        que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                        que.truncate(DBHandler.TABLE_BARANG_BELANJA);
                        que.truncate(DBHandler.TABLE_LAUNDRY_BELANJA);
                        que.truncate(DBHandler.TABLE_DESTINASI_MBOX);
                        que.truncate(DBHandler.TABLE_MAKANAN_BELANJA);
                        que.updateStatus(1);
                        que.closeDatabase();
                        changeFragment(new DashboardFragment(), false);
                        activity.ordering = false;
                    }else{
                        que.truncate(DBHandler.TABLE_CHAT);
                        que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                        que.truncate(DBHandler.TABLE_BARANG_BELANJA);
                        que.truncate(DBHandler.TABLE_LAUNDRY_BELANJA);
                        que.truncate(DBHandler.TABLE_DESTINASI_MBOX);
                        que.truncate(DBHandler.TABLE_MAKANAN_BELANJA);
                        que.updateStatus(1);
                        que.closeDatabase();

                        Intent toRate = new Intent(activity, RatingUserActivity.class);
                        toRate.putExtra("waktu_order", myTrans.waktu_order);
                        toRate.putExtra("harga", myTrans.harga);
                        toRate.putExtra("id_transaksi", myTrans.id_transaksi);
                        toRate.putExtra("id_pelanggan", myTrans.id_pelanggan);
                        toRate.putExtra("order_fitur", myTrans.order_fitur);
                        toRate.putExtra("id_driver", driver.id);
                        startActivity(toRate);
//                        activity.finish();
                    }
                }else if(status == 0){
                    if(acc == 3){
                        que.updateStatus(3);
                        que.closeDatabase();
                    }else if(acc == 2){
                        que.truncate(DBHandler.TABLE_CHAT);
                        que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                        que.truncate(DBHandler.TABLE_BARANG_BELANJA);
                        que.truncate(DBHandler.TABLE_LAUNDRY_BELANJA);
                        que.truncate(DBHandler.TABLE_DESTINASI_MBOX);
                        que.truncate(DBHandler.TABLE_MAKANAN_BELANJA);
                        que.updateStatus(1);
                        que.closeDatabase();
                        changeFragment(new DashboardFragment(), false);
                        activity.ordering = false;
                    }else{
                        que.truncate(DBHandler.TABLE_CHAT);
                        que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                        que.truncate(DBHandler.TABLE_BARANG_BELANJA);
                        que.truncate(DBHandler.TABLE_LAUNDRY_BELANJA);
                        que.truncate(DBHandler.TABLE_DESTINASI_MBOX);
                        que.truncate(DBHandler.TABLE_MAKANAN_BELANJA);
                        que.updateStatus(1);
                        que.closeDatabase();

                        Intent toRate = new Intent(activity, RatingUserActivity.class);
                        toRate.putExtra("waktu_order", myTrans.waktu_order);
                        toRate.putExtra("harga", myTrans.harga);
                        toRate.putExtra("id_transaksi", myTrans.id_transaksi);
                        toRate.putExtra("id_pelanggan", myTrans.id_pelanggan);
                        toRate.putExtra("order_fitur", myTrans.order_fitur);
                        toRate.putExtra("id_driver", driver.id);
                        startActivity(toRate);
//                        activity.finish();
                    }
                    Toast.makeText(activity, "Message sending failed to customer", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onAsyncTaskPreExecute(AsyncTaskHelper asyncTask) {

            }
        });
        asyncTask.execute();
    }

    private MaterialDialog showWarningCancel(final JSONObject dataOrder) {
        final MaterialDialog md = new MaterialDialog.Builder(activity)
                .title("Warning!")
                .content("Do you want to cancel the trip?")
                .icon(new IconicsDrawable(activity)
                        .icon(FontAwesome.Icon.faw_exclamation_triangle)
                        .color(Color.BLUE)
                        .sizeDp(24))
                .positiveText("Yes")
                .positiveColor(Color.BLUE)
                .negativeText("No")
                .negativeColor(Color.DKGRAY)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);
        View negative = md.getActionButton(DialogAction.NEGATIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelPerjalanan(dataOrder);
                md.dismiss();
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
            }
        });

        return md;
    }

    private MaterialDialog showWarningFinish(final JSONObject dataOrder) {
        final MaterialDialog md = new MaterialDialog.Builder(activity)
                .title("Warning!")
                .content("Has your trip finished?")
                .icon(new IconicsDrawable(activity)
                        .icon(FontAwesome.Icon.faw_exclamation_triangle)
                        .color(Color.BLUE)
                        .sizeDp(24))
                .positiveText("Yes")
                .positiveColor(Color.BLUE)
                .negativeText("No")
                .negativeColor(Color.DKGRAY)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);
        View negative = md.getActionButton(DialogAction.NEGATIVE);
//        Log.d("data_order_finish", dataOrder.toString());
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
                finishPerjalanan(dataOrder);
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
            }
        });

        return md;
    }

    private void finishPerjalanan(final JSONObject dataOrder){
        final ProgressDialog finishProgress = showLoading();

        if(myTrans.order_fitur.equals("3") || myTrans.order_fitur.equals("9")|| myTrans.order_fitur.equals("10")){
            Intent toKonfirm = new Intent(activity, KonfirmasiBarangActivity.class);
            toKonfirm.putExtra("transaksi", myTrans);
            startActivity(toKonfirm);
            finishProgress.dismiss();
        }else{
            HTTPHelper.getInstance(activity).finishOrder(dataOrder, new NetworkActionResult() {
                @Override
                public void onSuccess(JSONObject obj) {
                    textStatus.setText("Status : Layanan Sudah Selesai");
                    finishProgress.dismiss();
                    announceToUser(myTrans.reg_id_pelanggan, myTrans.id_transaksi, 4);
                }

                @Override
                public void onFailure(String message) {
                }

                @Override
                public void onError(String message) {
                    if(maxRetrySP == 0){
                        finishProgress.dismiss();
                        Toast.makeText(activity, "Koneksi bermasalah..", Toast.LENGTH_SHORT).show();
                        maxRetrySP = 4;
                    }else{
                        finishPerjalanan(dataOrder);
                        maxRetrySP--;
                        Log.d("Try_ke_finish_perjalanan", String.valueOf(maxRetrySP));
                        finishProgress.dismiss();
                    }
                }
            });
        }
    }

    private MaterialDialog showWarningCall() {
        final MaterialDialog md = new MaterialDialog.Builder(activity)
                .title("Cost Warning")
                .content("Do you want to contact the driver?")
                .icon(new IconicsDrawable(activity)
                        .icon(FontAwesome.Icon.faw_phone)
                        .color(Color.BLUE)
                        .sizeDp(24))
                .positiveText("Yes")
                .positiveColor(Color.BLUE)
                .negativeText("No")
                .negativeColor(Color.LTGRAY)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);
        View negative = md.getActionButton(DialogAction.NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:"+myTrans.telepon_pelanggan));
                startActivity(phoneIntent);
                md.dismiss();
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
            }
        });

        return md;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        updateLastLocation(true);
        setMyLocationLayerEnabled();
        syncronizingAccount();



    }

    private void setMapData(GoogleMap googleMap, final LatLng str, final LatLng des) {

        googleMap.addMarker(createMark(myTrans.alamat_asal, str));
        googleMap.addMarker(createMark1(myTrans.alamat_tujuan, des));

        getLocation(googleMap, str, des);
        centerIncidentRouteOnMap(googleMap, str, des);

    }

    private MarkerOptions createMark(String name, final LatLng str){
        MarkerOptions markerOptions = new MarkerOptions();
        LinearLayout ln = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.marker_dialog, null, false);
        TextView title = (TextView) ln.findViewById(R.id.titleMarker);
        title.setText(name);
        ln.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        ln.layout(0, 0, ln.getMeasuredWidth(), ln.getMeasuredHeight());

        ln.setDrawingCacheEnabled(true);
        ln.buildDrawingCache();
        Bitmap bm = ln.getDrawingCache();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bm));
        markerOptions.position(str);

        return markerOptions;
    }
    private MarkerOptions createMarkresto(String name, final LatLng str){
        MarkerOptions markerOptions = new MarkerOptions();
        LinearLayout ln = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.marker_resto, null, false);
        TextView title = (TextView) ln.findViewById(R.id.titleMarker);
        title.setText(name);
        ln.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        ln.layout(0, 0, ln.getMeasuredWidth(), ln.getMeasuredHeight());

        ln.setDrawingCacheEnabled(true);
        ln.buildDrawingCache();
        Bitmap bm = ln.getDrawingCache();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bm));
        markerOptions.position(str);

        return markerOptions;
    }
    private MarkerOptions createMark1(String name, final LatLng str){
        MarkerOptions markerOptions = new MarkerOptions();
        LinearLayout ln = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.marker_tujuan, null, false);
        TextView title = (TextView) ln.findViewById(R.id.titleMarker);
        title.setText(name);
        ln.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        ln.layout(0, 0, ln.getMeasuredWidth(), ln.getMeasuredHeight());

        ln.setDrawingCacheEnabled(true);
        ln.buildDrawingCache();
        Bitmap bm = ln.getDrawingCache();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bm));
        markerOptions.position(str);

        return markerOptions;
    }

    private void setMapDestinasiMbox(GoogleMap googleMap, LatLng start){
        googleMap.addMarker(createMark("START", start));
        ArrayList<LatLng> arrLat = new ArrayList<>();

        Queries qued = new Queries(new DBHandler(activity));
        ArrayList<DestinasiMbox> arrD  = qued.getAllDestinasiMbox();
        qued.closeDatabase();

        for(int i=0; i<arrD.size(); i++){
            LatLng latLng = new LatLng(Double.parseDouble(arrD.get(i).latitude),
                    Double.parseDouble(arrD.get(i).longitude));
            Log.d("destinasi_latlng_mbox", arrD.get(i).latitude+" "+arrD.get(i).longitude);
            googleMap.addMarker(createMark1("Destination "+(i+1), latLng));
            arrLat.add(latLng);
        }

        getLocation(googleMap, start, arrLat.get(0));
        for(int i=0; i<arrLat.size()-1; i++){
            getLocation(googleMap, arrLat.get(i), arrLat.get(i+1));
        }
        centerIncidentRouteOnMap(googleMap, start, arrLat.get(arrLat.size()-1));
    }

    private void centerIncidentRouteOnMap(GoogleMap googleMap, LatLng latLng1, LatLng latLng2) {
        LatLngBounds.Builder bld = new LatLngBounds.Builder();

        bld.include(latLng1);
        bld.include(latLng2);

        LatLngBounds bounds = bld.build();

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        googleMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        metrics.widthPixels,
                        getResources().getDimensionPixelSize(R.dimen.mapHeight),
                        100));
    }

    private void addMarkerToMap(){
        LatLng lokasiPelanggan = new LatLng(Double.parseDouble(myTrans.start_latitude),
                Double.parseDouble(myTrans.start_latitude));

        jakCar = mMap.addMarker(new MarkerOptions()
                .position(lokasiPelanggan)
                .title("LOKASI")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_tag_blue)));

       //mMap.addMarker(lokasiPelanggan);

    }

    public void setMyLocationLayerEnabled() {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            addMarkerToMap();
            return;
        }


        mMap.setMyLocationEnabled(true);
        LatLng malang = new LatLng(Double.parseDouble(myTrans.end_latitude),Double.parseDouble(myTrans.end_longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(malang, 15));

        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setAllGesturesEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);

//        }
        Log.d("ORDER FITUR : ","" +myTrans.order_fitur);

        switch (myTrans.order_fitur){
            case  "3":{
                LatLng start = new LatLng(Double.parseDouble(myTrans.start_latitude),
                        Double.parseDouble(myTrans.start_longitude));

                LatLng end = new LatLng(Double.parseDouble(myTrans.end_latitude),
                        Double.parseDouble(myTrans.end_longitude));
                //setMapData(mMap, end, start);
                mMap.addMarker(createMarkresto(myTrans.nama_resto, end));
                mMap.addMarker(createMark1("Destination", start));

                getLocation(mMap, end, start);
                break;
            }
            case "6":{
                LatLng start = new LatLng(Double.parseDouble(myTrans.start_latitude),
                        Double.parseDouble(myTrans.start_longitude));
                LatLng end = new LatLng(Double.parseDouble(driver.latitude),
                        Double.parseDouble(driver.longitude));
                mMap.addMarker(createMark("Destination", start));
                getLocation(mMap, end, start);
                centerIncidentRouteOnMap(mMap, start, end);
                break;
            }
            case "7":{
                LatLng start = new LatLng(Double.parseDouble(myTrans.start_latitude),
                        Double.parseDouble(myTrans.start_longitude));
                setMapDestinasiMbox(mMap, start);
                break;
            }
            case "8":{
                LatLng start = new LatLng(Double.parseDouble(myTrans.start_latitude),
                        Double.parseDouble(myTrans.start_longitude));
                LatLng end = new LatLng(Double.parseDouble(driver.latitude),
                    Double.parseDouble(driver.longitude));
                mMap.addMarker(createMark("Location", start));
                getLocation(mMap, end, start);
                centerIncidentRouteOnMap(mMap, start, end);
                break;
            }
            case "9" : {
                LatLng start = new LatLng(Double.parseDouble(myTrans.start_latitude),
                        Double.parseDouble(myTrans.start_longitude));

                LatLng end = new LatLng(Double.parseDouble(myTrans.end_latitude),
                        Double.parseDouble(myTrans.end_longitude));
                //setMapData(mMap, end, start);
                mMap.addMarker(createMark(myTrans.nama_toko, end));
                mMap.addMarker(createMark("DESTINATION", start));

                getLocation(mMap, end, start);
                Log.d("LatLog Marker : ",""+ start + " " + end);
                break;

            }
            case "10" : {
                LatLng start = new LatLng(Double.parseDouble(myTrans.start_latitude),
                        Double.parseDouble(myTrans.start_longitude));

                LatLng end = new LatLng(Double.parseDouble(myTrans.end_latitude),
                        Double.parseDouble(myTrans.end_longitude));
                //setMapData(mMap, end, start);
                mMap.addMarker(createMarkresto(myTrans.nama_laundry, end));
                mMap.addMarker(createMark1(myTrans.nama_pelanggan, start));

                getLocation(mMap, end, start);
                break;
            }
            default:{
                LatLng start = new LatLng(Double.parseDouble(myTrans.start_latitude),
                        Double.parseDouble(myTrans.start_longitude));
                LatLng end = new LatLng(Double.parseDouble(myTrans.end_latitude),
                        Double.parseDouble(myTrans.end_longitude));
                setMapData(mMap, start, end);
                break;
            }
        }
    }

    private void drawRoute(GoogleMap googleMap, String json) {
        if (json != null) {

            Directions directions = new Directions(activity);
            try {
                List<Route> routes = directions.parse(json);
                for (Route route : routes) {

                    PolylineOptions rectLineRed = new PolylineOptions().width(10).color(getResources().getColor(R.color.greyText));

                    for (Leg leg : route.getLegs()) {
                        for (Step step : leg.getSteps()) {

                            rectLineRed.add(step.getStartLocation());
                            for (LatLng latlng : step.getPoints()) {
                                rectLineRed.add(latlng);
                            }
                            rectLineRed.add(step.getEndLocation());
                        }
                        googleMap.addPolyline(rectLineRed);
                    }
                }

                if (routes != null && routes.size() == 0) {
                    Toast.makeText(
                            activity,
                            "We cannot route your current location. Maybe you are too far.",
                            Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getLocation(final GoogleMap googleMap, final LatLng origin, final LatLng destination) {
        AsyncTaskHelperNoLoad asyncTask = new AsyncTaskHelperNoLoad(activity, true);
        asyncTask.setAsyncTaskListener(new AsyncTaskHelperNoLoad.OnAsyncTaskListener() {

            String json = null;

            @Override
            public void onAsyncTaskDoInBackground(AsyncTaskHelperNoLoad asyncTask) {
                GMapDirection directions = new GMapDirection();
                json = HTTPHelper.getJSONFromUrl(directions.getUrl(origin, destination, GMapDirection.MODE_DRIVING, false));
            }

            @Override
            public void onAsyncTaskProgressUpdate(AsyncTaskHelperNoLoad asyncTask) {

            }

            @Override
            public void onAsyncTaskPostExecute(AsyncTaskHelperNoLoad asyncTask) {
                if (json != null) {
                    drawRoute(googleMap, json);
                }
            }

            @Override
            public void onAsyncTaskPreExecute(AsyncTaskHelperNoLoad asyncTask) {

            }
        });
        asyncTask.execute();
    }

    public void changeFragment(Fragment frag, boolean addToBackStack) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container_body, frag);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }



    private String amountAdapter(int amo){
        return "$"+NumberFormat.getNumberInstance(Locale.GERMANY).format(amo)+".00";
    }

    private void updateLastLocation(boolean move) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        syncronizingAccount();
    }
    private void syncronizingAccount() {
        //get saldo
        //get status account
        //get get order

        JSONObject jSync = new JSONObject();
        try {
            jSync.put("id", driver.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HTTPHelper.getInstance(activity).syncAccount(jSync, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                try {
                    if (obj.getString("message").equals("success")) {
                        driver = HTTPHelper.getInstance(activity).parseUserSync(activity, obj.toString());
                        Queries que = new Queries(new DBHandler(activity));
                        que.updateStatus(driver.status);
                      //  Transaksi runTrans = HTTPHelper.getInstance(activity).parseTransaksi(activity, obj.toString());

                        que.closeDatabase();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //pd.dismiss();
                maxRetry = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
//                pd.dismiss();
                if (maxRetry == 0) {
                    Toast.makeText(activity, "Koneksi bermasalah..", Toast.LENGTH_SHORT).show();
                    //pd.dismiss();
                    maxRetry = 4;
                } else {
//                    pd = showLoading();
                    syncronizingAccount();
                    Log.d("try_ke_sync ", String.valueOf(maxRetry));
                    maxRetry--;
                }

            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(activity, "Location Changed " + location.getLatitude()
                + location.getLongitude(), Toast.LENGTH_LONG).show();

        myLocation = location;
        if (myMarker != null) {
            myMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_tag_blue));
        myMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        Log.e("IsMyLocationChange", "Yes : "+location.getLatitude()+" "+location.getLongitude());

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_order, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_navigate) {
            navigateToDestination();
            Toast.makeText(activity, "Navigasi ke : "+myTrans.alamat_tujuan, Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void navigateToDestination(){

        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=")
                + android.net.Uri.encode(String.format("%s@%f,%f", myTrans.alamat_tujuan, Double.parseDouble(myTrans.end_latitude), Double.parseDouble(myTrans.end_longitude)), "UTF-8");
        startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));

    }
    /*Deklarasi variable*/

    String goolgeMap = "com.google.android.apps.maps"; // identitas package aplikasi google masps android
    Uri gmmIntentUri;
    Intent mapIntent;

    /*Deklarasi variable*/
    private void tuujuan(){
        // menyamakan variable pada layout activity_main.xml
        gmmIntentUri = Uri.parse("google.navigation:q=" + myTrans.alamat_tujuan);

        // Buat Uri dari intent gmmIntentUri. Set action => ACTION_VIEW
        mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        // Set package Google Maps untuk tujuan aplikasi yang di Intent yaitu google maps
        mapIntent.setPackage(goolgeMap);

        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(getActivity(), "Google Maps Belum Terinstal. Install Terlebih dahulu.",
                    Toast.LENGTH_LONG).show();
        }
        Log.e("rute",myTrans.end_latitude+"");


    }

    /*Deklarasi variable*/
    private void asal(){
        // menyamakan variable pada layout activity_main.xml
        gmmIntentUri = Uri.parse("google.navigation:q=" + myTrans.alamat_asal);

        // Buat Uri dari intent gmmIntentUri. Set action => ACTION_VIEW
        mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        // Set package Google Maps untuk tujuan aplikasi yang di Intent yaitu google maps
        mapIntent.setPackage(goolgeMap);

        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(getActivity(), "Google Maps Belum Terinstal. Install Terlebih dahulu.",
                    Toast.LENGTH_LONG).show();
        }
        Log.e("rute",myTrans.end_latitude+"");


    }
}
