package com.geopush.library

import android.util.Log

internal class GeoLog {
    companion object {
        private val TAG = "GeoPush"
        fun log(text: String ) {
            Log.d(TAG, text)
        }
    }
}