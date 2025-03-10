package com.hzy.restaurant.mvvm.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dc.lg_ac012.util.dialog.TipDialog.TipClickListener
import com.hzy.restaurant.R
import com.hzy.restaurant.base.BaseActivity
import com.hzy.restaurant.bean.Category
import com.hzy.restaurant.databinding.ActivityCategoryManagerBinding
import com.hzy.restaurant.databinding.ItemBinding
import com.hzy.restaurant.mvvm.vm.CategoryVM
import com.hzy.restaurant.utils.dialog.EditValueDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.Collections

/**
 * Created by hzy 2025/1/21
 * @Description: 分类管理
 */
@AndroidEntryPoint
class CategoryManagerActivity : BaseActivity<ActivityCategoryManagerBinding>() {
    private val vm: CategoryVM by viewModels()
    private val adapter by lazy { CategoryAdapter() }
    private var isSelectCategory = false
    override fun getViewBinding(): ActivityCategoryManagerBinding {
        return ActivityCategoryManagerBinding.inflate(layoutInflater)
    }

    override fun initLocal() {
        super.initLocal()
        isSelectCategory = intent.getBooleanExtra("isSelectCategory", false)

        if (isSelectCategory) {
            setTitle(getString(R.string.select_category))
        } else {
            setTitle(getString(R.string.category_manager))
        }

        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvCategory.layoutManager = LinearLayoutManager(this)
        } else {
            binding.rvCategory.layoutManager = GridLayoutManager(this, 6)
        }
        binding.rvCategory.adapter = adapter

        binding.tvAddCategory.setOnClickListener {
            showCategoryDialog(getString(R.string.add_category), null)
        }
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

                val items = adapter.data.toMutableList()
                // 更新内存中的数据顺序
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
                // 删除数据
                showDeleteCategoryDialog(position)
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                // 只有在发生拖拽后，更新数据库
                if (hasMoved) {
                    vm.updateCategoryPositions(adapter.data)
                    hasMoved = false
                }
            }
        })

        helper.attachToRecyclerView(binding.rvCategory)
    }

    /**
     * 删除分类弹窗
     */
    private fun showDeleteCategoryDialog(position: Int) {
        val dialog = getTipDialog()
        dialog.show()
        dialog.setTipTitle(getString(R.string.remind_warn))
            .setTipMessage(getString(R.string.delete_category_tips, adapter.data[position].categoryName))
            .setSureText(getString(R.string.ok), R.drawable.tip_dialog_right_selector)
            .setGravity(Gravity.CENTER_HORIZONTAL)
            .setTipClickListener(object : TipClickListener() {
                override fun clickSure() {
                    super.clickSure()
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
        vm.categoryList.observe(this) {
            adapter.refreshData(it)
            adapter.notifyDataSetChanged()
        }
    }

    //添加编辑分类
    private fun showCategoryDialog(title: String, category: Category?) {
        EditValueDialog.newInstance(category?.categoryName, title)
            .setDialogListener {
                val id = category?.id ?: 0
                val position = category?.position ?: -1
                vm.addCategory(Category(id, categoryName = it, position))
                if (position == -1) {
                    binding.rvCategory.postDelayed({
                        binding.rvCategory.scrollToPosition(adapter.itemCount - 1)
                    }, 100)
                }
            }
            .show(supportFragmentManager, "add_category")
    }

    inner class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryVH>() {
        val data: MutableList<Category> = mutableListOf()

        @SuppressLint("NotifyDataSetChanged")
        fun refreshData(categoryList: List<Category>) {
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
                if (isSelectCategory) {
                    val intent = Intent()
                    intent.putExtra("categoryName", data[position].categoryName)
                    setResult(RESULT_OK, intent)
                    this@CategoryManagerActivity.finish()
                } else {
                    showCategoryDialog(getString(R.string.edit_category), data[position])
                }
            }
        }

        inner class CategoryVH(view: View) : RecyclerView.ViewHolder(view) {
            private val binding = ItemBinding.bind(view)
            fun bind(category: Category) {
                binding.tvContent.text = category.categoryName
                if (isSelectCategory) {
                    binding.tvContent.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                } else {
                    binding.tvContent.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getCompatDrawable(R.drawable.dialog_corner_bg_shape), null)
                }
            }
        }
    }
}