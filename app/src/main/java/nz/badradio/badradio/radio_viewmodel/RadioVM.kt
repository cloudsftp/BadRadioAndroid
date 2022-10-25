package nz.badradio.badradio.radio_viewmodel

import android.content.Context
import android.content.res.Resources
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

    fun initialize(context: Context) {
        RadioManager.initialize(context)

        if (!::resources.isInitialized) {
            resources = context.resources
        }

        if (!::state.isInitialized) {
            state = RadioVMState(
                displayPause = false,
                enableButtons = true,
                title = resources.getString(R.string.default_song_name),
                artist = resources.getString(R.string.default_artist),
                art = null, // for notification this depends on the android version
            )
        }
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
            state.art = getAlbumArt(metadata)
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