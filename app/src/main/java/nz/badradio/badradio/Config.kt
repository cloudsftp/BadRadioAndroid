package nz.badradio.badradio

import nz.badradio.badradio.model.station.StationInfo

object Config {
    const val DEFAULT_VOLUME = 11

    private const val STATION_ID = "s2b2b68744"

    const val FETCH_METADATA_INTERVAL = 2000L // in millis
    const val METADATA_URL = "https://public.radio.co/stations/$STATION_ID/status"

    const val STATION_DATA_URL = "https://badradio.nz/streamingassets/stations.json"
    val FALLBACK_STATION_INFO = StationInfo(
        "Badradio",
        "https://s2.radio.co/$STATION_ID/listen",
        "https://badradio.nz/streamingassets/RadioSchmadio/station-badradio.jpg",
        "Phonk, Trap, Hip Hop & Chill",
        "Badradio is officially back for 2020, with all your favourite artists. Visit badradio.nz"
    )
}
