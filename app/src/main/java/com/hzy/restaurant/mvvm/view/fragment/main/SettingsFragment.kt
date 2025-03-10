package com.hzy.restaurant.mvvm.view.fragment.main

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.SPUtils
import com.gprinter.bean.PrinterDevices
import com.gprinter.utils.Command
import com.gprinter.utils.ConnMethod
import com.gprinter.utils.SDKUtils
import com.hzy.restaurant.MainActivity
import com.hzy.restaurant.app.Constants
import com.hzy.restaurant.base.BaseFragment
import com.hzy.restaurant.bean.event.MsgEvent
import com.hzy.restaurant.databinding.FragmentSettingsBinding
import com.hzy.restaurant.mvvm.view.activity.BlueToothDeviceActivity
import com.hzy.restaurant.mvvm.view.activity.CategoryManagerActivity
import com.hzy.restaurant.mvvm.view.activity.OrderActivity
import com.hzy.restaurant.mvvm.view.activity.PackagesManagerActivity
import com.hzy.restaurant.mvvm.view.activity.ProductActivity
import com.hzy.restaurant.mvvm.view.activity.WeekProductActivity
import com.hzy.restaurant.mvvm.vm.MainViewModel
import com.hzy.restaurant.utils.ActivityResultLauncherCompat
import com.hzy.restaurant.utils.Events
import org.greenrobot.eventbus.EventBus

/**
 * Created by hzy in 2025/1/2
 * description: 设置
 * */
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    private val vm by activityViewModels<MainViewModel>()
    private val launcher =
        ActivityResultLauncherCompat(this, ActivityResultContracts.StartActivityForResult())
    override fun getViewBinding(inflater: LayoutInflater, viewGroup: ViewGroup?): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater)
    }

    override fun initLocal() {
        super.initLocal()
        val isCategory = SPUtils.getInstance().getBoolean(Constants.IS_CATEGORY, false)
        val isWeek = SPUtils.getInstance().getBoolean(Constants.IS_WEEK, false)
        binding.isCategory.isChecked = isCategory
        binding.isWeek.isChecked = isWeek
        binding.isFixed.isChecked = !vm.isFixed
        binding.isShowPosition.isChecked = vm.isShowPosition

        binding.tvDevice.setOnClickListener {
            val intent = Intent(requireContext(), BlueToothDeviceActivity::class.java)
            launcher.launch(intent) { result ->
                if (result.resultCode == RESULT_OK) {
                    val mac: String? = result.data?.getStringExtra(BlueToothDeviceActivity.EXTRA_DEVICE_ADDRESS)
                    Log.e("hzyxym", SDKUtils.bytesToHexString(mac?.toByteArray()))
                    val blueTooth = PrinterDevices.Build()
                        .setContext(context)
                        .setConnMethod(ConnMethod.BLUETOOTH)
                        .setMacAddress(mac)
                        .setCommand(Command.ESC)
                        .setCallbackListener(requireActivity() as MainActivity)
                        .build()
                    vm.printer.connect(blueTooth)
                }
            }
        }

        binding.isCategory.setOnCheckedChangeListener { _, isCheck ->
            SPUtils.getInstance().put(Constants.IS_CATEGORY, isCheck)
            EventBus.getDefault().post(MsgEvent<Any>(Events.REFRESH_MAIN_PRODUCT))
        }
        binding.isWeek.setOnCheckedChangeListener { _, isCheck ->
            SPUtils.getInstance().put(Constants.IS_WEEK, isCheck)
            EventBus.getDefault().post(MsgEvent<Any>(Events.REFRESH_MAIN_PRODUCT))
        }
        binding.isFixed.setOnCheckedChangeListener { _, isCheck ->
            SPUtils.getInstance().put(Constants.IS_FIXED, !isCheck)
            vm.isFixed = isCheck
        }
        binding.isShowPosition.setOnCheckedChangeListener { _, isCheck ->
            SPUtils.getInstance().put(Constants.IS_SHOW_POSITION, isCheck)
            vm.isShowPosition = isCheck
        }

        binding.tvCategory.setOnClickListener {
            val intent = Intent(requireContext(), CategoryManagerActivity::class.java)
            startActivity(intent)
        }

        binding.tvMenu.setOnClickListener {
            val intent = Intent(requireContext(), ProductActivity::class.java)
            startActivity(intent)
        }

        binding.tvWeekProduct.setOnClickListener {
            val intent = Intent(requireContext(), WeekProductActivity::class.java)
            startActivity(intent)
        }

        binding.tvPackages.setOnClickListener {
            val intent = Intent(requireContext(), PackagesManagerActivity::class.java)
            startActivity(intent)
        }

        binding.tvOrder.setOnClickListener {
            val intent = Intent(requireContext(), OrderActivity::class.java)
            startActivity(intent)
        }
    }
}