package com.badradio.nz.metadata.art

import android.util.Log
import com.badradio.nz.metadata.SongMetadata
import com.badradio.nz.utilities.MetadataObserver
import com.squareup.picasso.Picasso
import java.io.IOException

private val albumArtGetters = listOf<IAlbumArtGetter>(
    SoundcloudAlbumArtGetter
)

fun getAlbumArt(songMetadata: SongMetadata, metadataObserver: MetadataObserver) {
    Log.d(TAG, "Searching for album art")

    for (albumGetter in albumArtGetters) {
        try {
            Log.d(TAG, "Try getting album art from ${albumGetter::class.qualifiedName}")
            val imageURL = albumGetter.getImageURL(songMetadata)
            val image = Picasso.get().load(imageURL).get()
            metadataObserver.onAlbumArt(image)
            break // If successful, don't try other album getters
        } catch (e: IOException) {
            Log.e(TAG, "See exception", e)
            Log.d(TAG, "Could not load album art from ${albumGetter::class.qualifiedName}")
            /*
            Encoding normal control flow in exceptions is considered harmful - idgaf
            Better than returning and checking for null
             */
        }
    }

    // TODO: if no hit, take standard pic
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
