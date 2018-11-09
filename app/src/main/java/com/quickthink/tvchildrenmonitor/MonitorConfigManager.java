package com.quickthink.tvchildrenmonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiesen.liu on 2017/12/13.
 */

public class MonitorConfigManager {
    private static final String TAG = MonitorConfigManager.class.getSimpleName();

    private static final String PREFERENCE_FILE_KEY = "MONITOR_CONFIG_PREFERENCE_FILE";
    private static final String KEY_ENABLEMONITOR = "KEY_ENABLEMONITOR";
    private static final String KEY_MONITORPERIODS = "KEY_MONITORPERIODS";
    private static final String KEY_BREAKDURATION = "KEY_BREAKDURATION";
    private static final String KEY_MONITORTIMERLISTSTR = "KEY_MONITORTIMERLIST";

    private static final String ITEM_SEPARATOR = ";";

    private static final boolean DEFAULT_ENABLE_MONITOR = true;
    private static final int DEFAULT_MONITOR_PERIODS = 2 ;//2 min
    private static final int DEFAULT_BREAK_DURATION = 1 ;//1 min
    private static final String DEFAULT_MONITOR_TIMER_LIST_STR = "2,3,4,5,6|12:00:00|14:00:00";//Monday-Friday, 12:00-14:00

    private static MonitorConfigManager monitorConfigManager;
    private Context context;
    private MonitorConfig monitorConfig;

    private MonitorConfigManager(){
        context = null;
        monitorConfig = null;
    }

    public static MonitorConfigManager getInstance(){
        if(monitorConfigManager == null){
            monitorConfigManager = new MonitorConfigManager();
        }
        return monitorConfigManager;
    }

    public void init(Context appContext){
        this.context = appContext;
    }

    public MonitorConfig getMonitorConfig(){
        if(monitorConfig == null) {
            boolean enableMonitor = DEFAULT_ENABLE_MONITOR;
            int monitorPeriods = DEFAULT_MONITOR_PERIODS;
            int breakDuration = DEFAULT_BREAK_DURATION;
            String monitorTimerListStr = DEFAULT_MONITOR_TIMER_LIST_STR;
            if (context != null) {
                SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
                enableMonitor = sharedPref.getBoolean(KEY_ENABLEMONITOR, DEFAULT_ENABLE_MONITOR);
                monitorPeriods = sharedPref.getInt(KEY_MONITORPERIODS, DEFAULT_MONITOR_PERIODS);
                breakDuration = sharedPref.getInt(KEY_BREAKDURATION, DEFAULT_BREAK_DURATION);
                monitorTimerListStr = sharedPref.getString(KEY_MONITORTIMERLISTSTR, DEFAULT_MONITOR_TIMER_LIST_STR);
            }
            List<MonitorTimer> monitorTimerList = extractMonitorTimerListStr(monitorTimerListStr);
            monitorConfig = new MonitorConfig(enableMonitor, monitorPeriods, breakDuration, monitorTimerList);
        }

        return monitorConfig;
    }

    public boolean UpdateMonitorConfig(MonitorConfig newMonitorConfig){
        boolean retVal = false;
        if(newMonitorConfig != null){
            retVal = saveMonitorConfig(newMonitorConfig);
            monitorConfig = newMonitorConfig;
        }
        Log.i(TAG, "UpdateMonitorConfig retVal = " + retVal);
        return retVal;
    }

    private boolean saveMonitorConfig(MonitorConfig config){
        boolean retVal = false;
        if(config != null && context != null){
            SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(KEY_ENABLEMONITOR, config.getEnableMonitor());
            editor.putInt(KEY_MONITORPERIODS, config.getMonitorPeriods());
            editor.putInt(KEY_BREAKDURATION, config.getBreakDuration());
            editor.putString(KEY_MONITORTIMERLISTSTR, formatMonitorTimerList(config.getMonitorTimerList()));
            editor.apply();
            retVal = true;
        }
        return retVal;
    }

    private String formatMonitorTimerList(List<MonitorTimer> monitorTimerList){
        StringBuilder sb = new StringBuilder();
        if(monitorTimerList != null && !monitorTimerList.isEmpty()){
            int last = monitorTimerList.size() - 1;
            for (int i = 0; i <= last; i++) {
                sb.append(monitorTimerList.get(i).toString());
                if(i < last){
                    sb.append(ITEM_SEPARATOR);
                }
            }
        }
        String str = sb.toString();
        Log.d(TAG, "str = " + str);
        return str;
    }

    private List<MonitorTimer> extractMonitorTimerListStr(String monitorTimerListStr){
        Log.d(TAG, "monitorTimerListStr = " + monitorTimerListStr);
        List<MonitorTimer> monitorTimerList = new ArrayList<>();
        if(monitorTimerListStr != null && !monitorTimerListStr.isEmpty()){
            String[] itemArr = monitorTimerListStr.split(ITEM_SEPARATOR);
            if(itemArr.length > 0){
                for (String item : itemArr) {
                    Log.i(TAG, item);
                    monitorTimerList.add(new MonitorTimer(item));
                }
            }
        }
        return monitorTimerList;
    }
}
