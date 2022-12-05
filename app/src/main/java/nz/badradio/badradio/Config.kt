package nz.badradio.badradio

import nz.badradio.badradio.model.station.StationInfo

object Config {
    private const val FALLBACK_STATION_ID = "s2b2b68744"

    const val FALLBACK_METADATA_URL = "https://public.radio.co/stations/$FALLBACK_STATION_ID/status"

    const val STATION_DATA_URL = "https://badradio.nz/streamingassets/stations.json"
    val FALLBACK_STATION_INFO = StationInfo(
        "Badradio",
        "https://s2.radioco.net/$FALLBACK_STATION_ID/listen",
        "https://badradio.nz/streamingassets/RadioSchmadio/station-badradio.jpg",
        "Phonk, Trap, Hip Hop & Chill",
        "Badradio is officially back for 2020, with all your favourite artists. Visit badradio.nz"
    )
}
