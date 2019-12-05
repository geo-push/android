package com.kipdev.geopushlib

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.kipdev.geopushlib.network.DataProvider
import com.kipdev.geopushlib.network.SilentConsumer
import com.kipdev.geopushlib.preferences.ThePreferences
import com.kipdev.geopushlib.service.LocationUpdatesService
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers


fun isDebug(): Boolean {
    return BuildConfig.DEBUG
}

fun sendSetProps(app: Application,props: HashMap<String, Any>){
    checkFirebaseToken(app).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe({ token->
        DataProvider.setProps(app, token, props, object : SilentConsumer<Any> {
            override fun onConsume(t: Any) {
                var k = 0
                k++
            }
        }, object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
            }
        })
    })
}

fun sendPushDelivered(app: Application, messageId: String){
    checkFirebaseToken(app).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe({ token->
        DataProvider.sendPushDelivered(app, token, messageId, object : SilentConsumer<Any> {
            override fun onConsume(t: Any) {
                var k = 0
                k++
            }
        }, object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
            }
        })
    })
}

fun sendPushOpened(app: Application, messageId: String){
    checkFirebaseToken(app).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe({ token->
        DataProvider.sendPushOpened(app, token, messageId, object : SilentConsumer<Any> {
            override fun onConsume(t: Any) {
                var k = 0
                k++
            }
        }, object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
            }
        })
    })
}

fun sendLocation(app: Application, lat: Double, lon: Double){
    checkFirebaseToken(app).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe({ token->
        DataProvider.sendLocation(app, token, lat, lon, ThePreferences.getTransition(app), object : SilentConsumer<Any> {
            override fun onConsume(t: Any) {
                var k = 0
                k++
            }
        }, object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
            }
        })
    })
}

fun checkAndSendToken(app:Application){
    checkFirebaseToken(app).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe({
        var token = it
    })
}

fun checkFirebaseToken(app:Application): Observable<String>
{
    return Observable.create<String> {
        var token = ThePreferences.getToken(app)

        if(token.isNullOrEmpty())
        {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { tokenIt->
                it.onNext(tokenIt.token)

                DataProvider.sendSubscribePush(app, tokenIt.token, object : SilentConsumer<Any> {
                    override fun onConsume(t: Any) {
                        ThePreferences.setToken(app, tokenIt.token)
                        it.onComplete()
                    }
                }, object : SilentConsumer<Throwable> {
                    override fun onConsume(t: Throwable) {
                    }
                })

            }
        }
        else {
            it.onNext(token)
            it.onComplete()
        }
    }
}

fun getMessageId(message:RemoteMessage):String?{
    if(message?.data?.containsKey("geopushData") == true)
    {
        var gData = message.data!!.get("geopushData")
        //gData = gData?.replace("{","[")
        //gData = gData?.replace("}","]")
        gData = gData?.replace("\"","'")

        var gson = Gson()
        var map = HashMap<String, String>()
        map = gson.fromJson(gData, map.javaClass)

        if(map.containsKey("id"))
            return map.get("id")
    }
    return null
}

fun getMessageTitle(message:RemoteMessage):String?{
    if(message?.data?.containsKey("title") == true)
        return message.data!!.get("title")
    return null
}

fun getMessageText(message:RemoteMessage):String?{
    if(message?.data?.containsKey("body") == true)
        return message.data!!.get("body")
    return null
}

fun getNotification(context: Context, text:String): Notification
{
    val intent = Intent(context, LocationUpdatesService::class.java)

    val servicePendingIntent = PendingIntent.getService(
        context, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val pm = context.packageManager
    val notificationIntent = pm.getLaunchIntentForPackage(context.packageName)
    //val notificationIntent = Intent(this, Class.forName("com.geopush.MainActivity"))
    notificationIntent.action = Intent.ACTION_MAIN
    notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

    val activityPendingIntent = PendingIntent.getActivity(
        context, 0,
        notificationIntent, 0
    )
    val channelId =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context, "my_service", "My Background Service")
        } else {
            // If earlier version channel ID is not used
            // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
            ""
        }

    val builder = NotificationCompat.Builder(context)
        .setContentText(text)
        .setContentTitle(text)
        //.setOngoing(true)
        .setPriority(Notification.PRIORITY_HIGH)
        .setSmallIcon(android.R.mipmap.sym_def_app_icon)
        .setChannelId(channelId)
        .setTicker(text)
        .setWhen(System.currentTimeMillis())

    return builder.build()
}


@RequiresApi(Build.VERSION_CODES.O)
fun createNotificationChannel(context:Context, channelId: String, channelName: String): String{
    val chan = NotificationChannel(channelId,
        channelName, NotificationManager.IMPORTANCE_NONE)
    chan.lightColor = Color.BLUE
    chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
    val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    service.createNotificationChannel(chan)
    return channelId
}