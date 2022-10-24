package nz.badradio.badradio.metadata.art

import nz.badradio.badradio.metadata.SongMetadata
import java.io.IOException

object BandcampAlbumArtGetter : IAlbumArtGetter {

    // search query: https://bandcamp.com/search?q=title&item_type=t

    // TODO: implement
    // A lot of HTML crawling required

    @Throws(IOException::class)
    override fun getImageURL(songMetadata: SongMetadata): String {
        TODO("Not yet implemented")
    }
}
