package com.badradio.nz.player

import com.badradio.nz.utilities.Log.printStackTrace
import android.os.IBinder
import com.badradio.nz.player.RadioService.RadioServiceBinder
import android.content.*
import com.badradio.nz.utilities.ListenersManager
import java.lang.IllegalArgumentException

class RadioManager private constructor() {
    private var serviceBound = false
    fun playOrPause(streamUrl: String?) {
        if (streamUrl == null) service!!.stop() else service!!.playOrPause(streamUrl)
    }

    fun stopServices() {
        service!!.stopForeground(true)
        service!!.stop()
    }

    fun bind(context: Context) {
        //Perhaps also catch a LeakedServiceConnection, and if caught: then call unbind and then try to bind again
        if (!serviceBound) {
            val intent = Intent(context, RadioService::class.java)
            context.startService(intent)
            val bound = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            if (service != null) ListenersManager.onEvent(service!!.status!!)
        }
    }

    fun unbind(context: Context) {
        if (serviceBound) {
            try {
                service!!.stop()
                context.unbindService(serviceConnection)
                context.stopService(Intent(context, RadioService::class.java))
                serviceBound = false
            } catch (e: IllegalArgumentException) {
                printStackTrace(e)
            }
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(arg0: ComponentName, binder: IBinder) {
            service = (binder as RadioServiceBinder).service
            serviceBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            serviceBound = false
        }
    }

    companion object {
        private var instance: RadioManager? = null
        var service: RadioService? = null
            private set

        fun with(): RadioManager? {
            if (instance == null) instance = RadioManager()
            return instance
        }
    }
}