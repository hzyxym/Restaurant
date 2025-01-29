package com.hzy.restaurant.bean

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * Created by hzy 2025/1/30
 * @Description: 套餐、菜品关联实体
 */
@Entity(
    primaryKeys = ["packagesId", "productName"],
    foreignKeys = [
        ForeignKey(
            entity = Packages::class,
            parentColumns = ["packagesId"],
            childColumns = ["packagesId"],
            onDelete = ForeignKey.CASCADE // 启用级联删除
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["productName"],
            childColumns = ["productName"],
            onDelete = ForeignKey.CASCADE // 启用级联删除
        )
    ]
)
data class PackagesProductListCrossRef(
    var packagesId: Long,
    var productName: String
)
