package com.badradio.nz.player

import android.app.Service
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.os.Binder
import android.os.IBinder
import com.badradio.nz.metadata.MetadataReceiver
import com.badradio.nz.notification.MediaNotificationManager.startNotify


class RadioService : Service() {
    private lateinit var stationInfo: StationInfo

    override fun onCreate() {
        super.onCreate()

        getStationInfo {
            stationInfo = it
            // TODO: enable player here
        }

        // Start periodic metadata fetcher
        MetadataReceiver.start()

        val mediaSession = MediaSession(this, "RadioService")
        mediaSession.setMetadata(
            MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, "STREAM")
                .putString(MediaMetadata.METADATA_KEY_ARTIST, "BADRADIO")
                .build()
        )
        mediaSession.isActive = true
        startNotify(this@RadioService, mediaSession)
    }

    fun playOrPause() {
        TODO("implement")
    }

    fun stop() {
        // TODO: remove
    }

    inner class RadioServiceBinder : Binder() {
        val service: RadioService
            get() = this@RadioService
    }

    override fun onBind(intent: Intent?): IBinder {
        return RadioServiceBinder()
    }

}

enum class PlaybackStatus {
    IDLE,
    LOADING,
    PLAYING,
    PAUSED,
    STOPPED,
    ERROR,
}
