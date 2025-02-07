package com.hzy.restaurant

import android.app.AlertDialog
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.activity.viewModels
import com.gprinter.bean.PrinterDevices
import com.gprinter.utils.CallbackListener
import com.hzy.restaurant.base.BaseActivity
import com.hzy.restaurant.databinding.ActivityMainBinding
import com.hzy.restaurant.mvvm.vm.MainViewModel
import com.hzy.restaurant.utils.ext.initMain
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), CallbackListener {
    private val vm: MainViewModel by viewModels()
    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initLocal() {
        super.initLocal()
        setToolsBarVisible(false)
        initNav()
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

    var handler: Handler = object : Handler(Looper.getMainLooper()) {
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
                            showToast("portManager is null")
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

    override fun onConnecting() {
        //连接打印机中
//        tvState.setText(getString(R.string.conning))
    }

    override fun onCheckCommand() {
        //查询打印机指令
//        tvState.setText(getString(R.string.checking))
    }

    override fun onSuccess(printerDevices: PrinterDevices?) {
        showToast(getString(R.string.conn_success))
//        tvState.setText( """
//                ${getString(R.string.conned)}
//                ${printerDevices.toString()}
//                """.trimIndent()
//        )
    }

    override fun onReceive(data: ByteArray?) {

    }

    override fun onFailure() {
        showToast(getString(R.string.conn_fail))
        handler.obtainMessage(0x02).sendToTarget()
    }

    override fun onDisconnect() {
        showToast(getString(R.string.disconnect))
        handler.obtainMessage(0x02).sendToTarget()
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

}