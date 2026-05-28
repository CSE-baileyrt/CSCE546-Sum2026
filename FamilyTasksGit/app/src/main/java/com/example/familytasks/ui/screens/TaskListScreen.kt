package com.example.familytasks.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.example.familytasks.viewmodel.FamilyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: FamilyViewModel,
    backgroundColor: Color,
    onAddTask: () -> Unit,
    onManageUsers: () -> Unit,
    onLogout: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val contentColor = if (backgroundColor.luminance() < 0.45f) Color.White else Color.Black

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text(currentUser?.displayName ?: "Tasks") },
                actions = {
                    IconButton(onClick = onManageUsers) {
                        Icon(Icons.Filled.People, contentDescription = "Manage profiles")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Log out")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Icon(Icons.Filled.Add, contentDescription = "Add task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            if (tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                    Text("No tasks yet.", color = contentColor)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(tasks, key = { it.id }) { task ->
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(task.task, style = MaterialTheme.typography.titleMedium)
                                    if (task.details.isNotBlank()) {
                                        Text(task.details, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                                IconButton(onClick = { viewModel.deleteTask(task) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete task")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
