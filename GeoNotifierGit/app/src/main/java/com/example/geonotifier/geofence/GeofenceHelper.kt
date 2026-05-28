package com.example.geonotifier.geofence

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.geonotifier.data.SavedLocation
import com.google.android.gms.location.*

class GeofenceHelper(private val context: Context) {

    private val client = LocationServices.getGeofencingClient(context)

    fun addGeofences(locations: List<SavedLocation>) {
        val geofences = locations.map {
            Geofence.Builder()
                .setRequestId(it.id.toString())
                .setCircularRegion(
                    it.latitude,
                    it.longitude,
                    (it.radiusMiles * 1609.34).toFloat()
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        }

        val request = GeofencingRequest.Builder()
            .addGeofences(geofences)
            .build()

        client.addGeofences(request, getPendingIntent())
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
}