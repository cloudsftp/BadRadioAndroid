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

        assert(imageUrl == "https://is3-ssl.mzstatic.com/image/thumb/Music126/v4/f4/ca/00/f4ca00c9-5cda-d48d-8eaf-3da176d68822/artwork.jpg/500x500bb.jpg") {
            println("was $imageUrl")
        }
    }
}