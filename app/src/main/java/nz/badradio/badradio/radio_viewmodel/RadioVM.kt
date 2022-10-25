package nz.badradio.badradio.radio_viewmodel

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
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
    private lateinit var mediaSession: MediaSessionCompat

    private lateinit var defaultAlbumArt: Bitmap
    private lateinit var defaultNotificationAlbumArt: Bitmap
    private val defaultNotificationAlbumArtRes =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            R.drawable.badradio_background
        } else {
            R.drawable.badradio
        }

    private val metadataBuilder = MediaMetadataCompat.Builder()

    fun initialize(context: Context) {
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

        mediaSession = MediaSessionCompat(context, "BADRADIO Media Session")

        RadioManager.initialize(context, mediaSession)
    }

    fun onPlayPause() {
        if (state.displayPause) {
            RadioManager.onPause()
        } else {
            RadioManager.onPlay()
        }
    }

    fun onStop() {
        RadioManager.onStop()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        state.enableButtons = playbackState != Player.STATE_BUFFERING
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        state.displayPause = isPlaying
        notifyObservers()
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {

    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        val rawTitle = mediaMetadata.title ?: return
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

            notifyObservers()
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        throw error
    }

    /**
     * Observers
     */

    private val observers: MutableList<RadioVMObserver> = mutableListOf()
    fun addObserver(o: RadioVMObserver) = observers.add(o)
    fun removeObserver(o: RadioVMObserver) = observers.remove(o)
    private fun notifyObservers() = observers.forEach { it.onStateChange(state) }

}