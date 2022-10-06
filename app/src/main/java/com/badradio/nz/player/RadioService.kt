package com.badradio.nz.player

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import com.badradio.nz.metadata.MetadataReceiver
import com.badradio.nz.notification.MediaNotificationManager
import com.badradio.nz.utilities.MetadataObserver
import com.badradio.nz.utilities.PlayerStateObserver


class RadioService : Service(), MetadataObserver {
    private val observers: MutableList<PlayerStateObserver> = mutableListOf()

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaNotificationManager: MediaNotificationManager

    private lateinit var wifiLock: WifiManager.WifiLock


    override fun onCreate() {
        super.onCreate()

        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "BADRADIO Wifi Lock")

        getStationInfo {
            createPlayer(it)
        }

        mediaNotificationManager = MediaNotificationManager(this)
        registerPlayerStateObserver(mediaNotificationManager)

        // Start periodic metadata fetcher
        MetadataReceiver.start(this)
    }

    private fun createPlayer(stationInfo: StationInfo) {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)

            setDataSource(stationInfo.streamURL)
            prepareAsync()

            setOnPreparedListener {
                playOrPause()
                // TODO: enable player here
            }
        }
    }

    fun playOrPause() {
        if (!mediaPlayer.isPlaying) {
            wifiLock.acquire()
            mediaPlayer.start()
        } else {
            mediaPlayer.pause()
            wifiLock.release()
        }
    }

    fun stop() {
        wifiLock.release()
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

    fun registerPlayerStateObserver(observer: PlayerStateObserver) {
        observers.add(observer)
    }

    private val TAG = RadioService::class.qualifiedName
}

enum class PlaybackState {
    NOT_READY,
    PLAYING,
    PAUSED,
    STOPPED,
}
