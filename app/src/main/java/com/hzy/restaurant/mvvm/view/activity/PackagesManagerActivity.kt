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
import com.blankj.utilcode.util.GsonUtils
import com.dc.lg_ac012.util.dialog.TipDialog.TipClickListener
import com.hzy.restaurant.R
import com.hzy.restaurant.base.BaseActivity
import com.hzy.restaurant.bean.Packages
import com.hzy.restaurant.bean.PackagesWithProductList
import com.hzy.restaurant.databinding.ActivityPackagesManagerBinding
import com.hzy.restaurant.databinding.ItemPackagesManagerBinding
import com.hzy.restaurant.mvvm.vm.PackagesVM
import com.hzy.restaurant.utils.ext.trimZero
import dagger.hilt.android.AndroidEntryPoint
import java.util.Collections

/**
 * Created by hzy 2025/1/30
 * @Description:套餐管理
 */
@AndroidEntryPoint
class PackagesManagerActivity : BaseActivity<ActivityPackagesManagerBinding>() {
    private val vm by viewModels<PackagesVM>()
    private val adapter by lazy { PackagesAdapter() }

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

        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvPackages.layoutManager = LinearLayoutManager(this)
        } else {
            binding.rvPackages.layoutManager = GridLayoutManager(this, 3)
        }
        binding.rvPackages.adapter = adapter

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
                    val list = mutableListOf<Packages>()
                    adapter.data.forEach {
                        list.add(it.packages)
                    }
                    vm.updatePackagesPositions(list)
                    hasMoved = false
                }
            }
        })

        helper.attachToRecyclerView(binding.rvPackages)
    }

    /**
     * 删除分类弹窗
     */
    private fun showDeleteCategoryDialog(position: Int) {
        val dialog = getTipDialog()
        dialog.show()
        dialog.setTipTitle(getString(R.string.remind_warn))
            .setTipMessage(getString(R.string.delete_sure_tips, adapter.data[position].packages.packagesName))
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
        vm.getPackagesList.observe(this) {
            adapter.refreshData(it)
            adapter.notifyDataSetChanged()
        }
    }

    inner class PackagesAdapter : RecyclerView.Adapter<PackagesAdapter.PackagesVH>() {
        val data: MutableList<PackagesWithProductList> = mutableListOf()

        @SuppressLint("NotifyDataSetChanged")
        fun refreshData(packagesWithProductList: List<PackagesWithProductList>) {
            data.clear()
            data.addAll(packagesWithProductList)
        }

        fun removeItem(position: Int) {
            vm.delPackages(data[position].packages)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackagesVH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_packages_manager, parent, false)
            return PackagesVH(view)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: PackagesVH, position: Int) {
            holder.bind(packagesWithProductList = data[position])
            holder.itemView.setOnClickListener {
                val intent = Intent(this@PackagesManagerActivity, AddPackagesActivity::class.java)
                intent.putExtra("selectProducts", GsonUtils.toJson(data[position].products))
                intent.putExtra("packages", data[position].packages)
                startActivity(intent)
            }
        }

        inner class PackagesVH(view: View) : RecyclerView.ViewHolder(view) {
            private val binding = ItemPackagesManagerBinding.bind(view)
            @SuppressLint("SetTextI18n")
            fun bind(packagesWithProductList: PackagesWithProductList) {
                binding.tvContent.text = packagesWithProductList.packages.packagesName
                binding.tvPrice.text = "￥${packagesWithProductList.packages.packagesPrice.trimZero()}"
                val str = StringBuilder("(")
                packagesWithProductList.products.forEach {
                    str.append(it.productName)
                    str.append("/")
                }
                str.deleteCharAt(str.length -1)
                str.append(")")
                binding.tvProducts.text = str.toString()
            }
        }
    }
}