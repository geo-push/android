package com.kipdev.geopushlib.preferences

import android.app.Application
private const val KEY_TOKEN: String = "PUSH_TOKEN"

class ThePreferences {
    companion object {
        fun getToken(app: Application): String? =
            app.getSharedPreferences(app.packageName, 0).getString(KEY_TOKEN, null)

        fun setToken(app: Application, token: String?) =
            app.getSharedPreferences(app.packageName, 0).edit().putString(KEY_TOKEN, token).apply()
    }
}