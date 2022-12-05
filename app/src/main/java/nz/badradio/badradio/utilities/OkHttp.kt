package nz.badradio.badradio.utilities

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.xbill.DNS.*
import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException

@Throws(IOException::class)
fun executeRequestAndCheckResponse(request: Request, name: String): Response {
    val response: Response = try {
        client.newCall(request).execute()
    } catch (e: IOException) {
        Log.e(tag, "Could not execute $name", e)
        throw e
    }

    if (!response.isSuccessful) {
        val e = IOException("$name returned not OK: ${response.code}")
        Log.e(tag, "See exception", e)
        throw e
    }

    if (response.body == null) {
        val e = IOException("$name had no body")
        Log.e(tag, "See exception", e)
        throw e
    }

    return response
}

object CustomDns: Dns {
    private var initialized = false

    private val hostAddressNameMap = mapOf(
        "badradio.nz" to "104.26.1.121",
        "s2.radio.co" to "51.77.148.63",
        "s2.radioco.net" to "51.77.148.63", // TODO: whats up with this?
        "soundcloud.com" to "18.66.122.119",
        "bandcamp.com" to "151.101.129.28",
    )
    private var hostAddressMap = mutableMapOf<String, InetAddress>()

    @Throws(IOException::class)
    override fun lookup(hostname: String): List<InetAddress> {
        initialize()

        try {
            return listOf(Address.getByName(hostname))
        } catch (e: Exception) {
            Log.w(tag, "Could not resolve host $hostname", e)
        }

        if (!hostAddressMap.containsKey(hostname)) {
            throw UnknownHostException("Could not resolve host $hostname")
        }

        return listOf(hostAddressMap[hostname]!!)
    }

    private fun initialize() = synchronized(this) {
        if (initialized) {
            return
        }

        for ((host, addressName) in hostAddressNameMap) {
            try {
                val address = InetAddress.getByName(addressName)
                hostAddressMap[host] = address
            } catch (e: Exception) {
                Log.w(tag, "Could not add static address of $host", e)
            }
        }

        try {
            Lookup.setDefaultResolver(
                ExtendedResolver(
                    arrayOf(
                        Lookup.getDefaultResolver(),
                        SimpleResolver("8.8.8.8"),
                        SimpleResolver("8.8.4.4"),
                    )
                )
            )
        } catch (e: Exception) {
            Log.w(tag, "Could not initialize resolvers", e)
        }

        initialized = true
    }
}

private const val tag = "OkHttpUtils"

val client: OkHttpClient =  OkHttpClient.Builder().dns(CustomDns).build()
val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
