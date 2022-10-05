package com.badradio.nz.player

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import com.badradio.nz.metadata.MetadataReceiver
import com.badradio.nz.notification.MediaNotificationManager
import com.badradio.nz.utilities.MetadataObserver
import com.badradio.nz.utilities.PlayerStateObserver


class RadioService : Service(), MetadataObserver {
    private val observers: MutableList<PlayerStateObserver> = mutableListOf()

    private lateinit var stationInfo: StationInfo
    private lateinit var mediaNotificationManager: MediaNotificationManager

    override fun onCreate() {
        super.onCreate()

        getStationInfo {
            stationInfo = it
            // TODO: enable player here
        }

        mediaNotificationManager = MediaNotificationManager(this)

        // Start periodic metadata fetcher
        MetadataReceiver.start(this)

        mediaNotificationManager.onSongTitle("test", "test")
    }

    fun playOrPause() {
        // TODO("implement")
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

    override fun onSongTitle(title: String, artist: String) {
        observers.forEach { it.onSongTitle(title, artist) }

        mediaNotificationManager.onSongTitle(title, artist)
    }

    override fun onAlbumArt(art: Bitmap) {
        observers.forEach { it.onAlbumArt(art) }
    }

    fun registerForPlayerState(observer: PlayerStateObserver) {
        observers.add(observer)
    }

}

enum class PlayerState {
    IDLE,
    LOADING,
    PLAYING,
    PAUSED,
    STOPPED,
    ERROR,
}
