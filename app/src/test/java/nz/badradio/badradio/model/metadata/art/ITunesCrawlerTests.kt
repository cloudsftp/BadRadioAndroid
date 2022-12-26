package nz.badradio.badradio.model.metadata.art

import nz.badradio.badradio.model.metadata.SongMetadata
import org.junit.Test

class ITunesCrawlerTests {

    @Test
    fun testGetImageUrl() {
        var imageUrl = ""
        ITunesCrawler.search(
            object : IStreamingServiceDataObserver {
                override fun notifyOfAlbumArtUrl(url: String) { imageUrl = url }
                override fun notifyOfSoundcloudUrl(url: String) { }
                override fun notifyOfBandcampUrl(url: String) { }
            },
            SongMetadata("come and see", "cassyb")
        )

        // Can't test against fixed URL, because it changes all the time
        assert(imageUrl.endsWith("/artwork.jpg/500x500bb.jpg")) {
            println("was $imageUrl")
        }
    }
}