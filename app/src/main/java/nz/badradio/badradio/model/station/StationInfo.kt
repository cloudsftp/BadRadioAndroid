package nz.badradio.badradio.model.station

import android.util.Log
import nz.badradio.badradio.Config
import nz.badradio.badradio.utilities.client
import nz.badradio.badradio.utilities.moshi
import com.squareup.moshi.JsonAdapter
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.jvm.Throws

fun getStationInfo(callback: (StationInfo) -> Unit) {
    val stationInfoListRequest = Request.Builder().url(Config.STATION_DATA_URL).build()
    
    client.newCall(stationInfoListRequest).enqueue(object : Callback {
       override fun onFailure(call: Call, e: IOException) {
           Log.w(tag, "Exception while executing station info request", e)
           fallbackStationInfo(callback)
       }

        override fun onResponse(call: Call, response: Response)
            = handleStationInfoResponse(response, callback)
   })
}

private fun handleStationInfoResponse(response:Response, callback: (StationInfo) -> Unit) {
    if (response.body == null) {
        Log.w(tag, "Station info response has no body")
        fallbackStationInfo(callback)
    }

    try {
        processStationInfoResponse(response.body!!.string(), callback)
    } catch (e: Exception) {
        Log.w(tag, "Exception while parsing station info response", e)
        fallbackStationInfo(callback)
    }
}

@Throws(IOException::class)
fun processStationInfoResponse(response: String, callback: (StationInfo) -> Unit) {
    val stationInfoList = stationListAdapter.fromJson(response)
        ?: throw IOException("Could not read station info list from response body")

    if (stationInfoList.station.size != 1) {
        throw IOException("Expected exactly 1 station")
    }

    callback(stationInfoList.station[0])
}

fun fallbackStationInfo(callback: (StationInfo) -> Unit) = callback(Config.FALLBACK_STATION_INFO)

data class StationInfoList(
    val station: List<StationInfo>
)

data class StationInfo(
    val name: String,
    val streamURL: String,
    val imageURL: String,
    val desc: String,
    val longDesc: String,
)

private val stationListAdapter: JsonAdapter<StationInfoList> = moshi.adapter(StationInfoList::class.java)

private const val tag = "StationInfo"
