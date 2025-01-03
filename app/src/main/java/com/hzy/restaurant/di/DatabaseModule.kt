package com.hzy.restaurant.di

import android.content.Context
import com.hzy.restaurant.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by hzy in 2023/4/3
 * description: ble历史数据module
 * */
@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideOrderDao(appDatabase: AppDatabase) = appDatabase.getOrderDao()

}