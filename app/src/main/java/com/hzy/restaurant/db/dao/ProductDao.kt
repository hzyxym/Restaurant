package com.hzy.restaurant.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hzy.restaurant.bean.Product

/**
 * Created by hzy 2025/1/22
 * @Description: 菜品DAO
 */
@Dao
interface ProductDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(product: Product)

    @Delete
    fun delete(product: Product)

    @Query("SELECT * FROM Product")
    fun getAll(): LiveData<List<Product>>
}