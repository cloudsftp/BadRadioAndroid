package nz.badradio.badradio.model.metadata.art

import nz.badradio.badradio.model.metadata.SongMetadata
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException

class ITunesCrawlerTests {
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

    @Test
    fun testGetImageUrl() {
        val mockSearchResponseContent = File(
            "${StreamingServiceCrawlerTests.testDataDirectory}/itunes_search_response.html",
        ).readText(Charsets.UTF_8)
        val mockSearchResponse = MockResponse().setBody(mockSearchResponseContent)
        mockWebServer.enqueue(mockSearchResponse)

        var imageUrl = ""
        ITunesCrawler(baseUrl.toString()).search(
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