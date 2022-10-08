package com.badradio.nz.player

import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.badradio.nz.R
import com.badradio.nz.metadata.SongMetadata
import com.badradio.nz.utilities.PlayerStateObserver
import com.badradio.nz.utilities.UserInputObserver
import com.badradio.nz.utilities.client
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource

class RadioService : Service(), Player.Listener, UserInputObserver {
    private lateinit var mediaPlayer: ExoPlayer

    private lateinit var playerState: PlayerState
    private val observers: MutableList<PlayerStateObserver> = mutableListOf()

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

        playerState = PlayerState(
            false,
            SongMetadata(
                resources.getString(R.string.default_song_name),
                resources.getString(R.string.default_artist)
            ),
            BitmapFactory.decodeResource(resources, R.drawable.badradio)
        )
        notifyObservers()

        getStationInfo {
            createPlayer(it)
        }
    }

    private fun createPlayer(stationInfo: StationInfo) {
        mediaPlayer = ExoPlayer.Builder(applicationContext).apply {
            setAudioAttributes(audioAttributes, true)
            setWakeMode(C.WAKE_MODE_NETWORK)
            setMediaSourceFactory(ProgressiveMediaSource.Factory(okHttpDataSourceFactory))
        }.build()

        runWhenPlayerInitialized { // for main loop
            mediaPlayer.apply {
                setMediaItem(MediaItem.fromUri(Uri.parse(stationInfo.streamURL)))
                addListener(this@RadioService)
                prepare()
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

    fun addListener(observer: PlayerStateObserver) {
        observers.add(observer)
    }

    fun removeListener(observer: PlayerStateObserver) {
        val success = observers.remove(observer)

        if (!success) {
            Log.w(tag, "Tried to remove observer $observer, was not added")
        }
    }

    override fun onPlay() = runWhenPlayerInitialized {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.play()
        }
    }

    override fun onPause() = runWhenPlayerInitialized {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        playerState.playing = isPlaying
        notifyObservers()
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        val rawTitle = mediaMetadata.title ?: return
        val metadata = SongMetadata.fromRawTitle(rawTitle.toString())

        playerState.metadata = metadata

        notifyObservers()
    }

    private fun notifyObservers() {
        observers.forEach {
            it.onStateChange(playerState)
        }
    }

    // Helpers

    private fun runWhenPlayerInitialized(r: Runnable) {
        Handler(Looper.getMainLooper()).post {
            runWhenPlayerInitializedInternal(r)
        }
    }

    private fun runWhenPlayerInitializedInternal(r: Runnable) {
        if (::mediaPlayer.isInitialized) {
            r.run()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                runWhenPlayerInitialized(r)
            }, 100)
        }
    }

    private val tag = "RadioService"
}
