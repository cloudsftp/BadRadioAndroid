package nz.badradio.badradio.view.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import nz.badradio.badradio.viewmodel.RadioVM

class MediaNotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) {
            return
        }

        when (val actionId = intent.getStringExtra("action")) {
            PLAY_ACTION,
            PAUSE_ACTION    -> RadioVM.onPlayPause()
            IS_LIVE_ACTION  -> {}
            GO_LIVE_ACTION  -> RadioVM.onGoLive()
            else            -> Log.w(tag, "Unknown action $actionId")
        }
    }

    private val tag = "MediaNotificationBroadcastReceiver"
}