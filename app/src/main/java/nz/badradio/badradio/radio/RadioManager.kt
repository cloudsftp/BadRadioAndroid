package nz.badradio.badradio.radio

import android.os.IBinder
import android.content.*
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import nz.badradio.badradio.notification.MediaNotificationManager
import nz.badradio.badradio.radio_viewmodel.RadioVM
import nz.badradio.badradio.radio_viewmodel.UserInputVMObserver

object RadioManager: UserInputVMObserver {
    private var service: RadioService? = null
    private var mediaNotificationManager: MediaNotificationManager? = null

    fun initialize(context: Context, mediaSession: MediaSessionCompat) {
        if (service != null) {
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

        executeWhenServiceBound {
            mediaNotificationManager = MediaNotificationManager(service!!, mediaSession)
            RadioVM.addObserver(mediaNotificationManager!!)
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
            service = (binder as RadioService.RadioServiceBinder).service
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            RadioVM.removeObserver(mediaNotificationManager!!)
            mediaNotificationManager = null
            service = null
        }
    }

    override fun onPlay() = executeWhenServiceBound {
        service!!.onPlay()
    }

    override fun onPause() = executeWhenServiceBound {
        service!!.onPause()
    }

    override fun onStop() = executeWhenServiceBound {
        service!!.onStop()
    }

    override fun onResynchronize() {
        TODO("Not yet implemented")
    }

    private fun executeWhenServiceBound(r: Runnable) {
        if (service != null) {
            r.run()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                executeWhenServiceBound(r)
            }, 100)
        }
    }

}