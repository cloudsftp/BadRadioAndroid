package com.badradio.nz.metadata

internal interface MetadataListener {
    fun onMetadataReceived(artist: String?, song: String?, show: String?)
}
class Metadata(
    val artist: String?,
    val song: String?,
    val show: String?,
    val channels: String?,
    val bitrate: String?,
    val station: String?,
    val genre: String?,
    val url: String?
)
