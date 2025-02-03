package com.hzy.restaurant.mvvm.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.hzy.restaurant.bean.Product
import com.hzy.restaurant.bean.Week
import com.hzy.restaurant.db.dao.ProductDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by hzy 2025/1/25
 * @Description:
 */
@HiltViewModel
class ProductVM @Inject constructor(private val productDao: ProductDao) : ViewModel() {
    val productList = productDao.getAll()

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

    //添加菜品
    fun addProduct(product: Product) {
        viewModelScope.launch(Dispatchers.Default) {
            if (product.position == -1) {
                product.position = (productDao.getMaxPosition() ?: -1) + 1
            }
            productDao.insert(product)
        }
    }

    //删除分类
    fun delProduct(product: Product) {
        viewModelScope.launch(Dispatchers.Default) {
            productDao.delete(product)
        }
    }

    //更新菜品周一到周日是否售卖
    fun setDayEnable(product: Product, day: Week, isEnable: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            when (day) {
                Week.Monday -> product.isMon = isEnable
                Week.Tuesday -> product.isTue = isEnable
                Week.Wednesday -> product.isWed = isEnable
                Week.Thursday -> product.isThu = isEnable
                Week.Friday -> product.isFri = isEnable
                Week.Saturday -> product.isSat = isEnable
                Week.Sunday -> product.isSun = isEnable
            }
            productDao.insert(product)
        }
    }

    //更新菜品位置
    @Transaction
    fun updateProductPositions(items: List<Product>) {
        viewModelScope.launch {
            // 更新数据库中的 position 字段
            items.forEachIndexed { index, item ->
                item.position = index
            }
            productDao.updateProducts(items)
        }
    }

    //更新菜品
    @Transaction
    fun updateProduct(items: List<Product>) {
        viewModelScope.launch {
            productDao.updateProducts(items)
        }
    }
}