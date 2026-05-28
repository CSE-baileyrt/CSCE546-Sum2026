package com.example.familytasks.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.familytasks.viewmodel.FamilyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    viewModel: FamilyViewModel,
    onBack: () -> Unit
) {
    var task by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        TopAppBar(
            title = { Text("Add task") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    androidx.compose.material3.Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = task,
            onValueChange = { task = it },
            label = { Text("Task") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = details,
            onValueChange = { details = it },
            label = { Text("Details") },
            modifier = Modifier.fillMaxWidth().height(140.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.addTask(task = task, details = details)
                onBack()
            },
            enabled = task.isNotBlank()
        ) {
            Text("Save task")
        }
    }
}
