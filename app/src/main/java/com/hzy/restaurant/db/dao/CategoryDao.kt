package com.hzy.restaurant.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hzy.restaurant.bean.Category

/**
 * Created by hzy 2025/1/21
 * @Description: 分类DAO
 */
@Dao
interface CategoryDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category)

    @Delete
    fun delete(category: Category)

    @Query("SELECT * FROM Category")
    fun getAll(): LiveData<List<Category>>
}