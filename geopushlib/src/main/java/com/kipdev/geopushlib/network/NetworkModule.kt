package com.kipdev.geopushlib.network

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import com.kipdev.geopushlib.BuildConfig
import com.kipdev.geopushlib.isDebug
import com.kipdev.geopushlib.preferences.ThePreferences
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object NetworkModule {
    var application: Application? = null
    fun api(): Api {
        return retrofit().create(Api::class.java)
    }

    private fun getUrl() : String {
        return if (isDebug()) {
            "http://geopush.ubank.su/api/"
        } else {
            "http://geopush.ubank.su/api/"
        }
    }

    private fun retrofit(): Retrofit {
        return Retrofit.Builder()
            .validateEagerly(true)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(NullOrEmptyConvertFactory())
            .addConverterFactory(gsonConverterFactory())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(getUrl())
            .client(client())
            .build()
    }

    fun gsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    fun client(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(headerInterceptor())
            .addNetworkInterceptor(logInterceptor())
            //.addInterceptor(networkHandleInterceptor())
            .build()
    }

    fun logInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    fun headerInterceptor(): Interceptor {


        /*
        "appBundle": "com.ubank.su"
"platform": "ios"
"vendor": "apple"
"model": "iPhone 7"
"osver": "12.1"
"version": "1.0"
"hwid": "A7E899C0-8C5C-4692-AB8C-4C6ED442D331"

"phone": "700091232234" - optional
         */

        return Interceptor { chain ->
            chain.proceed(chain.request().newBuilder()
                .addHeader("appBundle", BuildConfig.APPLICATION_ID)
                .addHeader("platform", "android")
                .addHeader("vendor", Build.MANUFACTURER)
                .addHeader("model", Build.MODEL)
                .addHeader("osver", Build.VERSION.RELEASE)
                .addHeader("version", BuildConfig.VERSION_NAME)
            .build())


        }
    }
/*
    fun networkHandleInterceptor(): Interceptor {
        return Interceptor { chain ->
            if (!isNetworkAvailable()) {
                throw NetworkConnectivityException()
            }
            chain.proceed(chain.request())
        }
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
*/
}