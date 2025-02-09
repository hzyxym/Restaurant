package com.hzy.restaurant.mvvm.view.fragment.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gprinter.bean.PrinterDevices
import com.gprinter.utils.Command
import com.gprinter.utils.ConnMethod
import com.gprinter.utils.LogUtils
import com.gprinter.utils.SDKUtils
import com.hzy.restaurant.MainActivity
import com.hzy.restaurant.R
import com.hzy.restaurant.base.BaseFragment
import com.hzy.restaurant.bean.Order
import com.hzy.restaurant.bean.PackagesWithProductList
import com.hzy.restaurant.databinding.FragmentPackagesBinding
import com.hzy.restaurant.databinding.ItemPackagesBinding
import com.hzy.restaurant.mvvm.view.activity.BlueToothDeviceActivity
import com.hzy.restaurant.mvvm.vm.MainViewModel
import com.hzy.restaurant.mvvm.vm.PackagesVM
import com.hzy.restaurant.utils.ActivityResultLauncherCompat
import com.hzy.restaurant.utils.ext.trimZero
import com.hzy.restaurant.utils.printer.PrintContent
import com.hzy.restaurant.utils.printer.ThreadPoolManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Created by hzy in 2025/1/2
 * description: 套餐
 * */
@AndroidEntryPoint
class PackagesFragment : BaseFragment<FragmentPackagesBinding>() {
    private val vm by activityViewModels<MainViewModel>()
    private val adapter by lazy { PackagesAdapter() }
    private val launcher =
        ActivityResultLauncherCompat(this, ActivityResultContracts.StartActivityForResult())

    override fun getViewBinding(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?
    ): FragmentPackagesBinding {
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

        binding.tvPrint.setOnClickListener {
            if (vm.isConnectPrinter.value == true) {
//                printMenu()
                lifecycleScope.launch(Dispatchers.Default) {
                    val mainActivity = (requireActivity() as MainActivity)
                    vm.selectPackages?.let {
                        val currentNo =
                            (vm.getOrderMaxCurrentNo(mainActivity.getStartOfDayMillis()) ?: 0) + 1
                        val selectedProducts = it.products
                        val order = Order(
                            0L,
                            System.currentTimeMillis(),
                            currentNo,
                            System.currentTimeMillis(),
                            selectedProducts,
                            it.packages.packagesName,
                            it.packages.packagesPrice
                        )
                        mainActivity.printMenu(order){ result ->
                            if (result && !vm.isFixed) {
                                binding.tvPrint.post {
                                    adapter.data.forEachIndexed { index, packagesWithProductList ->
                                        if (packagesWithProductList.packages.packagesId == vm.selectPackages?.packages?.packagesId) {
                                            packagesWithProductList.isCheck = false
                                            vm.selectPackages = null
                                            adapter.notifyItemChanged(index)
                                        }
                                    }
                                }
                            }
                        }
                    } ?: run {
                        mainActivity.tipsToast((getString(R.string.please_select_packages)))
                    }
                }
            } else {
                val intent = Intent(requireContext(), BlueToothDeviceActivity::class.java)
                launcher.launch(intent) { result ->
                    if (result.resultCode == RESULT_OK) {
                        val mac: String? =
                            result.data?.getStringExtra(BlueToothDeviceActivity.EXTRA_DEVICE_ADDRESS)
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
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun createObserver() {
        super.createObserver()
        vm.getPackagesList.observe(this) { list ->
            val removeList = list.filter { it.products.isEmpty() }.map { it.packages }

            vm.delPackages(*removeList.toTypedArray())
            adapter.refreshData(list.filter { it.products.isNotEmpty() })
            adapter.notifyDataSetChanged()
        }
        vm.isConnectPrinter.observe(this) {
            if (it) {
                binding.tvPrint.text = getString(R.string.print_receipt)
            } else {
                binding.tvPrint.text = getString(R.string.conn_printer)
            }
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
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_packages, parent, false)
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
                binding.tvPrice.text =
                    "￥${packagesWithProductList.packages.packagesPrice.trimZero()}"
                val str = StringBuilder("(")
                packagesWithProductList.products.forEach {
                    str.append(it.productName)
                    str.append("/")
                }
                str.deleteCharAt(str.length - 1)
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