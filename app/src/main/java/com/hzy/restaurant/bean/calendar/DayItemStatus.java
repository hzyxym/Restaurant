package com.hzy.restaurant.bean.calendar;

import android.text.TextUtils;


/**
 * author : zhang
 * e-mail : 616852636@qq.com
 * time   : 2021/04/17
 * desc   :
 * version:
 */
public class DayItemStatus {

    /**
     * batteryTempStatus : 0
     * batteryVolStatus : 0
     * chartData :
     * mac : F0C77F196A80
     * osType : 1
     * recordDate : 2021-04-07
     * userId : 5
     */

    private int batteryTempStatus;
    private int batteryVolStatus;
    private String recordDate;
    private String mac;

    public DayItemStatus() {
    }

    public DayItemStatus(String recordDate) {
        this.recordDate = recordDate;
    }

    public DayItemStatus(int batteryTempStatus, int batteryVolStatus, String recordDate, String mac) {
        this.batteryTempStatus = batteryTempStatus;
        this.batteryVolStatus = batteryVolStatus;
        this.recordDate = recordDate;
        this.mac = mac;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getBatteryTempStatus() {
        return batteryTempStatus;
    }

    public void setBatteryTempStatus(int batteryTempStatus) {
        this.batteryTempStatus = batteryTempStatus;
    }

    public int getBatteryVolStatus() {
        return batteryVolStatus;
    }

    public void setBatteryVolStatus(int batteryVolStatus) {
        this.batteryVolStatus = batteryVolStatus;
    }


    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getYYYYMM() {
        if (TextUtils.isEmpty(recordDate)) {
            return "";
        }
        return recordDate.substring(0, 7);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DayItemStatus that = (DayItemStatus) o;

        return recordDate.equals(that.recordDate);
    }

    @Override
    public int hashCode() {
        return recordDate.hashCode();
    }
}
