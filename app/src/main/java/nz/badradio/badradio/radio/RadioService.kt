package nz.badradio.badradio.radio

import android.content.Intent
import android.net.Uri
import android.os.*
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import nz.badradio.badradio.utilities.client
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import nz.badradio.badradio.radio_viewmodel.RadioVM
import nz.badradio.badradio.radio_viewmodel.UserInputVMObserver
import nz.badradio.badradio.station.StationInfo
import nz.badradio.badradio.station.getStationInfo
import java.lang.Runnable

class RadioService : MediaBrowserServiceCompat(), UserInputVMObserver {
    private lateinit var mediaPlayer: ExoPlayer

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

        runWhenPlayerInitialized { // for main loop
            mediaPlayer.apply {
                setMediaItem(MediaItem.fromUri(Uri.parse(stationInfo.streamURL)))
                addListener(RadioVM)

                onPlay()
            }
        }
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

    override fun onPlay() {
        if (mediaPlayer.playbackState == Player.STATE_IDLE) {
            mediaPlayer.prepare()
        }

        mediaPlayer.play()
    }
    override fun onPause() = mediaPlayer.pause()
    override fun onSkip() {
        mediaPlayer.seekForward()
        mediaPlayer.play()
    }

    // Media Browser

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(BADRADIO_RECENT_BROWSER_ROOT, Bundle())
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        if (parentId == BADRADIO_RECENT_BROWSER_ROOT) {
            RadioVM.loadRecentMediaItem(result)
            result.detach()
        } else {
            result.sendResult(null)
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
}

private const val BADRADIO_EMPTY_BROWSER_ROOT = "nz.badradio.badradio.radio.BADRADIO_EMPTY_BROWSER_ROOT"
private const val BADRADIO_RECENT_BROWSER_ROOT = "nz.badradio.badradio.radio.BADRADIO_RECENT_BROWSER_ROOT"
