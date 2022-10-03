package com.badradio.nz.player

import android.app.Service.STOP_FOREGROUND_REMOVE
import android.os.IBinder
import com.badradio.nz.player.RadioService.RadioServiceBinder
import android.content.*
import com.badradio.nz.utilities.ListenersManager

class RadioManager private constructor() {
    private var serviceBound = false
    fun playOrPause() {
        service!!.playOrPause()
    }

    fun stopServices() {
        service!!.stopForeground(STOP_FOREGROUND_REMOVE)
        service!!.stop()
    }

    fun bind(context: Context) {
        //Perhaps also catch a LeakedServiceConnection, and if caught: then call unbind and then try to bind again
        if (!serviceBound) {
            val intent = Intent(context, RadioService::class.java)
            context.startService(intent)
            if (service != null) ListenersManager.onEvent(service!!.status!!)
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