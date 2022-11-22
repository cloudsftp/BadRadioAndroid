package nz.badradio.badradio.metadata.art

import android.graphics.Bitmap
import android.util.Log
import nz.badradio.badradio.metadata.SongMetadata
import com.squareup.picasso.Picasso
import java.io.IOException

class StreamingServiceCrawler : IStreamingServiceDataObserver {
    private val crawlers = listOf<IStreamingServiceCrawler>(
        ITunesCrawler,
        SoundcloudCrawler,
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

    override fun notifyOfAlbumArtUrl(url: String) { albumArtUrls.add(url) }
    override fun notifyOfSoundcloudUrl(url: String) = run { soundcloudUrl = url }
    // fun notifyBandcampUrl(url: String) = run { bandcampUrl = url }

    fun getAlbumArt(): Bitmap? {
        if (albumArtUrls.isEmpty()) {
            return null
        }

        return Picasso.get().load(albumArtUrls[0]).get()
    }
}

interface IStreamingServiceDataObserver {
    fun notifyOfAlbumArtUrl(url: String)
    fun notifyOfSoundcloudUrl(url: String)
}

interface IStreamingServiceCrawler {
    @Throws(IOException::class)
    fun search(parent: IStreamingServiceDataObserver,songMetadata: SongMetadata)
}

private const val TAG = "AlbumArtGetter"

fun songTitleMatches(songTitle: String, songMetadata: SongMetadata): Boolean {
    val title = songTitle.lowercase()

    var mTitle = songMetadata.title
        .lowercase()
        .replace("(", "")
        .replace(")", "")
        .replace("[", "")
        .replace("]", "")

    val mTitleRegex = Regex("(.*)(feat|w/).*")
    val mTitleMatch = mTitleRegex.matchEntire(mTitle)
    if (mTitleMatch != null) {
        mTitle = mTitleMatch.groupValues[1].trim()
    }

    return title.contains(mTitle)
}
