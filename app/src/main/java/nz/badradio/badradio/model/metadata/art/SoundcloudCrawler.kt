package nz.badradio.badradio.model.metadata.art

import nz.badradio.badradio.model.metadata.SongMetadata
import nz.badradio.badradio.utilities.buildSearchUrl
import nz.badradio.badradio.utilities.executeRequestAndCheckResponse
import nz.badradio.badradio.utilities.firstMatch
import okhttp3.Request
import java.io.IOException

object SoundcloudCrawler : IStreamingServiceCrawler {

    // search query: https://soundcloud.com/search/sounds?q=come%20and%20see%20cassyb

    private const val urlBase = "https://soundcloud.com"
    private const val searchEndpoint = "search/sounds"

    @Throws(IOException::class)
    override fun search(parent: IStreamingServiceDataObserver, songMetadata: SongMetadata) {
        val songUrl = getSongUrl(parent, songMetadata)
        val songPageRequest = Request.Builder().url(songUrl).build()

        val response = executeRequestAndCheckResponse(songPageRequest, "Song page request (sc html)")

        val imageUrl = getImageUrlFromSongPage(response.body!!.string())
        parent.notifyOfAlbumArtUrl(imageUrl)
    }

    @Throws(IOException::class)
    fun getSongUrl(parent: IStreamingServiceDataObserver, songMetadata: SongMetadata): String {
        val searchURL = buildSearchUrl(
            "$urlBase/$searchEndpoint",
            "q",
            songMetadata,
        )
        val searchRequest = Request.Builder().url(searchURL).build()

        val response = executeRequestAndCheckResponse(searchRequest, "Search request (sc html)")

        val songUrl = getSongUrlFromSearchResult(songMetadata, response.body!!.string())
        parent.notifyOfSoundcloudUrl(songUrl)
        return songUrl
    }

    @Throws(IOException::class)
    fun getSongUrlFromSearchResult(songMetadata: SongMetadata, result: String): String {
        val songUrlPattern = Regex(".*<li><h2><a href=\"([^\"]+)\">([^<]+).*")
        val match = firstMatch(result, songUrlPattern)

        val songTitle = match.groupValues[2]
        if (!songTitleMatches(songTitle, songMetadata)) {
            throw IOException("Song title $songTitle does not match metadata $songMetadata")
        }

        val songUrlSuffix = match.groupValues[1]
        return "$urlBase$songUrlSuffix"
    }

    @Throws(IOException::class)
    fun getImageUrlFromSongPage(songPage: String): String {
        val imageUrlPattern = Regex(".*<img src=\"([^\"]*)\".*")
        return firstMatch(songPage, imageUrlPattern).groupValues[1]
    }
}
