package com.kipdev.geopushlib.network

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface Api {
    @POST("terminal/pushToken/")
    fun sendSubscribePush(@Header ("hwid") header: String, @Body body: Any) : Observable<Response<Unit>>

    @POST("terminal/pushDelivered/")
    fun sendPushDelivered(@Header ("hwid") header: String, @Body body: Any) : Observable<Response<Unit>>

    @POST("terminal/pushOpened/")
    fun sendPushOpened(@Header ("hwid") header: String, @Body body: Any) : Observable<Response<Unit>>

    @POST("terminal/location/")
    fun sendLocation(@Header ("hwid") header: String, @Body body: Any) : Observable<Response<Unit>>

    @POST("terminal/setProps/")
    fun setProps(@Header ("hwid") header: String, @Body body: Any) : Observable<Response<Unit>>

    @GET("promotion/points")
    fun getDeals(@Header("hwid") header: String, @Query ("lat") lat: Double, @Query ("lon") lon: Double, @Query ("radius") radius: Int) : Observable<List<Any>>
}