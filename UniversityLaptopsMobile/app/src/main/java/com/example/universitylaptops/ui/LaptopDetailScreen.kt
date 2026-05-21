package com.example.universitylaptops.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.universitylaptops.LaptopsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaptopDetailScreen(
    laptopId: Long,
    viewModel: LaptopsViewModel,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val laptop by viewModel.observeLaptop(laptopId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laptop Details") },
                actions = {
                    IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                    IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, contentDescription = "Delete") }
                }
            )
        }
    ) { padding ->
        val item = laptop ?: return@Scaffold
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Brand: ${item.laptop.brand}")
            Text("Price: $${item.laptop.price}")
            Text("Screen size: ${item.laptop.screenSize}")
            Text("HDMI: ${item.laptop.hasHDMI}")
            Text("USB-C: ${item.laptop.hasUSBC}")
            Text("Processor: ${item.processor.manufacturer} ${item.processor.speed} GHz")
            Text("RAM: ${item.ram.capacity} GB")
            Text("Hard drives:")
            item.hardDrives.forEach { hd ->
                Text("- ${hd.manufacturer} ${hd.capacity} GB")
            }
        }
    }
}