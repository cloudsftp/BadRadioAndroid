package nz.badradio.badradio.model.metadata.art

import nz.badradio.badradio.model.metadata.SongMetadata
import org.junit.Test

class ITunesCrawlerTests {

    @Test
    fun testGetImageUrl() {
        var imageUrl = ""
        ITunesCrawler.search(
            object : IStreamingServiceDataObserver {
                override fun notifyOfAlbumArtUrl(url: String) { imageUrl = url }
                override fun notifyOfSoundcloudUrl(url: String) { }
                override fun notifyOfBandcampUrl(url: String) { }
            },
            SongMetadata("come and see", "cassyb")
        )

        assert(imageUrl == "https://is4-ssl.mzstatic.com/image/thumb/Music112/v4/b8/67/6d/b8676dfc-a60d-45b3-8136-a3e02e5db168/artwork.jpg/500x500bb.jpg") {
            println("was $imageUrl")
        }
    }

    @Test
    fun testGetImageUrlFromSearchResults() {
        val url = ITunesCrawler.getImageUrlFromSearchResults(comeAndSeeSearchResults)
        assert(url == "https://is4-ssl.mzstatic.com/image/thumb/Music112/v4/b8/67/6d/b8676dfc-a60d-45b3-8136-a3e02e5db168/artwork.jpg/500x500bb.jpg") {
            println("was $url")
        }
    }

    private val comeAndSeeSearchResults = """
{
    "resultCount": 1,
    "results": [
        {
            "wrapperType": "track",
            "kind": "song",
            "artistId": 1530227322,
            "collectionId": 1616796837,
            "trackId": 1616797117,
            "artistName": "North Posse",
            "collectionName": "Paris Robbery II",
            "trackName": "Come and See (feat. Cassyb)",
            "collectionCensoredName": "Paris Robbery II",
            "trackCensoredName": "Come and See (feat. Cassyb)",
            "artistViewUrl": "https://music.apple.com/us/artist/north-posse/1530227322?uo=4",
            "collectionViewUrl": "https://music.apple.com/us/album/come-and-see-feat-cassyb/1616796837?i=1616797117&uo=4",
            "trackViewUrl": "https://music.apple.com/us/album/come-and-see-feat-cassyb/1616796837?i=1616797117&uo=4",
            "previewUrl": "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview122/v4/db/1d/0d/db1d0d79-c2c7-be9c-80b0-af55b98b28cd/mzaf_11468037081690366165.plus.aac.p.m4a",
            "artworkUrl30": "https://is4-ssl.mzstatic.com/image/thumb/Music112/v4/b8/67/6d/b8676dfc-a60d-45b3-8136-a3e02e5db168/artwork.jpg/30x30bb.jpg",
            "artworkUrl60": "https://is4-ssl.mzstatic.com/image/thumb/Music112/v4/b8/67/6d/b8676dfc-a60d-45b3-8136-a3e02e5db168/artwork.jpg/60x60bb.jpg",
            "artworkUrl100": "https://is4-ssl.mzstatic.com/image/thumb/Music112/v4/b8/67/6d/b8676dfc-a60d-45b3-8136-a3e02e5db168/artwork.jpg/100x100bb.jpg",
            "collectionPrice": 9.90,
            "trackPrice": 0.99,
            "releaseDate": "2022-03-09T12:00:00Z",
            "collectionExplicitness": "explicit",
            "trackExplicitness": "explicit",
            "discCount": 1,
            "discNumber": 1,
            "trackCount": 10,
            "trackNumber": 10,
            "trackTimeMillis": 165010,
            "country": "USA",
            "currency": "USD",
            "primaryGenreName": "Hip-Hop/Rap",
            "contentAdvisoryRating": "Explicit",
            "isStreamable": true
        }
    ]
}
"""
}