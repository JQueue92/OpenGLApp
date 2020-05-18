package com.jqueue.openglapp.utils

import android.util.Log
import com.jqueue.openglapp.BuildConfig

object LogUtil {
    fun d(tag: String = "AppLog", msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg)
        }
    }
}