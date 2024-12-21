package com.hzy.restaurant.utils

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.UriUtils
import com.hzy.restaurant.MainActivity
import com.hzy.restaurant.R
import com.hzy.restaurant.app.Constants
import com.hzy.restaurant.app.MyApp
import com.hzy.restaurant.utils.glide.GlideEngine
import com.luck.picture.lib.animators.AnimationType
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.utils.DateUtils
import org.greenrobot.eventbus.EventBus
import top.zibin.luban.CompressionPredicate
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by hzy in 2024/7/24
 * description:
 * */
object Utils {
    fun isOn(): Boolean {
        val adapter =
            (MyApp.instance.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        return null != adapter && adapter.isEnabled
    }

    fun openBluetooth(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(
                    MyApp.instance, Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                ActivityUtils.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                return true
            } else {
                return false
            }
        } else {
            val adapter = BluetoothAdapter.getDefaultAdapter()
            if (adapter != null) {
                return adapter.enable()
            }
        }
        return false
    }

//    fun startBleService(context: Context) {
//        try {
//            if (!isServiceRunning(BleService::class.java.name)) {
//                val intent = Intent(context, BleService::class.java)
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    context.startForegroundService(intent)
//                } else {
//                    context.startService(intent)
//                }
//            } else {
//                EventBus.getDefault().post(MsgEvent<Any>(Events.MSG_RESCAN))
//            }
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//            LogUtils.e("org.altbeacon.beacon.Region startBleService Exception:" + e.message)
//        }
//    }

    private fun isServiceRunning(className: String): Boolean {
        val am = MyApp.getApp().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = am.getRunningServices(0x7FFFFFFF)
        if (info == null || info.size == 0) return false
        for (aInfo in info) {
            if (className == aInfo.service.className) return true
        }
        return false
    }

    fun buildNotification(): Notification {
        val context: Context = MyApp.getApp()
        val builder = Notification.Builder(context)

        val intent = Intent(context, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        builder.setContentIntent(pendingIntent).setPriority(Notification.PRIORITY_MIN)
            .setSmallIcon(R.mipmap.ic_launcher)//todo

        builder.setStyle(
            Notification.BigTextStyle().bigText(context.getString(R.string.service_notify_content))
        )

        builder.setColor(context.resources.getColor(R.color.black))
        if (Build.VERSION.SDK_INT >= 26) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                context.packageName + ".Service",
                context.getString(R.string.ble_service_title),
                NotificationManager.IMPORTANCE_MIN
            )
            channel.setShowBadge(false)
            channel.setBypassDnd(true)
            manager.createNotificationChannel(channel)
            builder.setChannelId(context.packageName + ".Service")
        }
        return builder.build()
    }

    fun getSerial(mac: String): String {
        return mac.replace(":".toRegex(), "")
    }

    fun getMac(mac: String): String {
        if (TextUtils.isEmpty(mac) || mac.contains(":")) return mac
        val chars = mac.uppercase().toCharArray()
        val sb = StringBuilder()
        for (i in chars.indices) {
            sb.append(chars[i])
            if (i % 2 == 1 && i < chars.size - 1) {
                sb.append(":")
            }
        }
        return sb.toString()
    }

    fun getMainPendingIntent(): PendingIntent {
        val context: Context = MyApp.getApp()
        val intent = Intent(context, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        return pendingIntent
    }

    private var lastClickTime: Long = 0
    fun isFastClick(): Boolean {
        var flag = false
        val curClickTime = System.currentTimeMillis()
        if ((curClickTime - lastClickTime) <= 200) {
            flag = true
        }
        lastClickTime = curClickTime
        return flag
    }

    fun downloadFile(httpurl: String?, target: File): File? {
        FileUtils.createFileByDeleteOldFile(target)
        if (target.exists()) target.delete()
        var inputStream: InputStream? = null
        var fos: FileOutputStream? = null
        try {
            val url = URL(httpurl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.readTimeout = 5000
            conn.connectTimeout = 10000
            conn.connect()
            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val contentLength = conn.contentLength.toLong()
                Log.e("tag", "target.length=" + target.length())
                Log.e("tag", "contentLength=$contentLength")
                val buff = ByteArray(1024 * 1024 * 100)
                inputStream = conn.inputStream
                fos = FileOutputStream(target)
                var read: Int
                while ((inputStream.read(buff).also { read = it }) != -1) {
                    fos.write(buff, 0, read)
                    fos.flush()
                }
                Log.e("tag", "文件下载成功！")
                return target
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("tag", "downloadFile: " + e.message)
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        Log.e("TAG", "文件下载失败！")
        return null
    }

    /**
     * 文件转为二进制数组
     * 等价于fileToBin
     * @param file
     * @return
     */
    fun getFileToByte(file: File?): ByteArray {
        val by: ByteArray
        try {
            val inputStream: InputStream = FileInputStream(file)
            val byteStream = ByteArrayOutputStream()
            val bb = ByteArray(2048)
            var ch: Int
            ch = inputStream.read(bb)
            while (ch != -1) {
                byteStream.write(bb, 0, ch)
                ch = inputStream.read(bb)
            }
            by = byteStream.toByteArray()
        } catch (ex: Exception) {
            throw RuntimeException("transform file into bin Array error", ex)
        }
        return by
    }

    /**
     * 判断手机是否安装微信
     *
     * @param context
     * @return
     */
    private fun isWechatAvailable(context: Context): Boolean {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage("com.tencent.mm")
        return intent != null // 如果获取的 Intent 不为空，说明微信已安装
    }


    fun getFilePrefix(): String {
        return Constants.APP_NAME + "_Android"
    }


    fun isContent(url: String): Boolean {
        if (TextUtils.isEmpty(url)) {
            return false
        }
        return url.startsWith("content://")
    }

    fun isLocServiceEnable(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return gps || network
    }

    fun openAlum(
        activity: Activity?,
        limit: Int,
        selectionData: List<LocalMedia>?,
        listener: OnResultCallbackListener<LocalMedia>?,
    ) {
        PictureSelector.create(activity)
            .openGallery(SelectMimeType.ofImage()) // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .setImageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
            .setCompressEngine(ImageFileCompressEngine())
            .setLanguage(LanguageConfig.CHINESE) // 设置语言，默认中文
            .isPageStrategy(false) // 是否开启分页策略 & 每页多少条；默认开启
            .setRecyclerAnimationMode(AnimationType.DEFAULT_ANIMATION) // 列表动画效果
            .isMaxSelectEnabledMask(true) // 选择数到了最大阀值列表是否启用蒙层效果
            .setMaxSelectNum(limit) // 最大图片选择数量
            .setMinSelectNum(1) // 最小选择数量
            .setImageSpanCount(4) // 每行显示个数
            .isEmptyResultReturn(false) // 未选择数据时点击按钮是否可以返回
            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) // 设置相册Activity方向，不设置默认使用系统
            .setSelectionMode(if (limit == 1) SelectModeConfig.SINGLE else SelectModeConfig.MULTIPLE) // 多选 or 单选
            .isDirectReturnSingle(true) // 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
            .isPreviewImage(true) // 是否可预览图片
            .isPreviewVideo(true) // 是否可预览视频
            .isDisplayCamera(true) // 是否显示拍照按钮
            .isPreviewZoomEffect(true) // 图片列表点击 缩放效果 默认true
            .isCameraRotateImage(true).isGif(false) // 是否显示gif图片
            .isOpenClickSound(false) // 是否开启点击声音
            .setSelectedData(selectionData) // 是否传入已选图片
            .forResult(listener)
    }

    /**
     * 自定义压缩
     */
    class ImageFileCompressEngine : CompressFileEngine {
        override fun onStartCompress(
            context: Context, source: ArrayList<Uri>, call: OnKeyValueResultCallbackListener,
        ) {
            Luban.with(context).load(source).ignoreBy(100).setRenameListener { filePath ->
                val indexOf = filePath.lastIndexOf(".")
                val postfix = if (indexOf != -1) filePath.substring(indexOf) else ".jpg"
                DateUtils.getCreateFileName("CMP_") + postfix
            }.filter(CompressionPredicate { path ->
                if (PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path)) {
                    return@CompressionPredicate true
                }
                !PictureMimeType.isUrlHasGif(path)
            }).setCompressListener(object : OnNewCompressListener {
                override fun onStart() {
                }

                override fun onSuccess(source: String, compressFile: File) {
                    call.onCallback(source, compressFile.absolutePath)
                }

                override fun onError(source: String, e: Throwable) {
                    call.onCallback(source, null)
                }
            }).launch()
        }
    }

    // 調用系統方法多文件分享
    fun shareFile(context: Context, fileList: List<File>?) {
        if (fileList == null || fileList.size == 0) {
            ToastUtils.showShort("fileList == null || fileList.size() == 0")
            return
        }
        val uris = java.util.ArrayList<Uri>()
        for (file in fileList) {
            if (file.exists()) {
                uris.add(UriUtils.file2Uri(file))
            }
        }
        if (uris.size == 0) {
            ToastUtils.showShort("分享文件不存在")
            return
        }
        val share = Intent(Intent.ACTION_SEND_MULTIPLE)
        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        share.setType("txt/*")
        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(share, context.getString(R.string.share)))
    }

    // 調用系統方法分享文件
    fun exportExcelFile(context: Context, file: File?) {
        if (null != file && file.exists()) {
            val share = Intent(Intent.ACTION_SEND)
            share.putExtra(Intent.EXTRA_STREAM, UriUtils.file2Uri(file))
            share.setType("application/vnd.ms-excel")
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(Intent.createChooser(share, context.getString(R.string.share)))
        } else {
            Toast.makeText(context, context.getString(R.string.export_fail), Toast.LENGTH_SHORT)
                .show()
        }
    }
}