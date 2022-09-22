package com.badradio.nz.metadata

import android.net.Uri
import kotlin.Throws
import com.google.android.exoplayer2.upstream.HttpDataSource.HttpDataSourceException
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidContentTypeException
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.Predicate
import okhttp3.*
import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.io.InterruptedIOException
import java.util.concurrent.atomic.AtomicReference
import kotlin.collections.HashMap

internal class ShoutcastDataSource(
    private val callFactory: Call.Factory,
    private val userAgent: String?,
    private val contentTypePredicate: Predicate<String?>?,
    private val transferListener: TransferListener<in ShoutcastDataSource?>?,
    private val shoutcastMetadataListener: ShoutcastMetadataListener?,
    private val cacheControl: CacheControl?
) : HttpDataSource, MetadataListener {
    private val requestProperties: HashMap<String, String> = HashMap()

    private lateinit var dataSpec: DataSpec
    private lateinit var response: Response
    private var responseByteStream: InputStream? = null
    private var opened = false

    private var bytesToSkip: Long = 0
    private var bytesToRead: Long = 0
    private var bytesSkipped: Long = 0
    private var bytesRead: Long = 0

    companion object {
        private const val MP3 = "audio/mpeg"
        private const val AAC = "audio/aac"
        private const val AACP = "audio/aacp"
        private const val OGG = "application/ogg"
        private const val ICY_METADATA = "Icy-Metadata"
        private const val ICY_METAINT = "icy-metaint"
        private val skipBufferReference = AtomicReference<ByteArray?>()
    }

    /**
     * @param callFactory An [Call.Factory] for use by the source.
     * @param userAgent The User-Agent string that should be used.
     * @param contentTypePredicate An optional [Predicate]. If a content type is rejected by the
     * predicate then a InvalidContentTypeException} is thrown from [.open].
     */
    constructor(
        callFactory: Call.Factory, userAgent: String,
        contentTypePredicate: Predicate<String?>
    ) : this(callFactory, userAgent, contentTypePredicate, null, null) {
    }

    /**
     * @param callFactory An [Call.Factory] for use by the source.
     * @param userAgent The User-Agent string that should be used.
     * @param contentTypePredicate An optional [Predicate]. If a content type is rejected by the
     * predicate then a [InvalidContentTypeException] is thrown from
     * [.open].
     * @param transferListener An optional transferListener.
     */
    private constructor(
        callFactory: Call.Factory,
        userAgent: String,
        contentTypePredicate: Predicate<String?>,
        transferListener: TransferListener<in ShoutcastDataSource?>?,
        shoutcastMetadataListener: ShoutcastMetadataListener?
    ) : this(
        callFactory,
        userAgent,
        contentTypePredicate,
        transferListener,
        shoutcastMetadataListener,
        null
    ) {
    }

    override fun getUri(): Uri {
        return Uri.parse(response.request().url().toString())
    }

    override fun getResponseHeaders(): Map<String, List<String>> {
        return response.headers().toMultimap()
    }

    override fun setRequestProperty(name: String, value: String) {
        Assertions.checkNotNull(name)
        Assertions.checkNotNull(value)
        synchronized(requestProperties) { requestProperties.put(name, value) }
    }

    override fun clearRequestProperty(name: String) {
        Assertions.checkNotNull(name)
        synchronized(requestProperties) { requestProperties.remove(name) }
    }

    override fun clearAllRequestProperties() {
        synchronized(requestProperties) { requestProperties.clear() }
    }

    @Throws(HttpDataSourceException::class)
    override fun open(dataSpec: DataSpec): Long {
        this.dataSpec = dataSpec
        bytesRead = 0
        bytesSkipped = 0
        setRequestProperty(ICY_METADATA, "1")
        val request = makeRequest(dataSpec)
        try {
            response = callFactory.newCall(request).execute()
            responseByteStream = getInputStream(response)
        } catch (e: IOException) {
            throw HttpDataSourceException(
                "Unable to connect to " + dataSpec.uri.toString(), e,
                dataSpec, HttpDataSourceException.TYPE_OPEN
            )
        }
        val responseCode = response.code()

        // Check for a valid response code.
        if (!response.isSuccessful) {
            val headers = request.headers().toMultimap()
            closeConnectionQuietly()
            val exception = InvalidResponseCodeException(
                responseCode, headers, dataSpec
            )
            if (responseCode == 416) {
                exception.initCause(DataSourceException(DataSourceException.POSITION_OUT_OF_RANGE))
            }
            throw exception
        }

        // Check for a valid content type.
        val mediaType = response.body()!!.contentType()
        val contentType = mediaType?.toString()
        if (contentTypePredicate != null && !contentTypePredicate.evaluate(contentType)) {
            closeConnectionQuietly()
            throw InvalidContentTypeException(contentType, dataSpec)
        }

        // If we requested a range starting from a non-zero position and received a 200 rather than a
        // 206, then the server does not support partial requests. We'll need to manually skip to the
        // requested position.
        bytesToSkip = if (responseCode == 200 && dataSpec.position != 0L) dataSpec.position else 0

        // Determine the length of the data to be read, after skipping.
        bytesToRead = if (dataSpec.length != C.LENGTH_UNSET.toLong()) {
            dataSpec.length
        } else {
            val contentLength = response.body()!!.contentLength()
            if (contentLength != -1L) contentLength - bytesToSkip else C.LENGTH_UNSET.toLong()
        }
        opened = true
        transferListener?.onTransferStart(this, dataSpec)
        return bytesToRead
    }

    @Throws(HttpDataSourceException::class)
    override fun read(buffer: ByteArray, offset: Int, readLength: Int): Int {
        return try {
            skipInternal()
            readInternal(buffer, offset, readLength)
        } catch (e: IOException) {
            throw HttpDataSourceException(e, dataSpec, HttpDataSourceException.TYPE_READ)
        }
    }

    @Throws(HttpDataSourceException::class)
    override fun close() {
        if (opened) {
            opened = false
            transferListener?.onTransferEnd(this)
            closeConnectionQuietly()
        }
    }

    /**
     * Establishes a connection.
     */
    private fun makeRequest(dataSpec: DataSpec): Request {
        val allowGzip = dataSpec.flags and DataSpec.FLAG_ALLOW_GZIP != 0
        val url = HttpUrl.parse(dataSpec.uri.toString())
        val builder = Request.Builder().url(url)
        if (cacheControl != null) {
            builder.cacheControl(cacheControl)
        }
        synchronized(requestProperties) {
            for ((key, value) in requestProperties) {
                builder.addHeader(key, value)
            }
        }
        builder.addHeader("User-Agent", userAgent)
        if (!allowGzip) {
            builder.addHeader("Accept-Encoding", "identity")
        }
        if (dataSpec.postBody != null) {
            builder.post(RequestBody.create(null, dataSpec.postBody))
        }
        return builder.build()
    }

    @Throws(IOException::class)
    private fun getInputStream(response: Response?): InputStream? {
        val contentType = response!!.header("Content-Type")
        setIcyHeader(response.headers())
        var `in` = response.body()!!.byteStream()
        when (contentType) {
            MP3, AAC, AACP -> {
                val interval = response.header(ICY_METAINT)!!
                    .toInt()
                `in` = IcyInputStream(`in`, interval, this)
            }
            OGG -> `in` = OggInputStream(`in`, this)
        }
        return `in`
    }

    /**
     * Skips any bytes that need skipping. Else does nothing.
     *
     *
     * This implementation is based roughly on `libcore.io.Streams.skipByReading()`.
     *
     * @throws InterruptedIOException If the thread is interrupted during the operation.
     * @throws EOFException If the end of the input stream is reached before the bytes are skipped.
     */
    @Throws(IOException::class)
    private fun skipInternal() {
        if (bytesSkipped == bytesToSkip) {
            return
        }

        // Acquire the shared skip buffer.
        var skipBuffer = skipBufferReference.getAndSet(null)
        if (skipBuffer == null) {
            skipBuffer = ByteArray(4096)
        }
        while (bytesSkipped != bytesToSkip) {
            val readLength = Math.min(bytesToSkip - bytesSkipped, skipBuffer.size.toLong()).toInt()
            val read = responseByteStream!!.read(skipBuffer, 0, readLength)
            if (Thread.interrupted()) {
                throw InterruptedIOException()
            }
            if (read == -1) {
                throw EOFException()
            }
            bytesSkipped += read.toLong()
            transferListener?.onBytesTransferred(this, read)
        }

        // Release the shared skip buffer.
        skipBufferReference.set(skipBuffer)
    }

    /**
     * Reads up to `length` bytes of data and stores them into `buffer`, starting at
     * index `offset`.
     *
     *
     * This method blocks until at least one byte of data can be read, the end of the opened range is
     * detected, or an exception is thrown.
     *
     * @param buffer The buffer into which the read data should be stored.
     * @param offset The start offset into `buffer` at which data should be written.
     * @param readLength The maximum number of bytes to read.
     * @return The number of bytes read, or [C.RESULT_END_OF_INPUT] if the end of the opened
     * range is reached.
     * @throws IOException If an error occurs reading from the source.
     */
    @Throws(IOException::class)
    private fun readInternal(buffer: ByteArray, offset: Int, readLength: Int): Int {
        var readLength = readLength
        if (readLength == 0) {
            return 0
        }
        if (bytesToRead != C.LENGTH_UNSET.toLong()) {
            val bytesRemaining = bytesToRead - bytesRead
            if (bytesRemaining == 0L) {
                return C.RESULT_END_OF_INPUT
            }
            readLength = readLength.toLong().coerceAtMost(bytesRemaining).toInt()
        }
        val read = responseByteStream!!.read(buffer, offset, readLength)
        if (read == -1) {
            if (bytesToRead != C.LENGTH_UNSET.toLong()) {
                // End of stream reached having not read sufficient data.
                throw EOFException()
            }
            return C.RESULT_END_OF_INPUT
        }
        bytesRead += read.toLong()
        transferListener?.onBytesTransferred(this, read)
        return read
    }

    /**
     * Closes the current connection quietly, if there is one.
     */
    private fun closeConnectionQuietly() {
        response.body()!!.close()
    }

    private inner class IcyHeader {
        var channels: String? = null
        var bitrate: String? = null
        var station: String? = null
        var genre: String? = null
        var url: String? = null
    }
    private var icyHeader: IcyHeader? = null


    private fun setIcyHeader(headers: Headers) {
        if (icyHeader == null) {
            icyHeader = IcyHeader()
        }
        icyHeader!!.station = headers["icy-name"]
        icyHeader!!.url = headers["icy-url"]
        icyHeader!!.genre = headers["icy-genre"]
        icyHeader!!.channels = headers["icy-channels"]
        icyHeader!!.bitrate = headers["icy-br"]
    }

    override fun onMetadataReceived(artist: String?, song: String?, show: String?) {
        if (shoutcastMetadataListener != null) {
            val metadata = Metadata(
                artist,
                song,
                show,
                icyHeader!!.channels,
                icyHeader!!.bitrate,
                icyHeader!!.station,
                icyHeader!!.genre,
                icyHeader!!.url
            )
            shoutcastMetadataListener.onMetadataReceived(metadata)
        }
    }
}

class ShoutcastDataSourceFactory private constructor(
    private val callFactory: Call.Factory,
    private val userAgent: String,
    private val transferListener: TransferListener<in DataSource?>,
    private val shoutcastMetadataListener: ShoutcastMetadataListener,
    private val cacheControl: CacheControl?
) : HttpDataSource.BaseFactory() {
    constructor(
        callFactory: Call.Factory, userAgent: String,
        transferListener: TransferListener<in DataSource?>,
        shoutcastMetadataListener: ShoutcastMetadataListener
    ) : this(callFactory, userAgent, transferListener, shoutcastMetadataListener, null) {
    }

    override fun createDataSourceInternal(requestProperties: HttpDataSource.RequestProperties): HttpDataSource {
        return ShoutcastDataSource(
            callFactory,
            userAgent,
            null,
            transferListener,
            shoutcastMetadataListener,
            cacheControl
        )
    }
}

interface ShoutcastMetadataListener {
    fun onMetadataReceived(data: Metadata)
}
