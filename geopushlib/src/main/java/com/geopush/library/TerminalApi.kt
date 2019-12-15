package com.geopush.library

import com.geopush.library.pojo.LocationRequest
import com.geopush.library.pojo.PushDeliveredRequest
import com.geopush.library.pojo.PushTokenRequest
import com.geopush.library.pojo.PushOpenedRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface TerminalApi {
    @POST("terminal/pushToken/")
    fun sendPushToken(@Body tokenBody: PushTokenRequest) : Call<ResponseBody>

    @POST("terminal/location/")
    fun sendLocation(@Body locationBody: LocationRequest) : Call<ResponseBody>

    @POST("terminal/pushDelivered/")
    fun sendPushDelivered(@Body pushDeliveredRequest: PushDeliveredRequest) : Call<ResponseBody>

    @POST("terminal/pushOpened/")
    fun sendPushOpened(@Body pushoOpenedRequest: PushOpenedRequest) : Call<Response<Unit>>

    @POST("terminal/setProps/")
    fun sendSetProps(@Body body: HashMap<String, Any>) : Call<Response<Unit>>
}