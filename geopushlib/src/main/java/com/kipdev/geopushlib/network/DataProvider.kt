package com.kipdev.geopushlib.network

import android.content.Context
import io.reactivex.functions.Consumer

object DataProvider {

    private var networkModule: NetworkModule = NetworkModule

    fun sendSubscribePush(context: Context, token: String, onSuccess: Consumer<Any>, onError: Consumer<Throwable>) {
        var map = HashMap<String, String>()
        map.put("token", token)

        networkModule.api(context)
            .sendSubscribePush(token, map)
            .compose(RxUtils.applyT())
            .subscribe(onSuccess, onError)
    }

    fun sendPushDelivered(context: Context, token: String, messageId: String, onSuccess: Consumer<Any>, onError: Consumer<Throwable>) {
        var map = HashMap<String, String>()
        map.put("messageId", messageId)

        networkModule.api(context)
            .sendPushDelivered(token, map)
            .compose(RxUtils.applyT())
            .subscribe(onSuccess, onError)
    }

    fun sendPushOpened(context: Context, token: String, messageId: String, onSuccess: Consumer<Any>, onError: Consumer<Throwable>) {
        var map = HashMap<String, String>()
        map.put("messageId", messageId)

        networkModule.api(context)
            .sendPushOpened(token, map)
            .compose(RxUtils.applyT())
            .subscribe(onSuccess, onError)
    }

    fun sendLocation(context: Context, token: String, lat: Double, lon: Double, status: String?, onSuccess: Consumer<Any>, onError: Consumer<Throwable>) {
        var map = HashMap<String, Any>()
        map.put("lat", lat)
        map.put("lon", lon)
        status?.let{
            map.put("status", status)
        }

        networkModule.api(context)
            .sendLocation(token, map)
            .compose(RxUtils.applyT())
            .subscribe(onSuccess, onError)
    }

    fun setProps(context: Context, token: String, props: HashMap<String, Any>, onSuccess: Consumer<Any>, onError: Consumer<Throwable>) {
        networkModule.api(context)
            .setProps(token, props)
            .compose(RxUtils.applyT())
            .subscribe(onSuccess, onError)
    }

    fun getDeals(context: Context, token:String, lat: Double, lon: Double, radius: Double, onSuccess: Consumer<List<Any>>, onError: Consumer<Throwable>) {
        networkModule.api(context)
            .getDeals(token, lat, lon, radius.toInt())
            .compose(RxUtils.applyT())
            .subscribe(onSuccess, onError)
    }
}