package com.example.universitylaptops

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.universitylaptops.data.AppDatabase
import com.example.universitylaptops.data.LaptopRepository
import com.example.universitylaptops.ui.AppNav

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getInstance(this)
        val repo = LaptopRepository(db.laptopDao(), db.studentDao())

        setContent {
            val vm = androidx.lifecycle.viewmodel.compose.viewModel<LaptopsViewModel>(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return LaptopsViewModel(repo) as T
                    }
                }
            )
            AppNav(viewModel = vm)
        }
    }
}