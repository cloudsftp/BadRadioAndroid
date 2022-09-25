package com.badradio.nz

object Config {
    const val DEFAULT_VOLUME = 11

    private const val STATION_ID = "s2b2b68744"

    const val FETCH_METADATA_INTERVAL = 2000L // in millis
    const val METADATA_URL = "https://public.radio.co/stations/$STATION_ID/status"
}
