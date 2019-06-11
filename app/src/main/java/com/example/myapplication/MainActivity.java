package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private final static String TAG = MainActivity.class.getSimpleName();
    private Intent intent;
    private SpeechRecognizer mRecognizer;
    private Boolean isListening = false;
    private WebView myWebView;
    private String accessUrl = "https://youtube.com/";
    private int mIndex;
    private String pageInfo = "home";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 音声設定
        button = findViewById(R.id.button);
        button.setText("一時停止中");

        if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, RECORD_AUDIO)) {
                // 拒否した場合
            } else {
                // 許可した場合
                int MY_PERMISSIONS_RECORD_AUDIO = 1;
                ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
            }
        }

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPAN.toString());
        intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);



        // Web view 系　設定
        mIndex = 0;
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



    public void buttonClicked(View view) {

        if (isListening) {
            button.setText("一時停止中");
            stopListening();
            isListening = false;
        } else {
            button.setText("聞いています");
            restartListeningService();
            isListening = true;
        }

    }



    private RecognitionListener listener = new RecognitionListener() {


        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Log.d(TAG, "準備できてます");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "始め！");
        }

        @Override
        public void onRmsChanged(float v) {
            Log.d(TAG, "音声が変わった");
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            Log.d(TAG, "新しい音声");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "終わりました");
        }


        @Override
        public void onError(int i) {
            switch (i) {
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:

                    Log.d(TAG, "ネットワークタイムエラー");
                    break;
                case SpeechRecognizer.ERROR_NETWORK:

                    Log.d(TAG,"その外ネットワークエラー");
                    break;
                case SpeechRecognizer.ERROR_AUDIO:

                    Log.d(TAG, "Audio エラー");
                    break;
                case SpeechRecognizer.ERROR_SERVER:

                    Log.d(TAG, "サーバーエラー");
                    break;
                case SpeechRecognizer.ERROR_CLIENT:

                    Log.d(TAG, "クライアントエラー");
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:

                    Log.d(TAG, "何も聞こえてないエラー");
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:

                    Log.d(TAG, "適当な結果を見つけてませんエラー");
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:

                    Log.d(TAG, "RecognitionServiceが忙しいエラー");
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:

                    Log.d(TAG, "RECORD AUDIOがないエラー");
                    break;
            }
            restartListeningService();
        }

        @Override
        public void onResults(Bundle bundle) {
            String key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = bundle.getStringArrayList(key);

            String[] result = new String[0];
            if (mResult != null) {
                result = new String[mResult.size()];
            }
            if (mResult != null) {
                mResult.toArray(result);
            }

            handleCommands(result[0]);
            Log.d("a", "onResults: 終わり");
            restartListeningService();


        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    };


    // 音声認識を開始する
    protected void startListening() {
        try {
            if (mRecognizer == null) {
                mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
                if (!SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "音声認識が使えません",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                mRecognizer.setRecognitionListener(listener);
            }

            mRecognizer.startListening(intent);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "startListening()でエラーが起こりました",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // 音声認識を終了する
    protected void stopListening() {
        if (mRecognizer != null) mRecognizer.destroy();
        mRecognizer = null;

    }

    // 音声認識を再開する
    public void restartListeningService() {
        stopListening();
        startListening();
    }


    private void onScriptReady() {
        myWebView.loadUrl("javascript:(function() {" +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var style = document.createElement('style');" +
                "style.type = 'text/css';" +
                "style.innerHTML = '.selected{background-color:hotpink;}';" +
                "parent.appendChild(style)" +
                "})()");
        browseHome(0);
        scrollHome(0);

    }

    private void desideHome(int i) {

        myWebView.loadUrl("javascript:var videos = document.getElementsByClassName('large-media-item-thumbnail-container');" +
                "videos["+ i + "].click();");
    }

    private void desideRelatedandSearch(int i) {

        myWebView.loadUrl("javascript:var videos = document.getElementsByClassName('compact-media-item-image');" +
                "videos["+ i + "].click();");

    }




    private void pause() {
        myWebView.loadUrl("javascript:" +
                "        var videoToPlay = document.getElementById(\"movie_player\");" +
                "        videoToPlay.click();");
    }

    private void browseHome(int i) {

        myWebView.loadUrl("javascript:(() => {var videos = document.getElementsByClassName('selected');videos[0].className=\"item\";})()");
        myWebView.loadUrl("javascript:(() => {var videos = document.getElementsByClassName('item');videos[" + i + "].className=\"selected\";})()");


    }

    private void browseRelatedandSearch(int i) {

        myWebView.loadUrl("javascript:(() => {var videos = document.getElementsByClassName('selected');videos[0].className=\"compact-media-item\";})()");
        myWebView.loadUrl("javascript:(() => {var videos = document.getElementsByClassName('compact-media-item');videos[" + (i + 1) + "].className=\"selected\";})()");


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
                "videos["+ (i + 1) + "].scrollIntoView({" +
                "        behavior: 'smooth'," +
                "        block: 'nearest'," +
                "        inline: 'nearest'" +
                "    });");



    }




    /*
    public void buttonClicked(View view) {
        int state = mIndex;

        switch (state) {
            case 0:
                //browse();
                search();
                inputText("犬");
                //scrollHome(state);
                //desideHome(0);


                break;
            case 1:

                scrollRelatedandSearch(0);
                browseRelatedandSearch(0);
                //browse();
                //search();
                break;
            case 2:
                scrollRelatedandSearch(1);
                browseRelatedandSearch(1);

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
        }
        //scrollHome(state);

        //scrollRelatedandSearch(state);
        mIndex += 1;

    }*/


    private void handleCommands(String command) {
        switch (pageInfo) {
            case "home":
                if ((command.contains("上") || command.contains("うえ")) && mIndex > 0) {
                    mIndex -= 1;
                    browseHome(mIndex);
                    scrollHome(mIndex);
                } else if (command.contains("下") || command.contains("した")) {
                    mIndex += 1;
                    browseHome(mIndex);
                    scrollHome(mIndex);
                } else if (command.contains("再生") || command.contains("さいせい")) {
                    desideHome(mIndex);
                    mIndex = 0;
                    pageInfo = "related";
                    browseHome(mIndex);
                    scrollHome(mIndex);
                }
                break;
            case "trending":
                break;
            case "searchResult":
                break;
            case "related":
                break;
            default:
                throw new RuntimeException("invalid page info");
        }

    }









}
