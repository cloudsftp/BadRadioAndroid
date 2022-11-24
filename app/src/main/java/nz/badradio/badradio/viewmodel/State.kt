package nz.badradio.badradio.viewmodel

import android.graphics.Bitmap

/**
 * Communication Views <-> View Model
 *
 * Views:   - PlayerActivity (I/O)
 *          - NotificationManager (O)
 *          - MediaNotificationBroadcastReceiver (I)
 */

interface RadioVMObserver {
    fun onStateChange(state: RadioVMState)
}

data class RadioVMState(
    var displayButtonsNotification: Boolean,
    var displayPause: Boolean,
    var enablePlayPauseButton: Boolean,
    var displayLive: Boolean,
    var actualTitle: String,
    var title: String,
    var artist: String,
    var art: Bitmap,
    var notificationArt: Bitmap,
)

/**
 * Communication View Model <-> Models
 *
 * Model:     RadioService
 */

interface UserInputVMObserver {
    fun onPlay()
    fun onPause()
    fun onGoLive()            // Experiment with inputs
}
