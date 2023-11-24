package com.example.sensorapp

import android.content.Context
import android.location.Geocoder
import android.location.Location
import java.io.IOException
import java.util.Locale

object LocationUtils {
    fun getLocation(context: Context, location: Location): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                return "${address.locality}, ${address.adminArea}"
            }
        } catch (e: IOException) {
            // Handle the IOException (e.g., network issue)
            e.printStackTrace()
        }
        return "Unknown Location"
    }
}
