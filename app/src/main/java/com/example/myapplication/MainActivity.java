package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private WebView myWebView;
    private String accessUrl = "https://youtube.com/";

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //web view作成
        myWebView = findViewById(R.id.webview);

        //setting系
        WebSettings webSettings = myWebView.getSettings();
        //javascript　有効化
        webSettings.setJavaScriptEnabled(true);
        //web storage を有効化
        webSettings.setDomStorageEnabled(true);

        //別のブラウザ開かないように
        myWebView.setWebViewClient(new WebViewClient());

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        // url読み込み
        myWebView.loadUrl(accessUrl);
    }



}
