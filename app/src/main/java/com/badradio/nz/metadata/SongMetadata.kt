package com.badradio.nz.metadata

import java.io.IOException

class SongMetadata(
    val title: String,
    val artist: String
) {
    constructor(): this("No Title", "Unknown")

    companion object {
        private val titleRegex = Regex("(.*) - (.*)")

        fun fromStationTrack(track: StationTrack): SongMetadata {
            val match = titleRegex.matchEntire(track.title)
                ?: throw IOException("Could not match title")

            val title = match.groupValues[2]
            val artist = match.groupValues[1].replace(" -", ",")

            return SongMetadata(title, artist)
        }
    }

    override fun equals(other: Any?) = other is SongMetadata
                && this.hashCode() == other.hashCode()

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + artist.hashCode()
        return result
    }
}
