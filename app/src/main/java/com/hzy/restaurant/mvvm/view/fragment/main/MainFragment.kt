package com.hzy.restaurant.mvvm.view.fragment.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SPUtils
import com.gprinter.bean.PrinterDevices
import com.gprinter.utils.Command
import com.gprinter.utils.ConnMethod
import com.gprinter.utils.LogUtils
import com.gprinter.utils.SDKUtils
import com.hzy.restaurant.MainActivity
import com.hzy.restaurant.R
import com.hzy.restaurant.app.Constants
import com.hzy.restaurant.base.BaseFragment
import com.hzy.restaurant.bean.Category
import com.hzy.restaurant.bean.Product
import com.hzy.restaurant.bean.ProductItem
import com.hzy.restaurant.bean.Week
import com.hzy.restaurant.bean.event.MsgEvent
import com.hzy.restaurant.databinding.FragmentMainBinding
import com.hzy.restaurant.databinding.ItemCategoryHeaderBinding
import com.hzy.restaurant.databinding.ItemProductBinding
import com.hzy.restaurant.databinding.SelectItemBinding
import com.hzy.restaurant.mvvm.view.activity.BlueToothDeviceActivity
import com.hzy.restaurant.mvvm.vm.MainViewModel
import com.hzy.restaurant.utils.ActivityResultLauncherCompat
import com.hzy.restaurant.utils.Events
import com.hzy.restaurant.utils.ext.trimZero
import com.hzy.restaurant.utils.printer.PrintContent
import com.hzy.restaurant.utils.printer.ThreadPoolManager
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.util.Calendar

/**
 * Created by hzy in 2025/1/2
 * description: 首页
 * */
@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {
    private val vm by activityViewModels<MainViewModel>()
    private val adapter by lazy { ProductAdapter() }
    private val categories = mutableListOf<Category>()
    private val products = mutableListOf<Product>()
    private val selectName = mutableSetOf<String>()
    private val selectAdapter by lazy { SelectProductAdapter(selectName) }
    private val launcher =
        ActivityResultLauncherCompat(this, ActivityResultContracts.StartActivityForResult())
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
        EventBus.getDefault().register(this)
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
        }
        binding.rvProduct.adapter = adapter

        //选中
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvSelectProduct.layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        } else {
            binding.rvSelectProduct.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        binding.rvSelectProduct.adapter = selectAdapter

        binding.tvPrint.setOnClickListener {
            if (vm.isConnectPrinter.value == true) {
                printMenu()
            } else {
                val intent = Intent(requireContext(), BlueToothDeviceActivity::class.java)
                launcher.launch(intent) { result ->
                    if (result.resultCode == RESULT_OK) {
                        val mac: String? = result.data?.getStringExtra(BlueToothDeviceActivity.EXTRA_DEVICE_ADDRESS)
                        Log.e("hzyxym", SDKUtils.bytesToHexString(mac?.toByteArray()))
                        val blueTooth = PrinterDevices.Build()
                            .setContext(context)
                            .setConnMethod(ConnMethod.BLUETOOTH)
                            .setMacAddress(mac)
                            .setCommand(Command.ESC)
                            .setCallbackListener(requireActivity() as MainActivity)
                            .build()
                        vm.printer.connect(blueTooth)
                    }
                }
            }
//            showToast("${vm.isFixed}, ${selectName.size}")
        }
    }

    override fun createObserver() {
        super.createObserver()
        val isCategory = SPUtils.getInstance().getBoolean(Constants.IS_CATEGORY, false)
        val isWeek = SPUtils.getInstance().getBoolean(Constants.IS_WEEK, false)
        if (isCategory) {
            vm.categoryList.observe(this) { result ->
                categories.clear()
                categories.addAll(result)
                val data = vm.getGroupedProducts(products, categories, getString(R.string.all))
                adapter.refreshData(data)
            }
        } else {
            vm.categoryList.removeObservers(this)
        }
        val dayOfWeek = getWeek()
        if (isWeek) {
            vm.productList.removeObservers(this)
            vm.getProductDayList(dayOfWeek, true).observe(this) { result ->
                handleData(result, isCategory)
            }
        } else {
            vm.getProductDayList(dayOfWeek, true).removeObservers(this)
            vm.productList.observe(this) { result ->
                handleData(result, isCategory)
            }
        }

        vm.isConnectPrinter.observe(this) {
            if (it) {
                binding.tvPrint.text = getString(R.string.print_receipt)
            } else {
                binding.tvPrint.text = getString(R.string.conn_printer)
            }
        }
    }

    private fun handleData(result: List<Product>, isCategory: Boolean) {
        selectName.clear()
        selectName.addAll(result.filter { it.isCheck }.map { it.productName })
        products.clear()
        products.addAll(result)
        if (isCategory) {
            val data = vm.getGroupedProducts(products, categories, getString(R.string.all))
            adapter.refreshData(data)
        } else {
            val data = vm.getGroupedProducts(products)
            adapter.refreshData(data)
        }
    }

    private fun getWeek(): Week {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        return when (dayOfWeek) {
            Calendar.SUNDAY -> Week.Sunday
            Calendar.MONDAY -> Week.Monday
            Calendar.TUESDAY -> Week.Tuesday
            Calendar.WEDNESDAY -> Week.Wednesday
            Calendar.THURSDAY -> Week.Thursday
            Calendar.FRIDAY -> Week.Friday
            Calendar.SATURDAY -> Week.Saturday
            else -> Week.Sunday
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMsgEvent(msgEvent: MsgEvent<*>) {
        when (msgEvent.type) {
            Events.REFRESH_MAIN_PRODUCT -> {
                createObserver()
            }
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
                binding.tvPrice.text = "￥${product.marketPrice.trimZero()}"
                binding.checkBox.isChecked = product.isCheck
//                binding.itemProduct.setBackgroundResource(if (product.isCheck) R.drawable.dialog_corner_bg_green_edge_shape else R.drawable.dialog_corner_bg_shape )
            }
        }
    }

    inner class SelectProductAdapter(private val selectData: MutableSet<String>) :
        RecyclerView.Adapter<SelectProductAdapter.SelectProductViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): SelectProductAdapter.SelectProductViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.select_item, parent, false)
            return SelectProductViewHolder(view)
        }

        override fun getItemCount(): Int {
            return selectData.size
        }

        override fun onBindViewHolder(
            holder: SelectProductAdapter.SelectProductViewHolder,
            position: Int
        ) {
            holder.bind(selectData.elementAt(position))
        }

        inner class SelectProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val binding = SelectItemBinding.bind(itemView)
            fun bind(name: String) {
                binding.tvName.text = name
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }
    private fun printMenu() {
        ThreadPoolManager.getInstance().addTask(Runnable {
            try {
                if (vm.printer.portManager == null) {
                    (requireActivity() as MainActivity).tipsToast(getString(R.string.conn_first))
                    return@Runnable
                }
                val result: Boolean = vm.printer.portManager?.writeDataImmediately(PrintContent.get58Menu(context)) ?: false
                if (result) {
                    (requireActivity() as MainActivity).tipsToast(getString(R.string.send_success))
                } else {
                    (requireActivity() as MainActivity).tipsDialog(getString(R.string.send_fail))
                }
                LogUtils.e("send result", result)
            } catch (e: IOException) {
                (requireActivity() as MainActivity).tipsDialog(
                    """
                    ${getString(R.string.disconnect)}
                    ${getString(R.string.print_fail)}${e.message}
                    """.trimIndent()
                )
            } catch (e: Exception) {
                (requireActivity() as MainActivity).tipsDialog(getString(R.string.print_fail) + e.message)
            } finally {
                showToast("打印成功")
            }
        })
    }
}