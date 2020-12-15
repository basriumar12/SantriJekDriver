package com.santrijek.driver.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.santrijek.driver.R;
import com.santrijek.driver.adapter.BarangBelanjaAdapter;
import com.santrijek.driver.adapter.ItemListener;
import com.santrijek.driver.database.DBHandler;
import com.santrijek.driver.database.Queries;
import com.santrijek.driver.model.StoreBelanja;
import com.santrijek.driver.network.Log;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ListBarangActivity extends AppCompatActivity {
    ArrayList<StoreBelanja> arrBarangBelanja;
    private ItemListener.OnItemTouchListener onItemTouchListener;
    private RecyclerView reviBarangBelanja;
    private BarangBelanjaAdapter barangBelanjaAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Queries que;
    ListBarangActivity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_barang);
        activity = ListBarangActivity.this;

        reviBarangBelanja = (RecyclerView) findViewById(R.id.reviListBarang);
        reviBarangBelanja.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(activity);
        que = new Queries(new DBHandler(activity));
        arrBarangBelanja = que.getAllStoreBelanja();
        Log.d("Isi_barang", arrBarangBelanja.get(0).nama_barang+" total : "+ arrBarangBelanja.get(0).jumlah_barang);
        TextView estimasiBiaya = (TextView) findViewById(R.id.estimasiBiaya);
        TextView namaToko = (TextView) findViewById(R.id.namaToko);

        namaToko.setText("Store "+getIntent().getStringExtra("nama_toko"));
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
        barangBelanjaAdapter = new BarangBelanjaAdapter(arrBarangBelanja, onItemTouchListener);
        reviBarangBelanja.setAdapter(barangBelanjaAdapter);
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
