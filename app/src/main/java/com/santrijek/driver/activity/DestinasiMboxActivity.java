package com.santrijek.driver.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.santrijek.driver.R;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import com.santrijek.driver.adapter.DestinasiMboxAdapter;
import com.santrijek.driver.adapter.ItemListener;
import com.santrijek.driver.database.DBHandler;
import com.santrijek.driver.database.Queries;
import com.santrijek.driver.model.DestinasiMbox;
import com.santrijek.driver.model.Transaksi;

import java.util.ArrayList;

public class DestinasiMboxActivity extends AppCompatActivity {
    ArrayList<DestinasiMbox> arrDestinasiMbox;
    private ItemListener.OnItemTouchListener onItemTouchListener;
    private RecyclerView reviDestinasiMbox;
    private DestinasiMboxAdapter destinasiAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Queries que;
    DestinasiMboxActivity activity;
    private static final int REQUEST_PERMISSION_CALL = 992;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinasi_mbox);
        activity = DestinasiMboxActivity.this;

        Bundle bund = getIntent().getExtras();
        TextView namaBarang = (TextView) findViewById(R.id.namaBarang);
        Transaksi myTrans = (Transaksi) bund.getSerializable("transaksi");
        namaBarang.setText(myTrans.nama_barang);

        reviDestinasiMbox = (RecyclerView) findViewById(R.id.reviDestinasi);
        reviDestinasiMbox.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(activity);
        que = new Queries(new DBHandler(activity));
        arrDestinasiMbox = que.getAllDestinasiMbox();

        initListener();
        updateListView();
    }

    private void initListener() {
        onItemTouchListener = new ItemListener.OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
            }

            @Override
            public void onButton1Click(View view, final int position) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL);
                    return;
                }
                showWarningCall(arrDestinasiMbox.get(position).telepon_penerima);

            }

            @Override
            public void onButton2Click(View view, int position) {
                tuujuan(arrDestinasiMbox.get(position).lokasi);
            }
        };
    }

    private void updateListView(){

        reviDestinasiMbox.setLayoutManager(mLayoutManager);
        destinasiAdapter = new DestinasiMboxAdapter(arrDestinasiMbox, onItemTouchListener);
        reviDestinasiMbox.setAdapter(destinasiAdapter);
    }

    private MaterialDialog showWarningCall(final String nomor) {
        final MaterialDialog md = new MaterialDialog.Builder(this)
                .title("Warning")
                .content("Do you want to call this number?")
                .icon(new IconicsDrawable(this)
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
                phoneIntent.setData(Uri.parse("tel:" + nomor));
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_PERMISSION_CALL){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "Call permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Call permission restricted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        que.closeDatabase();
    }


    String goolgeMap = "com.google.android.apps.maps"; // identitas package aplikasi google masps android
    Uri gmmIntentUri;
    Intent mapIntent;

    /*Deklarasi variable*/
    private void tuujuan(final String lokasi){

        // menyamakan variable pada layout activity_main.xml
        gmmIntentUri = Uri.parse("google.navigation:q=" + lokasi);


       /// gmmIntentUri = android.net.Uri.parse("google.navigation:q=")+android.net.Uri.encode(String.format("%s@%f,%f", lokasi,lokasi2);
        mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        // Set package Google Maps untuk tujuan aplikasi yang di Intent yaitu google maps
        mapIntent.setPackage(goolgeMap);

        if (mapIntent.resolveActivity(DestinasiMboxActivity.this.getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(getBaseContext(), "Google Maps Belum Terinstal. Install Terlebih dahulu.",
                    Toast.LENGTH_LONG).show();
        }


    }
}
