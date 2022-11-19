package nz.badradio.badradio.metadata.art

import android.graphics.Bitmap
import android.util.Log
import nz.badradio.badradio.metadata.SongMetadata
import com.squareup.picasso.Picasso
import java.io.IOException

class StreamingServiceCrawler {
    private val crawlers = listOf<IAlbumArtGetter>(
        SoundcloudAlbumArtGetter
    )

    private val albumArtUrls = mutableListOf<String>()
    private var soundcloudUrl: String? = null
    // private var bandcampUrl: String? = null

    fun search(songMetadata: SongMetadata) {
        crawlers.forEach {
            try {
                Log.d(TAG, "Try crawling ${it::class.qualifiedName}")
                it.search(this, songMetadata)
            } catch (e: IOException) {
                Log.w(TAG, e)
            }
        }
    }

    fun notifyOfAlbumArtUrl(url: String) = albumArtUrls.add(url)
    fun notifySoundcloudUrl(url: String) = run { soundcloudUrl = url }
    // fun notifyBandcampUrl(url: String) = run { bandcampUrl = url }

    fun getAlbumArt(): Bitmap? {
        if (albumArtUrls.isEmpty()) {
            return null
        }

        return Picasso.get().load(albumArtUrls[0]).get()
    }
}

interface IAlbumArtGetter {
    @Throws(IOException::class)
    fun search(parent: StreamingServiceCrawler,songMetadata: SongMetadata)
}

private const val TAG = "AlbumArtGetter"

/*
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
*/
