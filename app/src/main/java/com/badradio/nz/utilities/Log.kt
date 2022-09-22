package com.badradio.nz.utilities

import android.util.Log
import com.badradio.nz.Config.LOG
import java.lang.Exception

object Log {
    fun i(tag: String, string: String) {
        if (LOG) Log.i(tag, string)
    }

    fun e(tag: String, string: String) {
        if (LOG) Log.e(tag, string)
    }

    @JvmStatic
    fun e(tag: String, string: String, e: Exception) {
        if (LOG) Log.e(tag, string, e)
    }

    fun d(tag: String, string: String) {
        if (LOG) Log.d(tag, string)
    }

    @JvmStatic
    fun v(tag: String, string: String) {
        if (LOG) Log.v(tag, string)
    }

    fun w(tag: String, string: String) {
        if (LOG) Log.w(tag, string)
    }

    fun w(tag: String, string: String, e: Exception) {
        if (LOG) Log.w(tag, string, e)
    }

    @JvmStatic
    fun printStackTrace(e: Exception) {
        if (LOG) e.printStackTrace()
    }
}