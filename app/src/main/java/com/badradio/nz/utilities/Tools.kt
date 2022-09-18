package com.badradio.nz.utilities

import com.badradio.nz.utilities.Log.v
import com.badradio.nz.utilities.Log.printStackTrace
import com.badradio.nz.utilities.Log.e
import com.badradio.nz.R
import com.badradio.nz.utilities.Tools
import org.json.JSONObject
import kotlin.jvm.JvmOverloads
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.badradio.nz.activity.PlayerActivity
import android.graphics.Bitmap
import android.util.Log
import com.badradio.nz.metadata.Metadata
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

object Tools {
    private const val DISPLAY_DEBUG = true
    var BACKGROUND_IMAGE_ID = R.drawable.ic_adjust_black_24dp
    private var listeners: ArrayList<EventListener>? = null

    //Get response from an URL request (GET)
    fun getDataFromUrl(url: String): String {
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

    @JvmOverloads
    fun noConnection(context: Activity, message: String? = null) {
        val ab = AlertDialog.Builder(context)
        if (isOnline(context)) {
            var messageText = ""
            if (message != null && DISPLAY_DEBUG) {
                messageText = """
                    
                    
                    $message
                    """.trimIndent()
            }
            ab.setMessage(context.resources.getString(R.string.dialog_connection_description) + messageText)
            ab.setPositiveButton(context.resources.getString(R.string.ok), null)
            ab.setTitle(context.resources.getString(R.string.dialog_connection_title))
        } else {
            ab.setMessage(context.resources.getString(R.string.dialog_internet_description))
            ab.setPositiveButton(context.resources.getString(R.string.ok), null)
            ab.setTitle(context.resources.getString(R.string.dialog_internet_title))
        }
        if (!context.isFinishing) {
            ab.show()
        }
    }

    /**
     * Returns if the user has an internet connection
     *
     * @param context the context
     */
    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = cm.activeNetworkInfo
        return ni != null && ni.isConnected
    }

    fun isOnlineShowDialog(c: Activity): Boolean {
        if (isOnline(c)) return true else noConnection(c)
        return false
    }

    fun isNetworkActive(activity: Activity): Boolean {
        val connectivity = activity
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity == null) {
            return false
        } else {
            val info = connectivity.allNetworkInfo
            if (info != null) {
                for (i in info.indices) {
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun getJSONString(url: String?): String? {
        var jsonString: String? = null
        var linkConnection: HttpURLConnection? = null
        try {
            val linkurl = URL(url)
            linkConnection = linkurl.openConnection() as HttpURLConnection
            val responseCode = linkConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val linkinStream = linkConnection!!.inputStream
                val baos = ByteArrayOutputStream()
                var j = 0
                while (linkinStream.read().also { j = it } != -1) {
                    baos.write(j)
                }
                val data = baos.toByteArray()
                jsonString = String(data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            linkConnection?.disconnect()
        }
        return jsonString
    }

    fun getDataFromURL(url: String): String {
        Log.v("INFO", "Requesting: $url")
        val stringBuffer = StringBuffer("")
        try {
            val urlCon = URL(url)
            val connection = urlCon.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "Your Single Radio")
            connection.requestMethod = "GET"
            connection.doInput = true
            connection.connect()
            val inputStream = connection.inputStream
            val rd = BufferedReader(InputStreamReader(
                    inputStream))
            var line: String? = ""
            while (rd.readLine().also { line = it } != null) {
                stringBuffer.append(line)
            }
        } catch (e: IOException) {
        }
        return stringBuffer.toString()
    }

    fun registerAsListener(listener: EventListener) {
        if (listeners == null) listeners = ArrayList()
        listeners!!.add(listener)
    }

    fun unregisterAsListener(listener: PlayerActivity) {
        listeners!!.remove(listener)
    }

    @JvmStatic
    fun onEvent(status: String) {
        if (listeners == null) return
        for (listener in listeners!!) {
            listener.onEvent(status)
        }
    }

    fun onAudioSessionId(id: Int) {
        if (listeners == null) return
        for (listener in listeners!!) {
            listener.onAudioSessionId(id)
        }
    }

    @JvmStatic
    fun onMetaDataReceived(meta: Metadata, image: Bitmap?) {
        if (listeners == null) return
        for (listener in listeners!!) {
            listener.onMetaDataReceived(meta, image)
        }
    }

    interface EventListener {
        fun onEvent(status: String)
        fun onAudioSessionId(i: Int)
        fun onMetaDataReceived(meta: Metadata, image: Bitmap?)
    }
}