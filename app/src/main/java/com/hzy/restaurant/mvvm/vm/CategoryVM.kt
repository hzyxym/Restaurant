package com.hzy.restaurant.mvvm.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.hzy.restaurant.bean.Category
import com.hzy.restaurant.db.dao.CategoryDao
import com.hzy.restaurant.db.dao.ProductDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by hzy 2025/1/21
 * @Description: 分类vm
 */
@HiltViewModel
class CategoryVM @Inject constructor(
    private val categoryDao: CategoryDao,
    private val productDao: ProductDao
) : ViewModel() {
    val categoryList = categoryDao.getAll()

    //添加分类
    fun addCategory(category: Category) {
        viewModelScope.launch(Dispatchers.Default) {
            if (category.position == -1) {
                category.position = (categoryDao.getMaxPosition() ?: -1) + 1
            }
            categoryDao.insert(category)
        }
    }

    //删除分类
    fun delCategory(category: Category) {
        viewModelScope.launch(Dispatchers.Default) {
            categoryDao.delete(category)
            productDao.setCategoryToNullFor(category.categoryName)
        }
    }

    @Transaction
    fun updateCategoryPositions(items: List<Category>) {
        viewModelScope.launch {
            // 更新数据库中的 position 字段
            items.forEachIndexed { index, item ->
                item.position = index
            }
            categoryDao.updateCategories(items)
        }
    }
}