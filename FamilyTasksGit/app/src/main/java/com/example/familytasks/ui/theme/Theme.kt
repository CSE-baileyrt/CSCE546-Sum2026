package com.example.familytasks.ui.theme

import android.graphics.Color.parseColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppDarkColors = darkColorScheme()

@Composable
fun FamilyTasksTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppDarkColors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}

fun backgroundFromHex(hex: String): Color = Color(parseColor(hex))
