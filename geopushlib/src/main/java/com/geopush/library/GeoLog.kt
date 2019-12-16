package com.geopush.library

import android.util.Log

internal class GeoLog(private val logEnabled: Boolean = false) {
    companion object {
        private val TAG = "GeoPush"
        private var INSTANCE: GeoLog? = null

        fun init(logEnabled: Boolean = false) {
            INSTANCE = GeoLog(logEnabled)
        }

        fun log(text: String) {
            INSTANCE?.log(text)
        }
    }

    fun log(text: String) {
        if (logEnabled)
            Log.d(TAG, text)
    }
}