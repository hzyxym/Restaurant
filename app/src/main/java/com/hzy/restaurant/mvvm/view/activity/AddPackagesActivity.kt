package com.hzy.restaurant.mvvm.view.activity

import com.hzy.restaurant.R
import com.hzy.restaurant.base.BaseActivity
import com.hzy.restaurant.databinding.ActivityAddPackagesBinding

/**
 * Created by hzy 2025/1/30
 * @Description:添加套餐管理
 */
class AddPackagesActivity : BaseActivity<ActivityAddPackagesBinding>() {
    override fun getViewBinding(): ActivityAddPackagesBinding {
        return ActivityAddPackagesBinding.inflate(layoutInflater)
    }

    override fun initLocal() {
        super.initLocal()
        setTitle(getString(R.string.add_packages))
    }
}