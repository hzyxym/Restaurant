package com.hzy.restaurant.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by hzy 2025/1/21
 * @Description:菜单分类
 */

@Entity
data class Category(@PrimaryKey(autoGenerate = true) val id: Long = 0, var name: String)
