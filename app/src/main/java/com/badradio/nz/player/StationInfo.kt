package com.badradio.nz.player

import com.badradio.nz.Config
import com.badradio.nz.utilities.client
import com.badradio.nz.utilities.moshi
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

fun getStationInfo(callback: (StationInfo) -> Unit) {
    val stationInfoListRequest = Request.Builder().url(Config.STATION_DATA).build()

    client.newCall(stationInfoListRequest).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            TODO("Think about what should happen on failure")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.body() == null) {
                throw IOException("Station info response has no body")
            }

            val stationInfoList = stationListAdapter.fromJson(response.body()!!.string())
                ?: throw IOException("Could not read station info list from response body")

            if (stationInfoList.station.size != 1) {
                throw IOException("Expected exactly 1 station")
            }

            callback(stationInfoList.station[0])
        }
    })
}

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

val stationListAdapter = moshi.adapter(StationInfoList::class.java)
