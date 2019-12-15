package com.geopush.library

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule
import kotlin.concurrent.timerTask

private val UPDATE_INTERVAL: Long = TimeUnit.MINUTES.toMillis(1)

class LocationManager(
    private val context: Context,
    private val updateInterval: Long = UPDATE_INTERVAL,
    private val fastestUpdateInterval: Long = UPDATE_INTERVAL/2){

    fun startListening(waitPermissions: Boolean  = false, onLocation: (lat: Double, lng: Double) -> Unit){
        GeoLog.log("Start tracking")
        if(permissionGranted(context)){
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    GeoLog.log("New Location: ${locationResult.lastLocation.latitude}, ${locationResult.lastLocation.longitude}")
                    onLocation(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
                }
            }
            fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper())
        }else if(waitPermissions){
            GeoLog.log("Location permissions not granted. Waiting")
            //throw Exception("waitPermissions: Unsupported in current version")
            val timer = Timer()
            timer.schedule(timerTask {
                if (permissionGranted(context)) {
                    GeoLog.log("Timer: granted!!!")
                    timer.cancel()
                    startListening(waitPermissions, onLocation)
                }else{
                    GeoLog.log("Timer: not granted")
                }
            },1000, TimeUnit.MINUTES.toMillis(1))
        }else{
            GeoLog.log("Ignore location")
        }
    }

    private fun createLocationRequest() = LocationRequest.create()?.apply {
        interval = updateInterval
        fastestInterval = fastestUpdateInterval
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    private fun permissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}