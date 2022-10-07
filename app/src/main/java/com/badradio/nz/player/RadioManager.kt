package com.badradio.nz.player

import android.app.Service.BIND_AUTO_CREATE
import android.os.IBinder
import android.content.*
import com.badradio.nz.utilities.PlayerStateObserver
import com.badradio.nz.utilities.UserInputObserver

object RadioManager : UserInputObserver {
    private lateinit var service: RadioService
    var ready = false

    fun bind(context: Context) {
        if (!ready) {
            val intent = Intent(context, RadioService::class.java)
            context.bindService(intent, serviceConnection, BIND_AUTO_CREATE)
            context.startService(intent)
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
            service = (binder as RadioService.RadioServiceBinder).service
            ready = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            ready = false
        }
    }

    override fun onPlay() = service.onPlay()
    override fun onPause() = service.onPause()

    fun registerPlayerStateObserver(observer: PlayerStateObserver)
        = service.registerPlayerStateObserver(observer)

}