package com.hzy.restaurant.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.hzy.restaurant.bean.Order

/**
 * Created by hzy in 2024/12/23
 * description:
 * */
@Dao
interface OrderDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(order: Order)
}