package com.badradio.nz.utilities;

import android.graphics.Bitmap
import com.badradio.nz.player.PlayerState

/*
object ListenersManager {
    private var listeners: ArrayList<EventListener> = ArrayList()

    fun registerAsListener(listener: EventListener) {
        listeners.add(listener)
    }

    fun unregisterAsListener(listener: EventListener) {
        listeners.remove(listener)
    }

    fun onEvent(status: PlaybackStatus) {
        listeners.forEach { it.onEvent(status) }
    }

    fun onSongTitle(title: String, artist: String) {
        listeners.forEach { it.onSongTitle(title, artist) }
    }

    fun onAlbumArt(art: Bitmap) {
        listeners.forEach { it.onAlbumArt(art) }
    }
    */

interface UserInputObserver {
    fun onPlay()
    fun onPause()
    fun onForward()
}

interface MetadataObserver {
    fun onSongTitle(title: String, artist: String)
    fun onAlbumArt(art: Bitmap)
}

interface PlayerStateObserver : MetadataObserver {
    fun onStateChange(state: PlayerState)
}
