package nz.badradio.badradio.metadata.art

import com.squareup.moshi.JsonAdapter
import nz.badradio.badradio.metadata.SongMetadata
import nz.badradio.badradio.utilities.moshi
import java.io.IOException

object ITunesCrawler : IStreamingServiceCrawler {
    override fun search(parent: IStreamingServiceDataObserver, songMetadata: SongMetadata) {
        TODO("Not yet implemented")
    }

    fun getImageUrlFromSearchResults(results: String): String {
        /**
         * 1. parse search results from json
         * 2. get url, replace 100x100bb w/ 500x500bb
         */
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
