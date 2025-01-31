package com.hzy.restaurant.mvvm.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.reflect.TypeToken
import com.hzy.restaurant.R
import com.hzy.restaurant.base.BaseActivity
import com.hzy.restaurant.bean.Packages
import com.hzy.restaurant.bean.Product
import com.hzy.restaurant.databinding.ActivityAddPackagesBinding
import com.hzy.restaurant.databinding.ItemPackagesMangementBinding
import com.hzy.restaurant.mvvm.vm.PackagesVM
import com.hzy.restaurant.utils.ActivityResultLauncherCompat
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by hzy 2025/1/30
 * @Description:添加套餐管理
 */
@AndroidEntryPoint
class AddPackagesActivity : BaseActivity<ActivityAddPackagesBinding>() {
    private val vm by viewModels<PackagesVM>()
    private val launcher =
        ActivityResultLauncherCompat(this, ActivityResultContracts.StartActivityForResult())
    private val adapter by lazy { ProductManagerAdapter() }
    private val selectProduct = mutableListOf<Product>()
    override fun getViewBinding(): ActivityAddPackagesBinding {
        return ActivityAddPackagesBinding.inflate(layoutInflater)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initLocal() {
        super.initLocal()
        setTitle(getString(R.string.add_packages))

        binding.rvProduct.layoutManager = LinearLayoutManager(this)
        binding.rvProduct.adapter = adapter
        binding.rvProduct.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // 获取被滑动的 item 的位置
                val position = viewHolder.bindingAdapterPosition
                adapter.removeItem(position)
                adapter.notifyDataSetChanged()
            }
        })

        helper.attachToRecyclerView(binding.rvProduct)

        binding.tvSelectProduct.setOnClickListener {
            val intent = Intent(this, ProductActivity::class.java)
            intent.putExtra("isPackages", true)
            intent.putExtra("selectProducts", GsonUtils.toJson(selectProduct))
            launcher.launch(intent) { result ->
                if (result.resultCode == RESULT_OK) {
                    val json = result.data?.extras?.getString("selectProducts")
                    val type = object : TypeToken<List<Product>>() {}.type
                    val list = GsonUtils.fromJson<List<Product>>(json, type)
                    selectProduct.clear()
                    selectProduct.addAll(list)
                    adapter.refreshData(selectProduct)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        binding.tvAdd.setOnClickListener {
            if (verify()) {
                val packages = Packages(
                    0,
                    binding.etPackagesName.text.toString(),
                    binding.etPackagesPrice.text.toString().toDouble()
                )
                //保存到数据库并关联关系
                vm.savePackages(packages, selectProduct)
                this.finish()
            }
        }
    }

    private fun verify(): Boolean {
        if (binding.etPackagesName.text.isNullOrEmpty()) {
            showToast(getString(R.string.please_input_packages_name))
            return false
        }
        if (binding.etPackagesPrice.text.isNullOrEmpty()) {
            showToast(getString(R.string.please_input_packages_price))
            return false
        }
        if (selectProduct.isEmpty()) {
            showToast(getString(R.string.select_packages_product))
            return false
        }
        return true
    }

    inner class ProductManagerAdapter : RecyclerView.Adapter<ProductManagerAdapter.ProductVH>() {
        val data: MutableList<Product> = mutableListOf()

        fun refreshData(productList: List<Product>) {
            data.clear()
            data.addAll(productList)
        }

        fun removeItem(position: Int) {
            data.removeAt(position)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_packages_mangement, parent, false)
            return ProductVH(view)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ProductVH, position: Int) {
            holder.bind(product = data[position])
        }

        inner class ProductVH(view: View) : RecyclerView.ViewHolder(view) {
            private val binding = ItemPackagesMangementBinding.bind(view)

            @SuppressLint("SetTextI18n")
            fun bind(product: Product) {
                binding.tvProductName.text = product.productName
                binding.tvPrice.text = "￥${product.marketPrice}"
            }
        }
    }
}