package com.example.familytasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.familytasks.sensor.AmbientLightMonitor
import com.example.familytasks.ui.FamilyTasksApp
import com.example.familytasks.ui.theme.FamilyTasksTheme
import com.example.familytasks.viewmodel.FamilyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val lightMonitor = remember { AmbientLightMonitor(context) }

            DisposableEffect(Unit) {
                lightMonitor.start()
                onDispose { lightMonitor.stop() }
            }

            val factory = remember { FamilyViewModelFactory(application) }
            val viewModel: FamilyViewModel = viewModel(factory = factory)
            val lux by lightMonitor.lux.collectAsState()

            FamilyTasksTheme {
                FamilyTasksApp(
                    viewModel = viewModel,
                    lightLux = lux
                )
            }
        }
    }
}

class FamilyViewModelFactory(private val application: android.app.Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FamilyViewModel::class.java)) {
            return FamilyViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
