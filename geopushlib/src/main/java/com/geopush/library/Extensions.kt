package com.geopush.library

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> Call<T>.silentEnqueue(){
    this.enqueue(object : Callback<T>{
        override fun onFailure(call: Call<T>, t: Throwable) {
            GeoLog.log("silentEnqueue: onFailure" )
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            GeoLog.log("silentEnqueue: onResponse" )
        }
    })
}