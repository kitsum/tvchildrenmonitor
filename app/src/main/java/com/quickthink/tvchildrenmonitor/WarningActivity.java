package com.quickthink.tvchildrenmonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import static com.quickthink.tvchildrenmonitor.Constants.WARNINGACTIVITY_EXTRA_BREAKDURATION;
import static com.quickthink.tvchildrenmonitor.Constants.WARNINGACTIVITY_EXTRA_PERIODS;

public class WarningActivity extends Activity implements Handler.Callback {
    private static final String TAG = WarningActivity.class.getSimpleName();

    private static final int MSG_COUNTDOWN = 0;

    private static final  int DEFAULT_PERIODS = 2;
    private static final int DEFAULT_BREAKDURATION = 1;

    private Handler handler;
    private TextView warningMsgTxtView;
    private TextView countdownMsgTextView;
    private String countdownMsg;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);
        warningMsgTxtView = findViewById(R.id.warning_msg);
        countdownMsgTextView = findViewById(R.id.countdown_msg);
        handler = new Handler(this);
        countdownMsg = getString(R.string.countdown_msg);
        Intent intent = getIntent();
        int periods = intent.getIntExtra(WARNINGACTIVITY_EXTRA_PERIODS, DEFAULT_PERIODS);
        int breakDuration = intent.getIntExtra(WARNINGACTIVITY_EXTRA_BREAKDURATION, DEFAULT_BREAKDURATION);
        counter =  breakDuration * 60;
        if(warningMsgTxtView != null) {
            warningMsgTxtView.setText(String.format(getString(R.string.warning_msg), periods, breakDuration));
        }
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        handler.sendEmptyMessage(MSG_COUNTDOWN);
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        warningMsgTxtView = null;
        countdownMsgTextView = null;
        countdownMsg = null;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean retVal = false;
        Log.i(TAG,":: dispatchKeyEvent keyCode = "+event.getKeyCode());
        switch(event.getKeyCode()){
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                this.finish();
                break;

            default:
                retVal = super.dispatchKeyEvent(event);
                break;
        }
        return retVal;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case MSG_COUNTDOWN:
                if(counter > 0){
                    countdownMsgTextView.setText("");
                    String tips = countdownMsg + counter;
                    countdownMsgTextView.setText(tips);
                    counter--;
                    handler.sendEmptyMessageDelayed(MSG_COUNTDOWN, 1000);
                }
                else {
                    this.finish();
                }
                break;

            default:
                break;
        }
        return false;
    }
}
