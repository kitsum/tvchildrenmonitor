package com.quickthink.tvchildrenmonitor;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by jiesen.liu on 2017/12/13.
 */
public class MonitorTimerTest {
    @Test
    public void getRepeatModeList() throws Exception {
    }

    @Test
    public void getStartTime() throws Exception {
    }

    @Test
    public void isValidTime() throws Exception {
        String monitorTimerStr = "2,3,5,6|12:00:00|17:00:00";
        MonitorTimer monitorTimer = new MonitorTimer(monitorTimerStr);
        assertFalse(monitorTimer.isValidTime(new Date()));
    }

}