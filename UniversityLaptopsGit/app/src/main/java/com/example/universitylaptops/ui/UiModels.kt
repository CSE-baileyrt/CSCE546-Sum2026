package com.example.universitylaptops.ui

import com.example.universitylaptops.data.*

data class LaptopFormState(
    val laptopId: Long = 0L,
    val brand: String = "",
    val price: String = "",
    val screenSize: String = "",
    val hasHDMI: Boolean = false,
    val hasUSBC: Boolean = false,
    val processorManufacturer: String = "",
    val processorSpeed: String = "",
    val ramCapacity: String = "",
    val hardDrives: List<Pair<String, String>> = listOf("" to ""),
    val selectedStudentIds: Set<Long> = emptySet()
)