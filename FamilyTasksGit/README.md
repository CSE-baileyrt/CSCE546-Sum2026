# Family Tasks Android App

This project is a single-module Android app written in Kotlin with Jetpack Compose and Room.

## What it does
- Stores users, tasks, and user color preferences in Room / SQLite.
- Identifies a family member by a custom tap code on the login screen.
- 3 taps = Son, 4 taps = Wife, 5 taps = Dad.
- Shows only the active user's tasks.
- Deletes a task when the trash icon is tapped.
- Lets the current user add a new task.
- Adjusts the background hue based on the ambient light sensor.

## Start from a new Android Studio project
1. Open Android Studio.
2. Choose **New Project**.
3. Select **Empty Activity** and make sure **Jetpack Compose** is enabled.
4. Use these settings:
   - **Name:** FamilyTasks
   - **Package name:** com.example.familytasks
   - **Language:** Kotlin
   - **Minimum SDK:** 24 or higher
5. Finish the wizard.
6. Replace the generated project files with the files in this archive.
7. Sync Gradle.
8. Run on a device or emulator.

## Notes
- The light sensor is optional. If the device does not expose it, the app falls back to the muted color variant.
- Tap-code identification is local to the app and is not Android biometric authentication.
- If you want real fingerprint-based login, Android's standard biometric API only gives authentication results, not raw fingerprint templates.
