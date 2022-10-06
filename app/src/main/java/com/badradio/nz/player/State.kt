package com.badradio.nz.player

import com.badradio.nz.metadata.SongMetadata

data class PlayerState(
    val playback: PlaybackState,
    val metadata: SongMetadata,
    val art: String,
)