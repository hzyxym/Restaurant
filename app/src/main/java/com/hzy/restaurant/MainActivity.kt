package com.hzy.restaurant

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.view.Gravity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.gprinter.bean.PrinterDevices
import com.gprinter.utils.CallbackListener
import com.gprinter.utils.LogUtils
import com.hzy.restaurant.base.BaseActivity
import com.hzy.restaurant.bean.Order
import com.hzy.restaurant.databinding.ActivityMainBinding
import com.hzy.restaurant.mvvm.vm.MainViewModel
import com.hzy.restaurant.utils.ActivityResultLauncherCompat
import com.hzy.restaurant.utils.PermissionsHelper
import com.hzy.restaurant.utils.Utils
import com.hzy.restaurant.utils.dialog.SingleTipDialog
import com.hzy.restaurant.utils.ext.initMain
import com.hzy.restaurant.utils.printer.PrintContent
import com.hzy.restaurant.utils.printer.ThreadPoolManager
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), CallbackListener {
    private val vm: MainViewModel by viewModels()
    private val launcher =
        ActivityResultLauncherCompat(this, ActivityResultContracts.StartActivityForResult())
    private val permissionsLauncher =
        ActivityResultLauncherCompat(this, ActivityResultContracts.RequestMultiplePermissions())
    private val permissionsNeeded = PermissionsHelper.getBlePermissionsNeeded().toTypedArray()
    private var permissionsTipDialog: SingleTipDialog? = null
    private var bleDialog: SingleTipDialog? = null
    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initLocal() {
        super.initLocal()
        setToolsBarVisible(false)
        initNav()
        if (!PermissionsHelper.isGranted(permissionsNeeded)) {
            showAllPermissionDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Utils.isOn()) {
            closeBleDialog()
        } else {
            showBleDialog()
        }
    }

    //初始化导航
    private fun initNav() {
        binding.mainViewPager.initMain(this)
        val destination = arrayListOf(
            binding.mainLayout.mainMotionLayout,
            binding.packagesLayout.packagesMotionLayout,
            binding.settingLayout.settingMotionLayout
        )
//        binding.mainLayout.mainMotionLayout.progress = 1f
        destination[vm.position].progress = 1f
        binding.mainViewPager.setCurrentItem(vm.position, false)
        destination.forEach { motionLayout ->
            motionLayout.setOnClickListener {
                destination.forEach {
                    it.progress = 0f
                }
                motionLayout.transitionToEnd()
                when (motionLayout) {
                    binding.mainLayout.mainMotionLayout -> {
                        vm.position = 0
                    }

                    binding.packagesLayout.packagesMotionLayout -> {
                        vm.position = 1
                    }

                    binding.settingLayout.settingMotionLayout -> {
                        vm.position = 2
                    }
                }
                binding.mainViewPager.setCurrentItem(vm.position, false)
            }
        }
    }

    private var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0x00 -> {
                    val tip = msg.obj as String
                    showToast(tip)
                }

                0x01 -> {
                    val status = msg.arg1
                    when (status) {
                        -1 -> { //获取状态失败
                            val alertDialog = AlertDialog.Builder(this@MainActivity)
                                .setTitle(getString(R.string.tip))
                                .setMessage(getString(R.string.status_fail))
                                .setIcon(R.mipmap.ic_launcher)
                                .setPositiveButton(
                                    getString(R.string.ok)
                                ) //添加"Yes"按钮
                                { _, _ -> }
                                .create()
                            alertDialog.show()
                            return
                        }

                        0 -> { //状态正常
                            showToast(getString(R.string.status_normal))
                            return
                        }

                        -2 -> { //状态缺纸
                            showToast(getString(R.string.status_out_of_paper))
                            return
                        }

                        -3 -> { //状态开盖
                            showToast(getString(R.string.status_open))
                            return
                        }

                        -4 -> {
                            showToast(getString(R.string.status_overheated))
                            return
                        }

                        -5 -> {
                            showToast(getString(R.string.conn_first))
                            return
                        }
                    }
                }

                0x02 -> {
                    Thread {
                        if (vm.printer.portManager != null) {
                            vm.printer.close()
                        }
                    }.start()
                    showToast(getString(R.string.conn_first))
//                    tvState.setText(getString(R.string.not_connected))
                }

                0x03 -> {
                    val message = msg.obj as String
                    val alertDialog = AlertDialog.Builder(this@MainActivity)
                        .setTitle(getString(R.string.tip))
                        .setMessage(message)
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton(
                            getString(R.string.ok)
                        ) //添加"Yes"按钮
                        { _, _ -> }
                        .create()
                    alertDialog.show()
                }

            }
        }
    }


    /**
     * 初始化权限
     */
    private fun getAllPermission() {
        permissionsLauncher.launch(permissionsNeeded) { permissions ->
            permissions.forEach { entry ->
                val permissionName = entry.key
                val isGranted = entry.value
                if (!isGranted) {
                    if (!shouldShowRequestPermissionRationale(permissionName)) {
                        gotoSystemSetting()
                        when (permissionName) {
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION -> showToast(
                                getString(R.string.no_permission)
                            )

                            Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT -> showToast(
                                getString(R.string.no_scan_permission)
                            )
                        }
                    } else {
                        showAllPermissionDialog()
                    }
                    return@launch
                }
            }
            if (PermissionsHelper.isGranted(permissionsNeeded)) {
                //启动服务
                showToast(getString(R.string.connect_device_next_tips))
            }
        }
    }

    //跳去app设置界面
    private fun gotoSystemSetting() {
        val packageURI = Uri.parse("package:$packageName")
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
        launcher.launch(intent) {
            //获取后台定位权限
            if (!PermissionsHelper.isGranted(permissionsNeeded)) {
                showAllPermissionDialog()
            }
        }
    }

    //显示位置权限提示弹框
    private fun showAllPermissionDialog() {
        if (permissionsTipDialog == null) {
            permissionsTipDialog = SingleTipDialog(this)
            permissionsTipDialog!!.show()
            permissionsTipDialog!!.setTitleText(getString(R.string.location_title))
                .setContentText(getString(R.string.location_content))
                .setCanceledOnTouch(true)
                .setCloseVisible(false)
                .setContentGravity(Gravity.START)
                .setDialogListener {
                    getAllPermission()
                }
        }
        permissionsTipDialog?.show()
    }

    //显示蓝牙权限提示弹框
    private fun showBleDialog() {
        //避免跟权限弹框覆盖
        if (!PermissionsHelper.isGranted(permissionsNeeded)) {
            return
        }
        if (bleDialog == null) {
            bleDialog = SingleTipDialog(this)
            bleDialog!!.show()
            bleDialog!!.setTitleText(getString(R.string.remind_warn))
                .setContentText(getString(R.string.ble_off_tips))
                .setCloseVisible(false)
                .setCanceledOnTouch(true)
                .setContentGravity(Gravity.START)
                .setDialogListener {
                    Utils.openBluetooth()
                }
        }
        bleDialog?.show()
    }

    //关闭bleDialog
    private fun closeBleDialog() {
        if (bleDialog?.isShowing == true) bleDialog?.dismiss()
    }


    override fun onConnecting() {
        //连接打印机中
//        tvState.setText(getString(R.string.conning))
        showLoading(getString(R.string.conn_printer))
        println("printer status: onConnecting")
    }

    override fun onCheckCommand() {
        //查询打印机指令
//        tvState.setText(getString(R.string.checking))
        println("printer status: onCheckCommand")
    }

    override fun onSuccess(printerDevices: PrinterDevices?) {
        println("printer status: onSuccess")
        goneLoading()
        showToast(getString(R.string.conn_success))
        vm.isConnectPrinter.value = true
    }

    override fun onReceive(data: ByteArray?) {
        println("printer status: onReceive")
    }

    override fun onFailure() {
        println("printer status: onFailure")
        goneLoading()
        showToast(getString(R.string.conn_fail))
        vm.isConnectPrinter.value = false
    }

    override fun onDisconnect() {
        println("printer status: onDisconnect")
        showToast(getString(R.string.disconnect))
        vm.isConnectPrinter.value = false
    }

    /**
     * 提示弹框
     * @param message
     */
    fun tipsDialog(message: String) {
        val msg = Message()
        msg.what = 0x03
        msg.obj = message
        handler.sendMessage(msg)
    }

    /**
     * 提示弹框
     * @param message
     */
    fun tipsToast(message: String) {
        val msg = Message()
        msg.what = 0x00
        msg.obj = message
        handler.sendMessage(msg)
    }

    /**
     * 获取每天的起始时间
     */
    fun getStartOfDayMillis(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun printMenu(order: Order, printListener: ((Boolean) -> Unit)? = null) {
        ThreadPoolManager.getInstance().addTask(Runnable {
            try {
                if (vm.printer.portManager == null) {
                    tipsToast(getString(R.string.conn_first))
                    return@Runnable
                }
                val result: Boolean =
                    vm.printer.portManager?.writeDataImmediately(PrintContent.get58Menu(order))
                        ?: false
                if (!result) {
                    tipsDialog(getString(R.string.send_fail))
                    printListener?.invoke(false)
                } else {
//                    tipsToast(getString(R.string.send_success))
                }
                LogUtils.e("send result", result)
            } catch (e: IOException) {
                printListener?.invoke(false)
                tipsDialog(
                    """
                    ${getString(R.string.disconnect)}
                    ${getString(R.string.print_fail)}${e.message}
                    """.trimIndent()
                )
            } catch (e: Exception) {
                printListener?.invoke(false)
                tipsDialog(getString(R.string.print_fail) + e.message)
            } finally {
                tipsToast(getString(R.string.print_complete))
                vm.insertOrder(order)
                printListener?.invoke(true)
            }
        })
    }
}