package com.kipdev.geopushlib.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.kipdev.geopushlib.*
import android.app.NotificationChannel

class FirebaseMessagingServiceExt : FirebaseMessagingService(){
    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        val messageId = getMessageId(message!!)
        if(messageId != null) {
            sendPushDelivered(application, messageId)

            val pm = packageManager
            val notificationIntent = pm.getLaunchIntentForPackage(applicationContext.packageName)
            //val notificationIntent = Intent(this, Class.forName("com.geopush.MainActivity"))
            notificationIntent.action = Intent.ACTION_MAIN
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            notificationIntent.putExtra("geopushId", messageId)
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val nMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val intent2 = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val nBuilder = NotificationCompat.Builder(this)
                //.setSmallIcon(R.mipmap.icon_app)
                .setContentTitle(getMessageTitle(message) ?:"")
                .setContentText(getMessageText(message) ?:"")
                .setContentIntent(intent2)
                .setAutoCancel(true)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channelId = "geopush"
                val channelName = "Geopush"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val mChannel = NotificationChannel(
                    channelId, channelName, importance
                )
                nMgr.createNotificationChannel(mChannel)
                nBuilder.setChannelId(mChannel.id)
            }
            nMgr.notify(0, nBuilder.build())
        }
    }
}