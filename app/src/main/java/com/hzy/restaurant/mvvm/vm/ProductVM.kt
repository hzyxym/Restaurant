package com.hzy.restaurant.mvvm.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.hzy.restaurant.bean.Product
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
}