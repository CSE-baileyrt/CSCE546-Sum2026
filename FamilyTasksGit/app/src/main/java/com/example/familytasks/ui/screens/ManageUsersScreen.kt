package com.example.familytasks.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.familytasks.viewmodel.FamilyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(
    viewModel: FamilyViewModel,
    onBack: () -> Unit
) {
    val users by viewModel.users.collectAsState()
    var sonName by remember { mutableStateOf("") }
    var wifeName by remember { mutableStateOf("") }
    var dadName by remember { mutableStateOf("") }

    LaunchedEffect(users) {
        sonName = users.firstOrNull { it.tapCode == 3 }?.displayName.orEmpty()
        wifeName = users.firstOrNull { it.tapCode == 4 }?.displayName.orEmpty()
        dadName = users.firstOrNull { it.tapCode == 5 }?.displayName.orEmpty()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        TopAppBar(
            title = { Text("Edit profiles") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text("Assign names to the three tap codes.", fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(20.dp))

        ProfileEditor(label = "3 taps = Son", value = sonName, onValueChange = { sonName = it })
        ProfileEditor(label = "4 taps = Wife", value = wifeName, onValueChange = { wifeName = it })
        ProfileEditor(label = "5 taps = Dad", value = dadName, onValueChange = { dadName = it })

        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            viewModel.saveUserNames(
                sonName = sonName,
                wifeName = wifeName,
                dadName = dadName
            )
        }) {
            Text("Save")
        }
    }
}

@Composable
private fun ProfileEditor(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Text(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}
