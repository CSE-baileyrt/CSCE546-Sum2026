package com.example.geonotifier.ui

/**
 * Represents the different screens in the app.
 * This is a simple sealed class used for manual navigation
 * without needing a navigation library.
 */
sealed class ScreenState {

    // Main screen (dashboard)
    object Main : ScreenState()

    // Screen for adding a new geofence location
    object AddLocation : ScreenState()

    // Settings screen (phone number for SMS)
    object Settings : ScreenState()
}
