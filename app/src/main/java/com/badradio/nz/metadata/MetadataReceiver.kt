package com.badradio.nz.metadata

import com.badradio.nz.Config
import com.badradio.nz.utilities.ListenersManager
import okhttp3.*
import java.io.IOException
import java.util.*

class MetadataReceiver : TimerTask(), Callback {
    private val client =  OkHttpClient.Builder().build()

    override fun run() {
        val request = Request.Builder().url(Config.METADATA_URL).build()
        client.newCall(request).enqueue(this)
    }

    override fun onFailure(call: Call, e: IOException) {
        TODO("Think about what should happen on failure")
    }

    override fun onResponse(call: Call, response: Response) {
        val stationStatus = stationStatusAdapter.fromJson(response.body()!!.source())
            ?: throw IOException("Could not parse API response")

        val songMetadata = SongMetadata.fromStationTrack(stationStatus.current_track)
        ListenersManager.onSongTitle(songMetadata.title, songMetadata.artist)

        // TODO: Fetch album art and call callback
    }

    companion object {
        fun start() {
           Timer().scheduleAtFixedRate(MetadataReceiver(), 0L, Config.FETCH_METADATA_INTERVAL)
        }
    }
}
