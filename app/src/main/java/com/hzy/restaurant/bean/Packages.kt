package com.hzy.restaurant.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by hzy 2025/1/29
 * @Description: 套餐实体
 */
@Entity
data class Packages(
    @PrimaryKey(autoGenerate = true) val packagesId: Long = 0,
    @ColumnInfo(name = "packagesName") var packagesName: String,
    @ColumnInfo(name = "packagesPrice") var packagesPrice: Float,
    @ColumnInfo(name = "packagesSubName") var packagesSubName: String? = null,
    @ColumnInfo(name = "iconPath") var iconPath: String? = null,
    @ColumnInfo(name = "position") var position: Int = 0
)
