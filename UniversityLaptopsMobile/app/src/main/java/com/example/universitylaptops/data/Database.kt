package com.example.universitylaptops.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProcessorEntity::class,
        RamEntity::class,
        LaptopEntity::class,
        HardDriveEntity::class,
        StudentEntity::class,
        StudentLaptopCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun laptopDao(): LaptopDao
    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "university_laptops.db"
                )
                    .addCallback(AppDatabaseCallback(context))
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}