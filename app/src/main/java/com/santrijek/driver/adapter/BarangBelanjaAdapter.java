package com.santrijek.driver.adapter;

/**
 * Created by GagahIB on 27/11/2016.
 */

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.santrijek.driver.R;
import com.santrijek.driver.model.StoreBelanja;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BarangBelanjaAdapter extends RecyclerView.Adapter<BarangBelanjaAdapter.MyViewHolder> {

    private ItemListener.OnItemTouchListener onItemTouchListener;
    ArrayList<StoreBelanja> prodList = new ArrayList<>();

    public BarangBelanjaAdapter(ArrayList<StoreBelanja> prodList, ItemListener.OnItemTouchListener onItemTouchListener){
        this.prodList = prodList;
        this.onItemTouchListener = onItemTouchListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_barang, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        holder.jumlahBarang.setText(prodList.get(position).jumlah_barang +"");
        holder.namaBarang.setText(prodList.get(position).nama_barang);
        holder.catatanBarang.setText("Note : " + prodList.get(position).catatan_barang);
        holder.hargaBarang.setText(amountAdapter(prodList.get(position).jumlah_barang * prodList.get(position).harga_barang));
//        if(prodList.get(position).isChecked == 1){
//            holder.isChecked.setChecked(true);
//        }else{
//            holder.isChecked.setChecked(false);
//        }
    }



    @Override
    public int getItemCount() {
        return prodList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView jumlahBarang, namaBarang, catatanBarang, hargaBarang;
        public CheckBox isChecked;

        public MyViewHolder(View itemView){
            super(itemView);

            jumlahBarang = (TextView) itemView.findViewById(R.id.jumlahBarang);
            namaBarang = (TextView) itemView.findViewById(R.id.namaBarang);
//            isChecked = (CheckBox) itemView.findViewById(R.id.cekBarang);
            catatanBarang = (TextView) itemView.findViewById(R.id.catatanBarang);
            hargaBarang = (TextView) itemView.findViewById(R.id.hargaBarang);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onCardViewTap(v, getLayoutPosition());

                }


            });

//            isChecked.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    onItemTouchListener.onButton1Click(view, getLayoutPosition());
//                }
//            });

        }


    }
    private String amountAdapter(int amo){
        return "$. "+ NumberFormat.getNumberInstance(Locale.GERMANY).format(amo)+",-";
    }





}