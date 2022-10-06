package com.badradio.nz.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media2.common.MediaMetadata
import com.badradio.nz.R
import com.badradio.nz.activity.PlayerActivity
import com.badradio.nz.player.PlaybackState
import com.badradio.nz.utilities.PlayerStateObserver

@SuppressLint("ObsoleteSdkInt")
class MediaNotificationManager(context: Context) : PlayerStateObserver {
    private val channelID = "BADRADIO Notification Channel"
    private val notificationID = 0

    private val mediaSession = MediaSessionCompat(context, "BADRADIO Media Session")
    private val mediaStyle = MediaStyle().setMediaSession(mediaSession.sessionToken)

    private val notification = NotificationCompat.Builder(context, channelID).apply {
        setStyle(mediaStyle)
        setSilent(true)
        setSmallIcon(R.drawable.ic_radio_black_24dp)
    }.build()

    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

    private val metadataBuilder = MediaMetadataCompat.Builder()

    init {
        val sessionIntent = Intent(context, PlayerActivity::class.java)
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            context,
            0,
            sessionIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        mediaSession.apply {
            setSessionActivity(sessionActivityPendingIntent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
            val channel = NotificationChannelCompat
                .Builder(channelID, importance)
                .setName(channelID)
                .build()
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onSongTitle(title: String, artist: String) {
        metadataBuilder.apply {
            putString(MediaMetadata.METADATA_KEY_TITLE, title)
            putString(MediaMetadata.METADATA_KEY_ARTIST, artist)
        }

        render()
    }

    override fun onAlbumArt(art: Bitmap) {
        metadataBuilder.apply {
            putBitmap(MediaMetadata.METADATA_KEY_ART, art)
        }

        render()
    }

    override fun onStateChange(state: PlaybackState) {
        TODO("Not yet implemented")
    }

    private fun render() {
        mediaSession.setMetadata(metadataBuilder.build())

        notificationManager.notify(notificationID, notification)
    }

}