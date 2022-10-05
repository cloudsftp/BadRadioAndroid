package com.badradio.nz.player

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import com.badradio.nz.Config
import com.badradio.nz.metadata.MetadataReceiver
import com.badradio.nz.notification.MediaNotificationManager
import com.badradio.nz.utilities.MetadataObserver
import com.badradio.nz.utilities.PlayerStateObserver
import java.lang.Thread.sleep


class RadioService : Service(), MetadataObserver {
    private val observers: MutableList<PlayerStateObserver> = mutableListOf()

    private lateinit var stationInfo: StationInfo
    private lateinit var mediaNotificationManager: MediaNotificationManager

    override fun onCreate() {
        super.onCreate()

        getStationInfo {
            stationInfo = it
            // TODO: enable player here
        } // TODO: make sync

        mediaNotificationManager = MediaNotificationManager(this)
        registerForPlayerState(mediaNotificationManager)

        // Start periodic metadata fetcher
        MetadataReceiver.start(this)

        val mediaPlayer = MediaPlayer().apply {
            setDataSource(applicationContext, Uri.parse("https://s2.radio.co/s2b2b68744/listen")) // get link from stationInfo
        }
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            Log.d(TAG, "media player prepared")
            mediaPlayer.start()
        }
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
    }

    override fun onAlbumArt(art: Bitmap) {
        observers.forEach { it.onAlbumArt(art) }
    }

    fun registerForPlayerState(observer: PlayerStateObserver) {
        observers.add(observer)
    }

    private val TAG = RadioService::class.qualifiedName
}

enum class PlayerState {
    IDLE,
    LOADING,
    PLAYING,
    PAUSED,
    STOPPED,
    ERROR,
}
