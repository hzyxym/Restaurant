package com.hzy.restaurant.db.dao

import androidx.room.Dao
import androidx.room.Insert
import com.hzy.restaurant.bean.PackagesProductListCrossRef

/**
 * Created by hzy 2025/1/30
 * @Description:套餐菜品关系Dao
 */
@Dao
interface PackagesProductDao {
    @Insert
    suspend fun insertPackagesProductCrossRef(packagesProduct: PackagesProductListCrossRef)

    @Insert
    suspend fun insertPackagesProductCrossRefs(packagesProducts: List<PackagesProductListCrossRef>)
}