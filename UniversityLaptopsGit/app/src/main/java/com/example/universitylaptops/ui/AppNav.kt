package com.example.universitylaptops.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.universitylaptops.LaptopsViewModel

@Composable
fun AppNav(viewModel: LaptopsViewModel = viewModel(), navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            LaptopListScreen(
                viewModel = viewModel,
                onLaptopClick = { id -> navController.navigate("detail/$id") },
                onAddClick = { navController.navigate("add") }
            )
        }
        composable("detail/{id}") { backStack ->
            val id = backStack.arguments?.getString("id")!!.toLong()
            LaptopDetailScreen(
                laptopId = id,
                viewModel = viewModel,
                onEditClick = { navController.navigate("edit/$id") },
                onDeleteClick = {
                    viewModel.deleteLaptop(id)
                    navController.popBackStack("list", false)
                }
            )
        }
        composable("add") {
            LaptopFormScreen(
                title = "Add Laptop",
                laptopId = null,
                viewModel = viewModel,
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
        composable("edit/{id}") { backStack ->
            val id = backStack.arguments?.getString("id")!!.toLong()
            LaptopFormScreen(
                title = "Edit Laptop",
                laptopId = id,
                viewModel = viewModel,
                onSave = { navController.popBackStack("detail/$id", false) },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}