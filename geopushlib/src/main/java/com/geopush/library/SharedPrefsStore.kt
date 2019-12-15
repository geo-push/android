package com.kipdev.geopushlib.preferences

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences

private const val GEO_STORE: String = "GEO_STORE_PREFS"
private const val KEY_TOKEN: String = "PUSH_TOKEN"

class SharedPrefsStore(private val context: Context) {
    private val prefs: SharedPreferences by lazy { context.getSharedPreferences(GEO_STORE, Context.MODE_PRIVATE) }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: SharedPrefsStore? = null
        fun getInstance(context: Context): SharedPrefsStore {
            if(INSTANCE == null)
                INSTANCE = SharedPrefsStore(context)
            return INSTANCE!!
        }
    }

    fun getToken(): String? =
        prefs.getString(KEY_TOKEN, null)

    fun setToken(token: String?) =
        prefs.edit().putString(KEY_TOKEN, token).apply()
}