package com.badradio.nz.metadata

import android.util.Log
import com.google.android.exoplayer2.util.ParsableByteArray
import kotlin.Throws
import java.io.IOException
import java.io.InputStream
import java.util.HashMap

internal class OggInputStream(`in`: InputStream, private val listener: MetadataListener) :
    PeekInputStream(
        `in`
    ) {

    private val holder = PacketInfoHolder()
    private val idHeader = IdHeader()
    private val commentHeader = CommentHeader()
    private val pageHeader = PageHeader()
    private val packetArray = ParsableByteArray(ByteArray(0), 0)
    private val headerArray = ParsableByteArray(282)

    @Throws(IOException::class)
    override fun read(target: ByteArray, offset: Int, length: Int): Int {
        try {
            if (peekPacket(this, packetArray, headerArray, pageHeader, holder)) {
                unpackIdHeader(packetArray, idHeader)
                unpackCommentHeader(packetArray, commentHeader, listener)
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return super.read(target, offset, length)
    }

    private class PageHeader {
        var revision = 0
        var type = 0
        var granulePosition: Long = 0
        var streamSerialNumber: Long = 0
        var pageSequenceNumber: Long = 0
        var pageChecksum: Long = 0
        var pageSegmentCount = 0
        var headerSize = 0
        var bodySize = 0
        val laces = IntArray(255)
        fun reset() {
            revision = 0
            type = 0
            granulePosition = 0L
            streamSerialNumber = 0L
            pageSequenceNumber = 0L
            pageChecksum = 0L
            pageSegmentCount = 0
            headerSize = 0
            bodySize = 0
        }
    }

    private class IdHeader {
        var version: Long = 0
        var audioChannels = 0
        var audioSampleRate: Long = 0
        var bitRateMaximum = 0
        var bitRateNominal = 0
        var bitRateMinimum = 0
        var blockSize0 = 0
        var blockSize1 = 0
        fun reset() {
            audioChannels = 0
            audioSampleRate = 0
            bitRateMaximum = 0
            bitRateNominal = 0
            bitRateMinimum = 0
            blockSize0 = 0
            blockSize1 = 0
        }
    }

    private class CommentHeader {
        var vendor: String? = null
        val comments: HashMap<String, String>
        var length = 0
        fun reset() {
            vendor = ""
            comments.clear()
            length = 0
        }

        init {
            comments = HashMap()
        }
    }

    private class PacketInfoHolder {
        var size = 0
        var segmentCount = 0
    }

    companion object {
        // TODO: Use PushbackInputStream instead of Peek?
        private val TAG = OggInputStream::class.java.name
        @Throws(IOException::class, InterruptedException::class)
        private fun peekPacket(
            stream: PeekInputStream,
            packetArray: ParsableByteArray,
            headerArray: ParsableByteArray,
            header: PageHeader,
            holder: PacketInfoHolder
        ): Boolean {
            var segmentIndex: Int
            var currentSegmentIndex = -1
            packetArray.reset()
            var packetComplete = false
            while (!packetComplete) {
                if (currentSegmentIndex < 0) {
                    if (!unpackPageHeader(stream, headerArray, header)) {
                        return false
                    }
                    segmentIndex = 0
                    if (header.type and 1 == 1 && packetArray.limit() == 0) {
                        calculatePacketSize(header, segmentIndex, holder)
                        segmentIndex += holder.segmentCount
                    }
                    currentSegmentIndex = segmentIndex
                }
                calculatePacketSize(header, currentSegmentIndex, holder)
                segmentIndex = currentSegmentIndex + holder.segmentCount
                if (holder.size > 0) {
                    stream.peekFully(packetArray.data, packetArray.limit(), holder.size)
                    packetArray.setLimit(packetArray.limit() + holder.size)
                    packetComplete = header.laces[segmentIndex - 1] != 255
                }
                currentSegmentIndex =
                    if (segmentIndex == header.pageSegmentCount) -1 else segmentIndex
            }
            return true
        }

        private fun calculatePacketSize(
            header: PageHeader,
            startSegmentIndex: Int,
            holder: PacketInfoHolder
        ) {
            holder.segmentCount = 0
            holder.size = 0
            var segmentLength: Int
            while (startSegmentIndex + holder.segmentCount < header.pageSegmentCount) {
                segmentLength = header.laces[startSegmentIndex + holder.segmentCount++]
                holder.size += segmentLength
                if (segmentLength != 255) {
                    break
                }
            }
        }

        @Throws(IOException::class, InterruptedException::class)
        private fun unpackPageHeader(
            stream: PeekInputStream,
            headerArray: ParsableByteArray,
            header: PageHeader
        ): Boolean {
            headerArray.reset()
            header.reset()
            if (stream.peekFully(headerArray.data, 0, 27, true)) {
                if (headerArray.readUnsignedByte() == 79 && headerArray.readUnsignedByte() == 103 && headerArray.readUnsignedByte() == 103 && headerArray.readUnsignedByte() == 83) {
                    header.revision = headerArray.readUnsignedByte()
                    return if (header.revision != 0) {
                        false
                    } else {
                        header.type = headerArray.readUnsignedByte()
                        header.granulePosition = headerArray.readLittleEndianLong()
                        header.streamSerialNumber = headerArray.readLittleEndianUnsignedInt()
                        header.pageSequenceNumber = headerArray.readLittleEndianUnsignedInt()
                        header.pageChecksum = headerArray.readLittleEndianUnsignedInt()
                        header.pageSegmentCount = headerArray.readUnsignedByte()
                        headerArray.reset()
                        header.headerSize = 27 + header.pageSegmentCount
                        stream.peekFully(headerArray.data, 0, header.pageSegmentCount)
                        for (i in 0 until header.pageSegmentCount) {
                            header.laces[i] = headerArray.readUnsignedByte()
                            header.bodySize += header.laces[i]
                        }
                        true
                    }
                }
            }
            return false
        }

        private fun unpackIdHeader(scratch: ParsableByteArray, header: IdHeader) {
            scratch.reset()
            if (scratch.readUnsignedByte() == 1) {
                if (scratch.readUnsignedByte() == 118 && scratch.readUnsignedByte() == 111 && scratch.readUnsignedByte() == 114 && scratch.readUnsignedByte() == 98 && scratch.readUnsignedByte() == 105 && scratch.readUnsignedByte() == 115) {
                    header.reset()
                    header.version = scratch.readLittleEndianUnsignedInt()
                    header.audioChannels = scratch.readUnsignedByte()
                    header.audioSampleRate = scratch.readLittleEndianUnsignedInt()
                    header.bitRateMaximum = scratch.readLittleEndianInt()
                    header.bitRateNominal = scratch.readLittleEndianInt()
                    header.bitRateMinimum = scratch.readLittleEndianInt()
                    val blockSize = scratch.readUnsignedByte()
                    header.blockSize0 = Math.pow(2.0, (blockSize and 15).toDouble()).toInt()
                    header.blockSize1 = Math.pow(2.0, (blockSize shr 4).toDouble()).toInt()
                }
            }
        }

        private fun unpackCommentHeader(
            scratch: ParsableByteArray,
            header: CommentHeader,
            listener: MetadataListener
        ) {
            scratch.reset()
            if (scratch.readUnsignedByte() == 3) {
                if (scratch.readUnsignedByte() == 118 && scratch.readUnsignedByte() == 111 && scratch.readUnsignedByte() == 114 && scratch.readUnsignedByte() == 98 && scratch.readUnsignedByte() == 105 && scratch.readUnsignedByte() == 115) {
                    header.reset()
                    val vendorLength = scratch.readLittleEndianUnsignedInt().toInt()
                    var length = 7 + 4
                    header.vendor = scratch.readString(vendorLength)
                    length += header.vendor!!.length
                    val commentListLen = scratch.readLittleEndianUnsignedInt()
                    length += 4
                    var len: Int
                    var comment: String
                    var i = 0
                    while (i.toLong() < commentListLen) {
                        len = scratch.readLittleEndianUnsignedInt().toInt()
                        length += 4
                        comment = scratch.readString(len)
                        unPackComment(comment, header.comments)
                        length += comment.length
                        ++i
                    }
                    header.length = length
                    metadataReceived(header.comments["ARTIST"], header.comments["TITLE"], listener)
                }
            }
        }

        private fun unPackComment(comment: String, commentContainer: HashMap<String, String>) {
            if (comment.contains("=")) {
                val kv = comment.split("=").toTypedArray()
                if (kv.size == 2) {
                    commentContainer[kv[0]] = kv[1]
                } else if (kv.size == 1) {
                    commentContainer[kv[0]] = ""
                }
            }
        }

        private fun metadataReceived(artist: String?, song: String?, listener: MetadataListener?) {
            Log.i(TAG, "Metadata received: ")
            Log.i(TAG, "Artist: $artist")
            Log.i(TAG, "Song: $song")
            listener?.onMetadataReceived(artist, song, "")
        }
    }
}