package nz.badradio.badradio.view.notification

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import nz.badradio.badradio.R
import nz.badradio.badradio.viewmodel.RadioVM
import nz.badradio.badradio.viewmodel.RadioVMObserver
import nz.badradio.badradio.viewmodel.RadioVMState

class MediaSessionManager(context: Context, state: RadioVMState) : RadioVMObserver {

    private val goLiveActionIdentifier = "nz.badradio.badradio.view.notification.MediaSessionManager.GO_LIVE"
    private val isLiveActionIdentifier = "nz.badradio.badradio.view.notification.MediaSessionManager.IS_LIVE"
    private val goLiveActionName = "Go Live"
    private val isLiveActionName = "Is Live"

    val mediaSession: MediaSessionCompat
    private val mediaSessionCallback: MediaSessionCompat.Callback =
        object : MediaSessionCompat.Callback() {
            override fun onPlay()       = RadioVM.onPlayPause()
            override fun onPause()      = RadioVM.onPlayPause()
            override fun onCustomAction(action: String?, extras: Bundle?) {
                when (action) {
                    goLiveActionIdentifier -> RadioVM.onGoLive()
                }
            }
        }

    private val metadataBuilder = MediaMetadataCompat.Builder()

    private val defaultTitle = context.resources.getString(R.string.default_song_name)

    init {
        mediaSession = MediaSessionCompat(context, "BADRADIO Media Session").apply {
            setPlaybackState(createStateBuilder(state).build())
            setCallback(mediaSessionCallback)
        }
    }

    // Needed, because in android 13 notification image only updates when title changed
    private var lastAlbumArt: Bitmap? = null

    override fun onStateChange(state: RadioVMState) {
        val playBackStateBuilder = createStateBuilder(state)

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

        if (
            lastAlbumArt != state.notificationArt
            || state.actualTitle == defaultTitle
        ) {
            metadataBuilder.apply {
                putString( androidx.media2.common.MediaMetadata.METADATA_KEY_TITLE, state.actualTitle)
                putString(androidx.media2.common.MediaMetadata.METADATA_KEY_ARTIST, state.artist)

                putBitmap( androidx.media2.common.MediaMetadata.METADATA_KEY_ART, state.notificationArt)
            }
            mediaSession.setMetadata(metadataBuilder.build())

            lastAlbumArt = state.notificationArt
        }
    }

    private fun createStateBuilder(state: RadioVMState): PlaybackStateCompat.Builder {
        return PlaybackStateCompat.Builder().apply {
            setState(PlaybackStateCompat.STATE_BUFFERING, 0, 1f)
            if (state.displayButtonsNotification) {
                setActions(
                    PlaybackStateCompat.ACTION_PAUSE or
                            PlaybackStateCompat.ACTION_PLAY
                )

                if (state.displayLive) {
                    addCustomAction(
                        isLiveActionIdentifier,
                        isLiveActionName,
                        R.drawable.ic_radio_button_checked,
                    )
                } else {
                    addCustomAction(
                        goLiveActionIdentifier,
                        goLiveActionName,
                        R.drawable.ic_radio_button_unchecked,
                    )
                }
            }
        }
    }

}
