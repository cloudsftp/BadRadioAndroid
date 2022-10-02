package com.badradio.nz.utilities

import android.util.Log
import com.badradio.nz.metadata.SoundcloudAlbumArtGetter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

object Tools {

    // TODO: remove them in favor of Mochi + OkHTTP3

    //Get response from an URL request (GET)
    private fun getDataFromUrl(url: String): String {
        // Making HTTP request
        Log.v("INFO", "Requesting: $url")
        val chaine = StringBuffer("")
        try {
            val urlCon = URL(url)

            //Open a connection
            var connection = urlCon
                .openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "Your Single Radio")
            connection.requestMethod = "GET"
            connection.doInput = true
            connection.connect()

            //Handle redirecti
            val status = connection.responseCode
            if (status != HttpURLConnection.HTTP_OK
                && (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER)) {

                // get redirect url from "location" header field
                val newUrl = connection.getHeaderField("Location")
                // get the cookie if need, for login
                val cookies = connection.getHeaderField("Set-Cookie")

                // open the new connnection again
                connection = URL(newUrl).openConnection() as HttpURLConnection
                connection.setRequestProperty("Cookie", cookies)
                connection.setRequestProperty("User-Agent", "Your Single Radio")
                connection.requestMethod = "GET"
                connection.doInput = true
                println("Redirect to URL : $newUrl")
            }

            //Get the stream from the connection and read it
            val inputStream = connection.inputStream
            val rd = BufferedReader(InputStreamReader(inputStream))
            var line: String? = ""
            while (rd.readLine().also { line = it } != null) {
                chaine.append(line)
            }
        } catch (e: IOException) {
            // writing exception to log
            Log.w(TAG, e)
        }
        return chaine.toString()
    }

    //Get JSON from an url and parse it to a JSON Object.
    @JvmStatic
    fun getJSONObjectFromUrl(url: String): JSONObject? {
        val data = getDataFromUrl(url)
        try {
            return JSONObject(data)
        } catch (e: Exception) {
            Log.w(TAG, e)
        }
        return null
    }

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
