package com.geopush.library

import android.content.Context
import android.os.Build
import com.kipdev.geopushlib.preferences.SharedPrefsStore
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkClient(
    private val context: Context,
    private val devServer: Boolean,
    private val logEnabled: Boolean = false
) {

    private val LIVE_SERVER = "http://geopush.ubank.su/api/"
    private val DEV_SERVER = "https://api.geopush.me/api/"

    private val BASE_URL by lazy { if (devServer) DEV_SERVER else LIVE_SERVER }


    public fun getTerminalApi() = getRetrofit().create(TerminalApi::class.java)

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .validateEagerly(true)
            .addConverterFactory(getGsonConverterFactory())
            .baseUrl(BASE_URL)
            .client(getOkHttpClient())
            .build()
    }


    private fun getGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    private fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(getHeaderInterceptor(context))
        if (logEnabled)
            builder.addInterceptor(getLogInterceptor())
        return builder.build()
    }

    private fun getHeaderInterceptor(context: Context): Interceptor {
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val req = chain.request().newBuilder()
                    .addHeader("appBundle", context.packageName)
                    .addHeader("platform", "android")
                    .addHeader("vendor", Build.MANUFACTURER)
                    .addHeader("model", Build.MODEL)
                    .addHeader("osver", Build.VERSION.RELEASE)
                    .addHeader("version", BuildConfig.VERSION_NAME)
                val token = SharedPrefsStore.getInstance(context).getToken()
                token?.let {
                    req.addHeader("hwid", it)
                }
                return chain.proceed(req.build())
            }
        }
    }

    private fun getLogInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }
}