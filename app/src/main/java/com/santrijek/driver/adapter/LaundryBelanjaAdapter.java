package com.santrijek.driver.adapter;

/**
 * Created by GagahIB on 27/11/2016.
 */

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.santrijek.driver.R;
import com.santrijek.driver.model.LaundryBelanja;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class LaundryBelanjaAdapter extends RecyclerView.Adapter<LaundryBelanjaAdapter.MyViewHolder> {

    private ItemListener.OnItemTouchListener onItemTouchListener;
    ArrayList<LaundryBelanja> prodList = new ArrayList<>();

    public LaundryBelanjaAdapter(ArrayList<LaundryBelanja> prodList, ItemListener.OnItemTouchListener onItemTouchListener){
        this.prodList = prodList;
        this.onItemTouchListener = onItemTouchListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_laundry, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        holder.jumlahItem.setText(prodList.get(position).jumlah_laundry+"");
        holder.namaItem.setText(prodList.get(position).nama_menu);
        holder.hargaItem.setText(amountAdapter(prodList.get(position).jumlah_laundry * prodList.get(position).harga_laundry));
        holder.catatanItem.setText("Note : " +prodList.get(position).catatan_laundry);

//        if(prodList.get(position).isChecked == 1){
//            holder.isChecked.setChecked(true);
//        }else{
//            holder.isChecked.setChecked(false);
//        }
    }

    private String amountAdapter(int amo){
        return "Rp "+ NumberFormat.getNumberInstance(Locale.GERMANY).format(amo)+",-";
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

        public TextView jumlahItem, namaItem, hargaItem, catatanItem;

        public MyViewHolder(View itemView){
            super(itemView);

            jumlahItem = (TextView) itemView.findViewById(R.id.jumlahBarang);
            namaItem = (TextView) itemView.findViewById(R.id.namaBarang);
            hargaItem = (TextView) itemView.findViewById(R.id.hargaBarang);
            catatanItem = (TextView) itemView.findViewById(R.id.catatanBarang) ;


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onCardViewTap(v, getLayoutPosition());

                }


            });
            hargaItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemTouchListener.onButton1Click(view, getLayoutPosition());
                }
            });
        }


    }





}