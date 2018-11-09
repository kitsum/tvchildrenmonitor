package com.quickthink.tvchildrenmonitor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jiesen.liu on 2017/12/14.
 */

public final class SimpleTimeFormater {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public static Date toTime(String timeStr){
        Date date = null;
        try {
            date = simpleDateFormat.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String toString(Date date){
        String retVal = String.format("%tT", date);
        return retVal;
    }

    public static Date toTime(int hour, int minute, int second){
        String str = String.format("%02d:%02d:%02d", hour, minute, second);
        Date retVal = toTime(str);
        return retVal;
    }
}
