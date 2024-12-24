package com.hzy.restaurant.mvvm.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hzy.restaurant.bean.Order
import com.hzy.restaurant.db.dao.OrderDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by hzy in 2024/12/24
 * description:
 * */
@HiltViewModel
class MainViewModel @Inject constructor(private val dao: OrderDao) : ViewModel() {
    fun insertOrder(order: Order) {
        viewModelScope.launch(Dispatchers.Default) {
            dao.insert(order)
        }
    }
}