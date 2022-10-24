package nz.badradio.badradio.player

import android.graphics.Bitmap
import nz.badradio.badradio.metadata.SongMetadata

data class PlayerState(
    var playbackStatus: PlaybackStatus,
    var metadata: SongMetadata,
    var art: Bitmap?,
)

enum class PlaybackStatus {
    LOADING,
    NOT_PLAYING,
    PLAYING
}
