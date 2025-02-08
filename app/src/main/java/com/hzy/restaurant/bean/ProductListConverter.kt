package com.hzy.restaurant.bean

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductListConverter {
    @TypeConverter
    fun stringToProductList(data: String?): List<Product> {
        if (data == null) {
            return emptyList()
        }
        val listType = object : TypeToken<List<Product>>() {}.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun productListToString(products: List<Product>): String {
        return Gson().toJson(products)
    }
}