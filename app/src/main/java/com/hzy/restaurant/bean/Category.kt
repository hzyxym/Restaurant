package com.hzy.restaurant.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Created by hzy 2025/1/21
 * @Description:菜单分类
 */

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "categoryName") var categoryName: String,
    @ColumnInfo(name = "position") var position: Int
)

