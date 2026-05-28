package com.example.geonotifier.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.geonotifier.util.Preferences

@Composable
fun SettingsScreen(onBack: () -> Unit) {

    val context = LocalContext.current

    var phone by remember {
        mutableStateOf(Preferences.getPhoneNumber(context) ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Settings", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Recipient Phone Number")

        TextField(
            value = phone,
            onValueChange = { phone = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {

            Button(onClick = onBack) {
                Text("Back")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                Preferences.savePhoneNumber(context, phone)
                onBack()
            }) {
                Text("Save")
            }
        }
    }
}
