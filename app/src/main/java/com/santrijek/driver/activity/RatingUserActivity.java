package com.santrijek.driver.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.mikepenz.iconics.IconicsDrawable;

import com.santrijek.driver.MainActivity;
import com.santrijek.driver.R;
import com.santrijek.driver.model.Transaksi;
import com.santrijek.driver.network.HTTPHelper;
import com.santrijek.driver.network.Log;
import com.santrijek.driver.network.NetworkActionResult;
import com.santrijek.driver.service.MyConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class RatingUserActivity extends AppCompatActivity {

    RatingUserActivity activity;
    float nilai;
    int maxRetry = 4;
    Transaksi myTrans;
    TextView rp2,rp3,rp4,rp5,rp6,rp7,rp8,rp9;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_user);

        activity = RatingUserActivity.this;


        final TextView butSubmit = (TextView) findViewById(R.id.butSubmit);
        final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        final EditText addComment = (EditText) findViewById(R.id.addComment);
        CircularImageView logoFitur = (CircularImageView) findViewById(R.id.logoFitur);


        rp2=(TextView)findViewById(R.id.rp20);
        rp3=(TextView)findViewById(R.id.rp30);
        rp4=(TextView)findViewById(R.id.rp40);
        rp5=(TextView)findViewById(R.id.rp50);
        rp6=(TextView)findViewById(R.id.rp60);
        rp7=(TextView)findViewById(R.id.rp70);
        rp8=(TextView)findViewById(R.id.rp80);
        rp9=(TextView)findViewById(R.id.rp90);


        rp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment.setText("Tank You");
            }
        });
        rp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment.setText("Have a nice day");
            }
        });
        rp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment.setText("Come back again");
            }
        });
        rp5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment.setText("I don't like you");
            }
        });
        rp5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment.setText("You are so talkative");
            }
        });
        rp6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment.setText("I like");
            }
        });
        rp7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment.setText("I don't like");
            }
        });
        rp8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment.setText("Thank you for choosing");
            }
        });
        rp9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment.setText("Like");
            }
        });
      //  getSerializableExtra

            String orderFitur = getIntent().getStringExtra("order_fitur");


            selectionFitur(orderFitur, logoFitur);


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                nilai = v;
            }
        });

        butSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final JSONObject jRate = new JSONObject();
                try {
                    jRate.put("id_transaksi", getIntent().getStringExtra("id_transaksi"));
                    jRate.put("id_pelanggan", getIntent().getStringExtra("id_pelanggan"));
                    jRate.put("id_driver", getIntent().getStringExtra("id_driver"));
                    jRate.put("rating", (int)nilai);
                    jRate.put("catatan", addComment.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ratingUser(jRate);
//                Toast.makeText(RatingUserActivity.this, "Rating : "+(int)nilai+"\nKomentar : "+addComment.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        getIncomingIntent();
    }

    private void getIncomingIntent(){
        //  Log.d(TAG,"getIncomingIntent: checking for incoming intents.");

        if(getIntent().hasExtra("id_transaksi")){

            // String imageUrl=getIntent().getStringExtra("image_url");
            String Tarif=getIntent().getStringExtra("harga");
            String Tanggal=getIntent().getStringExtra("waktu_order");

            setImage(Tarif,Tanggal);

            Log.e("hasil",Tarif+"");

        }
    }
    private void setImage(String Tarif,String Tanggal){

        TextView tanggal,tarif;
        tarif =(TextView)findViewById(R.id.biaya);
        tanggal=(TextView)findViewById(R.id.tanggal);
        tarif.setText("$"+Tarif+".00");
        tanggal.setText("Finish Time :"+Tanggal);
    }

    private void selectionFitur(String fitur, ImageView logo){
        switch (fitur){
            case "1":
                logo.setImageResource(R.drawable.e_ride_on);
                break;
            case "2":
                logo.setImageResource(R.mipmap.ic_fitur_mcar);
                break;
            case "3":
                logo.setImageResource(R.drawable.ic_e_food);
                break;
            case "4":
                logo.setImageResource(R.mipmap.ic_fitur_mmart);
                break;
            case "5":
                logo.setImageResource(R.mipmap.ic_fitur_msend);
                break;
            case "6":
                logo.setImageResource(R.mipmap.ic_fitur_mmassage);
                break;
            case "7":
                logo.setImageResource(R.mipmap.ic_fitur_mbox);
                break;
            case "8":
                logo.setImageResource(R.mipmap.ic_fitur_mservice);
                break;
            case "9":
                logo.setImageResource(R.drawable.ic_e_electronic);
                break;
            case "10":
                logo.setImageResource(R.drawable.ic_e_laundry);
                break;
            default:
                break;
        }
    }

    private void ratingUser(final JSONObject jRate){

        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).rateUser(jRate, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                pd.dismiss();
                maxRetry = 4;
                try {
                    if(obj.getString("message").equals("success")){
                        showFinishMessage();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String message) {
                pd.dismiss();
            }

            @Override
            public void onError(String message) {
                if(maxRetry == 0){
                    pd.dismiss();
                    Toast.makeText(activity, "Koneksi bermasalah..", Toast.LENGTH_SHORT).show();
                    Log.d("data_sync_mfood", "Retrieving Data Null");
                    maxRetry = 4;
                }else{
                    ratingUser(jRate);
                    maxRetry--;
                    Log.d("Try_ke_rating ", String.valueOf(maxRetry));
                    pd.dismiss();
                }
            }
        });
    }


    private ProgressDialog showLoading(){
        ProgressDialog ad = ProgressDialog.show(activity, "", "Loading...", true);
        return ad;
    }


    private MaterialDialog showFinishMessage() {
        final MaterialDialog md = new MaterialDialog.Builder(activity)
                .title("thank you")
                .content("Happy working again")
                .icon(new IconicsDrawable(activity)
                        .color(Color.BLUE)
                        .sizeDp(24))
                .positiveText("Closed")
                .cancelable(false)
                .positiveColor(Color.DKGRAY)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
                Bundle data = new Bundle();
                Intent toMaps = new Intent(activity, MainActivity.class);
                toMaps.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                toMaps.putExtra("SOURCE", MyConfig.dashFragment);
                startActivity(toMaps);
                finish();
            }
        });
        return md;
    }

}
