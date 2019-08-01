package com.kipdev.geopushlib.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.kipdev.geopushlib.service.LocationUpdatesService

private val mServiceConnection = object : ServiceConnection {

    private var mService: LocationUpdatesService? = null

    private var mBound = false

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        val binder = service as LocationUpdatesService.LocalBinder
        mService = binder.service
        mService?.requestLocationUpdates()
        mBound = true
    }

    override fun onServiceDisconnected(name: ComponentName) {
        mService = null
        mBound = false
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        p0?.applicationContext?.bindService(
            Intent(p0, LocationUpdatesService::class.java), mServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }
}

class UpdateReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        p0?.applicationContext?.bindService(
            Intent(p0, LocationUpdatesService::class.java), mServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }
}