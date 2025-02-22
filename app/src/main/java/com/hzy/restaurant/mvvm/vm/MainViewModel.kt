package com.hzy.restaurant.mvvm.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.SPUtils
import com.hzy.restaurant.app.Constants
import com.hzy.restaurant.bean.Category
import com.hzy.restaurant.bean.Order
import com.hzy.restaurant.bean.Packages
import com.hzy.restaurant.bean.PackagesWithProductList
import com.hzy.restaurant.bean.Product
import com.hzy.restaurant.bean.ProductItem
import com.hzy.restaurant.bean.Week
import com.hzy.restaurant.db.dao.CategoryDao
import com.hzy.restaurant.db.dao.OrderDao
import com.hzy.restaurant.db.dao.PackagesDao
import com.hzy.restaurant.db.dao.ProductDao
import com.hzy.restaurant.utils.printer.Printer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by hzy in 2024/12/24
 * description:
 * */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val orderDao: OrderDao,
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao,
    private val packagesDao: PackagesDao,
) : ViewModel() {
    var printer: Printer = Printer.getInstance()
    var isConnectPrinter = MutableLiveData(false)
    var position = 0
    var isFixed = SPUtils.getInstance().getBoolean(Constants.IS_FIXED, false)
    var isShowPosition = SPUtils.getInstance().getBoolean(Constants.IS_SHOW_POSITION, true)
    val productList = productDao.getAll()
    val categoryList = categoryDao.getAll()
    val getPackagesList = packagesDao.getAll()
    var selectPackages: PackagesWithProductList? = null

    fun insertOrder(order: Order) {
        viewModelScope.launch(Dispatchers.Default) {
            orderDao.insert(order)
        }
    }

    //删除套餐
    fun delPackages(vararg packages: Packages) {
        viewModelScope.launch(Dispatchers.Default) {
            packagesDao.delete(*packages)
        }
    }

    //获取一周的菜品
    fun getProductDayList(week: Week, isEnable: Boolean): LiveData<List<Product>> {
        return when (week) {
            Week.Monday -> productDao.getAllMonDay(isEnable)
            Week.Tuesday -> productDao.getAllTueDay(isEnable)
            Week.Wednesday -> productDao.getAllWedDay(isEnable)
            Week.Thursday -> productDao.getAllThuDay(isEnable)
            Week.Friday -> productDao.getAllFriDay(isEnable)
            Week.Saturday -> productDao.getAllSatDay(isEnable)
            Week.Sunday -> productDao.getAllSunDay(isEnable)
        }
    }

    //菜品组合分类
    fun getGroupedProducts(
        products: List<Product>,
        categories: List<Category>,
        all: String
    ): List<ProductItem> {
        val groupedList = mutableListOf<ProductItem>()

        // 按 sortOrder 对 categoryName 进行排序
        val categoryOrderMap = categories.associateBy({ it.categoryName }, { it.position })

        // 先按 categoryName 的顺序排序（默认 "未分类" 放最后）
        val groupedMap = products.groupBy { it.categoryName ?: all }
            .toSortedMap(compareBy { categoryOrderMap[it] ?: Int.MAX_VALUE })

        // 按顺序添加分类标题和产品
        groupedMap.forEach { (category, productList) ->
            groupedList.add(ProductItem.CategoryHeader(category))
            productList.forEach { groupedList.add(ProductItem.ProductData(it)) }
        }

        return groupedList
    }

    //菜品不组合分类
    fun getGroupedProducts(products: List<Product>): List<ProductItem> {
        val groupedList = mutableListOf<ProductItem>()
        products.forEach { groupedList.add(ProductItem.ProductData(it)) }
        return groupedList
    }

    suspend fun getOrderMaxCurrentNo(time : Long): Long? {
        return orderDao.getMaxCurrentNo(time)
    }
}