package com.badradio.nz.player

import android.os.IBinder
import android.media.AudioManager.OnAudioFocusChangeListener
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.MediaControllerCompat
import android.media.AudioManager
import com.badradio.nz.R
import android.support.v4.media.MediaMetadataCompat
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import android.text.TextUtils
import okhttp3.OkHttpClient
import android.app.Service
import android.content.*
import android.net.Uri
import android.os.Binder
import com.badradio.nz.metadata.MetadataReceiver
import com.badradio.nz.utilities.ListenersManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.runBlocking

class RadioService : Service(), Player.Listener, OnAudioFocusChangeListener {
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var stationInfo: StationInfo

    private lateinit var notificationManager: MediaNotificationManager

    private val binder: IBinder = RadioServiceBinder()
    var mediaSession: MediaSessionCompat? = null
        private set
    private var transportControls: MediaControllerCompat.TransportControls? = null
    private var audioManager: AudioManager? = null
    private var serviceInUse = false
    var status: String? = null
        private set
    private var strAppName: String? = null
    private var strLiveBroadcast: String? = null

    inner class RadioServiceBinder : Binder() {
        val service: RadioService
            get() = this@RadioService
    }

    private val becomingNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            pause()
        }
    }

    private val mediasSessionCallback: MediaSessionCompat.Callback = object : MediaSessionCompat.Callback() {
        override fun onPause() {
            super.onPause()
            pause()
        }

        override fun onStop() {
            super.onStop()
            stop()
            notificationManager.cancelNotify()
        }

        override fun onPlay() {
            super.onPlay()
            resume()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        serviceInUse = true
        return binder
    }

    override fun onCreate() {
        super.onCreate()

        getStationInfo {
            stationInfo = it
        }

        // Start periodic metadata fetcher
        MetadataReceiver.start()

        // TODO: clean up this mess
        strAppName = resources.getString(R.string.app_name)
        strLiveBroadcast = resources.getString(R.string.notification_playing)
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        notificationManager = MediaNotificationManager(this)
        mediaSession = MediaSessionCompat(this, javaClass.simpleName)
        transportControls = mediaSession!!.controller.transportControls
        mediaSession!!.isActive = true
        mediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession!!.setMetadata(MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "...")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, strAppName)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, strLiveBroadcast)
                .build())
        mediaSession!!.setCallback(mediasSessionCallback)

        // Create player

        exoPlayer = ExoPlayer.Builder(applicationContext).build()
        exoPlayer.addListener(this)
        exoPlayer.playWhenReady = true

        registerReceiver(becomingNoisyReceiver, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))

        status = PlaybackStatus.IDLE
    }

    private fun play() {
        val bandwidthMeter = DefaultBandwidthMeter.Builder(applicationContext).build()
        val dataSourceFactory = ShoutcastDataSourceFactory(
            OkHttpClient.Builder().build(),
            Util.getUserAgent(this, javaClass.simpleName),
            bandwidthMeter
        )
        val mediaSource = DefaultMediaSourceFactory(applicationContext)
            .setDataSourceFactory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(stationInfo.streamURL)))

        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action
        if (TextUtils.isEmpty(action)) {
            return START_STICKY
        }
        val result = audioManager!!.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            stop()
            return START_STICKY
        }
        if (action.equals(ACTION_PLAY, ignoreCase = true)) {
            transportControls!!.play()
        } else if (action.equals(ACTION_PAUSE, ignoreCase = true)) {
            transportControls!!.pause()
        } else if (action.equals(ACTION_STOP, ignoreCase = true)) {
            transportControls!!.stop()
        }
        return START_STICKY
    }

    override fun onUnbind(intent: Intent): Boolean {
        serviceInUse = false
        if (status == PlaybackStatus.IDLE) stopSelf()
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent) {
        serviceInUse = true
    }

    override fun onDestroy() {
        pause()
        exoPlayer.release()
        exoPlayer.removeListener(this)
        notificationManager.cancelNotify()
        mediaSession!!.release()
        unregisterReceiver(becomingNoisyReceiver)
        super.onDestroy()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                exoPlayer.volume = 0.8f
                resume()
            }
            AudioManager.AUDIOFOCUS_LOSS -> stop()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> if (isPlaying) pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> if (isPlaying) exoPlayer.volume = 0.1f
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_BUFFERING -> status = PlaybackStatus.LOADING
            Player.STATE_ENDED -> {
                status = PlaybackStatus.STOPPED
                notificationManager.cancelNotify()
            }
            Player.STATE_IDLE -> status = PlaybackStatus.IDLE
            Player.STATE_READY -> status = if (playWhenReady) PlaybackStatus.PLAYING else PlaybackStatus.PAUSED
            else -> status = PlaybackStatus.IDLE
        }
        if (status != PlaybackStatus.IDLE) notificationManager.startNotify(status)
        ListenersManager.onEvent(status!!)
    }

    override fun onRepeatModeChanged(repeatMode: Int) {}
    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
    override fun onTimelineChanged(timeline: Timeline, reason: Int) {}
    override fun onTracksChanged(tracks: Tracks) {}
    @Deprecated("Deprecated in Java")
    override fun onLoadingChanged(isLoading: Boolean) {}
    override fun onPlayerError(error: PlaybackException) {
        ListenersManager.onEvent(PlaybackStatus.ERROR)
    }

    @Deprecated("Deprecated in Java")
    override fun onPositionDiscontinuity(reason: Int) {}
    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
    @Deprecated("Deprecated in Java")
    override fun onSeekProcessed() {}

    val audioSessionId: Int
        get() = exoPlayer.audioSessionId

    fun resume() {
        play()
    }

    fun pause() {
        exoPlayer.playWhenReady = false
        audioManager!!.abandonAudioFocus(this)
    }

    fun stop() {
        exoPlayer.stop()
        audioManager!!.abandonAudioFocus(this)
    }

    fun playOrPause() {
        if (isPlaying) {
            pause()
        } else {
            play()
        }
    }

    val isPlaying: Boolean
        get() = status == PlaybackStatus.PLAYING

    companion object {
        const val ACTION_PLAY = "com.app.yoursingleradio.ACTION_PLAY"
        const val ACTION_PAUSE = "com.app.yoursingleradio.ACTION_PAUSE"
        const val ACTION_STOP = "com.app.yoursingleradio.ACTION_STOP"
    }
}