package nz.badradio.badradio.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import nz.badradio.badradio.radio_viewmodel.RadioVM

class MediaNotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) {
            return
        }

        when (val actionId = intent.getStringExtra("action")) {
            PLAY_ACTION -> RadioVM.onPlayPause()
            PAUSE_ACTION -> RadioVM.onPlayPause()
            // STOP_ACTION -> RadioVM.onStop()
            else -> Log.w(tag, "Unknown action $actionId")
        }
    }

    private val tag = "MediaNotificationBroadcastReceiver"
}