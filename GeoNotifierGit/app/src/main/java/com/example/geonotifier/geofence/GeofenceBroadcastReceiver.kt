package com.example.geonotifier.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.example.geonotifier.R

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return

        if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val phone = prefs.getString("phone_number", null) ?: return

            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(
                phone,
                null,
                context.getString(R.string.geofence_alert),
                null,
                null
            )
        }
    }
}