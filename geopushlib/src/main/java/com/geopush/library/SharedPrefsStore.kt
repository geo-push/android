package com.kipdev.geopushlib.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import java.util.*

private const val GEO_STORE: String = "GEO_STORE_PREFS"
private const val KEY_TOKEN: String = "PUSH_TOKEN"
private const val HWID: String = "HWID"

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

    fun getHwId() = prefs.getString(HWID, null)?.let { it } ?: kotlin.run {
        val hwid = UUID.randomUUID().toString()
        setHwId(hwid)
        hwid
    }

    private fun setHwId(hwid: String) {
        prefs.edit().putString(HWID, hwid).apply()
    }

    fun getToken(): String? =
        prefs.getString(KEY_TOKEN, null)

    fun setToken(token: String?) =
        prefs.edit().putString(KEY_TOKEN, token).apply()
}