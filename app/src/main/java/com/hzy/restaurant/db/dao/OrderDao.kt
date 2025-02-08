package com.hzy.restaurant.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hzy.restaurant.bean.Order

/**
 * Created by hzy in 2024/12/23
 * description: 订单Dao
 * */
@Dao
interface OrderDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(order: Order)

    @Query("SELECT MAX(currentNo) FROM ORDERS Where createTime > :currentDayTime")
    suspend fun getMaxCurrentNo(currentDayTime: Long): Long?

    @Update
    suspend fun updateOrder(items: List<Order>)
}