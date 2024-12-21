package com.dc.lg_ac012.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import com.hzy.restaurant.app.Constants
import com.hzy.restaurant.app.MyApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun getApplication(@ApplicationContext app: Context): MyApp {
        return app as MyApp
    }

    @Singleton
    @Provides
    fun provideBleDataOperation(): BleDataOperation {
        return BleDataOperation.instance
    }

    @Singleton
    @Provides
    fun getBluetoothManager(@ApplicationContext context: Context): BluetoothManager {
        return context.getSystemService(BluetoothManager::class.java)
    }

    @Singleton
    @Provides
    fun getBluetoothAdapter(bluetoothManager: BluetoothManager): BluetoothAdapter {
        return bluetoothManager.adapter
    }

    @Singleton
    @Provides
    fun getScanSettings(): ScanSettings {
        val settingBuilder = ScanSettings.Builder()
        settingBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        settingBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        return settingBuilder.build()
    }

    @Singleton
    @Provides
    fun getScanFilter(): List<ScanFilter> {
        //蓝牙设备过滤条件
        val scanFilterList = arrayListOf<ScanFilter>()
        //过滤蓝牙设备
        val builder1 = ScanFilter.Builder()
        builder1.setDeviceName(Constants.BLE_NAME1)
//        builder1.setDeviceName("BM6")
//        builder1.setServiceUuid(ParcelUuid(UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")))
        val scanFilter1 = builder1.build()
        scanFilterList.add(scanFilter1)

        //过滤除了BM6的蓝牙设备
//        val builder2 = ScanFilter.Builder()
//        builder2.setDeviceName(Constants.BLE_NAME2)
//        val scanFilter2 = builder2.build()
//        scanFilterList.add(scanFilter2)
        return scanFilterList
    }
}