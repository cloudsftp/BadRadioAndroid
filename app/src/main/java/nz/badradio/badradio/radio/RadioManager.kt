package nz.badradio.badradio.radio

import android.os.IBinder
import android.content.*
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import nz.badradio.badradio.metadata.art.soundcloudSearchResultAdapter
import nz.badradio.badradio.notification.MediaNotificationManager
import nz.badradio.badradio.radio_viewmodel.RadioVM
import nz.badradio.badradio.radio_viewmodel.UserInputVMObserver
import java.util.concurrent.atomic.AtomicBoolean

object RadioManager: UserInputVMObserver {
    private var service: RadioService? = null
    private var mediaNotificationManager: MediaNotificationManager? = null

    private var initializing = AtomicBoolean(false)

    fun initialize(context: Context, mediaSession: MediaSessionCompat) {
        startService(context, mediaSession)
    }

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

    fun restartService(context: Context, mediaSession: MediaSessionCompat) {
        stopService()
        runWhenServiceUnbound {
            startService(context, mediaSession)
        }
    }

    fun stopService() = runIfServiceBound {
        service!!.stopSelf()
    }

    private fun startService(context: Context, mediaSession: MediaSessionCompat) {
        if (
            service != null
        || !initializing.compareAndSet(false, true)
        ) {
            return
        }

        val intent = Intent(context, RadioService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // context.startForegroundService(intent) TODO: revert
            context.startService(intent)
        } else {
            context.startService(intent)
        }

        context.bindService(intent, serviceConnection, 0)

        runWhenServiceBound {
            mediaNotificationManager = MediaNotificationManager(service!!, mediaSession)
            RadioVM.addObserver(mediaNotificationManager!!)
        }
    }

    // Music Controls

    override fun onPlay() = runWhenServiceBound {
        service!!.onPlay()
    }

    override fun onPause() = runWhenServiceBound {
        service!!.onPause()
    }

    /*
    override fun onStop() = executeWhenServiceBound {
        service!!.onStop()
    }
     */

    override fun onSkip() = runWhenServiceBound {
        service!!.onSkip()
    }

    // Helpers

    private fun runIfServiceBound(r: Runnable) {
        if (service != null) {
            r.run()
        }
    }

    private fun runWhenServiceBound(r: Runnable) {
        if (service != null) {
            r.run()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                runWhenServiceBound(r)
            }, 100)
        }
    }

    private fun runWhenServiceUnbound(r: Runnable) {
        if (service == null) {
            r.run()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                runWhenServiceUnbound(r)
            }, 100)
        }
    }
}