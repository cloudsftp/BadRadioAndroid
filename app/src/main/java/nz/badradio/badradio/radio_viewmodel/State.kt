package nz.badradio.badradio.radio_viewmodel

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
    var displayPause: Boolean,
    var enableButtons: Boolean,
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
    fun onStop()            // Experiment with inputs
    fun onResynchronize()
}
