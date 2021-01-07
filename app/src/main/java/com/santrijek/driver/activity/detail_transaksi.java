package com.santrijek.driver.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.santrijek.driver.R;
import com.santrijek.driver.model.Transaksi;

public class detail_transaksi extends AppCompatActivity {

//private static final String TAG="GalleryActivity";

    Transaksi mytrans;
    boolean a,b;

@Override
protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_transaksi);
       // Log.d(TAG,"onCreate: started.");
        getIncomingIntent();
        }

        private void getIncomingIntent(){
              //  Log.d(TAG,"getIncomingIntent: checking for incoming intents.");

                if(getIntent().hasExtra("id_transaksi")){

               // String imageUrl=getIntent().getStringExtra("image_url");
                    String Id_transaksi=getIntent().getStringExtra("id_transaksi");
                    String Tanggal=getIntent().getStringExtra("tanggal");
                    String Alamat_asal=getIntent().getStringExtra("alamat_asal");
                    String Alamat_tujuan=getIntent().getStringExtra("alamat_tujuan");
                    String Jarak=getIntent().getStringExtra("jarak");
                    String Tarif=getIntent().getStringExtra("tarif");
                    String Total_cost=getIntent().getStringExtra("total_cost");
                    String Admin_fee=getIntent().getStringExtra("potongan");
                    String Estimasi=getIntent().getStringExtra("estimasi");
                    String Totalcos=getIntent().getStringExtra("totalpaid");
                    String TotalPaid=getIntent().getStringExtra("total_paid");
                    String Saldo=getIntent().getStringExtra("saldo");

                setImage(Id_transaksi,Tanggal,Alamat_asal,Alamat_tujuan,Jarak,Tarif,Total_cost,Admin_fee,Estimasi,Totalcos,TotalPaid,Saldo);

                    Log.e("hasil",Id_transaksi+"");

                }
        }
    private void setImage(String Id_transaksi,String Tanggal,String Alamat_asal,String Alamat_tujuan,String Jarak,String Tarif,String Total_cost,String Admin_fee,String Estimasi,String Totalcos,String TotalPaid,String Saldo){
       // Log.d(TAG, "setImage: setting te image and name to widgets.");
        TextView id_transksi,tanggal,alamat_asal,alamat_tujuan,tarif,jarak,total_cost,admin_fee,estimasi,totalcost,total_paid,saldo;
        id_transksi =findViewById(R.id.id_transaksi);
        tanggal = (TextView) findViewById(R.id.tgl_transaksi);
        alamat_asal = (TextView) findViewById(R.id.alamat1);
        alamat_tujuan = (TextView) findViewById(R.id.alamat2);
        jarak = (TextView) findViewById(R.id.km);
        tarif = (TextView) findViewById(R.id.Tarif);
        total_cost = (TextView) findViewById(R.id.total_harga);
        admin_fee = (TextView) findViewById(R.id.potonga);
        estimasi= (TextView) findViewById(R.id.pendapatan);
        totalcost = (TextView) findViewById(R.id.dibayar_penumpang);
        total_paid=(TextView)findViewById(R.id.total_penumpang);
        saldo=(TextView)findViewById(R.id.saldo);


        id_transksi.setText("Order ID "+Id_transaksi);
        tanggal.setText(Tanggal);
        alamat_asal.setText(Alamat_asal);
        alamat_tujuan.setText(Alamat_tujuan);
        jarak.setText("Distance "+Jarak);
        tarif.setText("$ "+Tarif+".00");
        total_cost.setText("$ "+Total_cost+".00");
        admin_fee.setText("$ "+Admin_fee+".00");
        estimasi.setText(Estimasi);
        totalcost.setText("$ "+Totalcos+".00");
        total_paid.setText("$ "+TotalPaid+".00");
        saldo.setText("$ "+Saldo+".00");


    }
}