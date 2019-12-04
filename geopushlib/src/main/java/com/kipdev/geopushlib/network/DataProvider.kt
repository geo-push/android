package com.kipdev.geopushlib.network

import io.reactivex.functions.Consumer

object DataProvider {

    private var networkModule: NetworkModule = NetworkModule

    fun sendSubscribePush(token: String, onSuccess: Consumer<Any>, onError: Consumer<Throwable>) {
        var map = HashMap<String, String>()
        map.put("token", token)

        networkModule.api()
            .sendSubscribePush(token, map)
            .compose(RxUtils.applyT())
            .subscribe(onSuccess, onError)
    }

    fun sendPushDelivered(token: String, messageId: String, onSuccess: Consumer<Any>, onError: Consumer<Throwable>) {
        var map = HashMap<String, String>()
        map.put("messageId", messageId)

        networkModule.api()
            .sendPushDelivered(token, map)
            .compose(RxUtils.applyT())
            .subscribe(onSuccess, onError)
    }

    fun sendPushOpened(token: String, messageId: String, onSuccess: Consumer<Any>, onError: Consumer<Throwable>) {
        var map = HashMap<String, String>()
        map.put("messageId", messageId)

        networkModule.api()
            .sendPushOpened(token, map)
            .compose(RxUtils.applyT())
            .subscribe(onSuccess, onError)
    }

    fun sendLocation(token: String, lat: Double, lon: Double, status: String?, onSuccess: Consumer<Any>, onError: Consumer<Throwable>) {
        var map = HashMap<String, Any>()
        map.put("lat", lat)
        map.put("lon", lon)
        status?.let{
            map.put("status", status)
        }

        networkModule.api()
            .sendLocation(token, map)
            .compose(RxUtils.applyT())
            .subscribe(onSuccess, onError)
    }

    fun setProps(token: String, props: HashMap<String, Any>, onSuccess: Consumer<Any>, onError: Consumer<Throwable>) {
        networkModule.api()
            .setProps(token, props)
            .compose(RxUtils.applyT())
            .subscribe(onSuccess, onError)
    }

    fun getDeals(token:String, lat: Double, lon: Double, radius: Double, onSuccess: Consumer<List<Any>>, onError: Consumer<Throwable>) {
        networkModule.api()
            .getDeals(token, lat, lon, radius.toInt())
            .compose(RxUtils.applyT())
            .subscribe(onSuccess, onError)
    }
}