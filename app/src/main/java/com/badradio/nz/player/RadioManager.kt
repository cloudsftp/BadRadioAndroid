package com.badradio.nz.player

import android.app.Service.BIND_AUTO_CREATE
import android.app.Service.STOP_FOREGROUND_REMOVE
import android.os.IBinder
import com.badradio.nz.player.RadioService.RadioServiceBinder
import android.content.*
import com.badradio.nz.utilities.ListenersManager

object RadioManager {
    lateinit var service: RadioService
    private var serviceBound = false

    fun bind(context: Context) {
        if (!serviceBound) {
            val intent = Intent(context, RadioService::class.java)
            context.bindService(intent, serviceConnection, BIND_AUTO_CREATE)
            context.startService(intent)
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

    fun playOrPause() {
        service.playOrPause()
    }

    fun stopServices() {
        service.stopForeground(STOP_FOREGROUND_REMOVE)
        service.stop()
    }
}