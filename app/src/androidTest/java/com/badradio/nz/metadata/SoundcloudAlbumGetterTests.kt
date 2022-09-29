package com.badradio.nz.metadata

import org.junit.Test

class SoundcloudAlbumGetterTests {

    @Test
    fun getSongURL() {
        val url = SoundcloudAlbumArtGetter.getSongURL(
            "come and see", "cassyb, north posse",
        )
        assert(url == "https://soundcloud.com/xxcassyb/come-and-see") {
            println("was $url")
        }
    }

}