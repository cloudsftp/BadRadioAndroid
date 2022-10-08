package com.badradio.nz.player

import android.graphics.Bitmap
import com.badradio.nz.metadata.SongMetadata

data class PlayerState(
    var playing: Boolean,
    var metadata: SongMetadata,
    var art: Bitmap,
)
