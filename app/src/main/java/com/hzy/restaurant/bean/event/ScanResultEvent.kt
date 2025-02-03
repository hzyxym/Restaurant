package com.hzy.restaurant.bean.event

import android.bluetooth.le.ScanResult

/**
 * Created by hzy in 2023/5/15
 * description: 蓝牙扫描结果
 * */
data class ScanResultEvent(val callbackType: Int = 1, val result: ScanResult? = null, val errorCode: Int? = null)