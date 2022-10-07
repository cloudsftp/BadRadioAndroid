package com.badradio.nz.metadata

import android.util.Log
import com.badradio.nz.Config
import com.badradio.nz.metadata.art.getAlbumArt
import com.badradio.nz.utilities.MetadataObserver
import com.badradio.nz.utilities.client
import okhttp3.*
import java.io.IOException
import java.util.*

class MetadataReceiver(private val metadataObserver: MetadataObserver) : TimerTask(), Callback {
    private var currentSongMetadata = SongMetadata()

    override fun run() {
        val request = Request.Builder().url(Config.METADATA_URL).build()
        client.newCall(request).enqueue(this)
    }

    override fun onFailure(call: Call, e: IOException) {
        // TODO: what should happen here?
    }

    override fun onResponse(call: Call, response: Response) {
        val stationStatus = stationStatusAdapter.fromJson(response.body()!!.source())
            ?: throw IOException("Could not parse API response")

        val songMetadata = SongMetadata.fromStationTrack(stationStatus.current_track)

        if (currentSongMetadata != songMetadata) {
            Log.d(TAG, "Metadata changed from before, notifying and searching for album art")
            metadataObserver.onSongTitle(songMetadata.title, songMetadata.artist)
            getAlbumArt(songMetadata, metadataObserver)

            currentSongMetadata = songMetadata
        }
    }

    companion object {
        private val TAG = MetadataReceiver::class.qualifiedName

        fun start(metadataObserver: MetadataObserver) {
           Timer().scheduleAtFixedRate(MetadataReceiver(metadataObserver), 0L, Config.FETCH_METADATA_INTERVAL)
        }
    }
}