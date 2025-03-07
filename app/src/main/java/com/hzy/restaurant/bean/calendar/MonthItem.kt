package com.hzy.restaurant.bean.calendar

import com.hzy.restaurant.utils.TimeUtils
import java.util.Calendar


/**
 * author : zhang
 * e-mail : 616852636@qq.com
 * time   : 2021/04/15
 * desc   :
 * version:
 */
class MonthItem(val time: Long, val isAll: Boolean) {
    fun isSelect(selectTime: Long): Boolean {
        return TimeUtils.getYyyyMM(time).equals(TimeUtils.getyyyyMMTime(selectTime), ignoreCase = true)
    }

    val isEnable: Boolean
        get() = time <= Calendar.getInstance().timeInMillis
}
