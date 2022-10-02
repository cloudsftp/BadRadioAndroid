package com.badradio.nz.metadata

import android.util.Log
import com.badradio.nz.utilities.Tools
import com.badradio.nz.utilities.client
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URLEncoder

/**
 * Using Soundcloud API v2.
 * This API is not intended for outside use.
 * It may break at any given moment! (Also breaking ToS by using it lmao)
 */
object SoundcloudAlbumArtGetter {

    // search query: https://api-v2.soundcloud.com/search/tracks?q=title&client_id=gsPNGqVqXY4QlaFqDv7WBWglYHdTPsh6&limit=1

    private const val hostname = "api-v2.soundcloud.com"
    private const val searchEndpoint = "search/tracks"

    private const val clientID = "gsPNGqVqXY4QlaFqDv7WBWglYHdTPsh6"
    private const val numSearchResults = 1

    @Throws(IOException::class)
    fun getImageURL(title: String, artist: String): String {
        val songURL = getSongURL(title, artist)
        val songPageRequest = Request.Builder().url(songURL).build()

        val response = Tools.executeRequestAndCheckResponse(songPageRequest, "Song page request (sc api-v2)")

        return getImageURLFromSongPage(response.body()!!.string())
    }

    @Throws(IOException::class)
    fun getSongURL(title: String, artist: String): String {
        val searchURL = buildSearchURL(title, artist)
        val searchRequest = Request.Builder().url(searchURL).build()

        val response = Tools.executeRequestAndCheckResponse(searchRequest, "Search request (sc api-v2)")

        return getSongURLFromSearchResult(title, artist, response.body()!!.string())
    }

    private fun buildSearchURL(title: String, artist: String): String {
        /*
            Don't use Uri.Builder since it won't run in normal unit tests
         */
        val searchTerm = URLEncoder.encode("$artist $title", "UTF-8")
        return "https://$hostname/$searchEndpoint?" +
                "q=$searchTerm&" +
                "client_id=$clientID&" +
                "limit=$numSearchResults"
    }

    @Throws(IOException::class)
    fun getSongURLFromSearchResult(title: String, artist: String, result: String): String {
        val searchResults = soundcloudSearchResultAdapter.fromJson(result)
            ?: throw IOException("Could not parse search results from Soundcloud api-v2")

        val firstTrack = searchResults.collection[0]

        return firstTrack.permalink_url
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

    private val TAG = SoundcloudAlbumArtGetter::class.qualifiedName!!

}
