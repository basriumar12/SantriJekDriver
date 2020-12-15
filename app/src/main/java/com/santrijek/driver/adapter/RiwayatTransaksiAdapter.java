package com.santrijek.driver.adapter;

/**
 * Created by GagahIB on 11/8/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.santrijek.driver.R;
import com.santrijek.driver.activity.detail_riwayat;
import com.santrijek.driver.activity.detail_transaksi;
import com.santrijek.driver.activity.detail_transaksi_saldo;
import com.santrijek.driver.model.RiwayatTransaksi;
import com.santrijek.driver.network.Log;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class RiwayatTransaksiAdapter extends RecyclerView.Adapter<RiwayatTransaksiAdapter.MyViewHolder> {

    private ItemListener.OnItemTouchListener onItemTouchListener;
    ArrayList<RiwayatTransaksi> prodList = new ArrayList<>();
    Context context;

    public RiwayatTransaksiAdapter(ArrayList<RiwayatTransaksi> prodList,ItemListener.OnItemTouchListener onItemTouchListener, Context context){
        this.prodList = prodList;
        this.onItemTouchListener = onItemTouchListener;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_history, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {

        holder.tanggalRT.setText(tanggalAdapter(prodList.get(position).waktu_riwayat));
        holder.saldoAkhirRT.setText("Balance "+amountAdapter(prodList.get(position).saldo));
        switch (prodList.get(position).tipe_transaksi){
            case "4":{
                Log.e("idfitur",prodList.get(position).id_fitur);

                holder.alamttujuan.setVisibility(View.GONE);
                holder.alamatAsal.setVisibility(View.GONE);
                holder.idRT.setVisibility(View.GONE);
                holder.asal.setVisibility(View.GONE);
                holder.tujuan.setVisibility(View.GONE);
                holder.biayaAkhir.setVisibility(View.GONE);
                holder.keterangan.setVisibility(View.VISIBLE);
                holder.namaRT.setText("Transaction: Top Up Balance");
                holder.keterangan.setText("Top Up balance successfully");
                holder.nominalRT.setText("$ "+amountAdapter(prodList.get(position).kredit)+".00");
                break;
            }
            case "5":{
                Log.e("idfitur",prodList.get(position).id_fitur);
                holder.keterangan.setVisibility(View.GONE);
                holder.idRT.setText("ID "+  prodList.get(position).id_transaksi);
                holder.namaRT.setText("Transaction : Order "+prodList.get(position).fitur);
                holder.nominalRT.setText("Debit "+amountAdapter(prodList.get(position).debit));
                holder.biayaAkhir.setText("Distance "+convertJarak(Double.parseDouble(prodList.get(position).jarak))+" Fare "+prodList.get(position).biay_akhir);
                holder.alamatAsal.setText((prodList.get(position).alamat_asal));
                holder.alamttujuan.setText((prodList.get(position).alamat_tujuan));
                holder.nominalRT.setTextColor(context.getResources().getColor(R.color.textColorRed));
                break;
            }
            case "6":{
                Log.e("idfitur",prodList.get(position).id_fitur);
                holder.keterangan.setVisibility(View.GONE);
                holder.idRT.setText("ID "+  prodList.get(position).id_transaksi);
                holder.namaRT.setText("Transaction : Order "+prodList.get(position).fitur);
                holder.nominalRT.setText("Kredit"+amountAdapter(prodList.get(position).kredit));
                holder.biayaAkhir.setText("Distance "+convertJarak(Double.parseDouble(prodList.get(position).jarak))+" Fare "+prodList.get(position).biay_akhir);
                holder.alamatAsal.setText((prodList.get(position).alamat_asal));
                holder.alamttujuan.setText((prodList.get(position).alamat_tujuan));
                holder.nominalRT.setTextColor(context.getResources().getColor(R.color.blue));
                break;
            }
            case "7":{
                holder.idRT.setVisibility(View.GONE);
                holder.namaRT.setText("Transaksi : Terima Bonus");
                holder.keterangan.setText("Terima Bonus dari PT Mangjek Indonesia");
                holder.nominalRT.setText("+"+amountAdapter(prodList.get(position).kredit));
                holder.nominalRT.setTextColor(context.getResources().getColor(R.color.textColorGreen));
                break;
            }
            case "8":{
                Log.e("idfitur",prodList.get(position).id_fitur);
                holder.idRT.setText(prodList.get(position).id_transaksi);
                holder.namaRT.setText("Transaksi : Order "+prodList.get(position).fitur);
                holder.keterangan.setText("Terima Tips dari "+prodList.get(position).nama_depan);
                holder.nominalRT.setText("+"+amountAdapter(prodList.get(position).kredit));
                holder.nominalRT.setTextColor(context.getResources().getColor(R.color.textColorGreen));
                break;
            }
            case "9":{
                Log.e("idfitur",prodList.get(position).id_fitur);
                holder.idRT.setVisibility(View.GONE);
                holder.namaRT.setText("Transaksi : Denda");
                holder.keterangan.setText(prodList.get(position).keterangan);
                holder.nominalRT.setText("-"+amountAdapter(prodList.get(position).debit));
                holder.nominalRT.setTextColor(context.getResources().getColor(R.color.textColorRed));
                break;
            }

            case "10":{
                Log.e("idfitur",prodList.get(position).id_fitur);
                holder.alamttujuan.setVisibility(View.GONE);
                holder.alamatAsal.setVisibility(View.GONE);
                holder.idRT.setVisibility(View.GONE);
                holder.asal.setVisibility(View.GONE);
                holder.tujuan.setVisibility(View.GONE);
                holder.biayaAkhir.setVisibility(View.GONE);
                holder.keterangan.setVisibility(View.VISIBLE);
                holder.namaRT.setText("Transaction: Withdraw funds");
                holder.keterangan.setText(prodList.get(position).keterangan+"\n\nPlease check your account");
                holder.nominalRT.setText("Amount withdrawn "+amountAdapter(prodList.get(position).debit));
                holder.nominalRT.setTextColor(context.getResources().getColor(R.color.textColorGreen));

                break;
            }

            case "11":{
                Log.e("idfitur",prodList.get(position).id_fitur);
                holder.alamttujuan.setVisibility(View.GONE);
                holder.alamatAsal.setVisibility(View.GONE);
                holder.idRT.setVisibility(View.GONE);
                holder.asal.setVisibility(View.GONE);
                holder.tujuan.setVisibility(View.GONE);
                holder.biayaAkhir.setVisibility(View.GONE);
                holder.keterangan.setVisibility(View.VISIBLE);
                holder.namaRT.setText("Transaction: Added Balance");
                holder.keterangan.setText(prodList.get(position).keterangan);
                holder.nominalRT.setText("+"+amountAdapter(prodList.get(position).kredit));
                holder.nominalRT.setTextColor(context.getResources().getColor(R.color.textColorGreen));

                break;
            }
            case "12":{
                Log.e("idfitur",prodList.get(position).id_fitur);
                holder.alamttujuan.setVisibility(View.GONE);
                holder.alamatAsal.setVisibility(View.GONE);
                holder.idRT.setVisibility(View.GONE);
                holder.asal.setVisibility(View.GONE);
                holder.tujuan.setVisibility(View.GONE);
                holder.biayaAkhir.setVisibility(View.GONE);
                holder.keterangan.setVisibility(View.VISIBLE);
                holder.namaRT.setText("Transaction: Balance Reduction");
                holder.keterangan.setText(prodList.get(position).keterangan);
                holder.nominalRT.setText("-"+amountAdapter(prodList.get(position).debit));
                holder.nominalRT.setTextColor(context.getResources().getColor(R.color.red));

                break;
            }
            default:
                break;
        }

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (prodList.get(position).tipe_transaksi) {
                    case "4": {
                        Intent intent = new Intent(context, detail_riwayat.class);
                        intent.putExtra("jumlah",  amountAdapter(prodList.get(position).kredit));
                        intent.putExtra("tanggal", prodList.get(position).waktu_riwayat);
                        intent.putExtra("saldo", amountAdapter(prodList.get(position).saldo));
                        intent.putExtra("keterangan", prodList.get(position).keterangan);
                        context.startActivity(intent);
                        break;
                    }
                    case "5":{
                        Intent intent = new Intent(context, detail_transaksi.class);
                        intent.putExtra("id_transaksi", prodList.get(position).id_transaksi);
                        intent.putExtra("tanggal", tanggalAdapter(prodList.get(position).waktu_riwayat));
                        intent.putExtra("alamat_asal", prodList.get(position).alamat_asal);
                        intent.putExtra("alamat_tujuan", prodList.get(position).alamat_tujuan);
                        intent.putExtra("jarak", convertJarak(Double.parseDouble(prodList.get(position).jarak)));
                        intent.putExtra("tarif", prodList.get(position).biay_akhir);
                        intent.putExtra("total_cost", prodList.get(position).biay_akhir);
                        intent.putExtra("potongan", amountAdapter(prodList.get(position).debit));
                        intent.putExtra("estimasi", prodList.get(position).fitur);
                        intent.putExtra("totalpaid", prodList.get(position).biay_akhir);
                        intent.putExtra("total_paid", prodList.get(position).biay_akhir);
                        intent.putExtra("saldo",amountAdapter(prodList.get(position).saldo));
                        context.startActivity(intent);
                        break;
                    }
                    case "6":{
                        Intent intent = new Intent(context, detail_transaksi_saldo.class);
                        intent.putExtra("id_transaksi1", prodList.get(position).id_transaksi);
                        intent.putExtra("tanggal1", tanggalAdapter(prodList.get(position).waktu_riwayat));
                        intent.putExtra("alamat_asal1", prodList.get(position).alamat_asal);
                        intent.putExtra("alamat_tujuan1", prodList.get(position).alamat_tujuan);
                        intent.putExtra("jarak1", convertJarak(Double.parseDouble(prodList.get(position).jarak)));
                        intent.putExtra("tarif1", prodList.get(position).biay_akhir);
                        intent.putExtra("total_cost1", prodList.get(position).biay_akhir);
                        intent.putExtra("potongan1", amountAdapter(prodList.get(position).kredit));
                        intent.putExtra("estimasi1", prodList.get(position).fitur);
                        intent.putExtra("totalpaid1", prodList.get(position).biay_akhir);
                        intent.putExtra("total_paid1", prodList.get(position).biay_akhir);
                        intent.putExtra("saldo1",amountAdapter(prodList.get(position).saldo));
                        context.startActivity(intent);
                        break;
                    }
                    case "7":{
                        Intent intent = new Intent(context, detail_riwayat.class);
                        intent.putExtra("jumlah", prodList.get(position).kredit);
                        intent.putExtra("tanggal", prodList.get(position).waktu_riwayat);
                        intent.putExtra("saldo", amountAdapter(prodList.get(position).kredit));
                        intent.putExtra("keterangan", prodList.get(position).keterangan);
                        context.startActivity(intent);
                        break;
                    }
                    case "8":{
                        Intent intent = new Intent(context, detail_transaksi.class);
                        intent.putExtra("jumlah", prodList.get(position).kredit);
                        intent.putExtra("tanggal", prodList.get(position).waktu_riwayat);
                        intent.putExtra("saldo", amountAdapter(prodList.get(position).kredit));
                        intent.putExtra("keterangan", prodList.get(position).keterangan);
                        context.startActivity(intent);
                        break;
                    }
                    case "9":{
                        Intent intent = new Intent(context, detail_riwayat.class);
                        intent.putExtra("jumlah", prodList.get(position).kredit);
                        intent.putExtra("tanggal", prodList.get(position).waktu_riwayat);
                        intent.putExtra("saldo", amountAdapter(prodList.get(position).kredit));
                        intent.putExtra("keterangan", prodList.get(position).keterangan);
                        context.startActivity(intent);
                        break;
                    }
                    case "10":{
                        Intent intent = new Intent(context, detail_riwayat.class);
                        intent.putExtra("jumlah", amountAdapter(prodList.get(position).debit));
                        intent.putExtra("tanggal", prodList.get(position).waktu_riwayat);
                        intent.putExtra("saldo", amountAdapter(prodList.get(position).saldo));
                        intent.putExtra("keterangan", prodList.get(position).keterangan);
                        context.startActivity(intent);
                        break;
                    }
                    case "11":{
                        Intent intent = new Intent(context, detail_riwayat.class);
                        intent.putExtra("jumlah",amountAdapter(prodList.get(position).debit));
                        intent.putExtra("tanggal", prodList.get(position).waktu_riwayat);
                        intent.putExtra("saldo", amountAdapter(prodList.get(position).saldo));
                        intent.putExtra("keterangan", prodList.get(position).keterangan);
                        context.startActivity(intent);
                        break;
                    }
                    case "12":{
                        Intent intent = new Intent(context, detail_riwayat.class);
                        intent.putExtra("jumlah", amountAdapter(prodList.get(position).debit));
                        intent.putExtra("tanggal", prodList.get(position).waktu_riwayat);
                        intent.putExtra("saldo", amountAdapter(prodList.get(position).kredit));
                        intent.putExtra("keterangan", prodList.get(position).keterangan);
                        context.startActivity(intent);
                        break;
                    }
                    default:
                        break;

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return prodList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private String tanggalAdapter(String tgl){
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(Long.parseLong(tgl)*1000));
    }

    private String amountAdapter(int amo){
        return NumberFormat.getNumberInstance(Locale.GERMANY).format(amo);
    }

    private String convertJarak(Double jarak){
        int range = (int)(jarak*10);
        jarak = (double)range/10;
        return String.valueOf(jarak)+" KM";
    }

    private long timeAdapter(long timestamp){
        return (timestamp*1000);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tanggalRT;
        public TextView idRT;
        public TextView namaRT;
        public TextView keterangan;
        public TextView nominalRT;
        public TextView alamatAsal;
        public TextView alamttujuan;
        public TextView biayaAkhir;
        public TextView saldoAkhirRT;
        public ImageView asal;
        public ImageView tujuan;
        public LinearLayout parentLayout;


        public MyViewHolder(View itemView){
            super(itemView);
            tanggalRT = (TextView) itemView.findViewById(R.id.tanggalRT);
            idRT = (TextView) itemView.findViewById(R.id.idRT);
            namaRT = (TextView) itemView.findViewById(R.id.namaRT);
            alamatAsal = (TextView) itemView.findViewById(R.id.alamat1);
            alamttujuan = (TextView) itemView.findViewById(R.id.alamat2);
            keterangan = (TextView) itemView.findViewById(R.id.keterangRT);
            nominalRT = (TextView) itemView.findViewById(R.id.nominalRT);
            saldoAkhirRT = (TextView) itemView.findViewById(R.id.saldoAkhirRT);
            biayaAkhir=(TextView)itemView.findViewById(R.id.biayaakhir);
            asal = (ImageView)itemView.findViewById(R.id.ic_lokasi_jemput);
            tujuan=(ImageView)itemView.findViewById(R.id.ic_lokasi_antar);
            parentLayout = itemView.findViewById(R.id.trnaskasi_1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onCardViewTap(v, getLayoutPosition());
                }
            });

        }
    }
}