package com.example.universitylaptops.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.universitylaptops.LaptopsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaptopListScreen(
    viewModel: LaptopsViewModel,
    onLaptopClick: (Long) -> Unit,
    onAddClick: () -> Unit
) {
    val laptops by viewModel.laptopList.collectAsState()
    val brands by viewModel.brands.collectAsState()
    val lastNames by viewModel.lastNames.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add laptop")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterDropdown(
                    label = "Student last name",
                    items = lastNames,
                    onSelected = { viewModel.setLastNameFilter(it.takeIf { v -> v.isNotBlank() }) },
                    modifier = Modifier.weight(1f)
                )
                FilterDropdown(
                    label = "Brand",
                    items = brands,
                    onSelected = { viewModel.setBrandFilter(it.takeIf { v -> v.isNotBlank() }) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(laptops) { laptop ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onLaptopClick(laptop.laptopId) }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("${laptop.brand} • $${laptop.price}", style = MaterialTheme.typography.titleMedium)
                            Text("Screen: ${laptop.screenSize}")
                            Text("Student: ${laptop.studentName ?: "Unassigned"} ${laptop.studentLastName ?: ""}")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdown(
    label: String,
    items: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var selected by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("All") },
                onClick = {
                    selected = ""
                    onSelected("")
                    expanded = false
                }
            )
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        selected = item
                        onSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}