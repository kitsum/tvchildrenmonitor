package com.quickthink.tvchildrenmonitor;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jiesen.liu on 2017/12/12.
 */

public class MonitorTimer {
    private static final String TAG = MonitorTimer.class.getSimpleName();

    public static final int ACTIVE_NONE = 0;
    public static final int ACTIVE_SUNDAY = Calendar.SUNDAY;
    public static final int ACTIVE_MONDAY = Calendar.MONDAY;
    public static final int ACTIVE_TUESDAY = Calendar.TUESDAY;
    public static final int ACTIVE_WEDNESDAY = Calendar.WEDNESDAY;
    public static final int ACTIVE_THURSDAY = Calendar.THURSDAY;
    public static final int ACTIVE_FRIDAY = Calendar.FRIDAY;
    public static final int ACTIVE_SATURDAY = Calendar.SATURDAY;

    private static final String MODE_SEPARATOR = ",";
    private static final String FIELD_SEPARATOR = "|";

    private ArrayList activeDayList;
    private Date startTime;
    private Date endTime;

    public MonitorTimer(String monitorTimerStr){
        extractMonitorTimerStr(monitorTimerStr);
    }

    public MonitorTimer(ArrayList repeatModeList, Date startTime, Date endTime){
        this.activeDayList = repeatModeList;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setActiveDayList(ArrayList activeDayList){
        this.activeDayList = activeDayList;
    }

    public List getActiveDayList(){
        return activeDayList;
    }

    public void setStartTime(Date startTime){
        this.startTime = startTime;
    }

    public Date getStartTime(){
        return startTime;
    }

    public void setEndTime(Date endTime){
        this.endTime = endTime;
    }

    public Date getEndTime(){
        return endTime;
    }

    @Override
    public String toString(){
        String monitorTimerStr = formatMonitorTimerStr(activeDayList, startTime, endTime);
        return monitorTimerStr;
    }

    public boolean isValidTime(Date now){
        Log.d(TAG, "now  = " + now.toString());
        boolean retVal = false;
        if(now != null && startTime != null && endTime != null){
            if(activeDayList != null && !activeDayList.isEmpty()){
                Calendar nc = Calendar.getInstance();
                nc.setTime(now);
                int dayOfWeek = nc.get(Calendar.DAY_OF_WEEK);
                Log.d(TAG, "DAY_OF_WEEK : " + dayOfWeek);
                boolean isValidDay = false;
                for (Object m : activeDayList) {
                    if((int)m == dayOfWeek){
                        isValidDay = true;
                        break;
                    }
                }
                Log.d(TAG, "isValidDay = " + isValidDay);
                if(isValidDay){
                    Date nt = SimpleTimeFormater.toTime(SimpleTimeFormater.toString(now));
                    retVal = nt != null && nt.compareTo(startTime) >= 0 && nt.compareTo(endTime) <= 0;
                }
            }
        }
        return  retVal;
    }

    private String formatMonitorTimerStr(ArrayList repeatModeList, Date startTime, Date endTime){
        StringBuilder sb = new StringBuilder();
        if(repeatModeList != null && !repeatModeList.isEmpty()){
            int last = repeatModeList.size() - 1;
            for (int i = 0; i <= last; i++) {
                sb.append(repeatModeList.get(i));
                if(i < last){
                    sb.append(MODE_SEPARATOR);
                }
            }
            sb.append(FIELD_SEPARATOR);
        }
        if(startTime != null){
            String startStr = SimpleTimeFormater.toString(startTime);
            sb.append(startStr);
            sb.append(FIELD_SEPARATOR);
        }
        if(endTime != null){
            String endStr = SimpleTimeFormater.toString(endTime);
            sb.append(endStr);
            sb.append(FIELD_SEPARATOR);
        }
        String str = sb.toString();
        Log.d(TAG, "str = " + str);
        return str;
    }

    private void extractMonitorTimerStr(String monitorTimerStr){
        Log.d(TAG, "monitorTimerStr = " + monitorTimerStr);
        if(monitorTimerStr != null && !monitorTimerStr.isEmpty()){
            String[] filedArr = monitorTimerStr.split("\\" + FIELD_SEPARATOR);
            Log.d(TAG, "filedArr.length = " + filedArr.length);
            if(filedArr.length > 0){
                Log.d(TAG, "filedArr[0] = " + filedArr[0]);
                activeDayList = new ArrayList();
                String[] modeArr = filedArr[0].split("\\" + MODE_SEPARATOR);
                for (String m : modeArr) {
                    activeDayList.add(Integer.parseInt(m));
                }
            }
            if(filedArr.length > 1){
                Log.d(TAG, "filedArr[1] = " + filedArr[1]);
                startTime = SimpleTimeFormater.toTime(filedArr[1]);
                Log.d(TAG, startTime.toString());
            }
            if(filedArr.length > 2){
                Log.d(TAG, "filedArr[2] = " + filedArr[2]);
                endTime = SimpleTimeFormater.toTime(filedArr[2]);
                Log.d(TAG, endTime.toString());
            }
        }
        else {
            activeDayList = new ArrayList();
            activeDayList.add(ACTIVE_MONDAY);
            activeDayList.add(ACTIVE_TUESDAY);
            activeDayList.add(ACTIVE_WEDNESDAY);
            activeDayList.add(ACTIVE_THURSDAY);
            activeDayList.add(ACTIVE_FRIDAY);

            startTime = new Date();
            endTime = new Date();
        }
    }
}
