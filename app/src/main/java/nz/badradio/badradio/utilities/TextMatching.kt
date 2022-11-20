package nz.badradio.badradio.utilities

import java.io.IOException

@Throws(IOException::class)
fun firstMatch(text: String, regex: Regex): MatchResult {
    text.split('\n').forEach {
        return regex.matchEntire(it) ?: return@forEach
    }

    throw IOException("No match found")
}
