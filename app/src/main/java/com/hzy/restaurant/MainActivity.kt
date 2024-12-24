package com.hzy.restaurant

import androidx.activity.viewModels
import com.hzy.restaurant.base.BaseActivity
import com.hzy.restaurant.databinding.ActivityMainBinding
import com.hzy.restaurant.mvvm.vm.MainViewModel
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

        binding.button.setOnClickListener {
            binding.mainLayout.mainMotionLayout.progress = 0f
            binding.mainLayout.mainMotionLayout.transitionToEnd()
        }
    }


}