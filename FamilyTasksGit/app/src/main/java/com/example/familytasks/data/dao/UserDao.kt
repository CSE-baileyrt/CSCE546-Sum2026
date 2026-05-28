package com.example.familytasks.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.familytasks.data.entity.FamilyUserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY tapCode ASC")
    fun observeAll(): Flow<List<FamilyUserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<FamilyUserEntity?>

    @Query("SELECT * FROM users ORDER BY tapCode ASC")
    suspend fun getAllOnce(): List<FamilyUserEntity>

    @Query("SELECT * FROM users WHERE tapCode = :tapCode LIMIT 1")
    suspend fun getByTapCode(tapCode: Int): FamilyUserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: FamilyUserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(users: List<FamilyUserEntity>)

    @Update
    suspend fun update(user: FamilyUserEntity)
}
