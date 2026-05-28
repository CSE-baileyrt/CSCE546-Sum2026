package com.example.geonotifier.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.geonotifier.data.AppDatabase
import com.example.geonotifier.data.SavedLocation
import com.example.geonotifier.geofence.GeofenceHelper
import kotlinx.coroutines.*

@Composable
fun AddLocationScreen(
    onSave: () -> Unit,
    onCancel: () -> Unit
) {

    val context = androidx.compose.ui.platform.LocalContext.current
    val dao = AppDatabase.getDatabase(context).locationDao()
    val geofenceHelper = GeofenceHelper(context)

    var lat by remember { mutableStateOf("") }
    var lng by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf("1.0") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("Add Location", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = lat,
            onValueChange = { lat = it },
            label = { Text("Latitude") }
        )

        TextField(
            value = lng,
            onValueChange = { lng = it },
            label = { Text("Longitude") }
        )

        TextField(
            value = radius,
            onValueChange = { radius = it },
            label = { Text("Radius (miles)") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {

            Button(onClick = onSave) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                val latitude = lat.toDoubleOrNull()
                val longitude = lng.toDoubleOrNull()
                val radiusMiles = radius.toDoubleOrNull()

                if (latitude != null && longitude != null && radiusMiles != null) {

                    CoroutineScope(Dispatchers.IO).launch {
                        val location = SavedLocation(
                            latitude = latitude,
                            longitude = longitude,
                            radiusMiles = radiusMiles
                        )

                        dao.insert(location)

                        // reload all + register geofences
                        val updated = dao.getAll()
                        geofenceHelper.addGeofences(updated)
                    }

                    onSave()
                }
            }) {
                Text("Save")
            }
        }
    }
}