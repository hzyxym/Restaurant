package com.hzy.restaurant.mvvm.view.fragment.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.SPUtils
import com.hzy.restaurant.app.Constants
import com.hzy.restaurant.base.BaseFragment
import com.hzy.restaurant.bean.event.MsgEvent
import com.hzy.restaurant.databinding.FragmentSettingsBinding
import com.hzy.restaurant.mvvm.view.activity.BlueToothDeviceActivity
import com.hzy.restaurant.mvvm.view.activity.CategoryManagerActivity
import com.hzy.restaurant.mvvm.view.activity.PackagesManagerActivity
import com.hzy.restaurant.mvvm.view.activity.ProductActivity
import com.hzy.restaurant.mvvm.view.activity.WeekProductActivity
import com.hzy.restaurant.mvvm.vm.MainViewModel
import com.hzy.restaurant.utils.Events
import org.greenrobot.eventbus.EventBus

/**
 * Created by hzy in 2025/1/2
 * description: 设置
 * */
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    private val vm by activityViewModels<MainViewModel>()
    override fun getViewBinding(inflater: LayoutInflater, viewGroup: ViewGroup?): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater)
    }

    override fun initLocal() {
        super.initLocal()
        val isCategory = SPUtils.getInstance().getBoolean(Constants.IS_CATEGORY, false)
        val isWeek = SPUtils.getInstance().getBoolean(Constants.IS_WEEK, false)
        binding.isCategory.isChecked = isCategory
        binding.isWeek.isChecked = isWeek
        binding.isFixed.isChecked = vm.isFixed

        binding.tvDevice.setOnClickListener {
            val intent = Intent(requireContext(), BlueToothDeviceActivity::class.java)
            startActivity(intent)
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
            SPUtils.getInstance().put(Constants.IS_FIXED, isCheck)
            vm.isFixed = isCheck
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
    }
}