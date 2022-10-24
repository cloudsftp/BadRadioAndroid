package com.badradio.badradio.metadata.art

import com.badradio.badradio.metadata.SongMetadata
import org.junit.Test

class AlbumArtGetterTests {

    private fun runSongMatchesMetadata(songTitle: String, metadataTitle: String): Boolean {
        val song = SoundcloudSong(
            0, "track", "some_link", songTitle,
            SoundcloudUser("username")
        )
        val metadata = SongMetadata(metadataTitle, "username")
        return songMatchesMetadata(song, metadata)
    }

    @Test
    fun testSongMatchesMetadata() {
        assert(runSongMatchesMetadata("testtitle", "testtitle"))
        assert(!runSongMatchesMetadata("wrongtitle", "righttitle"))
        assert(runSongMatchesMetadata("thissong w/ collaborator", "thissong"))
        assert(runSongMatchesMetadata("thissong", "thissong w/ collaborator"))
        assert(runSongMatchesMetadata("thissong", "thissong (feat. collaborator)"))
    }
    
}