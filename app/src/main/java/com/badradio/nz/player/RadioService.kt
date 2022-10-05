package com.badradio.nz.player

import android.content.Intent
import android.media.browse.MediaBrowser
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.service.media.MediaBrowserService
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media2.common.MediaMetadata
import com.badradio.nz.metadata.MetadataReceiver
import com.badradio.nz.notification.MediaNotificationManager


class RadioService : MediaBrowserService() {

    private lateinit var stationInfo: StationInfo
    private lateinit var mediaNotificationManager: MediaNotificationManager

    override fun onCreate() {
        super.onCreate()

        getStationInfo {
            stationInfo = it
            // TODO: enable player here
        }

        mediaNotificationManager = MediaNotificationManager(this)

        // Start periodic metadata fetcher
        MetadataReceiver.start()

        val mediaSession = MediaSessionCompat(this, "RadioService")
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, "STREAM")
                .putString(MediaMetadata.METADATA_KEY_ARTIST, "BADRADIO")
                .build()
        )

        mediaNotificationManager.createChannel()
        mediaNotificationManager.startNotify(mediaSession)
    }

    fun playOrPause() {
        TODO("implement")
    }

    fun stop() {
        // TODO: remove
    }

    inner class RadioServiceBinder : Binder() {
        val service: RadioService
            get() = this@RadioService
    }

    override fun onBind(intent: Intent?): IBinder {
        return RadioServiceBinder()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return null
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowser.MediaItem>>
    ) {

    }

}

enum class PlaybackStatus {
    IDLE,
    LOADING,
    PLAYING,
    PAUSED,
    STOPPED,
    ERROR,
}
