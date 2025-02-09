package com.hzy.restaurant.bean

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Junction
import androidx.room.Relation

/**
 * Created by hzy 2025/1/29
 * @Description: 套餐综合实体
 */
data class PackagesWithProductList(
    @Embedded val packages: Packages,
    @Relation(
        parentColumn = "packagesId",
        entityColumn = "productId",
        associateBy = Junction(PackagesProductListCrossRef::class)
    )
    val products: List<Product>
) {
    @Ignore
    var isCheck: Boolean = false
}
