package com.hzy.restaurant.utils.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.hzy.restaurant.mvvm.view.fragment.main.MainFragment
import com.hzy.restaurant.mvvm.view.fragment.main.PackagesFragment
import com.hzy.restaurant.mvvm.view.fragment.main.SettingsFragment

/**
 * Created by hzy on 2024/8/1 15:55
 * @description:
 */
fun ViewPager2.initMain(activity: FragmentActivity): ViewPager2 {
    //是否可滑动
    this.isUserInputEnabled = false
    this.offscreenPageLimit = 3
    //设置适配器
    adapter = object : FragmentStateAdapter(activity) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    MainFragment()
                }
                1 -> {
                    PackagesFragment()
                }
                2 -> {
                    SettingsFragment()
                }
                else -> {
                    MainFragment()
                }
            }
        }

        override fun getItemCount() = 3
    }
    return this
}