package nz.badradio.badradio.metadata.art

import nz.badradio.badradio.metadata.SongMetadata
import org.junit.Test

class StreamingServiceCrawlerTests {

    private fun runSongMatchesMetadata(songTitle: String, metadataTitle: String): Boolean {
        val metadata = SongMetadata(metadataTitle, "username")
        return songTitleMatches(songTitle, metadata)
    }

    @Test
    fun testSongMatchesMetadata() {
        assert(runSongMatchesMetadata("testtitle", "testtitle"))
        assert(!runSongMatchesMetadata("wrongtitle", "righttitle"))
        assert(runSongMatchesMetadata("thissong w/ collaborator", "thissong"))
        assert(runSongMatchesMetadata("thissong", "thissong w/ collaborator"))
        assert(runSongMatchesMetadata("thissong", "thissong (feat. collaborator)"))
        assert(runSongMatchesMetadata("thissong", "thissong feat. collaborator"))
    }

}