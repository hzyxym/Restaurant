package com.hzy.restaurant.mvvm.view.activity

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hzy.restaurant.R
import com.hzy.restaurant.base.BaseActivity
import com.hzy.restaurant.bean.Category
import com.hzy.restaurant.databinding.ActivityCategoryManagerBinding
import com.hzy.restaurant.databinding.ItemBinding
import com.hzy.restaurant.mvvm.vm.CategoryVM
import com.hzy.restaurant.utils.dialog.EditValueDialog
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by hzy 2025/1/21
 * @Description: 分类管理
 */
@AndroidEntryPoint
class CategoryManagerActivity : BaseActivity<ActivityCategoryManagerBinding>() {
    private val vm: CategoryVM by viewModels()
    private val adapter by lazy { CategoryAdapter() }
    override fun getViewBinding(): ActivityCategoryManagerBinding {
        return ActivityCategoryManagerBinding.inflate(layoutInflater)
    }

    override fun initLocal() {
        super.initLocal()
        setTitle(getString(R.string.category_manager))

        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvCategory.layoutManager = LinearLayoutManager(this)
        } else {
            binding.rvCategory.layoutManager = GridLayoutManager(this, 6)
        }
        binding.rvCategory.adapter = adapter

        binding.tvAddCategory.setOnClickListener {
            showCategoryDialog(getString(R.string.add_category), null)
        }

        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // 获取被滑动的 item 的位置
                val position = viewHolder.adapterPosition
                // 删除数据
                adapter.removeItem(position)
            }

        })

        helper.attachToRecyclerView(binding.rvCategory)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun createObserver() {
        super.createObserver()
        vm.categoryList.observe(this) {
            adapter.refreshData(it)
            adapter.notifyDataSetChanged()
        }
    }

    //添加编辑分类
    private fun showCategoryDialog(title: String, category: Category?) {
        EditValueDialog.newInstance(category?.name, title)
            .setDialogListener {
                val id = category?.id ?: 0
                vm.addCategory(Category(id, name = it))
            }
            .show(supportFragmentManager, "add_category")
    }

    inner class CategoryAdapter: RecyclerView.Adapter<CategoryAdapter.CategoryVH>() {
        private val data: MutableList<Category> = mutableListOf()

        fun refreshData(categoryList : List<Category>) {
            data.clear()
            data.addAll(categoryList)
        }

        fun removeItem(position: Int) {
            vm.delCategory(data[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
            return CategoryVH(view)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: CategoryVH, position: Int) {
            holder.bind(category = data[position])
            holder.itemView.setOnClickListener {
                showCategoryDialog(getString(R.string.edit_category), data[position])
            }
        }

        inner class CategoryVH(view: View): RecyclerView.ViewHolder(view) {
            private val binding = ItemBinding.bind(view)
            fun bind(category: Category) {
                binding.tvContent.text = category.name
            }
        }
    }
}