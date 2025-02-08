package com.hzy.restaurant.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.hzy.restaurant.app.MyApp

/**
 * Created by hzy in 2024/8/30
 * description: android权限工具类
 * */
object PermissionsHelper {
    //获取蓝牙连接必要权限集
    fun getBlePermissionsNeeded(): MutableList<String> {
        val permissions = mutableListOf<String>()
        permissions.addAll(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.addAll(arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT))
        }
        return permissions
    }

    //获取蓝牙连接必要权限集
    fun getBluetoothPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf()
        }
    }

    //获取定位权限组
    fun getLocationPermissions() = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    //是否有权限（单个）
    fun isGranted(permissionString: String): Boolean {
        return (ContextCompat.checkSelfPermission(MyApp.instance, permissionString) == PackageManager.PERMISSION_GRANTED)
    }

    //是否有权限(多个)
    fun isGranted(permissions: Array<String>): Boolean {
        permissions.forEach {
            if (!isGranted(it)) {
                return false
            }
        }
        return true
    }
}