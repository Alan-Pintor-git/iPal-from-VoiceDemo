package com.example.robotvoicedemo;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.robot.speech.SpeechManager;
import android.robot.speech.SpeechManager.TtsListener;
import android.robot.speech.SpeechService;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
//import com.avatar.speech.LISTEN;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private ImageView mBtnBack;

    private TextView mTTSStatus;

    private Button mBtnTTSStart, commandBtn;

    private EditText mTTSContent;

    private InputMethodManager mInputMethodManager;

    private SpeechManager mSpeechManager;




    private TtsListener mTtsListener = new TtsListener() {
        @Override
        public void onBegin(int requestId) {
            mTTSStatus.setText(getString(R.string.tts_start_speaking)
                    + requestId);
        }

        @Override
        public void onEnd(int requestId) {
            mTTSStatus.setText(getString(R.string.tts_stop_speaking)
                    + requestId);
        }

        @Override
        public void onError(int error) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null) {
            getActionBar().hide();
        }
        setContentView(R.layout.activity_main);


        initData();
        initView();
        initListener();

        doAvatarAction("WAVE");


    }

    public void doAvatarAction(String action) {
        String baseUrl = "http://192.168.50.155:8080/?method=doAction&&action=";
        final String fullUrl = baseUrl + action;

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(fullUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.getResponseCode();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("IOException: ", e.getMessage() + e.getCause());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpeechManager.setTtsListener(null);
    }

    private void initData() {
        mSpeechManager = (SpeechManager) getSystemService(SpeechService.SERVICE_NAME);
        mInputMethodManager = (InputMethodManager) this.getApplicationContext()
                .getSystemService(Context
                        .INPUT_METHOD_SERVICE);
    }

    private void initView() {
        mBtnBack = (ImageView) findViewById(R.id.common_title_back);
        mTTSContent = (EditText) findViewById(R.id.et_tts_content);
        mTTSStatus = (TextView) findViewById(R.id.tv_tts_status);
        mBtnTTSStart = (Button) findViewById(R.id.btn_tts_start);
        commandBtn = (Button) findViewById(R.id.command_btn);

        mBtnBack.setOnClickListener(this);
        mBtnTTSStart.setOnClickListener(this);
        commandBtn.setOnClickListener(this);

    }

    private void initListener() {
        mSpeechManager.setTtsListener(mTtsListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_title_back:
                finish();
                break;
            case R.id.btn_tts_start:
                mInputMethodManager.hideSoftInputFromWindow(
                        mTTSContent.getWindowToken(), 0);
                String tts = mTTSContent.getText().toString();
                if (!TextUtils.isEmpty(tts)) {
                    mSpeechManager.startSpeaking(tts);
                }
                break;
            case R.id.command_btn:
                doAvatarAction("OH_YEAH");
                break;
            default:
                break;
        }
    }
}
