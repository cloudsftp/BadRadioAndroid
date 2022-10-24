package com.badradio.badradio.metadata.art

import com.badradio.badradio.metadata.SongMetadata
import com.badradio.badradio.utilities.executeRequestAndCheckResponse
import com.badradio.badradio.utilities.moshi
import com.squareup.moshi.JsonAdapter
import okhttp3.Request
import java.io.IOException
import java.net.URLEncoder

/**
 * Using Soundcloud API v2.
 * This API is not intended for outside use.
 * It may break at any given moment! (Also breaking ToS by using it lmao)
 */
object SoundcloudAlbumArtGetter : IAlbumArtGetter {

    // search query: https://api-v2.soundcloud.com/search/tracks?q=title&client_id=gsPNGqVqXY4QlaFqDv7WBWglYHdTPsh6&limit=1

    private const val hostname = "api-v2.soundcloud.com"
    private const val searchEndpoint = "search/tracks"

    private const val clientID = "gsPNGqVqXY4QlaFqDv7WBWglYHdTPsh6"
    private const val numSearchResults = 1

    @Throws(IOException::class)
    override fun getImageURL(songMetadata: SongMetadata): String {
        val songURL = getSongURL(songMetadata)
        val songPageRequest = Request.Builder().url(songURL).build()

        val response = executeRequestAndCheckResponse(songPageRequest, "Song page request (sc api-v2)")

        return getImageURLFromSongPage(response.body!!.string())
    }

    @Throws(IOException::class)
    fun getSongURL(songMetadata: SongMetadata): String {
        val searchURL = buildSearchURL(songMetadata)
        val searchRequest = Request.Builder().url(searchURL).build()

        val response = executeRequestAndCheckResponse(searchRequest, "Search request (sc api-v2)")

        return getSongURLFromSearchResult(songMetadata, response.body!!.string())
    }

    private fun buildSearchURL(songMetadata: SongMetadata): String {
        /*
            Don't use Uri.Builder since it won't run in normal unit tests
         */
        val searchTerm = URLEncoder.encode(
            "${songMetadata.artist} ${songMetadata.title}",
            "UTF-8"
        )
        return "https://$hostname/$searchEndpoint?" +
                "q=$searchTerm&" +
                "client_id=$clientID&" +
                "limit=$numSearchResults"
    }

    @Throws(IOException::class)
    fun getSongURLFromSearchResult(songMetadata: SongMetadata, result: String): String {
        val searchResults = soundcloudSearchResultAdapter.fromJson(result)
            ?: throw IOException("Could not parse search results from Soundcloud api-v2")

        if (searchResults.collection.isEmpty()) {
            throw IOException("Empty search results")
        }

        val firstSong = searchResults.collection[0]
        if (!songMatchesMetadata(firstSong, songMetadata)) {
            throw IOException("Song $firstSong does not match metadata $songMetadata")
        }

        return firstSong.permalink_url
    }

    @Throws(IOException::class)
    fun getImageURLFromSongPage(songPage: String): String {
        val imageURLRegex = Regex(".*<img src=\"([^\"]*)\".*")

        songPage.split('\n').forEach { line ->
            val imageURLMatch = imageURLRegex.matchEntire(line)
                ?: return@forEach

            return imageURLMatch.groupValues[1]
        }

        throw IOException("Could not find image in Soundcloud song page.")
    }
}

data class SoundcloudSearchResults(
    val collection: List<SoundcloudSong>,
    val total_results: Int,
    val query_urn: String,
)

// Only necessary fields
data class SoundcloudSong(
    val id: Int,
    val kind: String,
    val permalink_url: String,
    val title: String,
    val user: SoundcloudUser,
)

// Only necessary fields
data class SoundcloudUser(
    val username: String,
)

val soundcloudSearchResultAdapter: JsonAdapter<SoundcloudSearchResults> = moshi.adapter(SoundcloudSearchResults::class.java)
