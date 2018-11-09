package com.quickthink.tvchildrenmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class TvStateReceiver extends BroadcastReceiver {
    private static final String TAG = TvStateReceiver.class.getSimpleName();

    private static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private static final String ACTION_NETWORK_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "Action: " + action);
        Toast.makeText(context, context.getText(R.string.toast_tips), Toast.LENGTH_LONG).show();
        StartTimerService sts = new StartTimerService(context);
        new Thread(sts).start();
    }

    public class StartTimerService implements Runnable{
        private Context context;

        public StartTimerService(Context context){
            this.context = context;
        }

        @Override
        public void run() {
            if(this.context != null){
                Log.i(TAG, "start TimerService");
                Intent intent = new Intent(context, TimerService.class);
                this.context.startService(intent);
                Log.i(TAG, "start TimerService done");
            }
        }
    }
}
