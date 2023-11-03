package nz.badradio.badradio.model.radio

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import nz.badradio.badradio.model.station.StationInfo
import nz.badradio.badradio.model.station.getStationInfo
import nz.badradio.badradio.utilities.client
import nz.badradio.badradio.utilities.generateFunExecuteWhen
import nz.badradio.badradio.viewmodel.RadioVM
import nz.badradio.badradio.viewmodel.UserInputVMObserver

@UnstableApi class RadioService : Service(), UserInputVMObserver {
    private lateinit var mediaPlayer: ExoPlayer

    private val audioAttributes = AudioAttributes.Builder().apply {
        setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        setUsage(C.USAGE_MEDIA)
    }.build()

    private val okHttpDataSourceFactory = OkHttpDataSource.Factory(client).apply {
        setDefaultRequestProperties(
            mapOf(
                Pair("Icy-Metadata", "1")
            )
        )
    }

    private val loadControl = DefaultLoadControl.Builder().apply {
        setBufferDurationsMs(
            20_000, 120_000,
            5_000, 2_000
        )
    }.build()

    override fun onCreate() {
        super.onCreate()

        // TODO: rethink command structure, only create player if want to play
        getStationInfo {
            createPlayer(it)
        }
    }

    private fun createPlayer(stationInfo: StationInfo) {
        mediaPlayer = ExoPlayer.Builder(applicationContext).apply {
            setAudioAttributes(audioAttributes, true)
            setWakeMode(C.WAKE_MODE_NETWORK)
            setMediaSourceFactory(ProgressiveMediaSource.Factory(okHttpDataSourceFactory))
            setLoadControl(loadControl)
        }.build()

        Handler(Looper.getMainLooper()).post {
            mediaPlayer.apply {
                setMediaItem(MediaItem.fromUri(Uri.parse(stationInfo.streamURL)))
                addListener(RadioVM)

                onPlay()
            }
        }
    }

    override fun onDestroy() = runWhenPlayerInitialized {
        mediaPlayer.removeListener(RadioVM)
        mediaPlayer.release()
    }

    // Binding Service

    inner class RadioServiceBinder : Binder() {
        val service: RadioService
            get() = this@RadioService
    }

    override fun onBind(intent: Intent?): IBinder {
        return RadioServiceBinder()
    }

    // Control service

    override fun onPlay() = runWhenPlayerInitialized {
        if (mediaPlayer.playbackState == Player.STATE_IDLE) {
            mediaPlayer.prepare()
        }

        mediaPlayer.play()
    }
    override fun onPause() = runWhenPlayerInitialized {
        mediaPlayer.pause()
    }
    override fun onGoLive() = runWhenPlayerInitialized {
        mediaPlayer.seekForward()
        mediaPlayer.play()
    }

    // Helpers

    private val runWhenPlayerInitialized = generateFunExecuteWhen { ::mediaPlayer.isInitialized }
}
