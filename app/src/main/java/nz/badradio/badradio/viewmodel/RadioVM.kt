package nz.badradio.badradio.viewmodel

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nz.badradio.badradio.R
import nz.badradio.badradio.model.metadata.SongMetadata
import nz.badradio.badradio.model.metadata.art.StreamingServiceCrawler
import nz.badradio.badradio.model.radio.RadioManager
import nz.badradio.badradio.utilities.generateFunExecuteIf
import nz.badradio.badradio.utilities.generateFunExecuteWhen
import nz.badradio.badradio.view.notification.MediaSessionManager
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

object RadioVM: Player.Listener {
    private lateinit var resources: Resources
    private lateinit var state: RadioVMState
    private fun defaultState() {
        state = RadioVMState(
            displayButtonsNotification = true,
            displayPause = false,
            enablePlayPauseButton = true,
            displayLive = true,
            actualTitle = resources.getString(R.string.default_song_name),
            title = resources.getString(R.string.initializing_service),
            artist = resources.getString(R.string.default_artist),
            art = defaultAlbumArt,
            notificationArt = defaultNotificationAlbumArt,
        )
    }

    private lateinit var mediaSessionManager: MediaSessionManager

    private lateinit var mediaDescriptionBuilder: MediaDescriptionCompat.Builder
    private lateinit var mediaItem: MediaBrowserCompat.MediaItem

    private lateinit var defaultAlbumArt: Bitmap
    private lateinit var defaultNotificationAlbumArt: Bitmap
    private val defaultNotificationAlbumArtRes =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            R.drawable.badradio_background
        } else {
            R.drawable.badradio
        }

    private val executor = Executors.newSingleThreadScheduledExecutor()
    private var shutdownServiceJob: ScheduledFuture<*>? = null

    private var initializing = AtomicBoolean(false)
    private var initialized = false

    fun initialize(context: Context) {
        if (
            initialized
        || !initializing.compareAndSet(false, true)
        ) {
            return
        }

        resources = context.resources

        defaultAlbumArt = BitmapFactory.decodeResource(resources, R.drawable.badradio)
        defaultNotificationAlbumArt = BitmapFactory.decodeResource(resources, defaultNotificationAlbumArtRes)

        defaultState()

        mediaDescriptionBuilder = MediaDescriptionCompat.Builder().apply {
            setMediaId(BADRADIO_MEDIA_ID)

            setTitle(resources.getString(R.string.default_song_name))
            setSubtitle(resources.getString(R.string.default_artist))

            setIconBitmap(defaultNotificationAlbumArt)
        }
        mediaItem = MediaBrowserCompat.MediaItem(mediaDescriptionBuilder.build(), FLAG_PLAYABLE)

        mediaSessionManager = MediaSessionManager(context, state)
        RadioManager.startService(context, mediaSessionManager.mediaSession)
        addObserver(mediaSessionManager)

        initialized = true
        initializing.set(false)
    }

    // Service Controls

    private fun startService(context: Context)
        = RadioManager.startService(context, mediaSessionManager.mediaSession)

    fun stopService() = runIfInitialized {
        RadioManager.stopService()

        defaultState()
        state.apply {
            displayButtonsNotification = false
            displayLive = false
        }

        state.title = resources.getString(R.string.service_stopped)

        notifyObservers()
    }

    private fun scheduleServiceShutdown() {
        cancelServiceShutdown()
        shutdownServiceJob = executor.schedule({
                stopService()
            },
            30, TimeUnit.MINUTES,
        )
    }

    private fun cancelServiceShutdown() = shutdownServiceJob?.cancel(false)

    fun isInitializedAndPlaying() = initialized && state.displayButtonsNotification

    // Music Controls

    fun onPlayPause(context: Context) {
        if (startService(context)) {
            state.displayLive = true
        }
        onPlayPause()
    }
    fun onPlayPause() = runWhenInitialized {
        if (!state.enablePlayPauseButton) {
            return@runWhenInitialized
        }

        if (state.displayPause) {
            state.displayLive = false
            RadioManager.onPause()
        } else {
            RadioManager.onPlay()
        }
    }

    fun onGoLive(context: Context) {
        startService(context)
        onGoLive()
    }
    fun onGoLive() = runWhenInitialized {
        if (state.displayLive) {
            return@runWhenInitialized
        }

        state.displayLive = true
        RadioManager.onGoLive()
    }

    // Player State Observation

    override fun onPlaybackStateChanged(playbackState: Int) = runWhenInitialized {
        state.enablePlayPauseButton = playbackState != Player.STATE_BUFFERING

        state.title = if (playbackState == Player.STATE_BUFFERING) {
            resources.getString(R.string.loading)
        } else {
            state.actualTitle
        }

        state.displayButtonsNotification = true

        notifyObservers()
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) = runWhenInitialized {
        state.displayPause = isPlaying

        if (isPlaying) {
            cancelServiceShutdown()
        } else {
            scheduleServiceShutdown()
        }

        notifyObservers()
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {

    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) =
        runWhenInitialized {
            val rawTitle = mediaMetadata.title ?: return@runWhenInitialized
            val metadata = SongMetadata.fromRawTitle(rawTitle.toString())

            state.actualTitle = metadata.title
            state.title = state.actualTitle
            state.artist = metadata.artist

            notifyObservers()

            GlobalScope.launch { // synchronous network in this function
                val albumArtGetter = StreamingServiceCrawler()

                albumArtGetter.search(metadata)

                val loadedArt = albumArtGetter.getAlbumArt()
                state.art = loadedArt ?: defaultAlbumArt
                state.notificationArt = loadedArt ?: defaultNotificationAlbumArt

                // TODO: move to extra file?
                mediaDescriptionBuilder.apply {
                    setTitle(state.title)
                    setSubtitle(state.artist)

                    setIconBitmap(state.notificationArt)
                }
                mediaItem = MediaBrowserCompat.MediaItem(mediaDescriptionBuilder.build(), FLAG_PLAYABLE)
                // end

                notifyObservers()
            }
        }

    // Media Browser

    fun loadRecentMediaItem(
        result: MediaBrowserServiceCompat.Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) = runWhenInitialized {
        result.sendResult(mutableListOf(mediaItem))
    }

    // Observers

    private val observers: MutableList<RadioVMObserver> = mutableListOf()
    fun addObserver(o: RadioVMObserver) = observers.add(o)
    fun removeObserver(o: RadioVMObserver) = observers.remove(o)
    fun requestState(o: RadioVMObserver) = o.onStateChange(state)
    private fun notifyObservers() = observers.forEach { requestState(it) }

    // Helpers

    private val runIfInitialized = generateFunExecuteIf { initialized }
    private val runWhenInitialized = generateFunExecuteWhen { initialized }

}

private const val BADRADIO_MEDIA_ID = "nz.badradio.badradio.radio_viewmodel.MEDIA_ID"
