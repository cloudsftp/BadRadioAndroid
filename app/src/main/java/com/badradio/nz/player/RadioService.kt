package com.badradio.nz.player

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.badradio.nz.utilities.UserInputObserver
import com.badradio.nz.utilities.client
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource


class RadioService : Service(), UserInputObserver {
    private lateinit var mediaPlayer: ExoPlayer
    private var initialized = false

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

        runWhenPlayerInitialized {
            mediaPlayer.apply {
                setMediaItem(MediaItem.fromUri(Uri.parse(stationInfo.streamURL)))
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

    fun addListener(listener: Player.Listener) = runWhenPlayerInitialized {
        mediaPlayer.addListener(listener)
    }

    fun removeListener(listener: Player.Listener) = runWhenPlayerInitialized {
        mediaPlayer.removeListener(listener)
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
}
