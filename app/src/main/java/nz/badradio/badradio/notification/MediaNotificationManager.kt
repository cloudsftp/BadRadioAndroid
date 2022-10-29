package nz.badradio.badradio.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import nz.badradio.badradio.R
import nz.badradio.badradio.activity.PlayerActivity
import nz.badradio.badradio.radio.RadioService
import nz.badradio.badradio.radio_viewmodel.RadioVMObserver
import nz.badradio.badradio.radio_viewmodel.RadioVMState

@SuppressLint("ObsoleteSdkInt")
class MediaNotificationManager(
    private val service: RadioService,
    mediaSession: MediaSessionCompat,
) : RadioVMObserver {

    private val activityRequestCode   = 0
    private val playRequestCode       = 1
    private val pauseRequestCode      = 2
    private val stopRequestCode       = 3

    private val channelID = "BADRADIO Notification Channel"
    private var notificationId = 1

    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(service)

    private val playAction = createAction(service, PLAY_ACTION, playRequestCode, R.drawable.vec_play, "Play")
    private val pauseAction = createAction(service, PAUSE_ACTION, pauseRequestCode, R.drawable.vec_pause, "Pause")
    private val stopAction = createAction(service, STOP_ACTION, stopRequestCode, R.drawable.vec_stop, "Stop")

    private val mediaStyle = MediaStyle()
        .setMediaSession(mediaSession.sessionToken)

    private val notificationBuilder = NotificationCompat.Builder(service, channelID).apply {
        setSilent(true)
        setStyle(mediaStyle)
        setSmallIcon(R.drawable.vec_radio)

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

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManagerCompat.IMPORTANCE_HIGH
            val channel = NotificationChannelCompat
                .Builder(channelID, importance)
                .setName(channelID)
                .build()
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStateChange(state: RadioVMState) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            notificationBuilder.apply {
                setContentTitle(state.title)
                setContentText(state.artist)
                setLargeIcon(state.notificationArt)
            }
        }

        notificationBuilder.apply {
            clearActions()

            addAction(
                if (state.displayPause) {
                    pauseAction
                } else {
                    playAction
                }
            )

            addAction(stopAction)
        }

        mediaStyle.setShowActionsInCompactView(0)

        val notification = notificationBuilder.build()

        notificationManager.notify(notificationId, notification)
        service.startForeground(notificationId, notification)
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