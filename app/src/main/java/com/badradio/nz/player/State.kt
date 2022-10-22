package com.badradio.nz.player

import android.graphics.Bitmap
import com.badradio.nz.metadata.SongMetadata

data class PlayerState(
    var playbackStatus: PlaybackStatus,
    var metadata: SongMetadata,
    var art: Bitmap?,
)

enum class PlaybackStatus {
    STOPPED,
    PAUSED,
    PLAYING
}
