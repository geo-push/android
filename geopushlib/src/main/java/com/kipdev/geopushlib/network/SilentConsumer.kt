package com.kipdev.geopushlib.network

import android.util.Log
import io.reactivex.functions.Consumer

const val TAG: String = "Silent Consumer"

interface SilentConsumer<T> : Consumer<T> {
    override fun accept(t: T) {
        try {
            onConsume(t)
        } catch (e: Exception) {
            Log.e(TAG, """ ${e.message}""", e)
        }
    }
    fun onConsume(t: T)
}