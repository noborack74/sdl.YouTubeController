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

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Button button;
    private final static String TAG = MainActivity.class.getSimpleName();

    private Intent intent;
    private SpeechRecognizer mRecognizer;
    private Boolean isListening = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //web view作成
        textView = findViewById(R.id.textView);
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
            Log.d("log:: ", "準備できてます");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d("log:: ", "始め！");
        }

        @Override
        public void onRmsChanged(float v) {
            Log.d("log:: ", "音声が変わった");
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            Log.d("log:: ", "新しい音声");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d("log:: ", "終わりました");
        }


        @Override
        public void onError(int i) {
            switch (i) {
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:

                    textView.setText("ネットワークタイムエラー");
                    break;
                case SpeechRecognizer.ERROR_NETWORK:

                    textView.setText("その外ネットワークエラー");
                    break;
                case SpeechRecognizer.ERROR_AUDIO:

                    textView.setText("Audio エラー");
                    break;
                case SpeechRecognizer.ERROR_SERVER:

                    textView.setText("サーバーエラー");
                    break;
                case SpeechRecognizer.ERROR_CLIENT:

                    textView.setText("クライアントエラー");
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:

                    textView.setText("何も聞こえてないエラー\nもう一度");



                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:

                    textView.setText("適当な結果を見つけてませんエラー");
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    textView.setText("RecognitionServiceが忙しいエラー");
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    textView.setText("RECORD AUDIOがないエラー");
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

            textView.setText(result[0]);
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









}
