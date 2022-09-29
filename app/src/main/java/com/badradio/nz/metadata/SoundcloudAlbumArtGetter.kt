package com.badradio.nz.metadata

import android.net.Uri
import com.badradio.nz.utilities.client
import com.google.android.exoplayer2.util.Log
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * Using Soundcloud API v2.
 * This API is not intended for outside use.
 * It may break at any given moment! (Also breaking ToS by using it lmao)
 */
object SoundcloudAlbumArtGetter {

    // search query: https://api-v2.soundcloud.com/search/tracks?q=title&client_id=gsPNGqVqXY4QlaFqDv7WBWglYHdTPsh6&limit=1

    private const val hostname = "api-v2.soundcloud.com"
    private const val searchEndpoint = "search"

    private const val clientID = "gsPNGqVqXY4QlaFqDv7WBWglYHdTPsh6"

    @Throws(IOException::class)
    fun getSongURL(title: String, artist: String): String {
        val searchTerm = "$artist $title"
        val searchURL = Uri.Builder()
            .scheme("https")
            .authority(hostname)
            .appendPath(searchEndpoint)
            .appendPath("tracks")
            .appendQueryParameter("q", searchTerm)
            .appendQueryParameter("client_id", clientID)
            .appendQueryParameter("limit", "1")
            .build().toString()
        val searchRequest = Request.Builder().url(searchURL).build()

        val response: Response = try {
            client.newCall(searchRequest).execute()
        } catch (e: IOException) {
            Log.e(TAG, "Could not search song via Soundcloud api-v2", e)
            throw e
        }

        if (!response.isSuccessful) {
            Log.e(TAG, "Search request returned not OK: ${response.code()}")
            throw IOException("Could not search for song $title by $artist. Soundcloud api-v2 returned not OK: ${response.code()}")
        }

        if (response.body() == null) {
            throw IOException("Soundcloud api-v2 response had no body")
        }

        return getSongURLFromSearchResult(title, artist, response.body()!!.string())
    }

    fun getSongURLFromSearchResult(title: String, artist: String, result: String): String {
        val searchResults = soundcloudSearchResultAdapter.fromJson(result)
            ?: throw IOException("Could not parse search results from Soundcloud api-v2")

        val firstTrack = searchResults.collection[0]

        return firstTrack.permalink_url
    }

    private val TAG = SoundcloudAlbumArtGetter::class.qualifiedName!!


}
