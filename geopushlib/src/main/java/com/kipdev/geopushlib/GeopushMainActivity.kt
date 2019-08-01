package com.kipdev.geopushlib

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.kipdev.geopushlib.service.LocationUpdatesService

open class GeopushMainActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        onNewIntent(intent)

        bindService(
            Intent(this, LocationUpdatesService::class.java), mServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onResume() {
        super.onResume()
        checkAndSendToken(application)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mServiceConnection)
    }

    public override fun onNewIntent(intent: Intent) {
        val extras = intent.extras
        val geopushid: String?

        if (extras != null) {
            geopushid = extras.getString("geopushId")
            if(!geopushid.isNullOrEmpty())
                sendPushOpened(application, geopushid)
        }
    }
}