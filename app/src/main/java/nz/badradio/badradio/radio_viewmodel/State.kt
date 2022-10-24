package nz.badradio.badradio.radio_viewmodel


/**
 * Communication Views <-> View Model
 *
 * Views:   - PlayerActivity (I/O)
 *          - NotificationManager (O)
 *          - MediaNotificationBroadcastReceiver (I)
 */

interface UserInputObserver {
    fun onPlayPause()
    fun onStop()
}

interface RadioVMObserver {
    fun onStateChange(state: RadioVMState)
}

data class RadioVMState(
    var displayPause: Boolean,
    var enableButtons: Boolean,

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

// Other Way: Player.Listener

