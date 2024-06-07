package nz.badradio.badradio.model.metadata.art.bandcamp

import nz.badradio.badradio.model.metadata.SongMetadata
import nz.badradio.badradio.model.metadata.art.BandcampCrawler
import nz.badradio.badradio.model.metadata.art.IStreamingServiceDataObserver
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException


class BandcampCrawlerTests {
    lateinit var mockWebServer: MockWebServer
    lateinit var baseUrl: HttpUrl
    @Before
    @Throws(IOException::class)
    fun setUp() {
        mockWebServer = MockWebServer()
        baseUrl = mockWebServer.url("/")
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        mockWebServer.shutdown()
    }

    val responsesFilesDirectory = "src/test/java/nz/badradio/badradio/model/metadata/art/bandcamp"

    @Test
    fun testGetImageUrl() {
        var mockSearchResponseContent = File(
            "${responsesFilesDirectory}/bandcamp_search_response.html",
        ).readText(Charsets.UTF_8)
        mockSearchResponseContent = mockSearchResponseContent
            .replace("https://cassyb.bandcamp.com", baseUrl.toString())
        val mockSearchResponse = MockResponse().setBody(mockSearchResponseContent)
        mockWebServer.enqueue(mockSearchResponse)

        val mockSongPageResponseContent = File("${responsesFilesDirectory}/bandcamp_song_page.html")
            .readText(Charsets.UTF_8)
        val mockSongPageResponse = MockResponse().setBody(mockSongPageResponseContent)
        mockWebServer.enqueue(mockSongPageResponse)

        var imageUrl = ""
        BandcampCrawler(baseUrl.toString()).search(
            object : IStreamingServiceDataObserver {
                override fun notifyOfAlbumArtUrl(url: String) { imageUrl = url }
                override fun notifyOfSoundcloudUrl(url: String) { }
                override fun notifyOfBandcampUrl(url: String) { }
            },
            SongMetadata("come and see", "cassyb, north posse"),
        )

        assert(imageUrl == "https://f4.bcbits.com/img/a3633279595_10.jpg") {
            println("was $imageUrl")
        }
    }

    @Test
    fun testGetSongUrl() {
        val mockSearchResponseContent = File(
            "${responsesFilesDirectory}/bandcamp_search_response.html",
        ).readText(Charsets.UTF_8)
        val mockSearchResponse = MockResponse().setBody(mockSearchResponseContent)
        mockWebServer.enqueue(mockSearchResponse)

        val songUrl = BandcampCrawler(baseUrl.toString()).getSongUrl(
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