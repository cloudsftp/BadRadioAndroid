package nz.badradio.badradio.metadata.art

import nz.badradio.badradio.metadata.SongMetadata
import nz.badradio.badradio.utilities.executeRequestAndCheckResponse
import okhttp3.Request
import java.io.IOException
import java.net.URLEncoder

object SoundcloudAlbumArtGetter : IAlbumArtGetter {

    // search query: https://soundcloud.com/search/sounds?q=come%20and%20see%20cassyb

    private const val urlBase = "https://soundcloud.com"
    private const val searchEndpoint = "search/sounds"

    @Throws(IOException::class)
    override fun search(parent: StreamingServiceCrawler, songMetadata: SongMetadata) {
        val songURL = getSongURL(parent, songMetadata)
        val songPageRequest = Request.Builder().url(songURL).build()

        val response = executeRequestAndCheckResponse(songPageRequest, "Song page request (sc html)")

        val imageUrl = getImageUrlFromSongPage(response.body!!.string())
        parent.notifyOfAlbumArtUrl(imageUrl)
    }

    @Throws(IOException::class)
    fun getSongURL(parent: StreamingServiceCrawler, songMetadata: SongMetadata): String {
        val searchURL = buildSearchURL(songMetadata)
        val searchRequest = Request.Builder().url(searchURL).build()

        val response = executeRequestAndCheckResponse(searchRequest, "Search request (sc html)")

        val songUrl = getSongURLFromSearchResult(songMetadata, response.body!!.string())
        parent.notifySoundcloudUrl(songUrl)
        return songUrl
    }

    private fun buildSearchURL(songMetadata: SongMetadata): String {
        /*
            Don't use Uri.Builder since it won't run in normal unit tests
         */
        val searchTerm = URLEncoder.encode(
            "${songMetadata.artist} ${songMetadata.title}",
            "UTF-8"
        )
        return "$urlBase/$searchEndpoint?q=$searchTerm"
    }

    @Throws(IOException::class)
    fun getSongURLFromSearchResult(songMetadata: SongMetadata, result: String): String {
        val songUrlPattern = Regex(".*<li><h2><a href=\"([^\"]+)\">([^<]+.*)")
        val songUrlSuffix = firstMatch(result, songUrlPattern).groupValues[1]
        return "$urlBase$songUrlSuffix"
    }

    @Throws(IOException::class)
    fun getImageUrlFromSongPage(songPage: String): String {
        val imageUrlPattern = Regex(".*<img src=\"([^\"]*)\".*")
        return firstMatch(songPage, imageUrlPattern).groupValues[1]
    }

    @Throws(IOException::class)
    fun firstMatch(text: String, regex: Regex): MatchResult {
        text.split('\n').forEach {
            return regex.matchEntire(it) ?: return@forEach
        }

        throw IOException("No match found")
    }
}
