package com.hzy.restaurant.mvvm.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
 * @Description:
 */
@HiltViewModel
class PackagesVM @Inject constructor(
    private val packagesDao: PackagesDao,
    private val packageProductDao: PackagesProductDao
) : ViewModel() {
    fun savePackages(packages: Packages, selectProduct: List<Product>) {
        viewModelScope.launch(Dispatchers.Default) {
            val id = packagesDao.insert(packages)
            selectProduct.forEach {
                val packagesProductListCrossRef = PackagesProductListCrossRef(id, it.productName)
                packageProductDao.insertPackagesProductCrossRef(packagesProductListCrossRef)
            }
        }
    }
}