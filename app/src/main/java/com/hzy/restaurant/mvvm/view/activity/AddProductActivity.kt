package com.hzy.restaurant.mvvm.view.activity

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.hzy.restaurant.R
import com.hzy.restaurant.base.BaseActivity
import com.hzy.restaurant.bean.Product
import com.hzy.restaurant.databinding.ActivityAddProductBinding
import com.hzy.restaurant.mvvm.vm.ProductVM
import com.hzy.restaurant.utils.ActivityResultLauncherCompat
import com.hzy.restaurant.utils.ext.trimZero
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by hzy 2025/1/22
 * @Description:添加菜品
 */
@AndroidEntryPoint
class AddProductActivity : BaseActivity<ActivityAddProductBinding>() {
    private val vm by viewModels<ProductVM>()
    private val launcher =
        ActivityResultLauncherCompat(this, ActivityResultContracts.StartActivityForResult())
    private var isEdit = false
    private var product: Product? = null
    override fun getViewBinding(): ActivityAddProductBinding {
        return ActivityAddProductBinding.inflate(layoutInflater)
    }

    override fun initLocal() {
        super.initLocal()
        if (intent.extras?.containsKey("product") == true) {
            product = intent.getSerializableExtra("product") as Product
        }
        if (intent.extras?.containsKey("isEdit") == true) {
            isEdit = intent.getBooleanExtra("isEdit", false)
        }
        if (isEdit) {
            setTitle(getString(R.string.edit_product))
            binding.tvAdd.text = getString(R.string.ok)

            binding.etProductName.setText(product?.productName)
            binding.etProductPrice.setText(product?.marketPrice?.trimZero())
            binding.tvProductCategory.text = product?.categoryName
            binding.isSoldOut.isChecked = product?.isSoldOut ?: false
        } else {
            binding.tvAdd.text = getString(R.string.add)
            setTitle(getString(R.string.add_product))
        }
        binding.tvProductCategory.setOnClickListener {
            val intent = Intent(this, CategoryManagerActivity::class.java)
            intent.putExtra("isSelectCategory", true)
            launcher.launch(intent) { result ->
                binding.tvProductCategory.text = result.data?.getStringExtra("categoryName")
            }
        }

        binding.tvAdd.setOnClickListener {
            if (verify()) {
                val productName = binding.etProductName.text.toString()
                val productPrice = binding.etProductPrice.text.toString().toDouble()
                val isSoldOut = binding.isSoldOut.isChecked
                val categoryName = if (binding.tvProductCategory.text.isNotEmpty()) binding.tvProductCategory.text.toString() else null
                product?.apply {
                    this.productName = productName
                    this.marketPrice = productPrice
                    this.isSoldOut = isSoldOut
                    this.categoryName = categoryName
                } ?: run {
                    product = Product(
                        0,
                        productName,
                        productPrice,
                        isSoldOut,
                        product?.position ?: -1,
                        categoryName
                    )
                }
                vm.addProduct(product!!)
                setResult(RESULT_OK)
                this.finish()
            }
        }
    }

    private fun verify(): Boolean {
        if (binding.etProductName.text.isNullOrEmpty()) {
            showToast(getString(R.string.no_product_name))
            return false
        }
        if (binding.etProductPrice.text.isNullOrEmpty()) {
            showToast(getString(R.string.no_product_price))
            return false
        }
        return true
    }
}