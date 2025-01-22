package com.hzy.restaurant.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by hzy 2025/1/22
 * @Description: 菜品
 */
@Entity
data class Product(
    @PrimaryKey var productName: String,
    var marketPrice: Float,
    var categoryName: String? = null,
    var iconPath: String? = null
)
