package nz.badradio.badradio.metadata

import nz.badradio.badradio.utilities.moshi
import com.squareup.moshi.JsonAdapter

data class StationStatus(
    val status: String,
    val source: StationSource,
    val collaborators: List<String>,
    val relays: List<String>,
    val current_track: StationTrack,
    val history: List<StationTrack>,
    val logo_url: String,
    val streaming_hostname: String,
    val outputs: List<StationOutput>
)

data class StationSource(
    val type: String,
    val collaborator: String?,
    val relay: String?
)

data class StationTrack(
    val title: String,
    val start_time: String?,
    val artwork_url: String?,
    val artwork_url_large: String?
)

data class StationOutput(
    val name: String,
    val format: String,
    val bitrate: Int
)

val stationStatusAdapter: JsonAdapter<StationStatus> = moshi.adapter(StationStatus::class.java)

/* example JSON:
{
    "status": "online",
    "source": {
        "type": "automated",
        "collaborator": null,
        "relay": null
    },
    "collaborators": [],
    "relays": [],
    "current_track": {
        "title": "NxxxxxS - + jason is dead +",
        "start_time": "2022-09-25T14:15:47+00:00",
        "artwork_url": "https:\/\/is2-ssl.mzstatic.com\/image\/thumb\/Music124\/v4\/ce\/17\/95\/ce17951c-0475-3b3d-d8ab-d6e264439614\/source\/100x100bb.jpg",
        "artwork_url_large": "https:\/\/is2-ssl.mzstatic.com\/image\/thumb\/Music124\/v4\/ce\/17\/95\/ce17951c-0475-3b3d-d8ab-d6e264439614\/source\/600x600bb.jpg"
    },
    "history": [
        {"title": "NxxxxxS - + jason is dead +"},
        {"title": "cloudymane - DATZ DA SIRUP"},
        {"title": "UINDIGO [Check Ya Technique] - CHA$M"},
        {"title": "BADRADIO - ALL DAY, ALL NIGHT"},
        {"title": "Lil\u0027 Rocketman - The Juice Vendor Feat. Sam Dorfman (instrumental)"},
        {"title": "SPYDER550 - stoneee hub"},
        {"title": "ZappBeats - DOOMSDAY w\/ worri"},
        {"title": "POOTY - WALK ON TOP"},
        {"title": "Inteus - Trepid"},
        {"title": "FREDDY KONFEDDY - ADDICTED w\/ OLDFLOP"},
        {"title": "E R R O R - WEST BANK (PROD. PURPP CADDY)"},
        {"title": "BERRYMANE - GET SMACKED IN THIS 94\u0027 (DJ SMOKEY x BACKWOOD BOY)"},
        {"title": "NxxxxxS - I Think That You Owe Me feat. Soudiere"},
        {"title": "EXISTANZE - LUMINOUS"},
        {"title": "MANSON MANE - BAD DREAM"},
        {"title": "Lil\u0027 Rocketman, Sotgore - CAST AWAY w\/ Sotgore"},
        {"title": "BADRADIO - ALL DAY, ALL NIGHT"},
        {"title":"Amperich - Stuff"},
        {"title": "SUICIDE FOREST - AMADEEZY"},
        {"title":"WAVYGENESIS - SYNTHETIC"}
    ],
    "logo_url": "https:\/\/images.radio.co\/station_logos\/s2b2b68744.20200406095314.jpg",
    "streaming_hostname": "s2.radio.co",
    "outputs": [
        {
            "name": "listen",
            "format": "MP3",
            "bitrate": 192
        }
    ]
}
 */
