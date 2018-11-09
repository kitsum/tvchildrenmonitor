package com.quickthink.tvchildrenmonitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import static com.quickthink.tvchildrenmonitor.Constants.MONITORTIMEREDIT_EXTRA_MONITORTIMERINDEX;
import static com.quickthink.tvchildrenmonitor.Constants.MONITORTIMEREDIT_EXTRA_MONITORTIMERSTR;

public class SettingActivity extends Activity {
    private static final String TAG = SettingActivity.class.getSimpleName();

    private static final int EDIT_MONITOR_TIMER_REQUEST = 101;

    private static final String TIME_CONNECTOR = "--";
    private static final String TIME_SPACE = " ";
    private static final String[] ACTIVE_DAYS = {"", "周日", "周一", "周二", "周三", "周四", "周五","周六"};

    private final MonitorConfigManager monitorConfigManager = MonitorConfigManager.getInstance();

    private ToggleButton enableMonitorBtn;
    private SeekBar monitorPeriodsSB;
    private SeekBar breakDurationSB;
    private ListView monitorTimersLV;
    private ArrayAdapter<String> mtAdapter;
    private Button saveMonitorConfigBtn;
    private Button addMonitorTimerBtn;
    private TextView monitorPeriodsTxt;
    private TextView breakDurationTxt;

    private MonitorConfig monitorConfig;
    private ITimerService timerService;
    private boolean isTimerServiceBound;

    private SeekBar.OnSeekBarChangeListener  onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.i(TAG, "seekBar:" + seekBar.getId() + ", progress = " + progress);
            String txt = formatProgressString(progress);
            if(monitorPeriodsTxt != null && seekBar.equals(monitorPeriodsSB)){
                monitorPeriodsTxt.setText(txt);
            }
            else if(breakDurationTxt != null && seekBar.equals(breakDurationSB)){
                breakDurationTxt.setText(txt);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected");
            timerService = (TimerService.TimerServiceBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected");
            isTimerServiceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        enableMonitorBtn = findViewById(R.id.setting_enable_monitor);
        monitorPeriodsSB = findViewById(R.id.setting_monitor_periods);
        breakDurationSB = findViewById(R.id.setting_break_duration);
        monitorTimersLV = findViewById(R.id.setting_monitor_timers);
        saveMonitorConfigBtn = findViewById(R.id.setting_save_monitor_config);
        addMonitorTimerBtn = findViewById(R.id.setting_add_monitor_timer);
        monitorPeriodsTxt = findViewById(R.id.setting_monitor_periods_txt);
        breakDurationTxt = findViewById(R.id.setting_break_duration_txt);
        mtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        monitorTimersLV.setAdapter(mtAdapter);
        addMonitorTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "click add button");
                startMonitorTimerEditActivity(-1, null);
            }
        });
        saveMonitorConfigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "click save button");
                boolean retVal = saveMonitorConfig();
                String msg = retVal ? getString(R.string.toast_save_success) : getString(R.string.toast_save_fail);
                Toast.makeText(SettingActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
        monitorTimersLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(position);
            }
        });
        monitorPeriodsSB.setOnSeekBarChangeListener(onSeekBarChangeListener);
        breakDurationSB.setOnSeekBarChangeListener(onSeekBarChangeListener);
        monitorConfigManager.init(getApplicationContext());
        monitorConfig = monitorConfigManager.getMonitorConfig();
        isTimerServiceBound = false;
        bindTimerService();
    }

    private void onListItemClick(int position){
        Log.i(TAG, "onListItemClick position = " + position);
        AlertDialog dialog = createDialog(position);
        dialog.show();
    }

    private void startMonitorTimerEditActivity(int index, MonitorTimer monitorTimer){
        Intent intent = new Intent();
        intent.setClass(SettingActivity.this, MonitorTimerEditActivity.class);
        String monitorTimerStr = monitorTimer != null ? monitorTimer.toString() : "";
        Log.i(TAG, "startMonitorTimerEditActivity index = " + index + "; monitorTimer = " + monitorTimerStr);
        intent.putExtra(MONITORTIMEREDIT_EXTRA_MONITORTIMERSTR, monitorTimerStr);
        intent.putExtra(MONITORTIMEREDIT_EXTRA_MONITORTIMERINDEX, index);
        startActivityForResult(intent, EDIT_MONITOR_TIMER_REQUEST);
    }

    private AlertDialog createDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.setting_edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "click edit button");
                if(monitorConfig != null) {
                    List<MonitorTimer> monitorTimers = monitorConfig.getMonitorTimerList();
                    if(monitorTimers != null && position >= 0 && position < monitorTimers.size()){
                        MonitorTimer monitorTimer = monitorTimers.get(position);
                        startMonitorTimerEditActivity(position, monitorTimer);
                    }
                }
            }
        });
        builder.setNegativeButton(R.string.setting_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "click delete button");
                if(monitorConfig != null) {
                    List<MonitorTimer> monitorTimers = monitorConfig.getMonitorTimerList();
                    if(monitorTimers != null && position >= 0 && position < monitorTimers.size()){
                        monitorTimers.remove(position);
                        initAdapter(monitorTimers);
                    }
                }
            }
        });
        builder.setNeutralButton(R.string.setting_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "click cancel button");
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    private void updateMonitorConfigFromView(){
        if(monitorConfig != null) {
            if (enableMonitorBtn != null) {
                monitorConfig.setEnableMonitor(enableMonitorBtn.isChecked());
            }
            if(monitorPeriodsSB != null){
                monitorConfig.setMonitorPeriods(monitorPeriodsSB.getProgress());
            }
            if(breakDurationSB != null){
                monitorConfig.setBreakDuration(breakDurationSB.getProgress());
            }
        }
    }

    private boolean saveMonitorConfig(){
        Log.i(TAG, "saveMonitorConfig");
        boolean retVal = false;
        updateMonitorConfigFromView();
        if(monitorConfig != null) {
            retVal = monitorConfigManager.UpdateMonitorConfig(monitorConfig);
            if(retVal){
                monitorConfig = monitorConfigManager.getMonitorConfig();
                initView();
                resetTimerService();
            }
        }
        return retVal;
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
        addMonitorTimerBtn.setOnClickListener(null);
        saveMonitorConfigBtn.setOnClickListener(null);
        monitorTimersLV.setOnItemClickListener(null);
        breakDurationSB.setOnSeekBarChangeListener(null);
        monitorPeriodsSB.setOnSeekBarChangeListener(null);
        onSeekBarChangeListener = null;
        monitorConfig = null;
        unbindService(serviceConnection);
        serviceConnection = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK && data != null){
            String monitorTimerStr = data.getStringExtra(MONITORTIMEREDIT_EXTRA_MONITORTIMERSTR);
            int index = data.getIntExtra(MONITORTIMEREDIT_EXTRA_MONITORTIMERINDEX, -1);
            if(monitorTimerStr != null && !monitorTimerStr.isEmpty() && monitorConfig != null){
                MonitorTimer newOne = new MonitorTimer(monitorTimerStr);
                List<MonitorTimer> monitorTimers = monitorConfig.getMonitorTimerList();
                if (monitorTimers == null) {
                    monitorTimers = new ArrayList<>();
                }
                if(index >= 0 && index < monitorTimers.size()){
                    monitorTimers.set(index, newOne);
                }
                else {
                    monitorTimers.add(newOne);
                }
                initAdapter(monitorTimers);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initAdapter(List<MonitorTimer> monitorTimers){
        if(mtAdapter != null && monitorTimers != null){
            mtAdapter.clear();
            for(MonitorTimer mt : monitorTimers){
                StringBuilder sb = new StringBuilder();
                sb.append(SimpleTimeFormater.toString(mt.getStartTime()));
                sb.append(TIME_CONNECTOR);
                sb.append(SimpleTimeFormater.toString(mt.getEndTime()));
                sb.append(TIME_SPACE);
                sb.append(TIME_SPACE);
                List activeDays = mt.getActiveDayList();
                if(activeDays != null && !activeDays.isEmpty()){
                    for(Object ad : activeDays){
                        sb.append(ACTIVE_DAYS[(int)ad]);
                        sb.append(TIME_SPACE);
                    }
                }
                mtAdapter.add(sb.toString());
            }
            mtAdapter.notifyDataSetChanged();
        }
    }

    private void initView(){
        Log.i(TAG, "initView");
        if(monitorConfig != null){
            if(enableMonitorBtn != null){
                enableMonitorBtn.setChecked(monitorConfig.getEnableMonitor());
            }
            if(monitorPeriodsSB != null){
                monitorPeriodsSB.setProgress(monitorConfig.getMonitorPeriods());
            }
            if(monitorPeriodsTxt != null){
                monitorPeriodsTxt.setText(formatProgressString(monitorConfig.getMonitorPeriods()));
            }
            if(breakDurationSB != null){
                breakDurationSB.setProgress(monitorConfig.getBreakDuration());
            }
            if(breakDurationTxt != null){
                breakDurationTxt.setText(formatProgressString(monitorConfig.getBreakDuration()));
            }

            initAdapter(monitorConfig.getMonitorTimerList());
        }
    }

    private String formatProgressString(int progress){
        String progressStr = "";
        StringBuilder sb = new StringBuilder();
        sb.append(progress);
        sb.append(getString(R.string.setting_time_unit));
        progressStr = sb.toString();
        return progressStr;
    }

    private void resetTimerService(){
        if(timerService != null){
            timerService.reset();
        }
    }

    private void bindTimerService(){
        if(!isTimerServiceBound) {
            startService(new Intent(this, TimerService.class));
            bindService(new Intent(this, TimerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
            isTimerServiceBound = true;
        }
    }
}
