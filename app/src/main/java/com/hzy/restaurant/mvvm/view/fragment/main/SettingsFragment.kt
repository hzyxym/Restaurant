package com.hzy.restaurant.mvvm.view.fragment.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hzy.restaurant.base.BaseFragment
import com.hzy.restaurant.databinding.FragmentSettingsBinding

/**
 * Created by hzy in 2025/1/2
 * description: 设置
 * */
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    override fun getViewBinding(inflater: LayoutInflater, viewGroup: ViewGroup?): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater)
    }
}