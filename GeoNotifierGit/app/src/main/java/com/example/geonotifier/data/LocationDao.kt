package com.example.geonotifier.data

import androidx.room.*

@Dao
interface LocationDao {

    @Insert
    suspend fun insert(location: SavedLocation)

    @Query("SELECT * FROM locations")
    suspend fun getAll(): List<SavedLocation>

    @Delete
    suspend fun delete(location: SavedLocation)
}