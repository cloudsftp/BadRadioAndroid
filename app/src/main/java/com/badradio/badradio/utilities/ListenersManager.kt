package com.badradio.badradio.utilities;

import android.graphics.Bitmap
import com.badradio.badradio.player.PlayerState

interface MetadataObserver {
    fun onSongTitle(title: String, artist: String)
    fun onAlbumArt(art: Bitmap)
}

interface PlayerStateObserver {
    fun onStateChange(state: PlayerState)
}

interface UserInputObserver {
    fun onPlayPause()
    fun onStop()
}
