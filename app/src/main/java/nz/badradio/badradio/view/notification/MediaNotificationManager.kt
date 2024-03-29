package nz.badradio.badradio.view.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import nz.badradio.badradio.R
import nz.badradio.badradio.model.radio.RadioService
import nz.badradio.badradio.view.PlayerActivity
import nz.badradio.badradio.viewmodel.RadioVMObserver
import nz.badradio.badradio.viewmodel.RadioVMState

@SuppressLint("ObsoleteSdkInt")
class MediaNotificationManager(
    private val service: RadioService,
    mediaSession: MediaSessionCompat,
) : RadioVMObserver {

    private val activityRequestCode   = 0
    private val playRequestCode       = 1
    private val pauseRequestCode      = 2
    private val isLiveRequestCode     = 4
    private val goLiveRequestCode     = 4

    private val channelID = "BADRADIO Notification Channel"
    private var notificationId = 1

    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(service)

    private val playAction  = createAction(service, PLAY_ACTION, playRequestCode, R.drawable.ic_play, "Play")
    private val pauseAction = createAction(service, PAUSE_ACTION, pauseRequestCode, R.drawable.ic_pause, "Pause")
    private val isLiveAction  = createAction(service, IS_LIVE_ACTION, isLiveRequestCode, R.drawable.ic_radio_button_checked, "Is Live")
    private val goLiveAction  = createAction(service, GO_LIVE_ACTION, goLiveRequestCode, R.drawable.ic_radio_button_unchecked, "Go Live")

    private val mediaStyle = MediaStyle()
        .setMediaSession(mediaSession.sessionToken)

    private val notificationBuilder = NotificationCompat.Builder(service, channelID).apply {
        setSilent(true)
        setStyle(mediaStyle)
        setSmallIcon(R.drawable.ic_radio)

        val sessionIntent = Intent(service, PlayerActivity::class.java)
        val pendingIntent: PendingIntent = TaskStackBuilder.create(service).run {
            addNextIntentWithParentStack(sessionIntent)
            getPendingIntent(
                activityRequestCode,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        setContentIntent(pendingIntent)
    }

    init { initialize() }
    private fun initialize() { /* hack needed for early return */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val importance = NotificationManagerCompat.IMPORTANCE_HIGH
        val channel = NotificationChannelCompat
            .Builder(channelID, importance)
            .setName(channelID)
            .build()
        notificationManager.createNotificationChannel(channel)
    }

    override fun onStateChange(state: RadioVMState) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            notificationBuilder.apply {
                setContentTitle(state.actualTitle)
                setContentText(state.artist)
                setLargeIcon(state.notificationArt)
            }
        }

        notificationBuilder.clearActions()

        if (state.displayButtonsNotification) {
            addActions(state)
            mediaStyle.setShowActionsInCompactView(0, 1)
        } else {
            mediaStyle.setShowActionsInCompactView()
        }

        val notification = notificationBuilder.build()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            service.startForeground(notificationId, notification)
        } else {
            service.startForeground(
                notificationId, notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        }
    }

    private fun addActions(state: RadioVMState) {
        notificationBuilder.addAction(
            if (state.displayPause) {
                pauseAction
            } else {
                playAction
            }
        )

        notificationBuilder.addAction(
            if (state.displayLive) {
                isLiveAction
            } else {
                goLiveAction
            }
        )
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