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

    fun sendLocation(token: String, lat: Double, lon: Double, onSuccess: Consumer<Any>, onError: Consumer<Throwable>) {
        var map = HashMap<String, Double>()
        map.put("lat", lat)
        map.put("lon", lon)

        networkModule.api()
            .sendLocation(token, map)
            .compose(RxUtils.applyT())
            .subscribe(onSuccess, onError)
    }

    fun getDeals(lat: Double, lon: Double, radius: Double, onSuccess: Consumer<List<Any>>, onError: Consumer<Throwable>) {
        networkModule.api()
            .getDeals(lat, lon, radius.toInt())
            .compose(RxUtils.applyT())
            .subscribe(onSuccess, onError)
    }
}