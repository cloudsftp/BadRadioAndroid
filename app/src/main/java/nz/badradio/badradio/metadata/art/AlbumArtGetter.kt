package nz.badradio.badradio.metadata.art

import android.graphics.Bitmap
import android.util.Log
import nz.badradio.badradio.metadata.SongMetadata
import nz.badradio.badradio.radio.RadioService
import com.squareup.picasso.Picasso
import java.io.IOException

private val albumArtGetters = listOf<IAlbumArtGetter>(
    SoundcloudAlbumArtGetter
)

fun getAlbumArt(songMetadata: SongMetadata): Bitmap? {
    Log.d(TAG, "Searching for album art for $songMetadata")

    albumArtGetters.forEach {
        try {
            Log.d(TAG, "Try getting album art from ${it::class.qualifiedName}")
            val imageURL = it.getImageURL(songMetadata)
            return Picasso.get().load(imageURL).get()
        } catch (e: IOException) {
            Log.e(TAG, "See exception", e)
            Log.d(TAG, "Could not load album art from ${it::class.qualifiedName}")
            /*
            Encoding normal control flow in exceptions is considered harmful - idgaf
            Better than returning and checking for null
             */
        }
    }

    return null

    /*BitmapFactory.decodeResource(
        (radioService as Context).resources,
        R.drawable.badradio
    )
     */
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
