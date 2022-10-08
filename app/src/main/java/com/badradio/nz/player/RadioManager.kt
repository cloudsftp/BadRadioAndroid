package com.badradio.nz.player

import android.app.Service.BIND_AUTO_CREATE
import android.os.IBinder
import android.content.*
import android.os.Handler
import android.os.Looper
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

    override fun onPlay() = executeWhenServiceBound {
        service.onPlay()
    }

    override fun onPause() = executeWhenServiceBound {
        service.onPause()
    }

    fun addObserver(observer: PlayerStateObserver) = executeWhenServiceBound {
        service.addObserver(observer)
    }

    fun removeObserver(observer: PlayerStateObserver) = executeWhenServiceBound {
        service.removeObserver(observer)
    }

    private fun executeWhenServiceBound(r: Runnable) {
        if (ready) {
            r.run()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                executeWhenServiceBound(r)
            }, 100)
        }
    }

}