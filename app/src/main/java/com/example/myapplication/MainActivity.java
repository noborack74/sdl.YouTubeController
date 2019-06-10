package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private WebView myWebView;
    private String accessUrl = "https://youtube.com/";
    //private String accessUrl = "https://m.youtube.com/watch?v=crp_ZWkR75c";
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        state = 0;

        //web view作成
        myWebView = findViewById(R.id.webview);

        //setting系
        WebSettings webSettings = myWebView.getSettings();
        //javascript　有効化
        webSettings.setJavaScriptEnabled(true);
        //web storage を有効化
        webSettings.setDomStorageEnabled(true);

        //別のブラウザ開かないように
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //ページ読み込みが完了してからJavascriptが利用可能になる
                onScriptReady();
            }
        });

        myWebView.setWebChromeClient(new WebChromeClient());

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        // url読み込み
        myWebView.loadUrl(accessUrl);

    }

    private void onScriptReady() {
        //Toast.makeText(this,"関数実行中", Toast.LENGTH_LONG).show();

        //最初のスクリプト内関数実行








    }

    private void desideHome(int i) {

        myWebView.loadUrl("javascript:var videos = document.getElementsByClassName('large-media-item-thumbnail-container');" +
                "videos["+ i + "].click();");
    }

    private void desideRelatedandSearch(int i) {

        myWebView.loadUrl("javascript:var videos = document.getElementsByClassName('compact-media-item-image');" +
                "videos["+ i + "].click();");
                //"alert(videos[" + i + "])");
    }




    private void pause() {
        myWebView.loadUrl("javascript:" +
                "        var videoToPlay = document.getElementById(\"movie_player\");" +
                "        videoToPlay.click();");
    }

    private void browse() {
        int i = 0;
        myWebView.loadUrl("javascript:var videos = document.getElementsByClassName('large-media-item-info cbox');alert(videos[0].style.background-color);");
        //myWebView.loadUrl("javascript:alert('foo');");
    }

    private void home() {
        myWebView.loadUrl("javascript: var homeButton = document.getElementById(\"home-icon\");" +
                "    homeButton.click();");
    }

    private void trending() {
        myWebView.loadUrl("javascript: var headButtons = document.getElementsByClassName(\"scbrr-tab center\");" +
                "    headButtons[1].click();");

    }

    private void search() {
        myWebView.loadUrl("javascript: var searchButton = document.getElementsByClassName(\"icon-button \")[1];" +
                "    searchButton.click();");
                //"    alert(searchButton[0]);");


    }

    private void inputText(String text) {
        myWebView.loadUrl("javascript: setTimeout (() => {var textBox = document.getElementsByClassName(\"searchbox-input-wrapper\")[0].firstChild;" +
                "    textBox.value = '"+ text +"';var searchButton = document.getElementsByClassName(\"icon-button \")[1];" +
                "    searchButton.click();}, 10);");



    }

    private void scrollHome(int i) {
        myWebView.loadUrl("javascript:var videos = document.getElementsByClassName('large-media-item-thumbnail-container');" +
                "videos["+ i + "].scrollIntoView({" +
                "        behavior: 'smooth'," +
                "        block: 'start'," +
                "        inline: 'nearest'" +
                "    });");
    }

    private void scrollRelatedandSearch(int i) {
        myWebView.loadUrl("javascript:var videos = document.getElementsByTagName('ytm-compact-video-renderer');" +
                "videos["+ i + "].scrollIntoView({" +
                "        behavior: 'smooth'," +
                "        block: 'nearest'," +
                "        inline: 'nearest'" +
                "    });");
    }




    public void buttonClicked(View view) {
        /*
        switch (state) {
            case 0:
                //browse();
                search();
                inputText("犬");
                //scrollHome(state);


                break;
            case 1:

                scrollRelatedandSearch(0);
                //browse();
                //search();
                break;
            case 2:
                scrollRelatedandSearch(1);

                //pause();
                break;
            case 3:
                desideRelatedandSearch(1);

                //home();
                break;
            case 4:
                pause();
                //trending();
                break;
            case 5:
                scrollRelatedandSearch(0);
                //deside();
                break;
            case 6:
                scrollRelatedandSearch(1);
                //pause();
                break;
            case 7:
                desideRelatedandSearch(1);
                //search();
                break;
            case 8:
                pause();
                //inputText();
                break;

            default:
                home();


                //browse();

                //play();
                break;
        }*/
        scrollHome(state);

        state += 1;

    }



}
