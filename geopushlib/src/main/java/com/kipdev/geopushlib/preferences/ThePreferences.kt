package com.kipdev.geopushlib.preferences

import android.app.Application
private const val KEY_TOKEN: String = "PUSH_TOKEN"
private const val KEY_TRANSITION: String = "TRANSITION_TYPE"

class ThePreferences {
    companion object {
        fun getToken(app: Application): String? =
            app.getSharedPreferences(app.packageName, 0).getString(KEY_TOKEN, null)

        fun setToken(app: Application, token: String?) =
            app.getSharedPreferences(app.packageName, 0).edit().putString(KEY_TOKEN, token).apply()

        fun getTransition(app: Application): String? =
            app.getSharedPreferences(app.packageName, 0).getString(KEY_TRANSITION, null)

        fun setTransition(app: Application, token: String?) =
            app.getSharedPreferences(app.packageName, 0).edit().putString(KEY_TRANSITION, token).apply()
    }
}