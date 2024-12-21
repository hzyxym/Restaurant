package com.hzy.restaurant.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Environment
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.LogUtils
import com.hzy.restaurant.utils.Utils
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp
class MyApp : Application() {
    private var mFinalCount = 0
    companion object {
        lateinit var instance: MyApp
        lateinit var logPath: String
        lateinit var crashPath: String
        fun getApp(): MyApp {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initPath()
        //注册lifecycle监听
        registerActivityLifecycleCallbacks(lifecycleCallbacks)
        //初始化日志
        initLogUtils()
    }

    private fun initPath() {
        logPath = filesDir.absolutePath + File.separator + Environment.DIRECTORY_DOCUMENTS + "/Log"
        crashPath =
            filesDir.absolutePath + File.separator + Environment.DIRECTORY_DOCUMENTS + "/Crash"
    }

    //初始化日志工具
    private fun initLogUtils() {
        LogUtils.getConfig()
            .setLogHeadSwitch(false)
            .setBorderSwitch(false)
            .setDir(logPath)
            .setSaveDays(3)
            .setLogSwitch(true)
            .setLog2FileSwitch(true)
            .setFilePrefix(Utils.getFilePrefix())
            .setConsoleSwitch(AppUtils.isAppDebug())
            .setGlobalTag(Constants.APP_NAME)
        CrashUtils.init(crashPath)
    }

    fun isBackGround(): Boolean {
        return mFinalCount == 0
    }

    private val lifecycleCallbacks: ActivityLifecycleCallbacks =
        object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
                mFinalCount++
                //如果mFinalCount ==1，说明是从后台到前台
                if (mFinalCount == 1) {
                    //说明从后台回到了前台
                }
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
                mFinalCount--
                if (mFinalCount == 0) {
                    //说明从前台回到了后台
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        }
}