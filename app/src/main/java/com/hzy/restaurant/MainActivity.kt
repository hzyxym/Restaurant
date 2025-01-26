package com.hzy.restaurant

import androidx.activity.viewModels
import com.hzy.restaurant.base.BaseActivity
import com.hzy.restaurant.databinding.ActivityMainBinding
import com.hzy.restaurant.mvvm.vm.MainViewModel
import com.hzy.restaurant.utils.ext.initMain
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
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
        val destination = arrayListOf(binding.mainLayout.mainMotionLayout, binding.packagesLayout.packagesMotionLayout, binding.settingLayout.settingMotionLayout)
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

}