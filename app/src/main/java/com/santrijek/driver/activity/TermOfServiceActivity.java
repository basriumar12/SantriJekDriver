package com.santrijek.driver.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.santrijek.driver.R;

public class TermOfServiceActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tersm_condition);

        webView = (WebView) findViewById(R.id.web_view);

        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadUrl("http://go-pickme.com/index.php/c_utama/kebijakanPrivasi_app");

    }

}