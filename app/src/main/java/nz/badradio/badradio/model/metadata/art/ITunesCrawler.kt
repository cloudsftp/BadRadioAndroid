package nz.badradio.badradio.model.metadata.art

import com.squareup.moshi.JsonAdapter
import nz.badradio.badradio.model.metadata.SongMetadata
import nz.badradio.badradio.utilities.buildSearchUrl
import nz.badradio.badradio.utilities.executeRequestAndCheckResponse
import nz.badradio.badradio.utilities.moshi
import okhttp3.Request
import java.io.IOException

object ITunesCrawler : IStreamingServiceCrawler {

    // search query: https://itunes.apple.com/search?term=come%20and%20see%20cassyb&media=music&limit=1

    private const val urlBase = "https://itunes.apple.com/search"

    @Throws(IOException::class, IndexOutOfBoundsException::class)
    override fun search(parent: IStreamingServiceDataObserver, songMetadata: SongMetadata) {
        val searchUrl = buildSearchUrl(
            urlBase,
            "term",
            songMetadata,
            mapOf(
                "media" to "music",
                "limit" to "1",
            ),
        )
        val searchRequest = Request.Builder().url(searchUrl).build()

        val response = executeRequestAndCheckResponse(searchRequest, "Search request (itunes html)")
        val imageUrl = getImageUrlFromSearchResults(response.body!!.string())
        parent.notifyOfAlbumArtUrl(imageUrl)
    }

    @Throws(IOException::class, IndexOutOfBoundsException::class)
    fun getImageUrlFromSearchResults(results: String): String {
        val searchResults = iTunesSearchResultsAdapter.fromJson(results)
            ?: throw IOException("Could not parse search results from iTunes api")

        return searchResults
            .results[0]
            .artworkUrl100
            .replace("100x100bb", "500x500bb")
    }
}

data class ITunesSearchResults(
    val resultCount: Int,
    val results: List<ITunesSong>,
)

data class ITunesSong(
    val artistName: String,
    val trackName: String,
    val artworkUrl100: String,
)

val iTunesSearchResultsAdapter: JsonAdapter<ITunesSearchResults> = moshi.adapter(ITunesSearchResults::class.java)
