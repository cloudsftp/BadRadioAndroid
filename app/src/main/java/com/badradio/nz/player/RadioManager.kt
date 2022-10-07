package com.badradio.nz.player

import android.app.Service.BIND_AUTO_CREATE
import android.app.Service.STOP_FOREGROUND_REMOVE
import android.os.IBinder
import android.content.*

object RadioManager {
    private lateinit var service: RadioService
    private var serviceBound = false

    fun bind(context: Context) {
        if (!serviceBound) {
            val intent = Intent(context, RadioService::class.java)
            context.bindService(intent, serviceConnection, BIND_AUTO_CREATE)
            context.startService(intent)
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
            service = (binder as RadioService.RadioServiceBinder).service
            serviceBound = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            serviceBound = false
        }
    }

    fun playOrPause() {
    }

    fun stopServices() {
        service.stopForeground(STOP_FOREGROUND_REMOVE)
    }
}