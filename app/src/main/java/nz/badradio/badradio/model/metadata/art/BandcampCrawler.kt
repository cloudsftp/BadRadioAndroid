package nz.badradio.badradio.model.metadata.art

import nz.badradio.badradio.model.metadata.SongMetadata
import nz.badradio.badradio.utilities.buildSearchUrl
import nz.badradio.badradio.utilities.executeRequestAndCheckResponse
import nz.badradio.badradio.utilities.firstMatch
import okhttp3.Request
import java.io.IOException

object BandcampCrawler : IStreamingServiceCrawler {

    // search query: https://bandcamp.com/search?q=come%20and%20see%20cassyb&item_type=t

    private const val urlBase = "https://bandcamp.com/search"

    @Throws(IOException::class)
    override fun search(parent: IStreamingServiceDataObserver, songMetadata: SongMetadata) {
        val songUrl = getSongUrl(parent, songMetadata)
        val songPageRequest = Request.Builder().url(songUrl).build()

        val response = executeRequestAndCheckResponse(songPageRequest, "Song page request (bandcamp html)")

        val imageUrl = getImageUrlFromSongPage(response.body!!.string())
        parent.notifyOfAlbumArtUrl(imageUrl)
    }

    @Throws(IOException::class)
    fun getImageUrlFromSongPage(songPage: String): String {
        val imageUrlPattern = Regex(".*<a class=\"popupImage\" href=\"([^\"]+).*")
        return firstMatch(songPage, imageUrlPattern).groupValues[1]
    }

    @Throws(IOException::class)
    fun getSongUrl(parent: IStreamingServiceDataObserver, songMetadata: SongMetadata): String {
        val searchUrl = buildSearchUrl(
            urlBase,
            "q",
            songMetadata,
            mapOf("item_type" to "t"),
        )
        val searchRequest = Request.Builder().url(searchUrl).build()

        val response = executeRequestAndCheckResponse(searchRequest, "Search request (bandcamp html)")

        val songUrl = getSongUrlFromSearchResult(response.body!!.string())
        parent.notifyOfBandcampUrl(songUrl)
        return songUrl
    }

    @Throws(IOException::class)
    fun getSongUrlFromSearchResult(result: String): String {
        val songUrlPattern = Regex(".*<a class=\"artcont\" href=\"([^\"]+).*")
        val songUrl = firstMatch(result, songUrlPattern).groupValues[1]

        return songUrl
            .replaceAfter("?", "")
            .replace("?", "")
    }
}
