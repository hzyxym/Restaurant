package com.hzy.restaurant.mvvm.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hzy.restaurant.bean.Category
import com.hzy.restaurant.db.dao.CategoryDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by hzy 2025/1/21
 * @Description:
 */
@HiltViewModel
class CategoryVM @Inject constructor(private val categoryDao: CategoryDao) : ViewModel() {
    val categoryList = categoryDao.getAll()

    //添加分类
    fun addCategory(category: Category) {
        viewModelScope.launch(Dispatchers.Default) {
            categoryDao.insert(category)
        }
    }

    //删除分类
    fun delCategory(category: Category) {
        viewModelScope.launch(Dispatchers.Default) {
            categoryDao.delete(category)
        }
    }
}