package com.example.geonotifier.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.geonotifier.data.AppDatabase
import com.example.geonotifier.geofence.GeofenceHelper

// ✅ IMPORTANT: These imports fix your error
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val permissionRequestCode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions()

        setContent {
            var screen by remember { mutableStateOf<ScreenState>(ScreenState.Main) }

            when (screen) {
                ScreenState.Main -> MainScreen(
                    onAddClick = { screen = ScreenState.AddLocation },
                    onSettingsClick = { screen = ScreenState.Settings }
                )

                ScreenState.AddLocation -> AddLocationScreen(
                    onSave = { screen = ScreenState.Main },
                    onCancel = { screen = ScreenState.Main }
                )

                ScreenState.Settings -> SettingsScreen(
                    onBack = { screen = ScreenState.Main }
                )
            }
        }


        setupGeofences()
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.SEND_SMS
        )

        val missing = permissions.filter { permission ->
            ContextCompat.checkSelfPermission(this@MainActivity, permission) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                missing.toTypedArray(),
                permissionRequestCode
            )
        }
    }

    private fun setupGeofences() {
        val db = AppDatabase.getDatabase(this)
        val dao = db.locationDao()
        val helper = GeofenceHelper(this)

        CoroutineScope(Dispatchers.IO).launch {
            val locations = dao.getAll()
            if (locations.isNotEmpty()) {
                helper.addGeofences(locations)
            }
        }
    }
}

@Composable
fun MainScreen() {
    var text by remember { mutableStateOf("GeoNotifier Running") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = text)

        Spacer(modifier = Modifier.height(16.dp))

        Text("App is monitoring saved geofence locations.")
    }
}
