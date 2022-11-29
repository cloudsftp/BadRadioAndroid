package nz.badradio.badradio.model.metadata.art

import nz.badradio.badradio.model.metadata.SongMetadata
import org.junit.Test

class SoundcloudCrawlerTests {

    @Test
    fun testGetImageUrl() {
        var imageUrl = ""
        SoundcloudCrawler.search(
            object : IStreamingServiceDataObserver {
                override fun notifyOfAlbumArtUrl(url: String) { imageUrl = url }
                override fun notifyOfSoundcloudUrl(url: String) { }
                override fun notifyOfBandcampUrl(url: String) { }
            },
            SongMetadata("come and see", "cassyb, north posse"),
        )

        assert(imageUrl == "https://i1.sndcdn.com/artworks-tOmHVP9GnI66ky4d-8ZWV8w-t500x500.jpg") {
            println("was $imageUrl")
        }
    }

    @Test
    fun testGetSongUrl() {
        val songUrl = SoundcloudCrawler.getSongUrl(
            object : IStreamingServiceDataObserver {
                override fun notifyOfAlbumArtUrl(url: String) { }
                override fun notifyOfSoundcloudUrl(url: String) { }
                override fun notifyOfBandcampUrl(url: String) { }
            },
            SongMetadata("come and see", "cassyb, north posse"),
        )
        assert(songUrl == "https://soundcloud.com/xxcassyb/come-and-see") {
            println("was $songUrl")
        }
    }

}