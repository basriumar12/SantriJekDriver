package com.santrijek.driver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;

import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import com.santrijek.driver.activity.FAQActivity;
import com.santrijek.driver.activity.PrivacyPolicyActivity;
import com.santrijek.driver.activity.RatingUserActivity;
import com.santrijek.driver.activity.TermOfServiceActivity;
import com.santrijek.driver.database.DBHandler;
import com.santrijek.driver.database.Queries;
import com.santrijek.driver.fragment.DashboardFragment;
import com.santrijek.driver.fragment.DepositFragment;
import com.santrijek.driver.fragment.FeedbackFragment;
import com.santrijek.driver.fragment.RiwayatTransaksiFragment;
import com.santrijek.driver.fragment.OrderFragment;
import com.santrijek.driver.fragment.SettingFragment;
import com.santrijek.driver.fragment.WithdrawFragment;
import com.santrijek.driver.model.Content;
import com.santrijek.driver.model.Driver;
import com.santrijek.driver.model.Kendaraan;
import com.santrijek.driver.model.TransaksiMcar;
import com.santrijek.driver.network.AsyncTaskHelper;
import com.santrijek.driver.network.HTTPHelper;
import com.santrijek.driver.network.Log;
import com.santrijek.driver.network.NetworkActionResult;
import com.santrijek.driver.preference.KendaraanPreference;
import com.santrijek.driver.preference.SettingPreference;
import com.santrijek.driver.preference.UserPreference;
import com.santrijek.driver.service.LocationService;
import com.santrijek.driver.service.MyConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    MainActivity activity;

    DrawerLayout drawer;
//    UserPreference up;
    Driver driver;
    public boolean statusFragment, ordering = false;
    ImageView imageDriver,image_home,image_kembali;

    //ImageView imageDriver;
    public TextView saldo, textRating;
    Intent service;
    ProgressDialog pd;
    int maxRetry1 = 4;

    private static final int REQUEST_PERMISSION_LOCATION = 991;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  toolbar.setTitleTextColor(getResources().getColor(R.color.greyText));



        Bundle recv = getIntent().getExtras();
        activity = MainActivity.this;
//        up = new UserPreference(activity);

        Queries que = new Queries(new DBHandler(activity));
        driver = que.getDriver();
        que.closeDatabase();
        drawerLayout();
        image_home = (ImageView)findViewById(R.id.image);
        image_home.setVisibility(View.VISIBLE);

        if (savedInstanceState == null && recv != null) {
            if (recv.getString("SOURCE").equals(MyConfig.orderFragment)) {
                changeFragment(new OrderFragment(), false);
                Log.d("Order","Fragment" + " kondisi atas true");
                hiden();
            }else if(recv.getString("SOURCE").equals(MyConfig.dashFragment)){
                if(recv.getInt("response") == 2){
                   // hiden();
                    Toast.makeText(activity, "Transaksi Dibatalkan", Toast.LENGTH_LONG).show();
                    image_home.setVisibility(View.VISIBLE);
                }
                changeFragment(new DashboardFragment(), false);
                image_home.setVisibility(View.VISIBLE);
            }
        }else{
            if(driver.status == 2 || driver.status == 3){
                changeFragment(new OrderFragment(), false);
                Log.d("Order","Fragment" + " kondisi bawah true");
                hiden();

            }else{
                changeFragment(new DashboardFragment(), false);
                image_home.setVisibility(View.VISIBLE);

            }
        }
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);

            //ActivityCompat.requestPermissions(activity, new String[] { android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);

            return;
        }
        service = new Intent(this, LocationService.class);
        startService(service);
        loadImageFromStorage(imageDriver);
    }

    private void hiden(){

        image_home = (ImageView)findViewById(R.id.image);
        image_home.setVisibility(View.GONE);
        image_kembali = (ImageView)findViewById(R.id.image_kembali);
        image_kembali.setVisibility(View.GONE);
    }

    private void drawerLayout(){
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        final ImageView hamMenu = findViewById(R.id.image);
        hamMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
                    // If navigation drawer is not open yet open it else close it.
                    if(!navDrawer.isDrawerOpen(GravityCompat.START)) navDrawer.openDrawer(Gravity.START);
                    else navDrawer.closeDrawer(Gravity.END);


            }
        });
        final ImageView kembali = findViewById(R.id.image_kembali);
        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
                // If navigation drawer is not open yet open it else close it.
                if(!navDrawer.isDrawerOpen(GravityCompat.START)) navDrawer.openDrawer(Gravity.START);
                else navDrawer.closeDrawer(Gravity.END);


            }
        });
        NavigationView navViewLeft = (NavigationView) findViewById(R.id.nav_view);
        navViewLeft.setNavigationItemSelectedListener(this);

        int width = getResources().getDisplayMetrics().widthPixels*9/10;


        View headerL = navViewLeft.getHeaderView(0);
        initMenuDrawer(headerL);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent service = new Intent(this, LocationService.class);
                startService(service);
            } else {
                Toast.makeText(activity, "Gagal menggunakan servis lokasi.", Toast.LENGTH_SHORT).show();
                // TODO: 10/15/2016 Tell user to use GPS
            }
        }



    }

    public void changeFragment(Fragment frag, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.container_body, frag);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImageFromStorage(imageDriver);
    }

    private void initMenuDrawer(View headerL){
        CardView menuRW, menuDeposit, menuWithdraw, menuPerforma,
                menuRating, menuBooking, menuInbox, menuFeedback, menuAccount,terms,privacy,faq;

        TextView namaDriver, namaKendaraan, platNomor;
        ImageView mobilAktif;

        final Button butAutoBid;
        ImageView fitMcar, fitMride, fitMelectronic, fitMbox, fitMmart, fitMlaundry, fitMfood, fitMmassage,fitMsend;
        final Spinner uangBelanja;

        namaDriver = (TextView) headerL.findViewById(R.id.namaDriver);
        namaKendaraan = (TextView) headerL.findViewById(R.id.carName);
        platNomor = (TextView) headerL.findViewById(R.id.carPlat);
        saldo = (TextView) headerL.findViewById(R.id.saldoDriver);
        imageDriver = (ImageView) headerL.findViewById(R.id.imageDriver);
        image_home =(ImageView)findViewById(R.id.image);
        image_kembali =(ImageView)findViewById(R.id.image_kembali);

        mobilAktif = (ImageView) headerL.findViewById(R.id.mobilAktif);
        menuRW = (CardView) headerL.findViewById(R.id.menu_rw);
        menuDeposit = (CardView) headerL.findViewById(R.id.menu_deposit);
        menuWithdraw = (CardView) headerL.findViewById(R.id.menu_withdraw);
        menuPerforma = (CardView) headerL.findViewById(R.id.menu_performa);
        menuRating = (CardView) headerL.findViewById(R.id.menu_rating);
        menuBooking = (CardView) headerL.findViewById(R.id.menu_booking);
        menuInbox = (CardView) headerL.findViewById(R.id.menu_inbox);
        privacy = (CardView) headerL.findViewById(R.id.privacy);
        faq = (CardView) headerL.findViewById(R.id.faq);
        menuFeedback = (CardView) headerL.findViewById(R.id.menu_feedback);
        menuAccount = (CardView) headerL.findViewById(R.id.menu_account);
        textRating = (TextView) headerL.findViewById(R.id.textRating);



        Glide.with(getApplicationContext())
                .load(driver.image)
                .placeholder(R.drawable.ic_camera)
                .error(R.drawable.ic_nama).into(imageDriver);


        Log.d("Image driver","" + driver.image);
        loadImageFromStorage(imageDriver);
        Kendaraan myRide = new KendaraanPreference(this).getKendaraan();
        namaDriver.setText(driver.name);
        textRating.setText(convertJarak(Double.parseDouble(driver.rating))+" / 5");
        saldo.setText(amountAdapter(driver.deposit)+".00");
        namaKendaraan.setText(myRide.merek);
        platNomor.setText(myRide.nopol);

        menuRW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                image_kembali.setVisibility(View.VISIBLE);
                image_home.setVisibility(View.GONE);
                if(ordering){

                }else{
                    changeFragment(new RiwayatTransaksiFragment(), false);
                    statusFragment = true;
                }
                closeLeftDrawer();

            }
        });
        menuInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRate = new Intent(activity, TermOfServiceActivity.class);
                startActivity(toRate);
                image_home.setVisibility(View.GONE);
                if(ordering){
                }else {
                }
                image_home.setVisibility(View.VISIBLE);
                closeLeftDrawer();
                statusFragment = true;
            }
        });
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRate = new Intent(activity, PrivacyPolicyActivity.class);
                startActivity(toRate);
                image_home.setVisibility(View.GONE);
                if(ordering){
                }else {
                }
                image_home.setVisibility(View.VISIBLE);
                closeLeftDrawer();
                statusFragment = true;
            }
        });
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRate = new Intent(activity, FAQActivity.class);
                startActivity(toRate);
                image_home.setVisibility(View.GONE);
                if(ordering){
                }else {
                }
                image_home.setVisibility(View.VISIBLE);
                closeLeftDrawer();
                statusFragment = true;
            }
        });
        menuDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_kembali.setVisibility(View.VISIBLE);
                image_home.setVisibility(View.GONE);
                if(ordering){
                }else{
                    changeFragment(new DepositFragment(), false);
                    image_home.setVisibility(View.GONE);
                    statusFragment = true;
                }
                closeLeftDrawer();

            }
        });
        menuBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRate = new Intent(activity, RatingUserActivity.class);
                startActivity(toRate);
                image_home.setVisibility(View.GONE);
                if(ordering){
                }else {
                }
                closeLeftDrawer();
               statusFragment = true;
            }
        });
        menuRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ordering){
                }else {
                    changeFragment(new FeedbackFragment(), false);
                    image_home.setVisibility(View.GONE);
                    statusFragment = true;
                }
                closeLeftDrawer();
            }
        });
       menuPerforma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_home.setVisibility(View.GONE);
                sendResponse(106);
               closeLeftDrawer();
               statusFragment = true;
          }
       });

        menuWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_kembali.setVisibility(View.VISIBLE);
                image_home.setVisibility(View.GONE);
                if(ordering){
                }else{
                    changeFragment(new WithdrawFragment(), false);
                    image_home.setVisibility(View.GONE);
                    statusFragment = true;
                }
                closeLeftDrawer();
            }
        });
        menuFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ordering){
                }else {
                    changeFragment(new FeedbackFragment(), false);
                    image_home.setVisibility(View.GONE);
                    statusFragment = true;
                }
                closeLeftDrawer();
            }
        });
        menuAccount.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               image_kembali.setVisibility(View.VISIBLE);
               image_home.setVisibility(View.GONE);
               if(ordering){
               }else {
                   changeFragment(new SettingFragment(), false);
                   image_home.setVisibility(View.GONE);
                   statusFragment = true;
               }
               closeLeftDrawer();
           }
        });
    }

    private String convertJarak(Double jarak){
        int range = (int)(jarak*10);
        jarak = (double)range/10;
        return String.valueOf(jarak);
    }

    private String amountAdapter(int amo){
        return NumberFormat.getNumberInstance(Locale.ENGLISH).format(amo);
    }

    int status = -1;
    private void sendResponse(final int acc){
        final String myCGM = new UserPreference(activity).getDriver().gcm_id;
        AsyncTaskHelper asyncTask = new AsyncTaskHelper(activity, true);
        asyncTask.setAsyncTaskListener(new AsyncTaskHelper.OnAsyncTaskListener() {
            @Override
            public void onAsyncTaskDoInBackground(AsyncTaskHelper asyncTask) {
                Map<String, String> dd = new TransaksiMcar().dataDummy();
                dd.put("reg_id_pelanggan", new UserPreference(activity).getDriver().gcm_id);
                Content content = new Content();
                content.addRegId(myCGM);
                content.createDataDummy(dd);
                status = HTTPHelper.sendToGCMServer(content);
            }

            @Override
            public void onAsyncTaskProgressUpdate(AsyncTaskHelper asyncTask) {
            }

            @Override
            public void onAsyncTaskPostExecute(AsyncTaskHelper asyncTask) {
                if (status == 1){
                    Toast.makeText(activity, "Message Sent", Toast.LENGTH_SHORT).show();
                }else if(status == 0){
                    Toast.makeText(activity, "Message sending failed", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onAsyncTaskPreExecute(AsyncTaskHelper asyncTask) {

            }
        });
        asyncTask.execute();
    }

    private void closeLeftDrawer(){
        drawer.closeDrawer(GravityCompat.START);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            drawer.openDrawer(GravityCompat.END);
            closeLeftDrawer();
            return true;
        }
//        if (id == R.id.action_refresh) {
//            syncronizingAccount();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private void loadImageFromStorage(ImageView civ){
        if(!driver.image.equals("")){
            ContextWrapper cw = new ContextWrapper(activity);
            File directory = cw.getDir("fotoDriver", Context.MODE_PRIVATE);
            File f = new File(directory, "profile.jpg");
            Bitmap circleBitmap = decodeFile(f);
            civ.setImageBitmap(circleBitmap);
        }
    }

    private Bitmap decodeFile(File f) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_SIZE=200;
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    @Override
    public void onBackPressed() {
        if(!ordering){
            exitByBackKey();
        }
    }

    protected void exitByBackKey() {

        if(statusFragment){
            changeFragment(new DashboardFragment(), false);
            image_home.setVisibility(View.VISIBLE);
            image_kembali.setVisibility(View.GONE);
            statusFragment = false;
        }else{
            if(drawer.isDrawerOpen(GravityCompat.START) || drawer.isDrawerOpen(GravityCompat.END)){
                closeLeftDrawer();
            }else{
                showWarnExit();
            }
        }
    }

    private MaterialDialog showWarnExit(){
        final MaterialDialog md = new  MaterialDialog.Builder(activity)
                .title("Peringatan")
                .content("Apakah anda ingin keluar aplikasi?\nAnda tidak akan menerima order.")
                .icon(new IconicsDrawable(activity)
                        .icon(FontAwesome.Icon.faw_exclamation_triangle)
                        .color(Color.RED)
                        .sizeDp(24))
                .positiveText("Iya")
                .positiveColor(Color.BLUE)
                .negativeColor(Color.DKGRAY)
                .negativeText("Tidak")
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);
        View negative = md.getActionButton(DialogAction.NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
                Queries que = new Queries(new DBHandler(activity));
                Driver dr = que.getDriver();
                if(dr.status == 4){
                    activity.finish();
                    stopService(service);
                }else{
                    pd = showLoading();
                    turningTheJob(false);
                }
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

    private void updateUangBelanja(final int uang){
        JSONObject jUang = new JSONObject();
        try {
            jUang.put("id_driver", driver.id);
            jUang.put("id_uang", uang);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog pdi = showLoading();
        HTTPHelper.getInstance(activity).settingUangBelanja(jUang, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                try {
                    if(obj.getString("message").equals("success")){
//                        Toast.makeText(activity, "Update Ok", Toast.LENGTH_SHORT).show();
                        new SettingPreference(activity).updateMaksimalBelanja(String.valueOf(uang-1));
                    }else{
//                        Toast.makeText(activity, "Update Fail", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pdi.dismiss();
            }

            @Override
            public void onFailure(String message) {

            }

            @Override
            public void onError(String message) {
                pdi.dismiss();
//                Toast.makeText(activity, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ProgressDialog showLoading(){
        ProgressDialog ad = ProgressDialog.show(activity, "", "Loading...", true);
        return ad;
    }

    private void initializeRigthDrawer(Button butAutoBid){
        SettingPreference sp = new SettingPreference(this);
        if(sp.getSetting()[0].equals("OFF")){
            butAutoBid.setText("OFF");
        }else{
            butAutoBid.setText("ON");
        }
    }

    private void turningTheJob(final boolean action){
        JSONObject jTurn = new JSONObject();
        try {
            jTurn.put("is_turn", action);
            jTurn.put("id_driver", driver.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("JSON_turning_on", jTurn.toString());
        HTTPHelper.getInstance(activity).turningOn(jTurn, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                try {
                    if(obj.getString("message").equals("banned")){
                        showMessage(true, "Peringatan", "Akun anda saat ini sedang disuspend, mohon segera menghubungi kantor kami!");
                    }else if(obj.getString("message").equals("success")){
                        turningActOff();
                    }else{
                        Toast.makeText(activity, "Already Off", Toast.LENGTH_SHORT).show();
                        turningActOff();
                    }
                    pd.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if(maxRetry1 == 0){
                    showMessage(false, "Maaf", "Terjadi kesalahan jaringan, mohon coba lagi!");
                    pd.dismiss();
                    maxRetry1 = 4;
                }else{
                    turningTheJob(false);
                    Log.d("try_ke", String.valueOf(maxRetry1));
                    maxRetry1--;
                }
            }
        });
    }

    private void turningActOff(){
        Queries que = new Queries(new DBHandler(activity));
        que.updateStatus(4);
        que.closeDatabase();
        activity.finish();
        stopService(service);
    }

    private MaterialDialog showMessage(final boolean exit, String title, String message){
        final MaterialDialog md  = new  MaterialDialog.Builder(activity)
                .title(title)
                .content(message)
                .icon(new IconicsDrawable(activity)
                        .icon(FontAwesome.Icon.faw_exclamation_triangle)
                        .color(Color.GREEN)
                        .sizeDp(24))
                .positiveText("Tutup")
                .positiveColor(Color.DKGRAY)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);
//        View negative = md.getActionButton(DialogAction.NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
                if(exit){
                    turningActOff();
                }
            }
        });

        return md;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
