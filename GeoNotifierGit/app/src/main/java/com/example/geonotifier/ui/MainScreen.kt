package com.example.geonotifier.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext

import com.example.geonotifier.data.AppDatabase
import com.example.geonotifier.data.SavedLocation
import com.example.geonotifier.util.LocationTracker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MainScreen(
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val context = LocalContext.current
    val dao = AppDatabase.getDatabase(context).locationDao()
    val tracker = remember { LocationTracker(context) }

    var locations by remember { mutableStateOf<List<SavedLocation>>(emptyList()) }
    var currentLat by remember { mutableStateOf("Loading...") }
    var currentLng by remember { mutableStateOf("") }

    // ✅ Load saved locations from Room
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            locations = dao.getAll()
        }
    }

    // ✅ Collect location updates every minute
    LaunchedEffect(Unit) {
        tracker.getLocationUpdates().collectLatest { location ->
            currentLat = location.latitude.toString()
            currentLng = location.longitude.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔹 Current Location Section
        Text(
            text = "Current Location",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Lat: $currentLat")
        Text("Lng: $currentLng")

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Saved Locations Section
        Text(
            text = "Saved Locations",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (locations.isEmpty()) {
            Text("No locations saved yet.")
        } else {
            locations.forEach { location ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Lat: ${location.latitude}")
                        Text("Lng: ${location.longitude}")
                        Text("Radius: ${location.radiusMiles} miles")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 🔹 Buttons Section
        Button(
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Location")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSettingsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Settings")
        }
    }
}
