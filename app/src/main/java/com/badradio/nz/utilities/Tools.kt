package com.badradio.nz.utilities

import com.badradio.nz.utilities.Log.v
import com.badradio.nz.utilities.Log.printStackTrace
import com.badradio.nz.utilities.Log.e
import com.badradio.nz.R
import org.json.JSONObject
import kotlin.jvm.JvmOverloads
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import com.badradio.nz.activity.PlayerActivity
import android.graphics.Bitmap
import com.badradio.nz.metadata.Metadata
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import kotlin.collections.ArrayList

object Tools {

    // TODO: remove them in favor of Mochi + OkHTTP3

    //Get response from an URL request (GET)
    private fun getDataFromUrl(url: String): String {
        // Making HTTP request
        v("INFO", "Requesting: $url")
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
            printStackTrace(e)
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
            e("INFO", "Error parsing JSON. Printing stacktrace now")
            printStackTrace(e)
        }
        return null
    }
}