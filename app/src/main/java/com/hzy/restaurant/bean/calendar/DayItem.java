package com.hzy.restaurant.bean.calendar;


import com.hzy.restaurant.utils.TimeUtils;

import java.util.Calendar;

/**
 * author : zhang
 * e-mail : 616852636@qq.com
 * time   : 2021/04/15
 * desc   :
 * version:
 */
public class DayItem {

    private long time;

    public DayItem(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public boolean isSelect(long selectTime) {
        return TimeUtils.getyyyyMMddTime(time).equalsIgnoreCase(TimeUtils.getyyyyMMddTime(selectTime));
    }

    public boolean isEnable() {
        return time <= Calendar.getInstance().getTimeInMillis();
    }
}
