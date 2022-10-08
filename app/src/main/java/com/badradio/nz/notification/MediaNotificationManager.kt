package com.badradio.nz.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
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
import com.badradio.nz.player.PlayerState
import com.badradio.nz.player.RadioService
import com.badradio.nz.utilities.PlayerStateObserver

@SuppressLint("ObsoleteSdkInt")
class MediaNotificationManager(context: RadioService) : PlayerStateObserver {
    private val activityRequestCode   = 0
    private val playRequestCode       = 1
    private val pauseRequestCode      = 2

    private val channelID = "BADRADIO Notification Channel"
    private val notificationID = 0

    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

    private val playAction = createAction(context, PLAY_ACTION, playRequestCode, R.drawable.icn_notification_play, "Play")
    private val pauseAction = createAction(context, PAUSE_ACTION, pauseRequestCode, R.drawable.icn_notification_pause, "Pause")

    private val metadataBuilder = MediaMetadataCompat.Builder()
    private val mediaSession = MediaSessionCompat(context, "BADRADIO Media Session")

    private val mediaStyle = MediaStyle()
        .setMediaSession(mediaSession.sessionToken)
        .setShowActionsInCompactView(0)

    private val notificationBuilder = NotificationCompat.Builder(context, channelID).apply {
        setStyle(mediaStyle)
        setSilent(true)
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

    private var lastPlaybackState = false

    override fun onStateChange(state: PlayerState) {
        metadataBuilder.apply {
            putString(MediaMetadata.METADATA_KEY_TITLE, state.metadata.title)
            putString(MediaMetadata.METADATA_KEY_ARTIST, state.metadata.artist)
            putBitmap(MediaMetadata.METADATA_KEY_ART, state.art)
        }

        mediaSession.setMetadata(metadataBuilder.build())

        if (lastPlaybackState != state.playing) {
            notificationBuilder.apply {
                clearActions()

                if (state.playing) {
                    addAction(pauseAction)
                } else {
                    addAction(playAction)
                }
            }

            lastPlaybackState = state.playing
        }

        notificationManager.notify(notificationID, notificationBuilder.build())
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