package com.santrijek.driver.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebView;

import com.santrijek.driver.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_police);
        webView=(WebView)findViewById(R.id.web_view);
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadUrl("http://go-pickme.com/index.php/c_utama/syaratKetentuan_app");


    }

}
