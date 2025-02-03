package com.hzy.restaurant.utils.ext

import android.view.View
import androidx.lifecycle.LifecycleOwner
import java.util.Locale


/**
 * Created by hzy in 2023/5/15
 * description:
 * */
/***
 * 设置延迟时间的View扩展
 * @param delay Long 延迟时间，默认600毫秒
 * @return T
 */
fun <T : View> T.withTrigger(delay: Long = 600): T {
    triggerDelay = delay
    return this
}

/***
 * 点击事件的View扩展
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun <T : View> T.click(block: (T) -> Unit) = setOnClickListener {
    block(it as T)
}

/***
 * 带延迟过滤的点击事件View扩展
 * @param time Long 延迟时间，默认600毫秒
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun <T : View> T.clickWithTrigger(time: Long = 600, block: (T) -> Unit) {
    triggerDelay = time
    setOnClickListener {
        if (clickEnable()) {
            block(it as T)
        }
    }
}

private var <T : View> T.triggerLastTime: Long
    get() = if (getTag(1123460103) != null) getTag(1123460103) as Long else -601
    set(value) {
        setTag(1123460103, value)
    }

private var <T : View> T.triggerDelay: Long
    get() = if (getTag(1123461123) != null) getTag(1123461123) as Long else 600
    set(value) {
        setTag(1123461123, value)
    }

private fun <T : View> T.clickEnable(): Boolean {
    var flag = false
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= triggerDelay) {
        flag = true
        triggerLastTime = currentClickTime
    } else {
        triggerLastTime = currentClickTime
    }
    return flag
}

/***
 * 带延迟过滤的点击事件监听，见[View.OnClickListener]
 * 延迟时间根据triggerDelay获取：600毫秒，不能动态设置
 */
interface OnLazyClickListener : View.OnClickListener {

    override fun onClick(v: View?) {
        if (v?.clickEnable() == true) {
            onLazyClick(v)
        }
    }

    fun onLazyClick(v: View)
}

/**
 * 多控件点击事件
 */
fun LifecycleOwner.setViewClick(listener: View.OnClickListener, vararg views: View) {
    for (it in views) {
        it.setOnClickListener(listener)
    }
}

/**
 * 多控件点击事件(防误触)
 */
fun LifecycleOwner.setViewClickDelay(
    listener: View.OnClickListener,
    vararg views: View,
    time: Long = 400
) {
    for (view in views) {
        view.triggerDelay = time
        view.setOnClickListener {
            if (view.clickEnable()) {
                listener.onClick(view)
            }
        }
    }
}

fun String.littleEndian(): String {
    // 将 16 进制字符串拆成字节对（每两个字符为一组）
    val bytes = this.chunked(2)
    // 将字节对反转以实现从小端序到大端序的转换
    val reversedBytes = bytes.reversed()
    // 将字节对重新拼接为大端序的 16 进制字符串
    return reversedBytes.joinToString("")
}

fun String.hexToAscii(): String? {
    //检查卡号是否全是0
    if (this.all { it == '0' }) return null

    // 检查 hexString 的长度是否为偶数
    if (this.length % 2 != 0) {
        return null
    }
    // 将 16 进制字符串转换为字节数组
    val output = StringBuilder()
    for (i in this.indices step 2) {
        val hexPair = this.substring(i, i + 2)
        val decimal = hexPair.toInt(16)  // 将 16 进制转换为 10 进制整数
        output.append(decimal.toChar())  // 将整数转换为 ASCII 字符
    }
    return output.toString()
}

fun String.padHexString(totalBytes: Int): String {
    // 计算需要补齐的总字符数（每个字节2个字符）
    val totalChars = totalBytes * 2
    // 去掉前导的 "0x"（如果有）
    var cleanHex = this.removePrefix("0x")

    // 如果字符长度为奇数，说明有单位数字符，后面补一个 '0'
    if (cleanHex.length % 2 != 0) {
        val lastByte = cleanHex.substring(cleanHex.length - 1).padStart(2, '0')
        cleanHex = cleanHex.dropLast(1) + lastByte
    }

    // 如果当前的字符长度小于总字符数，则在后面补 '0'
    return cleanHex.padEnd(totalChars, '0')
}

fun Int.toBinaryString(bitLength: Int): String {
    return this.toString(2).padStart(bitLength, '0')
}

/**
 * 对齐两位数
 */
fun Number.padTwo(): String {
    return this.toString().padStart(2, '0')
}

/**
 * 保留两位小数
 * 	超过100保留一位小数
 * 	超过1000取整
 */
fun Float.padAlign(): String {
    return if (this > 1000) {
        String.format(Locale.ENGLISH, "%.0f", this)
    } else if (this > 100) {
        String.format(Locale.ENGLISH, "%.1f", this)
    } else {
        String.format(Locale.ENGLISH, "%.2f", this)
    }
}

/**
 * 匹配一个可选的点号 .，紧跟着零个或多个零 0，并且这些字符出现在字符串的结尾;
 * 3.0->3  4.10->4.1
 */
fun String.removeEnd0(): String {
    return this.replace(Regex("\\.?0*$"), "")
}


/**
 * Float去掉小数末位的0。 13.0->13
 */
fun Double.trimZero(): String {
    return this.toBigDecimal().stripTrailingZeros().toPlainString()
}