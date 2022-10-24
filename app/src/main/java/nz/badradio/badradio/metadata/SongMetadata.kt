package nz.badradio.badradio.metadata

import java.io.IOException

class SongMetadata(
    var title: String,
    var artist: String
) {
    constructor(): this("No Title", "Unknown")
    val isBadradioTag = artist == "BADRADIO"

    companion object {
        private val titleRegex = Regex("(.*) - (.*)")

        fun fromStationTrack(track: StationTrack) = fromRawTitle(track.title)
        fun fromRawTitle(rawTitle: String): SongMetadata {
            val match = titleRegex.matchEntire(rawTitle)
                ?: throw IOException("Could not match title $rawTitle")

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

    override fun toString() = "SongMetadata(title=$title, artist=$artist)"
}
