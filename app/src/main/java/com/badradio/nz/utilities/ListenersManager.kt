package com.badradio.nz.utilities;

import android.graphics.Bitmap

object ListenersManager {
    private var listeners: ArrayList<EventListener> = ArrayList()

    fun registerAsListener(listener: EventListener) {
        listeners.add(listener)
    }

    fun unregisterAsListener(listener: EventListener) {
        listeners.remove(listener)
    }

    fun onEvent(status: String) {
        listeners.forEach { it.onEvent(status) }
    }

    fun onSongTitle(title: String, artist: String) {
        listeners.forEach { it.onSongTitle(title, artist) }
    }

    fun onAlbumArt(art: Bitmap) {
        listeners.forEach { it.onAlbumArt(art) }
    }

    interface EventListener {
        fun onEvent(status: String)
        fun onSongTitle(title: String, artist: String)
        fun onAlbumArt(art: Bitmap)
    }
}
