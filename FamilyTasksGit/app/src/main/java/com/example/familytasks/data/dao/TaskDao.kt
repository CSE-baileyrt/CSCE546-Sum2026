package com.example.familytasks.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.familytasks.data.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE owner = :owner ORDER BY id DESC")
    fun observeForOwner(owner: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE owner = :owner ORDER BY id DESC")
    suspend fun getForOwnerOnce(owner: String): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)
}
