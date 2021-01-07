package com.santrijek.driver.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import com.santrijek.driver.MainActivity;
import com.santrijek.driver.R;
import com.santrijek.driver.database.DBHandler;
import com.santrijek.driver.database.Queries;
import com.santrijek.driver.model.BarangBelanja;
import com.santrijek.driver.model.DestinasiMbox;
import com.santrijek.driver.model.Driver;
import com.santrijek.driver.model.Kendaraan;
import com.santrijek.driver.model.LaundryBelanja;
import com.santrijek.driver.model.MakananBelanja;
import com.santrijek.driver.model.StoreBelanja;
import com.santrijek.driver.model.Transaksi;
import com.santrijek.driver.network.HTTPHelper;
import com.santrijek.driver.network.Log;
import com.santrijek.driver.network.NetworkActionResult;
import com.santrijek.driver.preference.KendaraanPreference;
import com.santrijek.driver.preference.SettingPreference;
//import driver.pacekurir.drivermangjek.preference.UserPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class DashboardFragment extends Fragment  implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;


    private LocationRequest mLocationRequest;
    private Marker mCurrLocationMarker;
    private Location mLastLocation;
    private static final String TAG = DashboardFragment.class.getSimpleName();
    private View rootView;
    MainActivity activity;
    ImageView imageBekerja,refresh;
    TextView namaDriver, namaKendaraan, platNomor, sts;
    CircularImageView imageDriver;

    Switch switchBekerja;
    FrameLayout switchWrapper;
    boolean isOn = false;
    Button btnauto;
    Driver driver;
    int maxRetry = 4;
    int maxRetry1 = 4;
    int maxRetry2 = 4;
    ProgressDialog pd, pd1;


    public DashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.content_main, container, false);
        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();

        sts = (TextView) rootView.findViewById(R.id.aktif);

        imageBekerja = (ImageView) rootView.findViewById(R.id.iconBekerja);
        refresh= (ImageView)rootView.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncronizingAccount();

            }
        });
        switchBekerja = (Switch) rootView.findViewById(R.id.switch_bekerja);
        switchWrapper = (FrameLayout) rootView.findViewById(R.id.switch_wrapper);
        activity = (MainActivity) getActivity();
///        activity.getSupportActionBar().setTitle("DRIVER");

        Queries quem = new Queries(new DBHandler(activity));
        driver = quem.getDriver();
        quem.closeDatabase();
        activate();
        pd = showLoading();
        syncronizingAccount();
        if (switchBekerja.isChecked()){
            sts.setText("Online");
        }else{
            sts.setText("Oflline");
        }

        namaDriver = (TextView) rootView.findViewById(R.id.namaDriver1);
        namaKendaraan = (TextView) rootView.findViewById(R.id.carName1);
        platNomor = (TextView) rootView.findViewById(R.id.carPlat1);
        btnauto = (Button) rootView.findViewById(R.id.butAutoBid);

        btnauto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingPreference sp = new SettingPreference(activity);
                if(sp.getSetting()[0].equals("OFF")){
                    sp.updateAutoBid("ON");
                    btnauto.setText("ON");
                    Toast.makeText(activity, "Autobid Aktif.", Toast.LENGTH_SHORT).show();
                }else{
                    sp.updateAutoBid("OFF");
                    btnauto.setText("OFF");
                    Toast.makeText(activity, "Autobid Nonaktif.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imageDriver = (CircularImageView) rootView.findViewById(R.id.imageDriver1);

        Kendaraan myRide = new KendaraanPreference(getContext()).getKendaraan();
        Glide.with(getContext()).load(driver.image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.ic_camera)
                .error(R.mipmap.ic_mride_on)
                .into(imageDriver);
        //    Glide.with(getContext()).load(driver.image).placeholder(R.drawable.ic_camera).error(R.drawable.ic_centang_yes).into(imageDriver);
        Log.d("Image driver", "" + driver.image);
        loadImageFromStorage(imageDriver);
        namaDriver.setText(driver.name);
        namaKendaraan.setText(myRide.merek);
        platNomor.setText(myRide.nopol);

        Queries que = new Queries(new DBHandler(activity));
        driver = que.getDriver();
        que.closeDatabase();

        switchWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Queries que = new Queries(new DBHandler(activity));
                Driver driver = que.getDriver();
                que.closeDatabase();
                if (driver.status == 4) {
                    pd1 = showLoading();

                    turningTheJob(true);
                } else {
                    showWarning();
                }
            }
        });



        return rootView;




    }
    private void initializeRigthDrawer(Button butAutoBid){
        SettingPreference sp = new SettingPreference(getActivity());
        if(sp.getSetting()[0].equals("OFF")){
            butAutoBid.setText("OFF");
        }else{
            butAutoBid.setText("ON");
        }
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Memuat SupportMapFragment dan memberi notifikasi saat telah siap.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map_main);
        mapFragment.getMapAsync(this);
    //    mMap.setMyLocationEnabled(true);



    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        que.closeDatabase();
    }

    private MaterialDialog showWarning() {
        final MaterialDialog md = new MaterialDialog.Builder(activity)
                .title("Peringatan")
                .content("Are you sure you want to stop working?")
                .icon(new IconicsDrawable(activity)
                        .icon(FontAwesome.Icon.faw_exclamation_triangle)
                        .color(Color.RED)
                        .sizeDp(24))
                .positiveText("Yes")
                .negativeText("Cancel")
                .positiveColor(Color.BLUE)
                .negativeColor(Color.DKGRAY)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);
        View negative = md.getActionButton(DialogAction.NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd1 = showLoading();
                turningTheJob(false);
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

    private MaterialDialog showMessage(String title, String message) {
        final MaterialDialog md = new MaterialDialog.Builder(activity)
                .title(title)
                .content(message)
                .icon(new IconicsDrawable(activity)
                        .icon(FontAwesome.Icon.faw_exclamation_triangle)
                        .color(Color.GREEN)
                        .sizeDp(24))
                .positiveText("Closed")
                .positiveColor(Color.DKGRAY)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);
//        View negative = md.getActionButton(DialogAction.NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
            }
        });

        return md;
    }

    public void activate() {
        Queries que = new Queries(new DBHandler(activity));
        Driver driver = que.getDriver();
        que.closeDatabase();
        if (driver.status == 4) {
            switchBekerja.setChecked(false);
            imageBekerja.setImageResource(R.drawable.dashboard_driver_offline);
            switchBekerja.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO Auto-generated method stub

                    if (isChecked) {
                        sts.setText("Online");
                    } else {
                        sts.setText("Offline");
                    }
                }
            });

        } else {
            switchBekerja.setChecked(true);
            imageBekerja.setImageResource(R.drawable.hykit);
            switchBekerja.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (isChecked) {
                        sts.setText("Online");
                    } else {
                        sts.setText("Offline");
                    }
                }
            });

        }
    }


    private void turningOff() {
        Queries que = new Queries(new DBHandler(activity));

//        SettingPreference sp = new SettingPreference(activity);
        switchBekerja.setChecked(false);
        imageBekerja.setImageResource(R.drawable.dashboard_driver_offline);

//        sp.updateKerja("OFF");
        que.updateStatus(4);
        que.closeDatabase();


    }

    private void turningOn() {
        Queries que = new Queries(new DBHandler(activity));

//        SettingPreference sp = new SettingPreference(activity);
        switchBekerja.setChecked(true);
        imageBekerja.setImageResource(R.drawable.hykit);
        que.updateStatus(1);
        que.closeDatabase();

//        sp.updateKerja("ON");
//        Intent service = new Intent(activity, LocationService.class);
//        activity.startService(service);
    }

    private void turningTheJob(final boolean action) {
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
                android.util.Log.d("DashboardFragment", "onSuccess: " + obj);
                pd1.dismiss();
                maxRetry1 = 4;
                try {
                    if (obj.getString("message").equals("banned")) {
                        showMessage("Sorry", "Your account is currently being suspended, please immediately contact our office!");
                        turningOff();
                    } else if (obj.getString("message").equals("success")) {
                        if (action) {
                            turningOn();
                            showMessage("thank you", "Happy working again.");
                        } else {
                            turningOff();
                        }
                    } else {
                        activate();
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
                if (maxRetry1 == 0) {
                    showMessage("Sorry", "A network error has occurred, please try again!");
                    pd1.dismiss();
                    maxRetry1 = 4;
                } else {
//                    pd = showLoading();
                    turningTheJob(action);
                    Log.d("try_ke_turn_off", String.valueOf(maxRetry1));
                    maxRetry1--;
                }
            }
        });
    }

    private ProgressDialog showLoading() {
        ProgressDialog ad = ProgressDialog.show(activity, "", "Loading...", true);
        return ad;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            pd = showLoading();
            syncronizingAccount();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        activity.saldo.setText(amountAdapter(driver.deposit));
                        if (driver.rating!=null)
                        activity.textRating.setText(convertJarak(Double.parseDouble(driver.rating)) + " / 5");
                        Queries que = new Queries(new DBHandler(activity));
                        que.updateDeposit(driver.deposit);
                        que.updateRating(driver.rating);
                        que.updateStatus(driver.status);
                        Transaksi runTrans = HTTPHelper.getInstance(activity).parseTransaksi(activity, obj.toString());
                        if (!runTrans.id_transaksi.equals("0")) {
                            selectRunTrans(runTrans);
                        }
                        que.closeDatabase();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pd.dismiss();
                maxRetry = 4;
            }

            @Override
            public void onFailure(String message) {

                Log.e("TAG","failure main "+message);
            }

            @Override
            public void onError(String message) {
                Log.e("TAG","error main "+message);
//                pd.dismiss();
                if (maxRetry == 0) {
                    Toast.makeText(activity, "Koneksi bermasalah..", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
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

    private String convertJarak(Double jarak) {
        int range = (int) (jarak * 10);
        jarak = (double) range / 10;
        return String.valueOf(jarak);
    }

    private String amountAdapter(int amo) {
        return NumberFormat.getNumberInstance(Locale.GERMANY).format(amo);
    }

    private void selectRunTrans(Transaksi runTruns) {
        JSONObject jTrans = new JSONObject();
        try {
            jTrans.put("id_transaksi", runTruns.id_transaksi);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (runTruns.order_fitur) {
            case "3": {
                get_data_transaksi_mfood(jTrans, runTruns);
                break;
            }
            case "4": {
                get_data_transaksi_mmart(jTrans, runTruns);
                break;
            }
            case "5": {
                get_data_transaksi_msend(jTrans, runTruns);
                break;
            }
            case "6": {
                get_data_transaksi_mmassage(jTrans, runTruns);
                break;
            }
            case "7": {
                get_data_transaksi_mbox(jTrans, runTruns);
                break;
            }
            case "8": {
                get_data_transaksi_mservice(jTrans, runTruns);
                break;
            }
            case "9": {
                get_data_transaksi_mstore(jTrans, runTruns);
                break;
            }
            case "10": {
                get_data_transaksi_mlaundry(jTrans, runTruns);
                break;
            }
            default: {
                Queries que = new Queries(new DBHandler(activity));
                que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                que.insertInProgressTransaksi(runTruns);
                que.closeDatabase();
                changeFragment(new OrderFragment(), false);
                break;
            }
        }
    }

    private void get_data_transaksi_mmart(final JSONObject jTrans, final Transaksi currTrans) {
        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).getTransaksiMmart(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                Transaksi transaksi = HTTPHelper.parseDataMmart(currTrans, obj);
                ArrayList<BarangBelanja> arrBarang = HTTPHelper.parseBarangBelanja(obj);
                Queries que = new Queries(new DBHandler(activity));
                que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                que.insertInProgressTransaksi(transaksi);
                que.truncate(DBHandler.TABLE_BARANG_BELANJA);
                que.insertBarangBelanja(arrBarang);
                que.closeDatabase();
                changeFragment(new OrderFragment(), false);
                pd.dismiss();
                maxRetry2 = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (maxRetry2 == 0) {
                    pd.dismiss();
                    Toast.makeText(activity, "Koneksi bermasalah..", Toast.LENGTH_SHORT).show();
                    Log.d("data_sync_mmart", "Retrieving Data Null");
                    maxRetry2 = 4;
                } else {
                    get_data_transaksi_mmart(jTrans, currTrans);
                    maxRetry2--;
                    Log.d("Try_ke_data_mmart", String.valueOf(maxRetry2));
                    pd.dismiss();
                }
            }
        });
    }

    private void get_data_transaksi_mfood(final JSONObject jTrans, final Transaksi currTrans) {
        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).getTransaksiMfood(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                Transaksi transaksi = HTTPHelper.parseDataMmart(currTrans, obj);
                ArrayList<MakananBelanja> arrBarang = HTTPHelper.parseMakananBelanja(obj);
                Queries que = new Queries(new DBHandler(activity));
                que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                que.insertInProgressTransaksi(transaksi);
                que.truncate(DBHandler.TABLE_MAKANAN_BELANJA);
                que.insertMakananBelanja(arrBarang);
                que.closeDatabase();
                changeFragment(new OrderFragment(), false);
                pd.dismiss();
                maxRetry2 = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (maxRetry2 == 0) {
                    pd.dismiss();
                    Toast.makeText(activity, "Koneksi bermasalah..", Toast.LENGTH_SHORT).show();
                    Log.d("data_sync_mfood", "Retrieving Data Null");
                    maxRetry2 = 4;
                } else {
                    get_data_transaksi_mfood(jTrans, currTrans);
                    maxRetry2--;
                    Log.d("Try_ke_data_mfood", String.valueOf(maxRetry2));
                    pd.dismiss();
                }
            }
        });
    }

    private void get_data_transaksi_mstore(final JSONObject jTrans, final Transaksi currTrans) {
        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).getTransaksiMstore(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                Transaksi transaksi = HTTPHelper.parseDataMstore(currTrans, obj);
                ArrayList<StoreBelanja> arrBarang = HTTPHelper.parseStoreBelanja(obj);
                Queries que = new Queries(new DBHandler(activity));
                que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                que.insertInProgressTransaksi(transaksi);
                que.truncate(DBHandler.TABLE_STORE_BELANJA);
                que.insertStoreBelanja(arrBarang);
                que.closeDatabase();
                changeFragment(new OrderFragment(), false);
                pd.dismiss();
                maxRetry2 = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (maxRetry2 == 0) {
                    pd.dismiss();
                    Toast.makeText(activity, "Koneksi bermasalah..", Toast.LENGTH_SHORT).show();
                    Log.d("data_sync_mfood", "Retrieving Data Null");
                    maxRetry2 = 4;
                } else {
                    get_data_transaksi_mstore(jTrans, currTrans);
                    maxRetry2--;
                    Log.d("Try_ke_data_mfood", String.valueOf(maxRetry2));
                    pd.dismiss();
                }
            }
        });
    }

    private void get_data_transaksi_mlaundry(final JSONObject jTrans, final Transaksi currTrans) {
        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).getTransaksiMlaundry(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                Transaksi transaksi = HTTPHelper.parseDataMstore(currTrans, obj);
                ArrayList<LaundryBelanja> arrBarang = HTTPHelper.parseLaundryBelanja(obj);
                Queries que = new Queries(new DBHandler(activity));
                que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                que.insertInProgressTransaksi(transaksi);
                que.truncate(DBHandler.TABLE_LAUNDRY_BELANJA);
                que.insertLaundryBelanja(arrBarang);
                que.closeDatabase();
                changeFragment(new OrderFragment(), false);
                pd.dismiss();
                maxRetry2 = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (maxRetry2 == 0) {
                    pd.dismiss();
                    Toast.makeText(activity, " Koneksibermasalah..", Toast.LENGTH_SHORT).show();
                    Log.d("data_sync_mfood", "Retrieving Data Null");
                    maxRetry2 = 4;
                } else {
                    get_data_transaksi_mlaundry(jTrans, currTrans);
                    maxRetry2--;
                    Log.d("Try_ke_data_mfood", String.valueOf(maxRetry2));
                    pd.dismiss();
                }
            }
        });
    }


    private void get_data_transaksi_mbox(final JSONObject jTrans, final Transaksi currTrans) {
        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).getTransaksiMbox(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                Transaksi transaksi = HTTPHelper.parseDataMbox(currTrans, obj);
                ArrayList<DestinasiMbox> arrDestinasi = HTTPHelper.parseDestinasiMbox(obj);
                Queries que = new Queries(new DBHandler(activity));
                que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                que.insertInProgressTransaksi(transaksi);
                que.truncate(DBHandler.TABLE_DESTINASI_MBOX);
                que.insertDestinasiMbox(arrDestinasi);
                que.closeDatabase();
                changeFragment(new OrderFragment(), false);
                pd.dismiss();
                maxRetry2 = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (maxRetry2 == 0) {
                    pd.dismiss();
                    Toast.makeText(activity, "Koneksi bermasalah..", Toast.LENGTH_SHORT).show();
                    Log.d("data_sync_mbox", "Retrieving Data Null");
                    maxRetry2 = 4;
                } else {
                    get_data_transaksi_mbox(jTrans, currTrans);
                    maxRetry2--;
                    Log.d("Try_ke_data_mbox", String.valueOf(maxRetry2));
                    pd.dismiss();
                }
            }
        });
    }

    private void get_data_transaksi_msend(final JSONObject jTrans, final Transaksi currTrans) {
        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).getTransaksiMsend(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                Transaksi transaksi = HTTPHelper.parseDataMsend(currTrans, obj);
                Queries que = new Queries(new DBHandler(activity));
                que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                que.insertInProgressTransaksi(transaksi);
                que.closeDatabase();
                changeFragment(new OrderFragment(), false);
                pd.dismiss();
                maxRetry2 = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (maxRetry2 == 0) {
                    pd.dismiss();
                    Toast.makeText(activity, "Koneksi bermasalah..", Toast.LENGTH_SHORT).show();
                    Log.d("data_sync_msend", "Retrieving Data Null");
                    maxRetry2 = 4;
                } else {
                    get_data_transaksi_msend(jTrans, currTrans);
                    maxRetry2--;
                    Log.d("Try_ke_data_msend", String.valueOf(maxRetry2));
                    pd.dismiss();
                }
            }
        });
    }

    private void get_data_transaksi_mservice(final JSONObject jTrans, final Transaksi currTrans) {
        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).getTransaksiMservice(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                Transaksi transaksi = HTTPHelper.parseDataMservice(currTrans, obj);
                Queries que = new Queries(new DBHandler(activity));
                que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                que.insertInProgressTransaksi(transaksi);
                que.closeDatabase();
                changeFragment(new OrderFragment(), false);
                pd.dismiss();
                maxRetry2 = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (maxRetry2 == 0) {
                    pd.dismiss();
                    Toast.makeText(activity, "Koneksi bermasalah..", Toast.LENGTH_SHORT).show();
                    Log.d("data_sync_mservice", "Retrieving Data Null");
                    maxRetry2 = 4;
                } else {
                    get_data_transaksi_mservice(jTrans, currTrans);
                    maxRetry2--;
                    Log.d("Try_ke_data_mservice", String.valueOf(maxRetry2));
                    pd.dismiss();
                }
            }
        });
    }

    private void get_data_transaksi_mmassage(final JSONObject jTrans, final Transaksi currTrans) {
        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).getTransaksiMmassage(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                Transaksi transaksi = HTTPHelper.parseDataMmassage(currTrans, obj);
                Queries que = new Queries(new DBHandler(activity));
                que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                que.insertInProgressTransaksi(transaksi);
                que.closeDatabase();
                changeFragment(new OrderFragment(), false);
                pd.dismiss();
                maxRetry2 = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (maxRetry2 == 0) {
                    pd.dismiss();
                    Toast.makeText(activity, "Koneksi bermasalah..", Toast.LENGTH_SHORT).show();
                    Log.d("data_sync_mmassage", "Retrieving Data Null");
                    maxRetry2 = 4;
                } else {
                    get_data_transaksi_mmassage(jTrans, currTrans);
                    maxRetry2--;
                    Log.d("Try_ke_data_mmassage", String.valueOf(maxRetry2));
                    pd.dismiss();
                }
            }
        });
    }

    public void changeFragment(Fragment frag, boolean addToBackStack) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.container_body, frag);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        loadImageFromStorage(imageDriver);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))
                .title("say disini"));
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }


        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latLng.latitude, latLng.longitude)).zoom(16).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        //menghentikan pembaruan lokasi
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Izin diberikan.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Izin ditolak.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        loadImageFromStorage(imageDriver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        //Memulai Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_pin);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }
}