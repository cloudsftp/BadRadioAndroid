package com.badradio.nz.metadata

import com.badradio.nz.Config
import com.badradio.nz.utilities.ListenersManager
import com.badradio.nz.utilities.Log
import okhttp3.OkHttpClient
import java.util.*

class MetadataReceiver : TimerTask() {
    private val client =  OkHttpClient.Builder().build()

    override fun run() {
        ListenersManager.onSongTitle("test", "bla")


    }

    companion object {
        fun start() {
           Timer().scheduleAtFixedRate(MetadataReceiver(), 0L, Config.FETCH_METADATA_INTERVAL)
        }
    }
}
