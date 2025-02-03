package com.hzy.restaurant.mvvm.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hzy.restaurant.bean.Category
import com.hzy.restaurant.bean.Order
import com.hzy.restaurant.bean.Product
import com.hzy.restaurant.bean.ProductItem
import com.hzy.restaurant.db.dao.CategoryDao
import com.hzy.restaurant.db.dao.OrderDao
import com.hzy.restaurant.db.dao.ProductDao
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
    private val dao: OrderDao,
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao,
) : ViewModel() {
    var position = 0
    val productList = productDao.getAll()
    val categoryList = categoryDao.getAll()

//    fun insertOrder(order: Order) {
//        viewModelScope.launch(Dispatchers.Default) {
//            dao.insert(order)
//        }
//    }

    fun getGroupedProducts(products: List<Product>, categories: List<Category>, all: String): List<ProductItem> {
        val groupedList = mutableListOf<ProductItem>()

        // 按 sortOrder 对 categoryName 进行排序
        val categoryOrderMap = categories.associateBy({ it.categoryName }, { it.position })

        // 先按 categoryName 的顺序排序（默认 "未分类" 放最后）
        val groupedMap = products.groupBy { it.categoryName ?: all}
            .toSortedMap(compareBy { categoryOrderMap[it] ?: Int.MAX_VALUE })

        // 按顺序添加分类标题和产品
        groupedMap.forEach { (category, productList) ->
            groupedList.add(ProductItem.CategoryHeader(category))
            productList.forEach { groupedList.add(ProductItem.ProductData(it)) }
        }

        return groupedList
    }

}