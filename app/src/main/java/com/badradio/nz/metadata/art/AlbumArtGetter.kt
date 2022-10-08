package com.badradio.nz.metadata.art

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.badradio.nz.R
import com.badradio.nz.metadata.SongMetadata
import com.badradio.nz.player.RadioService
import com.squareup.picasso.Picasso
import java.io.IOException

private val albumArtGetters = listOf<IAlbumArtGetter>(
    SoundcloudAlbumArtGetter
)

fun getAlbumArt(songMetadata: SongMetadata, radioService: RadioService) {
    Log.d(TAG, "Searching for album art for $songMetadata")

    var foundAlbumArt = false

    albumArtGetters.forEach {
        if (foundAlbumArt) {
            return@forEach
        }

        try {
            Log.d(TAG, "Try getting album art from ${it::class.qualifiedName}")
            val imageURL = it.getImageURL(songMetadata)
            val image = Picasso.get().load(imageURL).get()
            // radioService.onAlbumArt(image)
            foundAlbumArt = true
        } catch (e: IOException) {
            Log.e(TAG, "See exception", e)
            Log.d(TAG, "Could not load album art from ${it::class.qualifiedName}")
            /*
            Encoding normal control flow in exceptions is considered harmful - idgaf
            Better than returning and checking for null
             */
        }
    }

    if (!foundAlbumArt) {
        val badradioArt = BitmapFactory.decodeResource(
            (radioService as Context).resources,
            R.drawable.badradio
        )
        // radioService.onAlbumArt(badradioArt)
    }
}

interface IAlbumArtGetter {
    @Throws(IOException::class)
    fun getImageURL(songMetadata: SongMetadata): String
}

private const val TAG = "AlbumArtGetter"

fun songMatchesMetadata(song: SoundcloudSong, songMetadata: SongMetadata): Boolean {
    val title = song.title.lowercase()

    var mtitle = songMetadata.title.lowercase()
    val mtitleRegex = Regex("(.*) (w/|\\().*")
    val mtitleMatch = mtitleRegex.matchEntire(mtitle)
    if (mtitleMatch != null) {
        mtitle = mtitleMatch.groupValues[1]
    }

    return title.contains(mtitle)
}
