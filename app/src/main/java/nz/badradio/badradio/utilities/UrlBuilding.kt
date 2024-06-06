package nz.badradio.badradio.utilities

import nz.badradio.badradio.model.metadata.SongMetadata
import java.net.URLEncoder

fun buildSearchUrl(
    url: String,
    searchTermParameterName: String,
    songMetadata: SongMetadata,
    additionalParameters: Map<String, String>? = null,
): String {
    val searchTerm = URLEncoder.encode(
        "${songMetadata.artist} ${songMetadata.title}",
        "UTF-8"
    )

    val additionalParametersStringBuilder = StringBuilder()
    additionalParameters?.forEach {
        additionalParametersStringBuilder.append(
            "&${encode(it.key)}=${encode(it.value)}"
        )
    }

    return "$url?$searchTermParameterName=$searchTerm$additionalParametersStringBuilder"
}

private fun encode(value: String): String = URLEncoder.encode(value, "UTF-8")
