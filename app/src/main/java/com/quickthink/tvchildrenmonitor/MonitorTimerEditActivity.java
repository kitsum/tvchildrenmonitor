package com.quickthink.tvchildrenmonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.quickthink.tvchildrenmonitor.Constants.MONITORTIMEREDIT_EXTRA_MONITORTIMERINDEX;
import static com.quickthink.tvchildrenmonitor.Constants.MONITORTIMEREDIT_EXTRA_MONITORTIMERSTR;

public class MonitorTimerEditActivity extends Activity {
    private static final String TAG = MonitorTimerEditActivity.class.getSimpleName();

    private final List<CheckBox> dayCBs = new ArrayList<>();

    private TimePicker startTimePicker;
    private TimePicker endTimePicker;
    private Button saveBtn;
    private Button cancelBtn;
    private CheckBox atMonCB;
    private CheckBox atTuesCB;
    private CheckBox atWednesCB;
    private CheckBox atThursCB;
    private CheckBox atFriCB;
    private CheckBox atSaturCB;
    private CheckBox atSunCB;

    private MonitorTimer monitorTimer;
    private int monitorTimerIndex;

    private TimePicker.OnTimeChangedListener onTimeChangedListener = new TimePicker.OnTimeChangedListener(){

        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            StringBuilder builder = new StringBuilder();
            builder.append("TimePicker ");
            builder.append(view.getId());
            builder.append(": hourOfDay = ");
            builder.append(hourOfDay);
            builder.append("; minute = ");
            builder.append(minute);
            Log.i(TAG, builder.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_timer_edit);
        startTimePicker = findViewById(R.id.setting_monitor_start_time);
        endTimePicker = findViewById(R.id.setting_monitor_end_time);
        saveBtn = findViewById(R.id.setting_save_monitor_timer);
        cancelBtn = findViewById(R.id.setting_cancel);
        atMonCB = findViewById(R.id.setting_monitor_active_Monday);
        atTuesCB = findViewById(R.id.setting_monitor_active_Tuesday);
        atWednesCB = findViewById(R.id.setting_monitor_active_Wednesdays);
        atThursCB = findViewById(R.id.setting_monitor_active_Thursday);
        atFriCB = findViewById(R.id.setting_monitor_active_Friday);
        atSaturCB = findViewById(R.id.setting_monitor_active_Saturday);
        atSunCB = findViewById(R.id.setting_monitor_active_Sunday);

        if(startTimePicker != null){
            startTimePicker.setIs24HourView(true);
            startTimePicker.setOnTimeChangedListener(onTimeChangedListener);
        }

        if(endTimePicker != null){
            endTimePicker.setIs24HourView(true);
            endTimePicker.setOnTimeChangedListener(onTimeChangedListener);
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "click save button");
                updateMonitorTimerFromView();
                returnResult();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "click cancel button");
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        dayCBs.add(null);
        dayCBs.add(atSunCB);
        dayCBs.add(atMonCB);
        dayCBs.add(atTuesCB);
        dayCBs.add(atWednesCB);
        dayCBs.add(atThursCB);
        dayCBs.add(atFriCB);
        dayCBs.add(atSaturCB);

        Intent intent = getIntent();
        String monitorTimerStr = intent.getStringExtra(MONITORTIMEREDIT_EXTRA_MONITORTIMERSTR);
        monitorTimer = new MonitorTimer(monitorTimerStr);
        monitorTimerIndex  = intent.getIntExtra(MONITORTIMEREDIT_EXTRA_MONITORTIMERINDEX, -1);
    }

    private void updateMonitorTimerFromView(){
        Log.i(TAG, "updateMonitorTimerFromView");
        if(monitorTimer != null){
            if(startTimePicker != null){
                monitorTimer.setStartTime(SimpleTimeFormater.toTime(startTimePicker.getCurrentHour(), startTimePicker.getCurrentMinute(),0));
            }
            if(endTimePicker != null){
                monitorTimer.setEndTime(SimpleTimeFormater.toTime(endTimePicker.getCurrentHour(), endTimePicker.getCurrentMinute(),0));
            }
            ArrayList acDays = new ArrayList();
            for(int i = 1; i < dayCBs.size(); i++){
                CheckBox cb = dayCBs.get(i);
                if(cb.isChecked()){
                    acDays.add(i);
                }
            }
            monitorTimer.setActiveDayList(acDays);
        }
    }

    private void returnResult(){
        Log.i(TAG, "returnResult");
        Intent result = new Intent();
        String monitorTimerStr = monitorTimer != null ? monitorTimer.toString() : "";
        Log.d(TAG, "monitorTimerStr = " + monitorTimerStr);
        result.putExtra(MONITORTIMEREDIT_EXTRA_MONITORTIMERSTR, monitorTimerStr);
        result.putExtra(MONITORTIMEREDIT_EXTRA_MONITORTIMERINDEX, monitorTimerIndex);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        initView();
        super.onResume();
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
        monitorTimer = null;
        if(startTimePicker != null){
            startTimePicker.setOnTimeChangedListener(null);
            startTimePicker = null;
        }
        if(endTimePicker != null){
            endTimePicker.setOnTimeChangedListener(null);
            endTimePicker = null;
        }
        super.onDestroy();
    }

    private void initView(){
        if(monitorTimer != null){
            Date startTime = monitorTimer.getStartTime();
            Date endTime = monitorTimer.getEndTime();
            if(startTimePicker != null &&  startTime != null){
                startTimePicker.setCurrentHour(startTime.getHours());
                startTimePicker.setCurrentMinute(startTime.getMinutes());
            }
            if(endTimePicker != null && endTime != null){
                endTimePicker.setCurrentHour(endTime.getHours());
                endTimePicker.setCurrentMinute(endTime.getMinutes());
            }
            List acDays = monitorTimer.getActiveDayList();
            if(acDays != null && !acDays.isEmpty()) {
                for (int i = 1; i < dayCBs.size(); i++) {
                    CheckBox cb = dayCBs.get(i);
                    if (cb != null && acDays.contains(i)) {
                        cb.setChecked(true);
                    }
                }
            }
        }
    }
}
