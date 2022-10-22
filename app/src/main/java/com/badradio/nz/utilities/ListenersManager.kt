package com.badradio.nz.utilities;

import android.graphics.Bitmap
import com.badradio.nz.player.PlayerState

interface MetadataObserver {
    fun onSongTitle(title: String, artist: String)
    fun onAlbumArt(art: Bitmap)
}

interface PlayerStateObserver {
    fun onStateChange(state: PlayerState)
}

interface UserInputObserver {
    fun onPlay()
    fun onPause()
    fun onStop()
}
