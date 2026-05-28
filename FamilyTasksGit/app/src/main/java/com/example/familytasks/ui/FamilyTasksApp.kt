package com.example.familytasks.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.familytasks.data.entity.ColorPreferenceEntity
import com.example.familytasks.data.entity.FamilyUserEntity
import com.example.familytasks.ui.screens.AddTaskScreen
import com.example.familytasks.ui.screens.LoginScreen
import com.example.familytasks.ui.screens.ManageUsersScreen
import com.example.familytasks.ui.screens.TaskListScreen
import com.example.familytasks.ui.theme.backgroundFromHex
import com.example.familytasks.viewmodel.FamilyViewModel

private object Routes {
    const val Login = "login"
    const val Tasks = "tasks"
    const val AddTask = "add_task"
    const val Users = "users"
}

@Composable
fun FamilyTasksApp(
    viewModel: FamilyViewModel,
    lightLux: Float?
) {
    val navController = rememberNavController()
    val currentUser by viewModel.currentUser.collectAsState()
    val currentColorPref by viewModel.currentColorPreference.collectAsState()

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate(Routes.Tasks) {
                launchSingleTop = true
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
            }
        }
    }

    val backgroundColor = remember(currentUser, currentColorPref, lightLux) {
        resolveBackgroundColor(currentUser, currentColorPref, lightLux)
    }
    val onBackground = remember(backgroundColor) {
        if (backgroundColor.luminance() < 0.45f) Color.White else Color.Black
    }

    Surface(color = backgroundColor, contentColor = onBackground, modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = Routes.Login) {
            composable(Routes.Login) {
                LoginScreen(
                    viewModel = viewModel,
                    onEditUsers = { navController.navigate(Routes.Users) }
                )
            }
            composable(Routes.Users) {
                ManageUsersScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.Tasks) {
                TaskListScreen(
                    viewModel = viewModel,
                    backgroundColor = backgroundColor,
                    onAddTask = { navController.navigate(Routes.AddTask) },
                    onManageUsers = { navController.navigate(Routes.Users) },
                    onLogout = {
                        viewModel.logout()
                        navController.navigate(Routes.Login) {
                            launchSingleTop = true
                            popUpTo(Routes.Tasks) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.AddTask) {
                AddTaskScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

private fun resolveBackgroundColor(
    user: FamilyUserEntity?,
    colorPreference: ColorPreferenceEntity?,
    lux: Float?
): Color {
    if (user == null || colorPreference == null) return Color(0xFF121212)
    val brightRoom = (lux ?: 0f) >= 1000f
    val hex = if (brightRoom) colorPreference.brightHex else colorPreference.mutedHex
    return backgroundFromHex(hex)
}
