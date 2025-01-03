package com.hzy.restaurant.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hzy.restaurant.bean.Order
import com.hzy.restaurant.db.dao.OrderDao

/**
 * Created by hzy in 2024/4/2
 * description: 数据库
 * */
@Database(
    entities = [Order::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getOrderDao(): OrderDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "AC012.db")
//                .allowMainThreadQueries() // Room 原则上不允许在主线程操作数据库 如果要在主线程操作数据库需要调用该函数
//                .addMigrations(MIGRATION_1_2)
                .build()
        }

        //数据库升级
//        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE ImageReportBean ADD COLUMN environmentTemp TEXT")
//                database.execSQL("ALTER TABLE ImageReportBean ADD COLUMN environmentDistance TEXT")
//                database.execSQL("ALTER TABLE ImageReportBean ADD COLUMN environmentEmissivity TEXT")
//            }
//        }
    }
}