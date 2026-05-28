package com.example.universitylaptops.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.universitylaptops.LaptopsViewModel
import com.example.universitylaptops.data.*

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun LaptopFormScreen(
    title: String,
    laptopId: Long?,
    viewModel: LaptopsViewModel,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val students by viewModel.observeAllStudents().collectAsState(initial = emptyList())
    var brand by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var screenSize by remember { mutableStateOf("") }
    var hasHDMI by remember { mutableStateOf(false) }
    var hasUSBC by remember { mutableStateOf(false) }
    var processorManufacturer by remember { mutableStateOf("") }
    var processorSpeed by remember { mutableStateOf("") }
    var ramCapacity by remember { mutableStateOf("") }
    var hardDrives by remember { mutableStateOf(listOf("" to "")) }
    var selectedStudentIds by remember { mutableStateOf(setOf<Long>()) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(title) }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(brand, { brand = it }, label = { Text("Brand") })
                OutlinedTextField(price, { price = it }, label = { Text("Price") })
                OutlinedTextField(screenSize, { screenSize = it }, label = { Text("Screen size") })
                Row {
                    Checkbox(checked = hasHDMI, onCheckedChange = { hasHDMI = it })
                    Text("HDMI")
                    Spacer(Modifier.width(16.dp))
                    Checkbox(checked = hasUSBC, onCheckedChange = { hasUSBC = it })
                    Text("USB-C")
                }
                OutlinedTextField(processorManufacturer, { processorManufacturer = it }, label = { Text("Processor manufacturer") })
                OutlinedTextField(processorSpeed, { processorSpeed = it }, label = { Text("Processor speed") })
                OutlinedTextField(ramCapacity, { ramCapacity = it }, label = { Text("RAM capacity") })
            }

            item {
                Text("Hard drives", style = MaterialTheme.typography.titleMedium)
            }

            itemsIndexed(hardDrives) { index, drive ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = drive.first,
                        onValueChange = { newValue ->
                            hardDrives = hardDrives.toMutableList().also { it[index] = newValue to drive.second }
                        },
                        label = { Text("Manufacturer") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = drive.second,
                        onValueChange = { newValue ->
                            hardDrives = hardDrives.toMutableList().also { it[index] = drive.first to newValue }
                        },
                        label = { Text("Capacity") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Button(onClick = { hardDrives = hardDrives + ("" to "") }) { Text("Add hard drive") }
            }

            item {
                Text("Assign to students", style = MaterialTheme.typography.titleMedium)
                students.forEach { student ->
                    val checked = selectedStudentIds.contains(student.id)
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                selectedStudentIds = if (it) selectedStudentIds + student.id else selectedStudentIds - student.id
                            }
                        )
                        Text("${student.lastName}, ${student.firstName}")
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        val laptop = LaptopEntity(
                            id = laptopId ?: System.currentTimeMillis(),
                            brand = brand,
                            price = price.toDoubleOrNull() ?: 0.0,
                            screenSize = screenSize.toDoubleOrNull() ?: 0.0,
                            hasHDMI = hasHDMI,
                            hasUSBC = hasUSBC,
                            processorId = System.currentTimeMillis() + 1,
                            ramId = System.currentTimeMillis() + 2
                        )
                        val processor = ProcessorEntity(
                            id = laptop.processorId,
                            manufacturer = processorManufacturer,
                            speed = processorSpeed.toDoubleOrNull() ?: 0.0
                        )
                        val ram = RamEntity(
                            id_manufacturer = laptop.ramId,
                            capacity = ramCapacity.toIntOrNull() ?: 0
                        )
                        val drives = hardDrives.filter { it.first.isNotBlank() && it.second.isNotBlank() }.mapIndexed { idx, item ->
                            HardDriveEntity(
                                id = (laptop.id * 10) + idx,
                                manufacturer = item.first,
                                capacity = item.second.toIntOrNull() ?: 0,
                                laptopOwnerId = laptop.id
                            )
                        }
                        viewModel.saveLaptop(laptop, processor, ram, drives, selectedStudentIds.toList())
                        onSave()
                    }) { Text("Save") }
                    OutlinedButton(onClick = onCancel) { Text("Cancel") }
                }
            }
        }
    }
}