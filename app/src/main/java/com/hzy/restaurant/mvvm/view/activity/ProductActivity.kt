package com.hzy.restaurant.mvvm.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Paint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.GsonUtils
import com.dc.lg_ac012.util.dialog.TipDialog.TipClickListener
import com.google.gson.reflect.TypeToken
import com.hzy.restaurant.R
import com.hzy.restaurant.base.BaseActivity
import com.hzy.restaurant.bean.Product
import com.hzy.restaurant.bean.Week
import com.hzy.restaurant.databinding.ActivityProductBinding
import com.hzy.restaurant.databinding.ItemProductMangementBinding
import com.hzy.restaurant.mvvm.vm.ProductVM
import com.hzy.restaurant.utils.ActivityResultLauncherCompat
import com.hzy.restaurant.utils.ext.trimZero
import dagger.hilt.android.AndroidEntryPoint
import java.util.Collections

/**
 * Created by hzy 2025/1/22
 * @Description: 菜品
 */
@AndroidEntryPoint
class ProductActivity : BaseActivity<ActivityProductBinding>() {
    private val vm by viewModels<ProductVM>()
    private val launcher =
        ActivityResultLauncherCompat(this, ActivityResultContracts.StartActivityForResult())
    private val adapter by lazy { ProductManagerAdapter() }
    private var type: Week? = null
    private var isPackages = false
    private var selectProductNames = mutableSetOf<String>()
    override fun getViewBinding(): ActivityProductBinding {
        return ActivityProductBinding.inflate(layoutInflater)
    }

    override fun initLocal() {
        super.initLocal()
        type = intent.getSerializableExtra("type") as? Week
        isPackages = intent.getBooleanExtra("isPackages", false)
        if (intent.hasExtra("selectProducts")) {
            val json = intent.getStringExtra("selectProducts")
            val typeToken =  object : TypeToken<List<Product>>(){}.type
            val list = GsonUtils.fromJson<MutableList<Product>>(json, typeToken)
            selectProductNames = list.map { it.productName }.toMutableSet()
        }

        if (type != null || isPackages) {
            setTitle(getString(R.string.add_product))
            binding.tvAddProduct.text = getString(R.string.ok)
        } else {
            setTitle(getString(R.string.product_manager))
            binding.tvAddProduct.text = getString(R.string.add_product)
        }

        binding.tvAddProduct.setOnClickListener {
            if (type != null) {
                val list = adapter.data.filter { getTypeEnable(it, type!!) }
                vm.updateProduct(list)
                this.finish()
            } else if (isPackages) {
                val list = adapter.data.filter { it.isCheck }
                val intent = Intent()
                intent.putExtra("selectProducts", GsonUtils.toJson(list))
                setResult(RESULT_OK, intent)
                this.finish()
            } else {
                val intent = Intent(this, AddProductActivity::class.java)
                launcher.launch(intent) { result ->
                    if (result.resultCode == RESULT_OK) {
                        binding.rvProduct.postDelayed({
                            binding.rvProduct.scrollToPosition(adapter.itemCount - 1)
                        }, 100)
                    }
                }
            }
        }

        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvProduct.layoutManager = LinearLayoutManager(this)
        } else {
            binding.rvProduct.layoutManager = GridLayoutManager(this, 3)
        }
        binding.rvProduct.adapter = adapter

        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
        ) {
            private var hasMoved = false // 标记是否发生拖拽
            private var isPortrait = config.orientation == Configuration.ORIENTATION_PORTRAIT
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.bindingAdapterPosition
                val toPosition = target.bindingAdapterPosition

                // 更新内存中的数据顺序
                val items = adapter.data.toMutableList()
                if (isPortrait) {
                    Collections.swap(items, fromPosition, toPosition)
                } else {
                    // 数据更新逻辑：移动数据并重新排序
                    if (fromPosition < toPosition) {
                        for (i in fromPosition until toPosition) {
                            Collections.swap(items, i, i + 1)
                        }
                    } else {
                        for (i in fromPosition downTo toPosition + 1) {
                            Collections.swap(items, i, i - 1)
                        }
                    }
                }
                adapter.refreshData(items)
                // 通知适配器更新数据
                adapter.notifyItemMoved(fromPosition, toPosition)

                hasMoved = true // 标记发生拖拽操作
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // 获取被滑动的 item 的位置
                val position = viewHolder.bindingAdapterPosition
                //删除菜品提示弹窗
                showDeleteProductDialog(position)
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                // 只有在发生拖拽后，更新数据库
                if (hasMoved) {
                    vm.updateProductPositions(adapter.data)
                    hasMoved = false
                }
            }
        })

        helper.attachToRecyclerView(binding.rvProduct)
    }

    /**
     * 删除分类弹窗
     */
    private fun showDeleteProductDialog(position: Int) {
        val dialog = getTipDialog()
        dialog.show()
        dialog.setTipTitle(getString(R.string.remind_warn))
            .setTipMessage(getString(R.string.delete_product_tips, adapter.data[position].productName))
            .setSureText(getString(R.string.ok), R.drawable.tip_dialog_right_selector)
            .setGravity(Gravity.CENTER_HORIZONTAL)
            .setTipClickListener(object : TipClickListener() {
                override fun clickSure() {
                    super.clickSure()
                    // 删除数据
                    adapter.removeItem(position)
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun clickCancel() {
                    super.clickCancel()
                    adapter.notifyDataSetChanged()
                }
            })
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun createObserver() {
        super.createObserver()
        if (type != null) {
            vm.getProductDayList(type!!, false).observe(this) {
                adapter.refreshData(it)
                adapter.notifyDataSetChanged()
            }
        } else if (isPackages) {
            vm.productList.observe(this) { list ->
                list.forEach { product ->
                    if (selectProductNames.contains(product.productName)) {
                        product.isCheck = true
                    }
                }
                adapter.refreshData(list)
                adapter.notifyDataSetChanged()
            }
        } else {
            vm.productList.observe(this) {
                adapter.refreshData(it)
                adapter.notifyDataSetChanged()
            }
        }
    }

    inner class ProductManagerAdapter : RecyclerView.Adapter<ProductManagerAdapter.ProductVH>() {
        val data: MutableList<Product> = mutableListOf()

        fun refreshData(productList: List<Product>) {
            data.clear()
            data.addAll(productList)
        }

        fun removeItem(position: Int) {
            vm.delProduct(data[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_mangement, parent, false)
            return ProductVH(view)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ProductVH, position: Int) {
            holder.bind(product = data[position])
            holder.itemView.setOnClickListener {
                if (type != null) {
                    toggleTypeEnable(data[position], type!!)
                    this.notifyItemChanged(position)

                } else if (isPackages) {
                    data[position].isCheck = !data[position].isCheck
                    if (data[position].isCheck) {
                        selectProductNames.add(data[position].productName)
                    } else {
                        selectProductNames.remove(data[position].productName)
                    }
                    this.notifyItemChanged(position)
                } else {
                    val intent = Intent(this@ProductActivity, AddProductActivity::class.java)
                    intent.putExtra("product", data[position])
                    intent.putExtra("isEdit", true)
                    startActivity(intent)
                }
            }
        }

        inner class ProductVH(view: View) : RecyclerView.ViewHolder(view) {
            private val binding = ItemProductMangementBinding.bind(view)
            @SuppressLint("SetTextI18n")
            fun bind(product: Product) {
                binding.tvProductName.text = product.productName
                binding.tvPrice.text = "￥${product.marketPrice.trimZero()}"
                binding.tvSoldOut.visibility = if (product.isSoldOut) View.VISIBLE else View.GONE
                if (product.isSoldOut) binding.tvPrice.paintFlags = binding.tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                binding.tvCategory.visibility = if (type == null && !isPackages && product.categoryName?.isNotEmpty() == true) View.VISIBLE else View.GONE
                binding.tvCategory.text = product.categoryName
                val daysStr = StringBuilder()
                if (product.isMon) daysStr.append("一、")
                if (product.isTue) daysStr.append("二、")
                if (product.isWed) daysStr.append("三、")
                if (product.isThu) daysStr.append("四、")
                if (product.isFri) daysStr.append("五、")
                if (product.isSat) daysStr.append("六、")
                if (product.isSun) daysStr.append("日、")
                if (daysStr.isNotEmpty()) {
                    daysStr.deleteCharAt(daysStr.length - 1)
                }
                binding.tvDay.visibility = if (type == null && !isPackages && daysStr.isNotEmpty()) View.VISIBLE else View.GONE
                binding.tvDay.text = daysStr.toString()

                if (type != null) {
                    binding.ivCheck.visibility = if (getTypeEnable(product, type!!)) View.VISIBLE else View.GONE
                } else if(isPackages) {
                    binding.ivCheck.visibility = if (product.isCheck) View.VISIBLE else View.GONE
                } else {
                    binding.ivCheck.visibility = View.GONE
                }
            }
        }
    }

    fun getTypeEnable(product: Product, week: Week): Boolean {
        return when(week) {
            Week.Monday -> product.isMon
            Week.Tuesday -> product.isTue
            Week.Wednesday -> product.isWed
            Week.Thursday -> product.isThu
            Week.Friday -> product.isFri
            Week.Saturday -> product.isSat
            Week.Sunday -> product.isSun
        }
    }

    fun toggleTypeEnable(product: Product, week: Week) {
        when(week) {
            Week.Monday -> product.isMon = !product.isMon
            Week.Tuesday -> product.isTue = !product.isTue
            Week.Wednesday -> product.isWed = !product.isWed
            Week.Thursday -> product.isThu = !product.isThu
            Week.Friday -> product.isFri = !product.isFri
            Week.Saturday -> product.isSat = !product.isSat
            Week.Sunday -> product.isSun = !product.isSun
        }
    }
}