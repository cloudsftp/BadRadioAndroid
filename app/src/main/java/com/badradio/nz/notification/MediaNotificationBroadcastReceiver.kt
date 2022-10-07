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
            PLAY_ACTION -> RadioManager.onPlay()
            PAUSE_ACTION -> RadioManager.onPause()
            else -> Log.w(TAG, "Unknown action $actionId")
        }
    }

    private val TAG = "MediaNotificationBroadcastReceiver"
}