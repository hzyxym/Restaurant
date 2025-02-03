package com.hzy.restaurant.mvvm.view.fragment.main

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hzy.restaurant.R
import com.hzy.restaurant.base.BaseFragment
import com.hzy.restaurant.bean.PackagesWithProductList
import com.hzy.restaurant.databinding.FragmentPackagesBinding
import com.hzy.restaurant.databinding.ItemPackagesBinding
import com.hzy.restaurant.mvvm.vm.MainViewModel
import com.hzy.restaurant.mvvm.vm.PackagesVM
import com.hzy.restaurant.utils.ext.trimZero
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by hzy in 2025/1/2
 * description: 套餐
 * */
@AndroidEntryPoint
class PackagesFragment : BaseFragment<FragmentPackagesBinding>() {
    private val vm by activityViewModels<MainViewModel>()
    private val adapter by lazy { PackagesAdapter() }

    override fun getViewBinding(inflater: LayoutInflater, viewGroup: ViewGroup?): FragmentPackagesBinding {
        return FragmentPackagesBinding.inflate(inflater)
    }

    override fun initLocal() {
        super.initLocal()
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvPackages.layoutManager = LinearLayoutManager(requireContext())
        } else {
            binding.rvPackages.layoutManager = GridLayoutManager(requireContext(), 3)
        }
        binding.rvPackages.adapter = adapter
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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackagesVH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_packages, parent, false)
            return PackagesVH(view)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: PackagesVH, position: Int) {
            holder.bind(packagesWithProductList = data[position])
            holder.itemView.setOnClickListener {
                data.forEachIndexed { index, item ->
                    if (item.isCheck) {
                        item.isCheck = false
                        this.notifyItemChanged(index)
                    }
                }
                data[position].isCheck = !data[position].isCheck
                vm.selectPackages = data[position]
                binding.tvSelectPackages.text = data[position].packages.packagesName
                this.notifyItemChanged(position)
            }
        }

        inner class PackagesVH(view: View) : RecyclerView.ViewHolder(view) {
            private val binding = ItemPackagesBinding.bind(view)
            @SuppressLint("SetTextI18n")
            fun bind(packagesWithProductList: PackagesWithProductList) {
                binding.tvName.text = packagesWithProductList.packages.packagesName
                binding.tvPrice.text = "￥${packagesWithProductList.packages.packagesPrice.trimZero()}"
                val str = StringBuilder("(")
                packagesWithProductList.products.forEach {
                    str.append(it.productName)
                    str.append("/")
                }
                str.deleteCharAt(str.length -1)
                str.append(")")
                binding.tvProducts.text = str.toString()

                if (packagesWithProductList.isCheck) {
                    binding.vBg.setBackgroundResource(R.drawable.dialog_corner_bg_edge_shape)
                } else {
                    binding.vBg.setBackgroundResource(R.drawable.dialog_corner_bg_shape)
                }
            }
        }
    }
}