package com.hzy.restaurant.mvvm.view.activity

import android.content.Intent
import com.hzy.restaurant.R
import com.hzy.restaurant.base.BaseActivity
import com.hzy.restaurant.databinding.ActivityPackagesManagerBinding

/**
 * Created by hzy 2025/1/30
 * @Description:套餐管理
 */
class PackagesManagerActivity : BaseActivity<ActivityPackagesManagerBinding>() {
    override fun getViewBinding(): ActivityPackagesManagerBinding {
        return ActivityPackagesManagerBinding.inflate(layoutInflater)
    }

    override fun initLocal() {
        super.initLocal()
        setTitle(getString(R.string.packages_manager))
        binding.tvAddPackages.setOnClickListener {
            val intent = Intent(this, AddPackagesActivity::class.java)
            startActivity(intent)
        }
    }
}