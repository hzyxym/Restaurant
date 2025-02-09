package com.hzy.restaurant.bean

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Created by hzy 2025/1/22
 * @Description: 菜品
 */
@Entity(indices = [Index(value = ["productName"], unique = true)])
data class Product(
    @PrimaryKey(autoGenerate = true) val productId: Long = 0,
    var productName: String,
    var marketPrice: Double,
    var isSoldOut: Boolean = false,
    var position: Int,
    var categoryName: String? = null,
    var iconPath: String? = null,
    var isMon: Boolean = false,
    var isTue: Boolean = false,
    var isWed: Boolean = false,
    var isThu: Boolean = false,
    var isFri: Boolean = false,
    var isSat: Boolean = false,
    var isSun: Boolean = false,
) : Serializable {
    @Ignore var isCheck: Boolean = false
}
