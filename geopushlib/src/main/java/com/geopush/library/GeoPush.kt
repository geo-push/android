package com.geopush.library

import android.content.Context
import android.telecom.Call
import com.geopush.library.pojo.LocationRequest
import com.geopush.library.pojo.PushDeliveredRequest
import com.geopush.library.pojo.PushOpenedRequest
import com.geopush.library.pojo.PushTokenRequest
import com.google.gson.Gson
import com.kipdev.geopushlib.preferences.SharedPrefsStore

public class GeoPush(private val context: Context) {

    private val locationManager by lazy { LocationManager(context) }
    private val networkClient by lazy { NetworkClient(context) }

    companion object {

        private var INSTANCE: GeoPush? = null

        @JvmStatic
        fun init(context: Context): GeoPush {
            if (INSTANCE == null)
                INSTANCE = GeoPush(context)
            return INSTANCE!!
        }

        fun shared(): GeoPush {
            INSTANCE?.let {
                return it
            } ?: run {
                throw Exception("GeoPush must be initialized with init(context)")
            }
        }
    }

    /**
     * Запуск трекинга местоположения
     */
    fun startTracking(waitPermissionIfNotGranted: Boolean = false) {
        locationManager.startListening(waitPermissionIfNotGranted) { lat, lng ->
            GeoLog.log("New location = $lat $lng")
            networkClient.getTerminalApi().sendLocation(LocationRequest(lat, lng)).silentEnqueue()
        }
    }

    /**
     * Отправка пуш-токена
     */
    fun sendPushToken(token:String){
        GeoLog.log("Token = $token")
        SharedPrefsStore.getInstance(context).setToken(token)
        networkClient.getTerminalApi().sendPushToken(PushTokenRequest(token)).silentEnqueue()
    }

    /**
     * Отметить пуш как полученный.
     * return messageId - Идентификатор сообещния, который необходимо передать в метод markPushOpened()
     */
    fun markPushDelivered(dataMap: Map<String, String>?) : String? {
        dataMap?.let {
            it.get("geopushData")?.let {
                val gData = it.replace("\"","'")
                val gson = Gson()
                var map = HashMap<String, String>()
                map = gson.fromJson(gData, map.javaClass)

                val id = map["id"]
                id?.let {_id->
                    networkClient.getTerminalApi().sendPushDelivered(PushDeliveredRequest(_id)).silentEnqueue()
                    return id
                }
            }
        }
        return null
    }

    /**
     * Пометить пуш как открытый
     */
    fun markPushOpened(messageId: String){
        networkClient.getTerminalApi().sendPushOpened(PushOpenedRequest(messageId)).silentEnqueue()
    }

    /**
     * Отправка кастомных полей
     */
    fun sendUserInfo(map: HashMap<String, Any>){
        networkClient.getTerminalApi().sendSetProps(map).silentEnqueue()
    }

}