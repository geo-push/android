package com.kipdev.geopushlib

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kipdev.geopushlib.AndroidPermissionsHelper.checkLocationPermission
import com.kipdev.geopushlib.service.LocationUpdatesService
import java.lang.Exception
import java.util.concurrent.TimeUnit
import android.os.Looper
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.R.attr.name
import android.os.Handler


open class GeopushMainActivity : AppCompatActivity() {

    private val mServiceConnection = object : ServiceConnection {

        private var mService: LocationUpdatesService? = null

        var mBound = false

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            println("--> onServiceConnected")
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
       /* if(checkLocationPermission(this))
            bindGeoPushService()*/
        PermissionsWaitThread(this){
            bindGeoPushService()
        }.start()
    }

    fun bindGeoPushService(){
        //if(mServiceConnection.mBound)
        //    return
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

    private inner class PermissionsWaitThread(val context: Context, val granted: ()-> Unit) : Thread() {
        override fun run() {
            while (!checkLocationPermission(context)) {
                /*println("--> PermissionsWaitThread: not granted")
                Handler(Looper.getMainLooper()).post(Runnable {
                    Toast.makeText(context, "Ожидаем разрешения", Toast.LENGTH_SHORT).show()
                })*/
                try {
                    sleep(TimeUnit.SECONDS.toMillis(10))
                } catch (e: Throwable) {
                    throw RuntimeException("waiting thread sleep() has been interrupted")
                }
            }
            /*Handler(Looper.getMainLooper()).post(Runnable {
                Toast.makeText(context, "Разрешения на локейшн получены", Toast.LENGTH_SHORT).show()
            })
            println("--> PermissionsWaitThread: GRANTED!!!")*/
            granted.invoke()
            interrupt()
        }
    }
}