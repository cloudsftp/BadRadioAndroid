package com.badradio.nz.player

import com.badradio.nz.R
import android.graphics.Bitmap
import androidx.core.app.NotificationManagerCompat
import android.graphics.BitmapFactory
import android.app.NotificationManager
import android.os.Build
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.*
import android.content.res.Resources
import androidx.core.app.NotificationCompat
import com.badradio.nz.activity.MainActivity
import com.badradio.nz.utilities.ListenersManager

class MediaNotificationManager(private val service: RadioService) {
    private var meta: Metadata? = null
    private val strAppName: String
    private val strLiveBroadcast: String
    private var notifyIcon: Bitmap? = null
    private var playbackStatus: PlaybackStatus? = null
    private val resources: Resources
    private val notificationManager: NotificationManagerCompat
    fun startNotify(playbackStatus: PlaybackStatus?) {
        this.playbackStatus = playbackStatus
        notifyIcon = BitmapFactory.decodeResource(resources, R.drawable.badradio)
        startNotify()
    }

    fun startNotify(notifyIcon: Bitmap?, meta: Metadata?) {
        this.notifyIcon = notifyIcon
        this.meta = meta
        startNotify()
    }

    private fun startNotify() {
        if (playbackStatus == null) return
        if (notifyIcon == null) notifyIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_adjust_black_24dp)
        val notificationManager = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* Create or update. */
            val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    service.getString(R.string.audio_notification),
                    NotificationManager.IMPORTANCE_LOW)
            channel.enableVibration(false)
            channel.setSound(null, null)
            notificationManager.createNotificationChannel(channel)
        }
        var icon = R.drawable.ic_pause_black_24dp
        val playbackAction = Intent(service, RadioService::class.java)
        playbackAction.action = RadioService.Companion.ACTION_PAUSE
        var action = PendingIntent.getService(service, 1, playbackAction, PendingIntent.FLAG_MUTABLE)
        if (playbackStatus == PlaybackStatus.PAUSED) {
            icon = R.drawable.ic_play_arrow_black_24dp
            playbackAction.action = RadioService.Companion.ACTION_PLAY
            action = PendingIntent.getService(service, 2, playbackAction, PendingIntent.FLAG_IMMUTABLE)
        }
        val stopIntent = Intent(service, RadioService::class.java)
        stopIntent.action = RadioService.Companion.ACTION_STOP
        val stopAction = PendingIntent.getService(service, 3, stopIntent, PendingIntent.FLAG_IMMUTABLE)
        val intent = Intent(service, MainActivity::class.java)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pendingIntent = PendingIntent.getActivity(service, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); TODO: remove commented out code?
        // PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, 0);
        //PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManagerCompat.from(service).cancel(NOTIFICATION_ID)
        val builder = NotificationCompat.Builder(service, NOTIFICATION_CHANNEL_ID)
        // val title = if (meta != null && meta!!.artist != null) meta!!.artist else strLiveBroadcast
        // val subTitle = if (meta != null && meta!!.song != null) meta!!.song else strAppName
        builder.setContentTitle("bla")
                .setContentText("blub")
                .setLargeIcon(notifyIcon)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_radio_black_24dp)
                .addAction(icon, "pause", action)
                .addAction(R.drawable.ic_stop_black_24dp, "stop", stopAction)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(longArrayOf(0L))
                .setWhen(System.currentTimeMillis())
        /* .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()   TODO: set style
                        .setMediaSession(service.getMediaSession().getSessionToken())
                        .setShowActionsInCompactView(0, 1)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopAction)); */
        val notification = builder.build()
        service.startForeground(NOTIFICATION_ID, notification)
    }

    fun cancelNotify() {
        service.stopForeground(true)
        val status = PlaybackStatus.STOPPED
        ListenersManager.onEvent(status)
    }

    companion object {
        const val NOTIFICATION_ID = 555
        const val NOTIFICATION_CHANNEL_ID = "single_radio_channel"
    }

    init {
        resources = service.resources
        strAppName = resources.getString(R.string.app_name)
        strLiveBroadcast = resources.getString(R.string.notification_playing)
        notificationManager = NotificationManagerCompat.from(service)
    }
}