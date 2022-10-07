package com.badradio.nz.player

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import com.badradio.nz.R
import com.badradio.nz.metadata.MetadataReceiver
import com.badradio.nz.metadata.SongMetadata
import com.badradio.nz.notification.MediaNotificationManager
import com.badradio.nz.utilities.MetadataObserver
import com.badradio.nz.utilities.PlayerStateObserver
import com.badradio.nz.utilities.UserInputObserver


class RadioService : Service(), MetadataObserver {
    private val observers: MutableList<PlayerStateObserver> = mutableListOf()
    private lateinit var state: PlayerState

    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var mediaNotificationManager: MediaNotificationManager

    private lateinit var wifiLock: WifiManager.WifiLock

    override fun onCreate() {
        super.onCreate()

        state = PlayerState(
            PlaybackState.NOT_READY,
            SongMetadata("Phonk Radio", "BADRADIO"),
            BitmapFactory.decodeResource(resources, R.drawable.badradio)
        )
        notifyPlayerStateObservers()

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
                state.playback = PlaybackState.PAUSED
                notifyPlayerStateObservers()
            }
        }
    }

    inner class RadioServiceBinder : Binder() {
        val service: RadioService
            get() = this@RadioService
    }

    override fun onBind(intent: Intent?): IBinder {
        return RadioServiceBinder()
    }

    override fun onSongTitle(title: String, artist: String) {

        state.metadata.title = title
        state.metadata.artist = artist

        metadataUpdated()
    }

    override fun onAlbumArt(art: Bitmap) {
        state.art = art

        metadataUpdated()
    }

    private fun metadataUpdated() {
        notifyPlayerStateObservers()
    }

    fun registerPlayerStateObserver(observer: PlayerStateObserver) {
        observers.add(observer)
    }

    private fun notifyPlayerStateObservers() {
        observers.forEach {
            it.onStateChange(state)
        }
    }

    /*
    override fun onPlay() {
        if (!mediaPlayer.isPlaying) {
            wifiLock.acquire()
            mediaPlayer.start()
        }
    }

    override fun onPause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            wifiLock.release()
        }
    } */
}

enum class PlaybackState {
    NOT_READY,
    PLAYING,
    PAUSED,
}
