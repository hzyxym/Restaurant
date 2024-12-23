package com.hzy.restaurant.bean

import androidx.room.Entity

/**
 * Created by hzy in 2024/12/21
 * description:
 * */
@Entity(tableName = "orders")
data class Order(val id: Long)
