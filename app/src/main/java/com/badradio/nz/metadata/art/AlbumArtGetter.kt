package com.badradio.nz.metadata.art

import android.graphics.Bitmap
import android.util.Log
import com.badradio.nz.metadata.SongMetadata
import com.badradio.nz.utilities.ListenersManager
import com.squareup.picasso.Picasso
import java.io.IOException

object AlbumArtGetter {

    private val albumArtGetters = listOf<IAlbumArtGetter>(
        SoundcloudAlbumArtGetter
    )

    fun getAlbumArt(songMetadata: SongMetadata) {
        Log.d(TAG, "Searching for album art")

        for (albumGetter in albumArtGetters) {
            try {
                Log.d(TAG, "Try getting album art from ${albumGetter::class.qualifiedName}")
                val imageURL = albumGetter.getImageURL(songMetadata)
                val image = Picasso.get().load(imageURL).get()
                ListenersManager.onAlbumArt(image)
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
    }

    private val TAG = AlbumArtGetter::class.qualifiedName!!
}

interface IAlbumArtGetter {
    @Throws(IOException::class)
    fun getImageURL(songMetadata: SongMetadata): String
}
