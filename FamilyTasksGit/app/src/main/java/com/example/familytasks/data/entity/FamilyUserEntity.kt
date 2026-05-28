package com.example.familytasks.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class FamilyUserEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val tapCode: Int
)
