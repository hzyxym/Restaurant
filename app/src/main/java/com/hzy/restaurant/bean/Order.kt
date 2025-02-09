package com.hzy.restaurant.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * Created by hzy in 2024/12/21
 * description: 订单表
 * */
@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val orderId: Long,
    @ColumnInfo(name = "orderNo") val orderNo: Long,
    @ColumnInfo(name = "currentNo") val currentNo: Long,
    @ColumnInfo(name = "createTime") val createTime: Long,
    @TypeConverters(ProductListConverter::class)
    @ColumnInfo(name = "products") var products: List<Product>,
    @ColumnInfo(name = "packagesName") var packagesName: String? = null,
    @ColumnInfo(name = "packagesPrice") var packagesPrice: Double? = null,
    @ColumnInfo(name = "updateTime") var updateTime: Long? = null,
)
