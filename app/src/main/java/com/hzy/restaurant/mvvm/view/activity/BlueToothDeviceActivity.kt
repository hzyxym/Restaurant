package com.hzy.restaurant.mvvm.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.hzy.restaurant.R
import com.hzy.restaurant.base.BaseActivity
import com.hzy.restaurant.bean.BluetoothParameter
import com.hzy.restaurant.databinding.ActivityBluetoothBinding
import com.hzy.restaurant.utils.PermissionUtils
import com.hzy.restaurant.utils.PermissionUtils.PermissionListener
import com.hzy.restaurant.utils.ble.BluetoothDeviceAdapter

class BlueToothDeviceActivity : BaseActivity<ActivityBluetoothBinding>() {
    private val TAG: String = BlueToothDeviceActivity::class.java.simpleName
    private var lvDevices: ListView? = null
    private var adapter: BluetoothDeviceAdapter? = null

    //已配对列表
    private val pairedDevices: MutableList<BluetoothParameter> = ArrayList()

    //新设备列表
    private val newDevices: MutableList<BluetoothParameter> = ArrayList()
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var permissionUtils: PermissionUtils? = null
    private var manager: LocationManager? = null
    private var btn_search: Button? = null

    /**
     * changes the title when discovery is finished
     */
    private val mFindBlueToothReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Get the BluetoothDevice object from the Intent
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                // If it's already paired, skip it, because it's been listed
                // already
                val parameter = BluetoothParameter()
                val rssi = intent.extras!!.getShort(BluetoothDevice.EXTRA_RSSI).toInt() //获取蓝牙信号强度
                if (device != null && device.name != null) {
                    parameter.bluetoothName = device.name
                } else {
                    parameter.bluetoothName = "unKnow"
                }
                parameter.bluetoothMac = device!!.address
                parameter.bluetoothStrength = rssi.toString() + ""
                Log.e(TAG, "\nBlueToothName:\t" + device.name + "\nMacAddress:\t" + device.address + "\nrssi:\t" + rssi)
                if (device.bondState != BluetoothDevice.BOND_BONDED) { //未配对
                    for (p in newDevices) {
                        if (p.bluetoothMac == parameter.bluetoothMac) { //防止重复添加
                            return
                        }
                    }
                    newDevices.add(parameter)
                    newDevices.sortedWith(Signal())
                    adapter!!.notifyDataSetChanged()
                } else { //更新已配对蓝牙
                    for (i in pairedDevices.indices) {
                        if (pairedDevices[i].bluetoothMac == parameter.bluetoothMac) {
                            pairedDevices[i].bluetoothStrength = parameter.bluetoothStrength
                            adapter!!.notifyDataSetChanged()
                            return
                        }
                    }
                    pairedDevices.add(parameter)
                    adapter!!.notifyDataSetChanged()
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                == action
            ) {
                setProgressBarIndeterminateVisibility(false)
                setTitle(R.string.complete)
                Log.i("tag", "finish discovery" + (adapter!!.count - 2))
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                val bluetooth_state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                )
                if (bluetooth_state == BluetoothAdapter.STATE_OFF) { //关闭
                    finish()
                }
                if (bluetooth_state == BluetoothAdapter.STATE_ON) { //开启
                }
            }
        }
    }


    // 自定义比较器：按信号强度排序
    internal class Signal : Comparator<Any?> {
        override fun compare(object1: Any?, object2: Any?): Int { // 实现接口中的方法
            val p1 = object1 as BluetoothParameter? // 强制转换
            val p2 = object2 as BluetoothParameter?
            return p1!!.bluetoothStrength!!.compareTo(p2!!.bluetoothStrength!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Request progress bar
        //启用窗口特征
        requestWindowFeature(Window.FEATURE_PROGRESS)
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
        super.onCreate(savedInstanceState)
        setTitle(R.string.blue_label)
        initView()
        initBluetooth()
        initBroadcast()
    }

    override fun getViewBinding(): ActivityBluetoothBinding {
        return ActivityBluetoothBinding.inflate(layoutInflater)
    }


    /**
     * 搜索蓝牙
     */
    @SuppressLint("MissingPermission")
    fun searchBlueTooth() {
        btn_search!!.visibility = View.GONE
        title = getString(R.string.searching)
        setProgressBarIndeterminateVisibility(true)
        mBluetoothAdapter!!.startDiscovery()
    }

    /**
     * 初始化广播
     */
    private fun initBroadcast() {
        try {
            // Register for broadcasts when a device is discovered
            val filter = IntentFilter()
            filter.addAction(BluetoothDevice.ACTION_FOUND)
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            // Register for broadcasts when discovery has finished
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED) //蓝牙状态改变
            this.registerReceiver(mFindBlueToothReceiver, filter)
        } catch (e: Exception) {
        }
    }

    @SuppressLint("MissingPermission")
    private fun initView() {
        lvDevices = findViewById<View>(R.id.lv_devices) as ListView
        btn_search = findViewById<View>(R.id.btn_search) as Button
        btn_search!!.setOnClickListener { initBluetooth() }
        adapter = BluetoothDeviceAdapter(pairedDevices, newDevices, this@BlueToothDeviceActivity)
        lvDevices!!.adapter = adapter
        lvDevices!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> //点击已配对设备、新设备title不响应
            if (position == 0 || position == pairedDevices.size + 1) {
                return@OnItemClickListener
            }
            var mac: String? = null
            mac = if (position <= pairedDevices.size) { //点击已配对设备列表
                pairedDevices[position - 1].bluetoothMac
            } else { //点击新设备列表
                newDevices[position - 2 - pairedDevices.size].bluetoothMac
            }
            mBluetoothAdapter!!.cancelDiscovery()

            // Create the result Intent and include the MAC address
            val intent = Intent()
            intent.putExtra(EXTRA_DEVICE_ADDRESS, mac)
            // Set result and finish this Activity
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    @SuppressLint("MissingPermission")
    private fun initBluetooth() {
        // Get the local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported by the device", Toast.LENGTH_LONG).show()
        } else {
            // If BT is not on, request that it be enabled.
            // setupChat() will then be called during onActivityResult
            if (!mBluetoothAdapter!!.isEnabled) {
                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
            } else {
                manager = getSystemService(LOCATION_SERVICE) as LocationManager
                permissionUtils = PermissionUtils(this@BlueToothDeviceActivity)
                permissionUtils!!.requestPermissions(
                    getString(R.string.permission),
                    object : PermissionListener {
                        override fun doAfterGrand(vararg permission: String) {
                            if ((Build.VERSION.SDK_INT >= 29) && !manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                val alertDialog = AlertDialog.Builder(this@BlueToothDeviceActivity)
                                    .setTitle(getString(R.string.tip))
                                    .setMessage(getString(R.string.gps_permission))
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setPositiveButton(
                                        getString(R.string.ok)
                                    ) { dialogInterface, i ->

                                        //添加"Yes"按钮
                                        val intent = Intent()
                                        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivityForResult(intent, REQUEST_ENABLE_GPS)
                                    }
                                    .create()
                                alertDialog.show()
                            } else {
                                searchBlueTooth()
                            }
                        }

                        override fun doAfterDenied(vararg permission: String) {
                            for (p in permission) {
                                when (p) {
                                    Manifest.permission.ACCESS_FINE_LOCATION -> {}
                                }
                            }
                        }
                    }, Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                // bluetooth is opened
                initBluetooth()
            } else {
                // bluetooth is not open
                Toast.makeText(this, R.string.bluetooth_is_not_enabled, Toast.LENGTH_SHORT).show()
                finish()
            }
        } else if (requestCode == REQUEST_ENABLE_GPS) {
            if (resultCode == RESULT_OK) {
                // bluetooth is opened
                initBluetooth()
            } else {
                // bluetooth is not open
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        try {
            // Make sure we're not doing discovery anymore
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter!!.cancelDiscovery()
            }
            // Unregister broadcast listeners
            unregisterReceiver(mFindBlueToothReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val EXTRA_DEVICE_ADDRESS: String = "address"
        const val REQUEST_ENABLE_BT: Int = 2
        const val REQUEST_ENABLE_GPS: Int = 3
    }
}