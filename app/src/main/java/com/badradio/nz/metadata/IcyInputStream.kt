package com.badradio.nz.metadata

import android.util.Log
import kotlin.Throws
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.util.regex.Pattern

internal class IcyInputStream(
    `in`: InputStream?,
    private val interval: Int,
    private val metadataListener: MetadataListener
) : FilterInputStream(`in`) {
    private var remaining = interval

    @Throws(IOException::class)
    override fun read(): Int {
        val ret = super.read()
        if (--remaining == 0) {
            getMetadata()
        }
        return ret
    }

    @Throws(IOException::class)
    override fun read(buffer: ByteArray, offset: Int, len: Int): Int {
        val ret = `in`.read(buffer, offset, remaining.coerceAtMost(len))
        if (remaining == ret) {
            getMetadata()
        } else {
            remaining -= ret
        }
        return ret
    }

    /**
     * Tries to read all bytes into the target buffer.
     * @param sizeToRead the requested size
     * @return the number of really bytes read; if less than requested, then eof detected
     */
    @Throws(IOException::class)
    private fun readFully(buffer: ByteArray, offset: Int, sizeToRead: Int): Int {
        var sizeReadTotal = 0

        while (sizeToRead - sizeReadTotal > 0) {
            val sizeRead = `in`.read(buffer, offset + sizeReadTotal, sizeToRead - sizeReadTotal)
            if (sizeRead == -1) break

            sizeReadTotal += sizeRead
        }

        return sizeReadTotal
    }

    @Throws(IOException::class)
    private fun getMetadata() {
            remaining = interval
            var size = `in`.read()

            // either no metadata or eof:
            if (size < 1) return

            size *= 16
            val buffer = ByteArray(size)
            readFully(buffer, 0, size)

            // find the string end:
            val stringSize = buffer.indexOfFirst { it.toInt() == 0 }

            val s = try {
                String(buffer, 0, stringSize)
            } catch (e: Exception) {
                Log.e(TAG, "Cannot convert bytes to String")
                return
            }

            parseMetadata(s)
        }

    /**
     * Parses the metadata
     * @param data the metadata string like: StreamTitle='...';StreamUrl='...';
     */
    private fun parseMetadata(data: String) {
        val match = Pattern.compile("StreamTitle='([^;]*)'").matcher( data.trim { it <= ' ' })
        if (match.find()) {
            // Presume artist/title is separated by " - ".
            val metadata = match.group(1)?.split(" - ")?.toTypedArray()
            if (metadata != null) {
                when (metadata.size) {
                    3 -> metadataReceived(metadata[1], metadata[2], metadata[0])
                    2 -> metadataReceived(metadata[0], metadata[1], null)
                    1 -> metadataReceived(null, null, metadata[0])
                }
            }
        }
    }

    private fun metadataReceived(artist: String?, song: String?, show: String?) {
        Log.i(TAG, "Metadata received: ")
        Log.i(TAG, "Show: $show")
        Log.i(TAG, "Artist: $artist")
        Log.i(TAG, "Song: $song")
        metadataListener.onMetadataReceived(artist, song, show)
    }

    companion object {
        private val TAG = IcyInputStream::class.java.name
    }
}