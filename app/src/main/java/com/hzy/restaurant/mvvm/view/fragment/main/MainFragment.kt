package com.hzy.restaurant.mvvm.view.fragment.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hzy.restaurant.base.BaseFragment
import com.hzy.restaurant.databinding.FragmentMainBinding

/**
 * Created by hzy in 2025/1/2
 * description: 首页
 * */
class MainFragment : BaseFragment<FragmentMainBinding>() {
    override fun getViewBinding(inflater: LayoutInflater, viewGroup: ViewGroup?): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater)
    }
}