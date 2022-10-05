package com.badradio.nz.notification

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media2.common.MediaMetadata
import com.badradio.nz.R
import com.badradio.nz.player.PlayerState
import com.badradio.nz.utilities.PlayerStateObserver

@SuppressLint("ObsoleteSdkInt")
class MediaNotificationManager(context: Context) : PlayerStateObserver {
    private val channelID = "BADRADIONotificationChannel"

    private val builder = NotificationCompat.Builder(context, channelID)
    private val metadataBuilder = MediaMetadataCompat.Builder()
    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

    private val mediaSession = MediaSessionCompat(context, "RadioService")

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
            val channel = NotificationChannelCompat
                .Builder(channelID, importance)
                .setName("BADRADIO notification channel")
                .build()
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onSongTitle(title: String, artist: String) {
        mediaSession.setMetadata(
            metadataBuilder
                .putString(MediaMetadata.METADATA_KEY_TITLE, title)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, artist)
                .build()
        )

        val mediaStyle = MediaStyle().setMediaSession(mediaSession.sessionToken)

        val notification = builder
            .setStyle(mediaStyle)
            .setSilent(true)
            .setSmallIcon(R.drawable.ic_radio_black_24dp)
            .build()

        notificationManager.notify(0, notification)
    }

    override fun onStateChange(state: PlayerState) {
        TODO("Not yet implemented")
    }

    override fun onAlbumArt(art: Bitmap) {
        TODO("Not yet implemented")
    }

}