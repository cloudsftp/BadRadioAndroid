package com.badradio.nz.player

import android.graphics.Bitmap
import com.badradio.nz.metadata.SongMetadata

data class PlayerState(
    val playback: PlaybackState,
    val metadata: SongMetadata,
    var art: Bitmap,
)