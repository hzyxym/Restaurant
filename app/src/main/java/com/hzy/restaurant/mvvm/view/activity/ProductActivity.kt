package com.hzy.restaurant.mvvm.view.activity

import android.content.Intent
import com.hzy.restaurant.R
import com.hzy.restaurant.base.BaseActivity
import com.hzy.restaurant.databinding.ActivityProductBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by hzy 2025/1/22
 * @Description: 菜品
 */
@AndroidEntryPoint
class ProductActivity : BaseActivity<ActivityProductBinding>() {

    override fun getViewBinding(): ActivityProductBinding {
        return ActivityProductBinding.inflate(layoutInflater)
    }

    override fun initLocal() {
        super.initLocal()
        setTitle(getString(R.string.product_manager))
        binding.tvAddProduct.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }
    }
}