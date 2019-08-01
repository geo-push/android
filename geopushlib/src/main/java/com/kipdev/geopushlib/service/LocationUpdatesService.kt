package com.kipdev.geopushlib.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import android.util.Log

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.kipdev.geopushlib.Utils
import com.kipdev.geopushlib.createNotificationChannel
import com.kipdev.geopushlib.getNotification
import com.kipdev.geopushlib.sendLocation
import java.util.*


class LocationUpdatesService : Service() {

    private val mBinder = LocalBinder()
    private var mChangingConfiguration = false
    private var mNotificationManager: NotificationManager? = null
    private var mLocationRequest: LocationRequest? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var mServiceHandler: Handler? = null
    private var mLocation: Location? = null

    val notification: Notification
        get() {
            val text = Utils.getLocationText(mLocation)
            val pm = packageManager
            val notificationIntent = pm.getLaunchIntentForPackage(applicationContext.packageName)
            notificationIntent?.action = Intent.ACTION_MAIN
            notificationIntent?.addCategory(Intent.CATEGORY_LAUNCHER)
            notificationIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

            val channelId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel(this, "my_service1", "My Background Service1")
                } else {
                    // If earlier version channel ID is not used
                    // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                    ""
                }

            val builder = NotificationCompat.Builder(this)
                .setContentText(text)
                .setOngoing(true)
                .setContentTitle(Utils.getLocationTitle(this))
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                .setTicker(text)
                .setChannelId(channelId)
                .setWhen(System.currentTimeMillis())

            return builder.build()
        }



    override fun onCreate() {
        //if(applicationContext != null)
        //    sendNotification(applicationContext, "onCreate started")

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult.getLastLocation())
            }
        }

        createLocationRequest()
        getLastLocation()

        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(CHANNEL_ID, "Test", NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager!!.createNotificationChannel(mChannel)
        }
        //if(applicationContext != null)
        //    sendNotification(applicationContext, "onCreate finished")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mChangingConfiguration = true
    }

    override fun onBind(intent: Intent): IBinder? {
        stopForeground(true)
        mChangingConfiguration = false
        //if(applicationContext != null)
        //    sendNotification(applicationContext, "onBind")
        return mBinder
    }

    override fun onRebind(intent: Intent) {
        stopForeground(true)
        mChangingConfiguration = false
        //if(applicationContext != null)
        //    sendNotification(applicationContext, "onRebind")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        //startForeground(NOTIFICATION_ID++, notification)
        //if(applicationContext != null)
        //    sendNotification(applicationContext, "onUnbind")
        return true
    }

    override fun onDestroy() {
        mServiceHandler!!.removeCallbacksAndMessages(null)
    }

    fun requestLocationUpdates() {
        //if(applicationContext != null)
        //    sendNotification(applicationContext, "requestLocationUpdates start")
        startService(Intent(applicationContext, LocationUpdatesService::class.java))
        try {
            mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback, Looper.myLooper()
            )
            //if(applicationContext != null)
            //    sendNotification(applicationContext, "requestLocationUpdates ok")
        } catch (unlikely: SecurityException) {
            //if(applicationContext != null)
            //    sendNotification(applicationContext, "requestLocationUpdates ${unlikely.toString()}")
        }
    }

    private fun getLastLocation() {
        try {
            if(mNotificationManager!= null)
            mFusedLocationClient!!.getLastLocation()
                .addOnCompleteListener(
                    { task ->
                        if (task.isSuccessful && task.result != null) {
                            mLocation = task.result

                            //if(applicationContext != null)
                            //    sendNotification(applicationContext, "getLastLocation success")
                        } else {
                            //if(applicationContext != null)
                            //    sendNotification(applicationContext, "getLastLocation fail")
                        }
                    }
                )
        } catch (unlikely: SecurityException) {
            //if(applicationContext != null)
            //    sendNotification(applicationContext, "getLastLocation ${unlikely.toString()}")
        }
    }

    private fun onNewLocation(location: Location) {
        mLocation = location
        //if(applicationContext != null)
        //    sendNotification(applicationContext, "onNewLocation")

        if (serviceIsRunningInForeground(this)) {
            if(mNotificationManager != null)
                mNotificationManager!!.notify(NOTIFICATION_ID++, notification)
        }
        else
            startForeground(NOTIFICATION_ID++, notification)
        sendLocation(application, mLocation!!.latitude, mLocation!!.longitude)
    }

    fun sendNotification(context: Context, text: String){

        var r = Random()
        if (serviceIsRunningInForeground(context)) {
            if(mNotificationManager != null)
                mNotificationManager!!.notify(r.nextInt(), getNotification(context, text))
        }
        else
            startForeground(r.nextInt(), getNotification(context, text))
    }

    private fun createLocationRequest() {
        //if(applicationContext != null)
        //    sendNotification(applicationContext, "createLocationRequest")
        mLocationRequest = LocationRequest()
        mLocationRequest!!.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
        mLocationRequest!!.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
        //mLocationRequest!!.setSmallestDisplacement(50f)
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
    }

    inner class LocalBinder : Binder() {
        internal val service: LocationUpdatesService
            get() = this@LocationUpdatesService
    }

    fun serviceIsRunningInForeground(context: Context): Boolean {
        val manager = context.getSystemService(
            Context.ACTIVITY_SERVICE
        ) as ActivityManager
        for (service in manager.getRunningServices(
            Integer.MAX_VALUE
        )) {
            if (javaClass.name == service.service.className) {
                if (service.foreground) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        private val TAG = LocationUpdatesService::class.java.simpleName
        private val CHANNEL_ID = "channel_01"
        private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 60000
        private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
        private var NOTIFICATION_ID = 1
    }
}