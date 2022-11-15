package nz.badradio.badradio.radio_viewmodel

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nz.badradio.badradio.R
import nz.badradio.badradio.metadata.SongMetadata
import nz.badradio.badradio.metadata.art.getAlbumArt
import nz.badradio.badradio.radio.RadioManager

object RadioVM: Player.Listener {
    private lateinit var resources: Resources
    private lateinit var state: RadioVMState

    private lateinit var playBackStateBuilder: PlaybackStateCompat.Builder
    private lateinit var mediaSession: MediaSessionCompat
    private val mediaSessionCallback: MediaSessionCompat.Callback =
        object : MediaSessionCompat.Callback() {
            override fun onPlay() = this@RadioVM.onPlayPause()
            override fun onPause() = this@RadioVM.onPlayPause()
            override fun onSkipToNext() = this@RadioVM.onSkip()
        }

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

    private var initialized = false

    private val metadataBuilder = MediaMetadataCompat.Builder()

    fun initialize(context: Context) {
        if (initialized) {
            return
        }

        resources = context.resources

        defaultAlbumArt = BitmapFactory.decodeResource(resources, R.drawable.badradio)
        defaultNotificationAlbumArt = BitmapFactory.decodeResource(resources, defaultNotificationAlbumArtRes)

        state = RadioVMState(
            displayPause = false,
            enableButtons = true,
            title = resources.getString(R.string.default_song_name),
            artist = resources.getString(R.string.default_artist),
            art = defaultAlbumArt,
            notificationArt = defaultNotificationAlbumArt,
        )

        mediaDescriptionBuilder = MediaDescriptionCompat.Builder().apply {
            setMediaId(BADRADIO_MEDIA_ID)

            setTitle(resources.getString(R.string.default_song_name))
            setSubtitle(resources.getString(R.string.default_artist))

            setIconBitmap(defaultNotificationAlbumArt)
        }
        mediaItem = MediaBrowserCompat.MediaItem(mediaDescriptionBuilder.build(), FLAG_PLAYABLE)

        playBackStateBuilder = PlaybackStateCompat.Builder().apply {
            setState(PlaybackStateCompat.STATE_BUFFERING, 0, 1f)
            setActions(
                PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            )
        }

        mediaSession = MediaSessionCompat(context, "BADRADIO Media Session").apply {
            setPlaybackState(playBackStateBuilder.build())
            setCallback(mediaSessionCallback)
        }
        RadioManager.initialize(context, mediaSession)

        initialized = true
    }

    // Service Controls

    fun restartService(context: Context) = runWhenInitialized {
        RadioManager.restartService(context, mediaSession)
    }

    // Music Controls

    fun onPlayPause() = runWhenInitialized {
        if (!state.enableButtons) {
            return@runWhenInitialized
        }

        if (state.displayPause) {
            RadioManager.onPause()
        } else {
            RadioManager.onPlay()
        }
    }

    /*
    fun onStop() = runWhenInitialized {
        if (!state.enableButtons) {
            return@runWhenInitialized
        }

        RadioManager.onStop()
    }
    */

    fun onSkip() = runWhenInitialized {
        if (!state.enableButtons) {
            return@runWhenInitialized
        }

        RadioManager.onSkip()
    }

    // Player State Observation

    override fun onPlaybackStateChanged(playbackState: Int) = runWhenInitialized {
        state.enableButtons = playbackState != Player.STATE_BUFFERING

        playBackStateBuilder.setState(
            when (playbackState) {
                Player.STATE_BUFFERING -> PlaybackStateCompat.STATE_BUFFERING
                Player.STATE_IDLE -> PlaybackStateCompat.STATE_STOPPED
                else -> PlaybackStateCompat.STATE_NONE
            }, 0, 1f
        )
        mediaSession.setPlaybackState(playBackStateBuilder.build())

        notifyObservers()
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) = runWhenInitialized {
        state.displayPause = isPlaying
        playBackStateBuilder.setState(
            if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
            0, 1f
        )
        mediaSession.setPlaybackState(playBackStateBuilder.build())

        notifyObservers()
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {

    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) =
        runWhenInitialized {
            val rawTitle = mediaMetadata.title ?: return@runWhenInitialized
            val metadata = SongMetadata.fromRawTitle(rawTitle.toString())

            state.title = metadata.title
            state.artist = metadata.artist

            GlobalScope.launch { // synchronous network in this function
                val loadedArt = getAlbumArt(metadata)
                state.art = loadedArt ?: defaultAlbumArt
                state.notificationArt = loadedArt ?: defaultNotificationAlbumArt

                metadataBuilder.apply {
                    putString(androidx.media2.common.MediaMetadata.METADATA_KEY_TITLE, state.title)
                    putString(androidx.media2.common.MediaMetadata.METADATA_KEY_ARTIST, state.artist)

                    putBitmap(androidx.media2.common.MediaMetadata.METADATA_KEY_ART, state.notificationArt)
                }
                mediaSession.setMetadata(metadataBuilder.build())

                mediaDescriptionBuilder.apply {
                    setTitle(state.title)
                    setSubtitle(state.artist)

                    setIconBitmap(state.notificationArt)
                }
                mediaItem = MediaBrowserCompat.MediaItem(mediaDescriptionBuilder.build(), FLAG_PLAYABLE)

                notifyObservers()
            }
        }

    override fun onPlayerError(error: PlaybackException) {
        throw error
    }

    // Media Browser

    fun loadRecentMediaItem(result: MediaBrowserServiceCompat.Result<MutableList<MediaBrowserCompat.MediaItem>>)
        = runWhenInitialized {
            result.sendResult(mutableListOf(mediaItem))
    }

    // Observers

    private val observers: MutableList<RadioVMObserver> = mutableListOf()
    fun addObserver(o: RadioVMObserver) = observers.add(o)
    fun removeObserver(o: RadioVMObserver) = observers.remove(o)
    fun requestState(o: RadioVMObserver) = o.onStateChange(state)
    private fun notifyObservers() = observers.forEach { requestState(it) }

    // Helper

    private fun runWhenInitialized(r: Runnable) {
        if (initialized) {
            r.run()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                runWhenInitialized(r)
            }, 100)
        }
    }
}

private const val BADRADIO_MEDIA_ID = "nz.badradio.badradio.radio_viewmodel.MEDIA_ID"
