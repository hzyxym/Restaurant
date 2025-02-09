package com.hzy.restaurant.mvvm.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.hzy.restaurant.bean.Category
import com.hzy.restaurant.bean.Packages
import com.hzy.restaurant.bean.PackagesProductListCrossRef
import com.hzy.restaurant.bean.Product
import com.hzy.restaurant.db.dao.PackagesDao
import com.hzy.restaurant.db.dao.PackagesProductDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by hzy 2025/2/1
 * @Description:套餐VM
 */
@HiltViewModel
class PackagesVM @Inject constructor(
    private val packagesDao: PackagesDao,
    private val packageProductDao: PackagesProductDao
) : ViewModel() {
    val getPackagesList = packagesDao.getAll()

    fun savePackages(packages: Packages, selectProduct: List<Product>) {
        viewModelScope.launch(Dispatchers.Default) {
            if (packages.position == -1) {
                packages.position = (packagesDao.getMaxPosition() ?: -1) + 1
            }
            val id = packagesDao.insert(packages)
            selectProduct.forEach {
                val packagesProductListCrossRef = PackagesProductListCrossRef(id, it.productId)
                packageProductDao.insertPackagesProductCrossRef(packagesProductListCrossRef)
            }
        }
    }

    //删除套餐
    fun delPackages(vararg packages: Packages) {
        viewModelScope.launch(Dispatchers.Default) {
            packagesDao.delete(*packages)
        }
    }

    @Transaction
    fun updatePackagesPositions(items: List<Packages>) {
        viewModelScope.launch(Dispatchers.Default) {
            // 更新数据库中的 position 字段
            items.forEachIndexed { index, item ->
                item.position = index
            }
            packagesDao.updateProducts(items)
        }
    }
}