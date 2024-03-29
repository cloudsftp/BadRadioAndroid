package nz.badradio.badradio.model.radio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import nz.badradio.badradio.utilities.generateFunExecuteIf
import nz.badradio.badradio.utilities.generateFunExecuteWhen
import nz.badradio.badradio.view.notification.MediaNotificationManager
import nz.badradio.badradio.viewmodel.RadioVM
import nz.badradio.badradio.viewmodel.UserInputVMObserver
import java.util.concurrent.atomic.AtomicBoolean

object RadioManager: UserInputVMObserver {
    private var service: RadioService? = null
    private var mediaNotificationManager: MediaNotificationManager? = null

    private var initializing = AtomicBoolean(false)

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
            service = (binder as RadioService.RadioServiceBinder).service
            initializing.set(false)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            RadioVM.removeObserver(mediaNotificationManager!!)
            mediaNotificationManager = null
            service = null

            initializing.set(false) // needed?
        }
    }

    // Service Controls

    fun stopService() = runIfServiceBound {
        service!!.stopSelf()
    }

    fun startService(context: Context, mediaSession: MediaSessionCompat): Boolean {
        if (
            service != null
        || !initializing.compareAndSet(false, true)
        ) {
            return false
        }

        val intent = Intent(context, RadioService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // context.startForegroundService(intent) TODO: revert (maybe not?)
            context.startService(intent)
        } else {
            context.startService(intent)
        }

        context.bindService(intent, serviceConnection, 0)

        runWhenServiceBound {
            mediaNotificationManager = MediaNotificationManager(service!!, mediaSession)
            RadioVM.addObserver(mediaNotificationManager!!)
        }

        return true
    }

    // Music Controls

    override fun onPlay() = runWhenServiceBound {
        service!!.onPlay()
    }

    override fun onPause() = runWhenServiceBound {
        service!!.onPause()
    }

    override fun onGoLive() = runWhenServiceBound {
        service!!.onGoLive()
    }

    // Helpers

    private val runIfServiceBound = generateFunExecuteIf { service != null }
    private val runWhenServiceBound = generateFunExecuteWhen { service != null }
    private val runWhenServiceUnbound = generateFunExecuteWhen { service == null }
}