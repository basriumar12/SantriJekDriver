package com.santrijek.driver.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.santrijek.driver.R;
import com.santrijek.driver.adapter.ItemListener;
import com.santrijek.driver.adapter.LaundryBelanjaAdapter;
import com.santrijek.driver.database.DBHandler;
import com.santrijek.driver.database.Queries;
import com.santrijek.driver.model.LaundryBelanja;
import com.santrijek.driver.network.Log;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ListLaundryActivity extends AppCompatActivity {
    ArrayList<LaundryBelanja> arrLaundryBelanja;
    private ItemListener.OnItemTouchListener onItemTouchListener;
    private RecyclerView reviBarangBelanja;
    private LaundryBelanjaAdapter laundryBelanjaAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Queries que;
    ListLaundryActivity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_barang);
        activity = ListLaundryActivity.this;
       // activity.getSupportActionBar().setTitle("Daftar Barang Belanja");

        reviBarangBelanja = (RecyclerView) findViewById(R.id.reviListBarang);
        reviBarangBelanja.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(activity);
        que = new Queries(new DBHandler(activity));
        arrLaundryBelanja = que.getAllLaundryBelanja();
        Log.d("Isi_barang", arrLaundryBelanja.get(0).nama_menu+" total : "+ arrLaundryBelanja.get(0).jumlah_laundry +
                " Catatan : " + arrLaundryBelanja.get(0).catatan_laundry);
        TextView estimasiBiaya = (TextView) findViewById(R.id.estimasiBiaya);
        TextView namaToko = (TextView) findViewById(R.id.namaToko);

        namaToko.setText("Market "+getIntent().getStringExtra("nama_laundry"));
        estimasiBiaya.setText("Estimated costs : "+amountAdapter(getIntent().getIntExtra("estimasi_biaya", 0)));
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
//                CheckBox cekBarang = (CheckBox) view.findViewById(R.id.cekBarang);
//                cekBarang.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(arrBarangBelanja.get(position).isChecked == 0){
////                            arrBarangBelanja.get(position).isChecked = 1;
//                            que.checkedBarang(position, 1);
//                            Toast.makeText(activity, "Clicked 0", Toast.LENGTH_SHORT).show();
//                        }else{
//                            arrBarangBelanja.get(position).isChecked = 0;
//                            Toast.makeText(activity, "Clicked 1", Toast.LENGTH_SHORT).show();
//                            que.checkedBarang(position, 0);
//                        }
//                    }
//                });
//                cekBarang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//                    }
//                });
            }

            @Override
            public void onButton2Click(View view, int position) {
            }
        };
    }

    private void updateListView(){
        reviBarangBelanja.setLayoutManager(mLayoutManager);
        laundryBelanjaAdapter = new LaundryBelanjaAdapter(arrLaundryBelanja, onItemTouchListener);
        reviBarangBelanja.setAdapter(laundryBelanjaAdapter);
//        reviBarangBelanja.setVerticalScrollbarPosition(arrBarangBelanja.size()-1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        que.closeDatabase();
    }

    private String amountAdapter(int amo){
        return "$ "+ NumberFormat.getNumberInstance(Locale.GERMANY).format(amo)+".00";
    }
}
