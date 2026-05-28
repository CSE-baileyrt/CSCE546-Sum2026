// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    // Android Gradle Plugin
    id("com.android.application") version "8.5.0" apply false

    // Kotlin Android plugin
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}

// Optional: Clean task
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}