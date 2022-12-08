package nz.badradio.badradio.model.metadata

import org.junit.Test

class SongMetadataTests {

    private fun runParser(title: String) =
        SongMetadata.fromStationTrack(StationTrack(title, null, null, null))

    @Test
    fun parseSimpleTitle() {
        val title = "nxexmxo - A Glitters Decay"
        val meta = runParser(title)

        assert(meta.title == "A Glitters Decay") { print(meta.title) }
        assert(meta.artist == "nxexmxo") { print(meta.artist) }
    }

    @Test
    fun parseTitleMultipleDash() {
        val title = "DJ YUNG VAMP - GORILLAZ - FEEL GOOD inc. (YUNG VAMP RMX)"
        val meta = runParser(title)

        assert(meta.title == "FEEL GOOD inc. (YUNG VAMP RMX)") { print(meta.title) }
        assert(meta.artist == "DJ YUNG VAMP, GORILLAZ") { print(meta.artist) }
    }

    @Test
    fun parseTitle() {
        val title = "ROLAND JONES, EMUNE & PHARMACIST - BAST YO ASS PT. 2"
        val meta = runParser(title)

        assert(meta.title == "BAST YO ASS PT. 2") { print(meta.title) }
        assert(meta.artist == "ROLAND JONES, EMUNE & PHARMACIST") { print(meta.artist) }
    }

    @Test
    fun parseNotMatchingTitle() {
        val title = "title not matching format"
        val meta = runParser(title)

        assert(meta.title == title) { print(meta.title) }
        assert(meta.artist == "") { print(meta.artist) }
    }

    @Test
    fun recognizeBadradioTags() {
        assert(SongMetadata("All Day, All Night", "BADRADIO").isBadradioTag)
        // assert(SongMetadata("", "BADRADIO").isBadradioTag) TODO: other Tag?
        assert(!SongMetadata("All Day", "someproducer").isBadradioTag)
    }

}