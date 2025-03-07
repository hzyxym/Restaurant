package com.hzy.restaurant.bean.calendar

/**
 * author : zhang
 * e-mail : 616852636@qq.com
 * time   : 2021/11/02
 * desc   :
 * version:
 */
class MonthItemStatus {
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as MonthItemStatus
        return month == that.month
    }

    override fun hashCode(): Int {
        return month.hashCode()
    }

    /**
     * id :
     * mac : B4:BC:7C:0A:E0:D2
     * month : 2021-06
     * updateTime : null
     * userId : 4
     * year : 2021
     */
    var id: String? = null
    var mac: String? = null
    var month: String? = null
    var updateTime: Any? = null
    var userId: Int = 0
    var year: String? = null
    var day: String? = null

    constructor()

    constructor(month: String?) {
        this.month = month
    }

    constructor(month: String?, year: String?) {
        this.month = month
        this.year = year
    }
}
