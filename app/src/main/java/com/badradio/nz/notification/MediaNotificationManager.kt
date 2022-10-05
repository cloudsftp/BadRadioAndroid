package com.badradio.nz.notification

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.badradio.nz.R

class MediaNotificationManager(private val context: Context) {
    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

    private val channelID = "BADRADIONotificationChannel"

    @SuppressLint("ObsoleteSdkInt")
    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
            val channel = NotificationChannelCompat
                .Builder(channelID, importance)
                .setName("BADRADIO notification channel")
                .build()
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun startNotify(mediaSession: MediaSessionCompat) {

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken)
        val notification = NotificationCompat.Builder(context, channelID)
            .setStyle(mediaStyle)
            .setSmallIcon(R.drawable.ic_radio_black_24dp)
            .build()

        notificationManager.notify(0, notification)

        Log.d("MediaNotificationManager", "Started to notify")
    }

}