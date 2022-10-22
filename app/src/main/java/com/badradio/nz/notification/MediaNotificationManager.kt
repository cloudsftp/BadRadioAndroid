package com.badradio.nz.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
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
import com.badradio.nz.player.PlaybackStatus
import com.badradio.nz.player.PlayerState
import com.badradio.nz.player.RadioService
import com.badradio.nz.utilities.PlayerStateObserver

@SuppressLint("ObsoleteSdkInt")
class MediaNotificationManager(private val context: RadioService) : PlayerStateObserver {
    private val activityRequestCode   = 0
    private val playRequestCode       = 1
    private val pauseRequestCode      = 2
    private val stopRequestCode       = 3

    private val channelID = "BADRADIO Notification Channel"
    private val notificationID = 1

    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

    private val playAction = createAction(context, PLAY_ACTION, playRequestCode, R.drawable.vec_play, "Play")
    private val pauseAction = createAction(context, PAUSE_ACTION, pauseRequestCode, R.drawable.vec_pause, "Pause")
    private val stopAction = createAction(context, STOP_ACTION, stopRequestCode, R.drawable.vec_stop, "Stop")

    private val metadataBuilder = MediaMetadataCompat.Builder()
    private val mediaSession = MediaSessionCompat(context, "BADRADIO Media Session")

    private val mediaStyle = MediaStyle()
        .setMediaSession(mediaSession.sessionToken)

    private val notificationBuilder = NotificationCompat.Builder(context, channelID).apply {
        setSilent(true)
        setStyle(mediaStyle)
        setSmallIcon(R.drawable.ic_radio_black_24dp)

        val sessionIntent = Intent(context, PlayerActivity::class.java)
        val pendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(sessionIntent)
            getPendingIntent(
                activityRequestCode,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        setContentIntent(pendingIntent)
    }

    private val defaultAlbumArtRes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        R.drawable.badradio_background
    } else {
        R.drawable.badradio
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
            val channel = NotificationChannelCompat
                .Builder(channelID, importance)
                .setName(channelID)
                .build()
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStateChange(state: PlayerState) {
        val artToDisplay = state.art
            ?: BitmapFactory.decodeResource(context.resources, defaultAlbumArtRes)

        metadataBuilder.apply {
            putString(MediaMetadata.METADATA_KEY_TITLE, state.metadata.title)
            putString(MediaMetadata.METADATA_KEY_ARTIST, state.metadata.artist)
            putBitmap(MediaMetadata.METADATA_KEY_ART, artToDisplay)
        }

        mediaSession.setMetadata(metadataBuilder.build())

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            notificationBuilder.apply {
                setLargeIcon(artToDisplay)
                setContentTitle(state.metadata.title)
                setContentText(state.metadata.artist)
            }
        }

        notificationBuilder.apply {
            clearActions()

            addAction(stopAction)

            if (state.playbackStatus == PlaybackStatus.PLAYING) {
                addAction(pauseAction)
            } else {
                addAction(playAction)
            }
        }

        mediaStyle.setShowActionsInCompactView(0)

        val notification = notificationBuilder.build()

        notificationManager.notify(notificationID, notification)
        context.startForeground(notificationID, notification)
    }

    private fun createAction(context: Context, actionId: String, requestCode: Int, iconId: Int, title: String): NotificationCompat.Action {
        val intent = Intent(context, MediaNotificationBroadcastReceiver::class.java).apply {
            putExtra("action", actionId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Action(
            iconId, title, pendingIntent
        )
    }
}