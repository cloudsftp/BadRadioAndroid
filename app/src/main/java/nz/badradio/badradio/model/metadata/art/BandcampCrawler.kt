package nz.badradio.badradio.model.metadata.art

import nz.badradio.badradio.model.metadata.SongMetadata
import java.io.IOException

object BandcampCrawler : IStreamingServiceCrawler {

    // search query: https://bandcamp.com/search?q=title&item_type=t

    // TODO: implement
    // A lot of HTML crawling required

    @Throws(IOException::class)
    override fun search(parent: IStreamingServiceDataObserver,songMetadata: SongMetadata) {
        TODO("Not yet implemented")
    }
}
