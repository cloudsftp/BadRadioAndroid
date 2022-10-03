package com.badradio.nz.notification

import android.app.Notification
import android.content.Context
import android.media.session.MediaSession
import android.util.Log

object MediaNotificationManager {

    fun startNotify(context: Context, mediaSession: MediaSession) {
        val mediaStyle = Notification.MediaStyle().setMediaSession(mediaSession.sessionToken)
        val notification = Notification.Builder(context, "BADRADIO")
            .setStyle(mediaStyle)
            .build()

        Log.d("MediaNotificationManager", "Started to notify")

        // TODO: implement https://developer.android.com/guide/topics/media/media-controls#mediabrowserservice_implementation
    }

}