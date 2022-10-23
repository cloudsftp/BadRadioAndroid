package com.badradio.nz.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.badradio.nz.player.RadioManager

class MediaNotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) {
            return
        }

        when (val actionId = intent.getStringExtra("action")) {
            PLAY_ACTION -> RadioManager.onPlayPause()
            PAUSE_ACTION -> RadioManager.onPlayPause()
            STOP_ACTION -> RadioManager.onStop()
            else -> Log.w(tag, "Unknown action $actionId")
        }
    }

    private val tag = "MediaNotificationBroadcastReceiver"
}