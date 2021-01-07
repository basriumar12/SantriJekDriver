package com.santrijek.driver.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.santrijek.driver.network.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.iconics.IconicsDrawable;

import com.santrijek.driver.R;
import com.santrijek.driver.model.Driver;
import com.santrijek.driver.model.Kendaraan;
import com.santrijek.driver.network.HTTPHelper;
import com.santrijek.driver.network.NetworkActionResult;
import com.santrijek.driver.preference.KendaraanPreference;
import com.santrijek.driver.preference.UserPreference;

import org.json.JSONException;
import org.json.JSONObject;

public class EditSettingKendaraanActivity extends AppCompatActivity {

    EditSettingKendaraanActivity activity;
    Driver driver;
    String whatUpd;
    EditText merek, tipe, nopol, warna;
    TextView title;
    int maxRetry = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kendaraan);

        activity = EditSettingKendaraanActivity.this;
        driver = new UserPreference(this).getDriver();
//
        merek = (EditText) findViewById(R.id.kolomMerek);
        tipe = (EditText) findViewById(R.id.kolomTipe);
        nopol = (EditText) findViewById(R.id.kolomNopol);
        warna = (EditText) findViewById(R.id.kolomWarna);

        Kendaraan now = new KendaraanPreference(this).getKendaraan();

        merek.setText(now.merek);
        tipe.setText(now.tipe);
        nopol.setText(now.nopol);
        warna.setText(now.warna);

        initView();
    }


    private boolean checkCompleteKendaraan(){
        boolean isValid1, isValid2, isValid3, isValid4, isValid5 = false;
        if(merek.getText().toString().equals("")){
            Toast.makeText(activity, "Please fill in the brand column!", Toast.LENGTH_SHORT).show();
            isValid1 = false;
        }else{
            isValid1 = true;
        }
        if(tipe.getText().toString().equals("")){
            Toast.makeText(activity, "Please fill in the type column!", Toast.LENGTH_SHORT).show();
            isValid2 = false;
        }else{
            isValid2 = true;
        }
        if(nopol.getText().toString().equals("")){
            Toast.makeText(activity, "Please fill in the police number column!", Toast.LENGTH_SHORT).show();
            isValid3 = false;
        }else{
            isValid3 = true;
        }
        if(warna.getText().toString().equals("")){
            Toast.makeText(activity, "Please fill in the color column!", Toast.LENGTH_SHORT).show();
            isValid4 = false;
        }else{
            isValid4 = true;
        }
        return (isValid1 && isValid2 && isValid3 & isValid4);
    }

    private void initView(){
        TextView butSubmit = (TextView) findViewById(R.id.butSubmitU);
        butSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkCompleteKendaraan()){
                    Kendaraan kendaraan = new Kendaraan();
                    kendaraan.merek = merek.getText().toString();
                    kendaraan.tipe = tipe.getText().toString();
                    kendaraan.nopol = nopol.getText().toString();
                    kendaraan.warna = warna.getText().toString();
                    updateSetting(kendaraan);
                }
            }
        });
    }

    private void updateSetting(final Kendaraan kendaraan){
        JSONObject jUp = new JSONObject();
        try {
            jUp.put("merek", kendaraan.merek);
            jUp.put("tipe", kendaraan.tipe);
            jUp.put("nomor_kendaraan", kendaraan.nopol);
            jUp.put("warna", kendaraan.warna);
            jUp.put("id_driver", driver.id);
            jUp.put("id_driver_d", driver.id.substring(1, driver.id.length()));

//            Log.d("id_driver_d", driver.id.substring(1, driver.id.length()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog sd = showLoading();
        Log.d("JSON_EDIT", jUp.toString());
        HTTPHelper.getInstance(activity).updateKendaraan(jUp, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                try {
                    if(obj.getString("message").equals("success")){
                        showFinishMessage();
                    }else{
                        Toast.makeText(activity, obj.getString("data"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sd.dismiss();
                maxRetry = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                sd.dismiss();
                if(maxRetry == 0){
                    sd.dismiss();
                    Toast.makeText(activity, "Connection is problematic", Toast.LENGTH_SHORT).show();
                    maxRetry = 4;
                }else{
                    updateSetting(kendaraan);
                    maxRetry--;
                    Log.d("Try_ke_update_kendaraan", String.valueOf(maxRetry));
                    sd.dismiss();
                }
            }
        });
    }
    private MaterialDialog showFinishMessage() {
        final MaterialDialog md = new MaterialDialog.Builder(activity)
                .title("Update berhasil")
                .icon(new IconicsDrawable(activity)
                        .color(Color.BLUE)
                        .sizeDp(24))
                .positiveText("Tutup")
                .positiveColor(Color.DKGRAY)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
//                Intent toMaps = new Intent(activity, MainActivity.class);
//                startActivity(toMaps);
                finish();
            }
        });
        return md;
    }

    private ProgressDialog showLoading(){
        ProgressDialog ad = ProgressDialog.show(activity, "", "Loading...", true);
        return ad;
    }
}
