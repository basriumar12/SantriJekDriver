package com.santrijek.driver.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.santrijek.driver.R;
import com.santrijek.driver.model.RiwayatTransaksi;

public class detail_riwayat extends AppCompatActivity {

//private static final String TAG="GalleryActivity";

    RiwayatTransaksi tipe;
    boolean a,b;

@Override
protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.riwayat);
       // Log.d(TAG,"onCreate: started.");







    RelativeLayout deial=(RelativeLayout)findViewById(R.id.detail_trans);
    deial.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(a=true){
                LinearLayout detai = (LinearLayout)findViewById(R.id.deskrip);
                detai.setVisibility(View.GONE);
            }else if(b=true){
                LinearLayout detai = (LinearLayout)findViewById(R.id.deskrip);
                detai.setVisibility(View.VISIBLE);
            }
        }
    });



        getIncomingIntent();
        }

        private void getIncomingIntent(){
              //  Log.d(TAG,"getIncomingIntent: checking for incoming intents.");

                if(getIntent().hasExtra("jumlah")){


               // String imageUrl=getIntent().getStringExtra("image_url");
                    String Jumlah=getIntent().getStringExtra("jumlah");
                    String tanggal=getIntent().getStringExtra("tanggal");
                    String Sisa=getIntent().getStringExtra("saldo");
                    String Keterangan=getIntent().getStringExtra("keterangan");

                setImage(Jumlah,tanggal,Sisa,Keterangan);

                    Log.e("hasil",Jumlah+"");

                }
        }
    private void setImage(String Jumlah,String tanggal,String Sisa,String Keterangan){
       // Log.d(TAG, "setImage: setting te image and name to widgets.");
        TextView jumlah,tgl,saldo,ket;

        jumlah=(TextView)findViewById(R.id.saldoawal);
        tgl=(TextView)findViewById(R.id.tgl_transaksi);
        saldo=(TextView)findViewById(R.id.nambah);
        ket =(TextView)findViewById(R.id.keterangan1);

        jumlah.setText("$ "+Jumlah+".00");
        tgl.setText(tanggal);
        saldo.setText("$ "+Sisa+".00");
        ket.setText(Keterangan);




    }
}