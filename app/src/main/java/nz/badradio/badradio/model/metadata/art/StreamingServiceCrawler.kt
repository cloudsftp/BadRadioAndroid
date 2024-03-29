package nz.badradio.badradio.model.metadata.art

import android.graphics.Bitmap
import android.util.Log
import com.squareup.picasso.Picasso
import nz.badradio.badradio.model.metadata.SongMetadata
import java.io.IOException

class StreamingServiceCrawler : IStreamingServiceDataObserver {
    private val crawlers = listOf(
        // ITunesCrawler, TODO: re-enable
        BandcampCrawler,
        SoundcloudCrawler,
    )

    private val albumArtUrls = mutableListOf<String>()
    private var soundcloudUrl: String? = null
    private var bandcampUrl: String? = null

    fun search(songMetadata: SongMetadata) {
        crawlers.forEach {
            try {
                Log.d(tag, "Try crawling ${it::class.qualifiedName}")
                it.search(this, songMetadata)
            } catch (e: Exception) {
                Log.w(tag, e)
            }
        }
    }

    override fun notifyOfAlbumArtUrl(url: String) { albumArtUrls.add(url) }
    override fun notifyOfSoundcloudUrl(url: String) = run { soundcloudUrl = url }
    override fun notifyOfBandcampUrl(url: String) = run { bandcampUrl = url }

    fun getAlbumArt(): Bitmap? {
        if (albumArtUrls.isEmpty()) {
            return null
        }

        return try {
            Picasso.get().load(albumArtUrls[0]).get()
        } catch (e: Exception) {
            Log.w(tag, "Could not download album art", e)
            null
        }
    }
}

interface IStreamingServiceDataObserver {
    fun notifyOfAlbumArtUrl(url: String)
    fun notifyOfSoundcloudUrl(url: String)
    fun notifyOfBandcampUrl(url: String)
}

interface IStreamingServiceCrawler {
    @Throws(IOException::class)
    fun search(parent: IStreamingServiceDataObserver,songMetadata: SongMetadata)
}

private const val tag = "AlbumArtGetter"

private val mTitleRegex = Regex("(.*)(feat|w/).*")
fun songTitleMatches(songTitle: String, songMetadata: SongMetadata): Boolean {
    val title = songTitle.lowercase()

    var mTitle = songMetadata.title
        .lowercase()
        .replace("(", "")
        .replace(")", "")
        .replace("[", "")
        .replace("]", "")

    val mTitleMatch = mTitleRegex.matchEntire(mTitle)
    if (mTitleMatch != null) {
        mTitle = mTitleMatch.groupValues[1].trim()
    }

    return title.contains(mTitle)
}
