package com.hzy.restaurant.mvvm.view.fragment.main

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hzy.restaurant.R
import com.hzy.restaurant.base.BaseFragment
import com.hzy.restaurant.bean.Category
import com.hzy.restaurant.bean.Product
import com.hzy.restaurant.bean.ProductItem
import com.hzy.restaurant.databinding.FragmentMainBinding
import com.hzy.restaurant.databinding.ItemCategoryHeaderBinding
import com.hzy.restaurant.databinding.ItemProductBinding
import com.hzy.restaurant.databinding.SelectItemBinding
import com.hzy.restaurant.mvvm.vm.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by hzy in 2025/1/2
 * description: 首页
 * */
@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {
    private val vm by viewModels<MainViewModel>()
    private val adapter by lazy { ProductAdapter() }
    private val categories = mutableListOf<Category>()
    private val products = mutableListOf<Product>()
    private val selectName = mutableSetOf<String>()
    private val selectAdapter by lazy { SelectProductAdapter(selectName) }

    companion object {
        const val VIEW_TYPE_CATEGORY_HEADER = 0
        const val VIEW_TYPE_PRODUCT = 1
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater)
    }

    override fun initLocal() {
        super.initLocal()
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvProduct.layoutManager = LinearLayoutManager(requireContext())
        } else {
            val gridLayoutManager = GridLayoutManager(requireContext(), 3) // 2列网格布局
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (adapter.getItemViewType(position)) {
                        VIEW_TYPE_CATEGORY_HEADER -> 3 // 分类标题占满 2 列
                        VIEW_TYPE_PRODUCT -> 1 // 商品占 1 列
                        else -> 1
                    }
                }
            }
            binding.rvProduct.layoutManager = gridLayoutManager
//            binding.rvProduct.layoutManager = GridLayoutManager(requireContext(), 3)
        }
        binding.rvProduct.adapter = adapter

        //选中
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvSelectProduct.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        } else {
            binding.rvSelectProduct.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        binding.rvSelectProduct.adapter = selectAdapter
    }

    override fun createObserver() {
        super.createObserver()
        vm.categoryList.observe(this) { result ->
            categories.clear()
            categories.addAll(result)
            val data = vm.getGroupedProducts(products, categories, getString(R.string.all))
            adapter.refreshData(data)
        }
        vm.productList.observe(this) { result ->
            selectName.clear()
            selectName.addAll(result.filter { it.isCheck }.map { it.productName })
            products.clear()
            products.addAll(result)
            val data = vm.getGroupedProducts(products, categories, getString(R.string.all))
            adapter.refreshData(data)
        }

    }

    inner class ProductAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val data: MutableList<ProductItem> = mutableListOf()

        @SuppressLint("NotifyDataSetChanged")
        fun refreshData(items: List<ProductItem>) {
            data.clear()
            data.addAll(items)
            this.notifyDataSetChanged()
        }

        override fun getItemViewType(position: Int): Int {
            return when (data[position]) {
                is ProductItem.CategoryHeader -> VIEW_TYPE_CATEGORY_HEADER
                is ProductItem.ProductData -> VIEW_TYPE_PRODUCT
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                VIEW_TYPE_CATEGORY_HEADER -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_category_header, parent, false)
                    CategoryHeaderViewHolder(view)
                }

                else -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_product, parent, false)
                    ProductViewHolder(view)
                }
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (val item = data[position]) {
                is ProductItem.CategoryHeader -> {
                    (holder as CategoryHeaderViewHolder).bind(item)
                }
                is ProductItem.ProductData -> {
                    (holder as ProductViewHolder).bind(item.product)
                    holder.itemView.setOnClickListener {
                        item.product.isCheck = !item.product.isCheck
                        if (item.product.isCheck) {
                            selectName.add(item.product.productName)
                        } else {
                            selectName.remove(item.product.productName)
                        }
                        selectAdapter.notifyDataSetChanged()
                        this.notifyItemChanged(position)
                    }
                }
            }
        }

        override fun getItemCount(): Int = data.size

        inner class CategoryHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val binding = ItemCategoryHeaderBinding.bind(itemView)
            fun bind(item: ProductItem.CategoryHeader) {
                binding.tvCategoryName.text = item.categoryName
            }
        }

        inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val binding = ItemProductBinding.bind(itemView)

            @SuppressLint("SetTextI18n")
            fun bind(product: Product) {
                binding.tvProductName.text = product.productName
                binding.tvPrice.text = "¥${product.marketPrice}"
                binding.checkBox.isChecked = product.isCheck
//                binding.itemProduct.setBackgroundResource(if (product.isCheck) R.drawable.dialog_corner_bg_green_edge_shape else R.drawable.dialog_corner_bg_shape )
            }
        }
    }

    inner class SelectProductAdapter(private val selectData: MutableSet<String>) : RecyclerView.Adapter<SelectProductAdapter.SelectProductViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectProductAdapter.SelectProductViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.select_item, parent, false)
            return SelectProductViewHolder(view)
        }

        override fun getItemCount(): Int {
            return selectData.size
        }

        override fun onBindViewHolder(holder: SelectProductAdapter.SelectProductViewHolder, position: Int) {
            holder.bind(selectData.elementAt(position))
        }

        inner class SelectProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val binding = SelectItemBinding.bind(itemView)
            fun bind(name: String) {
                binding.tvName.text = name
            }
        }

    }

}