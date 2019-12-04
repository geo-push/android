package com.kipdev.geopushlib

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.preference.PreferenceManager
import android.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.DateFormat
import java.util.*

internal object Utils {
    fun getLocationText(location: Location?): String {
        return if (location == null)
            "Unknown location"
        else
            "(" + location.latitude + ", " + location.longitude + ")"
    }

    fun getLocationTitle(context: Context): String {
        return context.getString(
            R.string.text_location_updated,
            DateFormat.getDateTimeInstance().format(Date())
        )
    }
}


object AndroidPermissionsHelper {

    const val WRITE_EXTERNAL_STORAGE_CODE = 1

    const val ACCESS_COARSE_LOCATION = 1102
    const val ACCESS_FINE_LOCATION = 1103

    fun checkPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(context: Activity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(context, arrayOf(permission), requestCode)
    }

    fun requestLocationPermission(context: Context) {
        if (context is ContextThemeWrapper) {
            val activity = if (context.baseContext is Activity) context.baseContext as Activity else context as Activity
            AndroidPermissionsHelper.requestPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION,
                AndroidPermissionsHelper.ACCESS_FINE_LOCATION
            )
        }
    }

    fun checkLocationPermission(context: Context): Boolean {
        var k = AndroidPermissionsHelper.checkPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return k
    }
}