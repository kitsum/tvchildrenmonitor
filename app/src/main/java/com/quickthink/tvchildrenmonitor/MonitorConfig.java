package com.quickthink.tvchildrenmonitor;

import java.util.List;

/**
 * Created by jiesen.liu on 2017/12/12.
 */

public class MonitorConfig {
    private boolean enableMonitor;
    private int monitorPeriods;//min
    private int breakDuration;//min
    private List<MonitorTimer> monitorTimerList;

    public MonitorConfig(boolean enableMonitor, int monitorPeriods, int breakDuration, List<MonitorTimer> monitorTimerList){
        this.enableMonitor = enableMonitor;
        this.monitorPeriods = monitorPeriods;
        this.breakDuration = breakDuration;
        this.monitorTimerList = monitorTimerList;
    }

    public int getMonitorPeriods() {
        return monitorPeriods;
    }

    public void setMonitorPeriods(int monitorPeriods) {
        this.monitorPeriods = monitorPeriods;
    }

    public int getBreakDuration() {
        return breakDuration;
    }

    public void setBreakDuration(int breakDuration) {
        this.breakDuration = breakDuration;
    }

    public boolean getEnableMonitor() {
        return enableMonitor;
    }

    public void setEnableMonitor(boolean enableMonitor) {
        this.enableMonitor = enableMonitor;
    }

    public List<MonitorTimer> getMonitorTimerList(){
        return monitorTimerList;
    }
}
