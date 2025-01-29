package com.hzy.restaurant.mvvm.view.fragment.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hzy.restaurant.base.BaseFragment
import com.hzy.restaurant.databinding.FragmentSettingsBinding
import com.hzy.restaurant.mvvm.view.activity.CategoryManagerActivity
import com.hzy.restaurant.mvvm.view.activity.PackagesManagerActivity
import com.hzy.restaurant.mvvm.view.activity.ProductActivity
import com.hzy.restaurant.mvvm.view.activity.WeekProductActivity

/**
 * Created by hzy in 2025/1/2
 * description: 设置
 * */
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    override fun getViewBinding(inflater: LayoutInflater, viewGroup: ViewGroup?): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater)
    }

    override fun initLocal() {
        super.initLocal()

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