package com.badradio.nz.metadata

import android.util.Log
import com.badradio.nz.Config
import com.badradio.nz.metadata.art.AlbumArtGetter
import com.badradio.nz.utilities.ListenersManager
import com.badradio.nz.utilities.client
import okhttp3.*
import java.io.IOException
import java.util.*

class MetadataReceiver : TimerTask(), Callback {
    private var currentSongMetadata = SongMetadata()

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

        if (currentSongMetadata != songMetadata) {
            Log.d(TAG, "Loaded new metadata")
            currentSongMetadata = songMetadata
            Log.d(TAG, "Updating song title and artist")
            ListenersManager.onSongTitle(songMetadata.title, songMetadata.artist)

            AlbumArtGetter.getAlbumArt(songMetadata)
        }
    }

    companion object {
        private val TAG = MetadataReceiver::class.qualifiedName

        fun start() {
           Timer().scheduleAtFixedRate(MetadataReceiver(), 0L, Config.FETCH_METADATA_INTERVAL)
        }
    }
}
