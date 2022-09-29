package com.badradio.nz.metadata

import com.badradio.nz.utilities.moshi
import com.squareup.moshi.JsonAdapter

data class SoundcloudSearchResults(
    val collection: List<SoundcloudSong>,
    val total_results: Int,
    val query_urn: String,
)

// Only necessary fields
data class SoundcloudSong(
    val id: Int,
    val kind: String,
    val permalink_url: String,
    val title: String,
    val user: SoundcloudUser,
)

// Only necessary fields
data class SoundcloudUser(
    val username: String,
)

val soundcloudSearchResultAdapter: JsonAdapter<SoundcloudSearchResults> = moshi.adapter(SoundcloudSearchResults::class.java)
