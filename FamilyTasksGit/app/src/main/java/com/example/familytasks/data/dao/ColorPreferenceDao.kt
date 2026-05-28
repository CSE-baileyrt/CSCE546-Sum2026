package com.example.familytasks.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.familytasks.data.entity.ColorPreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ColorPreferenceDao {
    @Query("SELECT * FROM color_preferences ORDER BY userId ASC")
    fun observeAll(): Flow<List<ColorPreferenceEntity>>

    @Query("SELECT * FROM color_preferences WHERE userId = :userId LIMIT 1")
    fun observeByUserId(userId: String): Flow<ColorPreferenceEntity?>

    @Query("SELECT * FROM color_preferences ORDER BY userId ASC")
    suspend fun getAllOnce(): List<ColorPreferenceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ColorPreferenceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<ColorPreferenceEntity>)
}
