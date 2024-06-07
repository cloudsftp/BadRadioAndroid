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

class SoundcloudCrawlerTests {
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
        var mockSearchResponseContent = File(
            "${StreamingServiceCrawlerTests.testDataDirectory}/soundcloud_search_response.html",
        ).readText(Charsets.UTF_8)
        val mockSearchResponse = MockResponse().setBody(mockSearchResponseContent)
        mockWebServer.enqueue(mockSearchResponse)

        val mockSongPageResponseContent = File("${StreamingServiceCrawlerTests.testDataDirectory}/soundcloud_song_page.html")
            .readText(Charsets.UTF_8)
        val mockSongPageResponse = MockResponse().setBody(mockSongPageResponseContent)
        mockWebServer.enqueue(mockSongPageResponse)

        var imageUrl = ""
        SoundcloudCrawler(baseUrl.toString()).search(
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
        val mockSearchResponseContent = File(
            "${StreamingServiceCrawlerTests.testDataDirectory}/soundcloud_search_response.html",
        ).readText(Charsets.UTF_8)
        val mockSearchResponse = MockResponse().setBody(mockSearchResponseContent)
        mockWebServer.enqueue(mockSearchResponse)

        val songUrl = SoundcloudCrawler(baseUrl.toString()).getSongUrl(
            object : IStreamingServiceDataObserver {
                override fun notifyOfAlbumArtUrl(url: String) { }
                override fun notifyOfSoundcloudUrl(url: String) { }
                override fun notifyOfBandcampUrl(url: String) { }
            },
            SongMetadata("come and see", "cassyb, north posse"),
        )
        assert(songUrl == "${baseUrl}/xxcassyb/come-and-see") {
            println("was $songUrl")
        }
    }

}