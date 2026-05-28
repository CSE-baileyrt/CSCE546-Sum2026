package com.example.familytasks.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "color_preferences")
data class ColorPreferenceEntity(
    @PrimaryKey val userId: String,
    val brightHex: String,
    val mutedHex: String
)
