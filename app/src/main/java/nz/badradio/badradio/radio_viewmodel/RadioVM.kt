package nz.badradio.badradio.radio_viewmodel

import android.content.Context
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import nz.badradio.badradio.radio.RadioManager


object RadioVM: UserInputObserver, Player.Listener {
    private val observers: MutableList<RadioVMObserver> = mutableListOf()

    fun initialize(context: Context) {
        RadioManager.initialize(context)
    }

    override fun onPlayPause() {

    }

    override fun onStop() {

    }

    override fun onPlaybackStateChanged(playbackState: Int) {

    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {

    }

    override fun onIsLoadingChanged(isLoading: Boolean) {

    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {

    }

    override fun onPlayerError(error: PlaybackException) {

    }

    /**
     * Observers
     */

    fun addObserver(o: RadioVMObserver) = observers.add(o)
    fun removeObserver(o: RadioVMObserver) = observers.remove(o)

}