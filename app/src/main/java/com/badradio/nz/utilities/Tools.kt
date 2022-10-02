package com.badradio.nz.utilities

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

object Tools {

    @Throws(IOException::class)
    fun executeRequestAndCheckResponse(request: Request, name: String): Response {
        val response: Response = try {
            client.newCall(request).execute()
        } catch (e: IOException) {
            Log.e(TAG, "Could not execute $name", e)
            throw e
        }

        if (!response.isSuccessful) {
            val e = IOException("$name returned not OK: ${response.code()}")
            Log.e(TAG, "See exception", e)
            throw e
        }

        if (response.body() == null) {
            val e = IOException("$name had no body")
            Log.e(TAG, "See exception", e)
            throw e
        }

        return response
    }

    private val TAG = Tools::class.qualifiedName
}

val client: OkHttpClient =  OkHttpClient.Builder().build()
val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
