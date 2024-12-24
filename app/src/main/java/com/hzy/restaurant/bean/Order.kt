package com.hzy.restaurant.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by hzy in 2024/12/21
 * description:
 * */
@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val createTime: Long,
    var updateTime: Long,
)
