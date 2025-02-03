package com.hzy.restaurant.bean

/**
 * Created by hzy 2025/2/2
 * @Description: 带分类Header
 */
sealed class ProductItem {
    data class CategoryHeader(val categoryName: String) : ProductItem()
    data class ProductData(val product: Product) : ProductItem()
}