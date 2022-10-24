package nz.badradio.badradio.metadata

import android.annotation.SuppressLint
import org.junit.Test

class StationStatusTests {

    @SuppressLint("CheckResult")
    @Test
    fun testParseStationResponse() {
        stationStatusAdapter.fromJson(exampleStationResponse)
    }

    @Test
    fun testParseStationResponseCurrentTrack() {
        val status = stationStatusAdapter.fromJson(exampleStationResponse)
        assert(status!!.current_track.title == "OGee-T - EXPLORATION")
    }

    private val exampleStationResponse = "{\"status\":\"online\",\"source\":{\"type\":\"automated\",\"collaborator\":null,\"relay\":null},\"collaborators\":[],\"relays\":[],\"current_track\":{\"title\":\"OGee-T - EXPLORATION\",\"start_time\":\"2022-09-25T14:47:20+00:00\",\"artwork_url\":\"https:\\/\\/images.radio.co\\/station_logos\\/s2b2b68744.20200406095314.jpg\",\"artwork_url_large\":\"https:\\/\\/images.radio.co\\/station_logos\\/s2b2b68744.20200406095314.jpg\"},\"history\":[{\"title\":\"OGee-T - EXPLORATION\"},{\"title\":\"ICEOUT CLICK - JUDGEMENT G X SHVBBVG - GOT THEM HOEZ\"},{\"title\":\"BADRADIO - 24\\/7 PHONK\"},{\"title\":\"Route 696 - Playa Cascadelic\"},{\"title\":\"TRE FLIP - MAGENTA\"},{\"title\":\"POOTY - IN THE NIGHT\"},{\"title\":\"CODY VALDEZ - TOO DAMN HIGH\"},{\"title\":\"DJ SMOKEY 666 - DJ SMOKEY - PREMEDITIZATE\"},{\"title\":\"KATEBI - XTS-400 w\\/V\\u00d31D\"},{\"title\":\"DOJAMANE, KILFIGER \\u0026 CODY VALDEZ - SEE RED\"},{\"title\":\"AntXres - War\"},{\"title\":\"OPEN - NIGHTMARES\"},{\"title\":\"NxxxxxS - + jason is dead +\"},{\"title\":\"cloudymane - DATZ DA SIRUP\"},{\"title\":\"UINDIGO [Check Ya Technique] - CHA\$M\"},{\"title\":\"BADRADIO - ALL DAY, ALL NIGHT\"},{\"title\":\"Lil\\u0027 Rocketman - The Juice Vendor Feat. Sam Dorfman (instrumental)\"},{\"title\":\"SPYDER550 - stoneee hub\"},{\"title\":\"ZappBeats - DOOMSDAY w\\/ worri\"},{\"title\":\"POOTY - WALK ON TOP\"}],\"logo_url\":\"https:\\/\\/images.radio.co\\/station_logos\\/s2b2b68744.20200406095314.jpg\",\"streaming_hostname\":\"s2.radio.co\",\"outputs\":[{\"name\":\"listen\",\"format\":\"MP3\",\"bitrate\":192}]}"

}