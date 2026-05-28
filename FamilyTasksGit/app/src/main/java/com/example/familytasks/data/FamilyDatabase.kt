package com.example.familytasks.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.familytasks.data.dao.ColorPreferenceDao
import com.example.familytasks.data.dao.TaskDao
import com.example.familytasks.data.dao.UserDao
import com.example.familytasks.data.entity.ColorPreferenceEntity
import com.example.familytasks.data.entity.FamilyUserEntity
import com.example.familytasks.data.entity.TaskEntity

@Database(
    entities = [FamilyUserEntity::class, TaskEntity::class, ColorPreferenceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FamilyDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun colorPreferenceDao(): ColorPreferenceDao

    companion object {
        @Volatile
        private var INSTANCE: FamilyDatabase? = null

        fun getInstance(context: Context): FamilyDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FamilyDatabase::class.java,
                    "family_tasks.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
