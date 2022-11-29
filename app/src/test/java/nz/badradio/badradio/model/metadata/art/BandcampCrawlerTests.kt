package nz.badradio.badradio.model.metadata.art

import nz.badradio.badradio.model.metadata.SongMetadata
import org.junit.Test

class BandcampCrawlerTests {

    @Test
    fun testGetSongUrl() {
        val songUrl = BandcampCrawler.getSongUrl(
            object : IStreamingServiceDataObserver {
                override fun notifyOfAlbumArtUrl(url: String) { }
                override fun notifyOfSoundcloudUrl(url: String) { }
                override fun notifyOfBandcampUrl(url: String) { }
            },
            SongMetadata("come and see", "cassyb, north posse")
        )
        assert(songUrl == "https://cassyb.bandcamp.com/track/come-and-see-w-north-posse") {
            println("was $songUrl")
        }
    }

}