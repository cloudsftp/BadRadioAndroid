package com.badradio.nz.player

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.badradio.nz.R
import com.badradio.nz.metadata.MetadataReceiver
import com.badradio.nz.metadata.SongMetadata
import com.badradio.nz.notification.MediaNotificationManager
import com.badradio.nz.utilities.MetadataObserver
import com.badradio.nz.utilities.PlayerStateObserver
import com.badradio.nz.utilities.UserInputObserver
import com.badradio.nz.utilities.client
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource


class RadioService : Service(), MetadataObserver, UserInputObserver {
    private val observers: MutableList<PlayerStateObserver> = mutableListOf()
    private lateinit var state: PlayerState

    private lateinit var mediaPlayer: ExoPlayer

    private lateinit var mediaNotificationManager: MediaNotificationManager

    private val audioAttributes = AudioAttributes.Builder().apply {
        setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        setUsage(C.USAGE_MEDIA)
    }.build()

    private val okHttpDataSourceFactory = OkHttpDataSource.Factory(client).apply {
        setDefaultRequestProperties(
            mutableMapOf(
                Pair("Icy-Metadata", "1")
            )
        )
    }

    override fun onCreate() {
        super.onCreate()

        state = PlayerState(
            PlaybackState.NOT_READY,
            SongMetadata("Phonk Radio", "BADRADIO"),
            BitmapFactory.decodeResource(resources, R.drawable.badradio)
        )
        notifyPlayerStateObservers()

        getStationInfo {
            createPlayer(it)
        }

        mediaNotificationManager = MediaNotificationManager(this)
        registerPlayerStateObserver(mediaNotificationManager)

        // Start periodic metadata fetcher
        MetadataReceiver.start(this)
    }

    private fun createPlayer(stationInfo: StationInfo) {
        mediaPlayer = ExoPlayer.Builder(applicationContext).apply {
            setAudioAttributes(audioAttributes, true)
            setWakeMode(C.WAKE_MODE_NETWORK)
            setMediaSourceFactory(ProgressiveMediaSource.Factory(okHttpDataSourceFactory))
        }.build()

        Handler(Looper.getMainLooper()).post {
            mediaPlayer.setMediaItem(
                MediaItem.fromUri(Uri.parse(stationInfo.streamURL))
            )

            mediaPlayer.playWhenReady = true
            mediaPlayer.prepare()
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
        notifyPlayerStateObservers()
    }

    fun unregisterPlayerStateObserver(observer: PlayerStateObserver) {
        val success = observers.remove(observer)

        if (!success) {
            Log.w(tag, "Tried to unregister observer $observer, was not registered in the first place")
        }
    }

    private fun notifyPlayerStateObservers() {
        observers.forEach {
            it.onStateChange(state)
        }
    }

    override fun onPlay() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.play()

            state.playback = PlaybackState.PLAYING
            notifyPlayerStateObservers()
        }
    }

    override fun onPause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()

            state.playback = PlaybackState.PAUSED
            notifyPlayerStateObservers()
        }
    }

    private val tag = "RadioService"
}

enum class PlaybackState {
    NOT_READY,
    PLAYING,
    PAUSED,
}
