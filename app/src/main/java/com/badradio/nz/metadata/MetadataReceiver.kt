package com.badradio.nz.metadata

import com.badradio.nz.Config
import com.badradio.nz.utilities.ListenersManager
import com.badradio.nz.utilities.Log
import java.util.*

class MetadataReceiver : TimerTask() {
    override fun run() {
        ListenersManager.onSongTitle("test", "bla")
    }

    companion object {
        fun start() {
           Timer().scheduleAtFixedRate(MetadataReceiver(), 0L, Config.FETCH_METADATA_INTERVAL)
        }
    }
}
