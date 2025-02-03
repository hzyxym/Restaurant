package com.hzy.restaurant.mvvm.view.fragment.setting

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Paint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dc.lg_ac012.util.dialog.TipDialog.TipClickListener
import com.hzy.restaurant.R
import com.hzy.restaurant.base.BaseActivity
import com.hzy.restaurant.base.BaseFragment
import com.hzy.restaurant.bean.Product
import com.hzy.restaurant.bean.Week
import com.hzy.restaurant.databinding.FragmentWeekProductBinding
import com.hzy.restaurant.databinding.ItemProductMangementBinding
import com.hzy.restaurant.mvvm.view.activity.ProductActivity
import com.hzy.restaurant.mvvm.vm.ProductVM
import com.hzy.restaurant.utils.ext.trimZero
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by hzy 2025/1/28
 * @Description: 一周食谱fragment
 */
@AndroidEntryPoint
class WeekProductFragment : BaseFragment<FragmentWeekProductBinding>() {
    private val vm by activityViewModels<ProductVM>()
    private val adapter by lazy { ProductManagerAdapter() }
    private lateinit var type: Week
    override fun getViewBinding(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?
    ): FragmentWeekProductBinding {
        return FragmentWeekProductBinding.inflate(layoutInflater)
    }

    companion object {
        fun newInstance(type: Week): WeekProductFragment {
            val fragment = WeekProductFragment()
            val args = Bundle()
            args.putSerializable("type", type)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initLocal() {
        super.initLocal()
        type = arguments?.getSerializable("type") as Week
        binding.tvAddProduct.setOnClickListener {
            val intent = Intent(requireContext(), ProductActivity::class.java)
            intent.putExtra("type", type)
            startActivity(intent)
        }

        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        } else {
            binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        }
        binding.recyclerView.adapter = adapter

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
                //删除菜品提示弹窗
                showDeleteProductDialog(position)
            }
        })

        helper.attachToRecyclerView(binding.recyclerView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun createObserver() {
        super.createObserver()
        vm.getProductDayList(type, true).observe(this) {
            adapter.refreshData(it)
            adapter.notifyDataSetChanged()
        }
    }

    /**
     * 删除分类弹窗
     */
    private fun showDeleteProductDialog(position: Int) {
        val activity = requireActivity() as BaseActivity<*>
        val dialog = activity.getTipDialog()
        dialog.show()
        dialog.setTipTitle(getString(R.string.remind_warn))
            .setTipMessage(getString(R.string.remove_product_tips, adapter.data[position].productName))
            .setSureText(getString(R.string.ok), R.drawable.tip_dialog_right_selector)
            .setGravity(Gravity.CENTER_HORIZONTAL)
            .setTipClickListener(object : TipClickListener() {
                override fun clickSure() {
                    super.clickSure()
                    // 删除数据
                    adapter.removeDay(position)
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun clickCancel() {
                    super.clickCancel()
                    adapter.notifyDataSetChanged()
                }
            })
    }


    inner class ProductManagerAdapter : RecyclerView.Adapter<ProductManagerAdapter.ProductVH>() {
        val data: MutableList<Product> = mutableListOf()

        fun refreshData(productList: List<Product>) {
            data.clear()
            data.addAll(productList)
        }

        fun removeDay(position: Int) {
            vm.setDayEnable(data[position], type, false)
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
        }

        inner class ProductVH(view: View) : RecyclerView.ViewHolder(view) {
            private val binding = ItemProductMangementBinding.bind(view)
            @SuppressLint("SetTextI18n")
            fun bind(product: Product) {
                binding.tvProductName.text = product.productName
                binding.tvPrice.text = "￥${product.marketPrice.trimZero()}"
                binding.tvSoldOut.visibility = if (product.isSoldOut) View.VISIBLE else View.GONE
                if (product.isSoldOut) binding.tvPrice.paintFlags = binding.tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvCategory.visibility = if (product.categoryName?.isNotEmpty() == true) View.VISIBLE else View.GONE
                binding.tvCategory.text = product.categoryName
            }
        }
    }

}