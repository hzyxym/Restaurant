package com.hzy.restaurant.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hzy.restaurant.bean.Packages
import com.hzy.restaurant.bean.PackagesWithProductList

/**
 * Created by hzy 2025/1/30
 * @Description: 套餐Dao
 */
@Dao
interface PackagesDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(packages: Packages)

    @Delete
    fun delete(packages: Packages)

    @Query("SELECT * FROM Packages ORDER BY position")
    fun getAll(): LiveData<List<PackagesWithProductList>>


    @Query("SELECT MAX(position) FROM Packages")
    suspend fun getMaxPosition(): Int?

    @Update
    suspend fun updateProducts(items: List<Packages>)
}