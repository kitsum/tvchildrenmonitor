package com.quickthink.tvchildrenmonitor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.quickthink.tvchildrenmonitor.Constants.WARNINGACTIVITY_EXTRA_BREAKDURATION;
import static com.quickthink.tvchildrenmonitor.Constants.WARNINGACTIVITY_EXTRA_PERIODS;

public class TimerService extends Service {
    private static final String TAG = TimerService.class.getSimpleName();

    private static final int MSG_START = 0;
    private static final int MSG_RESET = 1;

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    private Timer timer;
    private final MonitorConfigManager monitorConfigManager = MonitorConfigManager.getInstance();
    private final IBinder mBinder = new TimerServiceBinder();
    private boolean isStarted;

    public class TimerServiceBinder extends Binder implements ITimerService {
        @Override
        public void reset() {
            Log.i(TAG, "reset");
            serviceHandler.sendEmptyMessage(MSG_RESET);
        }
    }

    private void resetTimer(){
        Log.i(TAG, "resetTimer");
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        startTimer();
    }

    private void startTimer(){
        MonitorConfig monitorConfig = monitorConfigManager.getMonitorConfig();
        timer = new Timer("TimerService", true);
        startTimer(monitorConfig);
    }

    private void startTimer(MonitorConfig monitorConfig){
        Log.i(TAG, "startTimer");
        if(timer != null && monitorConfig != null && monitorConfig.getEnableMonitor()) {
            int periods = monitorConfig.getMonitorPeriods() * 60 * 1000; //ms
            timer.scheduleAtFixedRate(new WarningTimerTask(this.getApplicationContext(), monitorConfig), periods, periods);
        }
    }

    @Override
    public void onCreate(){
        Log.i(TAG, "onCreate");
        timer = new Timer("TimerService", true);
        monitorConfigManager.init(this.getApplicationContext());
        isStarted = false;
        HandlerThread thread = new HandlerThread("TimerServiceHandlerThread",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand flags = " + flags + ", startId = " + startId);
        serviceHandler.sendEmptyMessage(MSG_START);
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG,"onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if(serviceHandler != null){
            serviceHandler.removeCallbacksAndMessages(null);
            serviceHandler = null;
        }
        if(serviceLooper != null){
            serviceLooper.quit();
            serviceLooper = null;
        }
        isStarted = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class WarningTimerTask extends TimerTask {

        private Context context;
        private MonitorConfig monitorConfig;

        public WarningTimerTask(Context context, MonitorConfig monitorConfig){
            this.context = context;
            this.monitorConfig = monitorConfig;
        }

        @Override
        public void run() {
            if(context != null) {
                Log.i(TAG, "start WarningTimerTask");
                Date now = new Date();
                if(checkTaskRunTime(monitorConfig, now)) {
                    Intent warningIntent = new Intent(context, WarningActivity.class);
                    warningIntent.putExtra(WARNINGACTIVITY_EXTRA_PERIODS, monitorConfig.getMonitorPeriods());
                    warningIntent.putExtra(WARNINGACTIVITY_EXTRA_BREAKDURATION, monitorConfig.getBreakDuration());
                    warningIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(warningIntent);
                }
                else {
                    Log.i(TAG,  String.format("now: %tT is not task time", now));
                }
            }
        }

        private boolean checkTaskRunTime(MonitorConfig monitorConfig, Date now){
            boolean retVal = false;
            if(monitorConfig != null && now != null){
                List<MonitorTimer> monitorTimers = monitorConfig.getMonitorTimerList();
                if(monitorTimers != null && !monitorTimers.isEmpty()){
                    for (MonitorTimer timer : monitorTimers){
                        if(timer.isValidTime(now)) {
                            retVal = true;
                            break;
                        }
                    }
                }
            }
            return  retVal;
        }
    }

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case  MSG_START:
                    if(!isStarted){
                        startTimer();
                        isStarted = true;
                    }
                    break;

                case MSG_RESET:
                    resetTimer();
                    break;

                default:
                    break;
            }
        }
    }
}
