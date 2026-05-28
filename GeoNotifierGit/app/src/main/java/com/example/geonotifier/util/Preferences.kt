package com.example.geonotifier.util

import android.content.Context

object Preferences {

    private const val PREFS_NAME = "settings"
    private const val KEY_PHONE_NUMBER = "phone_number"

    /**
     * Save the recipient phone number
     */
    fun savePhoneNumber(context: Context, phoneNumber: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_PHONE_NUMBER, phoneNumber)
            .apply()
    }

    /**
     * Retrieve the saved phone number
     */
    fun getPhoneNumber(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_PHONE_NUMBER, null)
    }

    /**
     * Clear saved phone number (optional utility)
     */
    fun clearPhoneNumber(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .remove(KEY_PHONE_NUMBER)
            .apply()
    }
}