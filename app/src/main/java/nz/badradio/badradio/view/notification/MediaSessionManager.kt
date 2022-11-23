package nz.badradio.badradio.view.notification

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import nz.badradio.badradio.viewmodel.RadioVM
import nz.badradio.badradio.viewmodel.RadioVMObserver
import nz.badradio.badradio.viewmodel.RadioVMState

class MediaSessionManager(context: Context) : RadioVMObserver {
    val mediaSession: MediaSessionCompat
    private val mediaSessionCallback: MediaSessionCompat.Callback =
        object : MediaSessionCompat.Callback() {
            override fun onPlay()       = RadioVM.onPlayPause()
            override fun onPause()      = RadioVM.onPlayPause()
            override fun onSkipToNext() = RadioVM.onGoLive()
        }

    private val playBackStateBuilder = PlaybackStateCompat.Builder().apply {
        setState(PlaybackStateCompat.STATE_BUFFERING, 0, 1f)
        setActions(
            PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        )
    }
    private val metadataBuilder = MediaMetadataCompat.Builder()

    init {
        mediaSession = MediaSessionCompat(context, "BADRADIO Media Session").apply {
            setPlaybackState(playBackStateBuilder.build())
            setCallback(mediaSessionCallback)
        }
    }

    override fun onStateChange(state: RadioVMState) {
        playBackStateBuilder.setState(
            if (!state.enablePlayPauseButton) {
                PlaybackStateCompat.STATE_BUFFERING
            } else if (state.displayPause) {
                PlaybackStateCompat.STATE_PLAYING
            } else {
                PlaybackStateCompat.STATE_PAUSED
            },
            0, 1f
        )
        mediaSession.setPlaybackState(playBackStateBuilder.build())

        metadataBuilder.apply {
            putString(androidx.media2.common.MediaMetadata.METADATA_KEY_TITLE, state.title)
            putString(androidx.media2.common.MediaMetadata.METADATA_KEY_ARTIST, state.artist)

            putBitmap(androidx.media2.common.MediaMetadata.METADATA_KEY_ART, state.notificationArt)
        }
        mediaSession.setMetadata(metadataBuilder.build())
    }

}
