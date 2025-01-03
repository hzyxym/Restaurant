package com.hzy.restaurant.mvvm.view.fragment.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hzy.restaurant.base.BaseFragment
import com.hzy.restaurant.databinding.FragmentPackagesBinding

/**
 * Created by hzy in 2025/1/2
 * description: 套餐
 * */
class PackagesFragment : BaseFragment<FragmentPackagesBinding>() {
    override fun getViewBinding(inflater: LayoutInflater, viewGroup: ViewGroup?): FragmentPackagesBinding {
        return FragmentPackagesBinding.inflate(inflater)
    }
}