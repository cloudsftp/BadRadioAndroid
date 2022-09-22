package com.badradio.nz.metadata

import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.Throws

internal open class PeekInputStream private constructor(
    private val stream: InputStream,
    private var position: Long
) : InputStream() {
    constructor(`in`: InputStream) : this(`in`, 0)

    private var peekBuffer = ByteArray(8192)
    private var peekBufferPosition = 0
    private var peekBufferLength = 0

    @Throws(IOException::class)
    override fun read(): Int {
        var bytesRead = this.readFromPeekBuffer()
        if (bytesRead == 0) {
            try {
                bytesRead = this.readFromStream()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        commitBytesRead(bytesRead)
        return bytesRead
    }

    @Throws(IOException::class)
    override fun read(target: ByteArray, offset: Int, length: Int): Int {
        var bytesRead = this.readFromPeekBuffer(target, offset, length)
        if (bytesRead == 0) {
            try {
                bytesRead = this.readFromStream(target, offset, length, 0, true)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        commitBytesRead(bytesRead)
        return bytesRead
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun readFully(
        target: ByteArray,
        offset: Int,
        length: Int,
        allowEndOfInput: Boolean
    ): Boolean {
        var bytesRead: Int
        bytesRead = this.readFromPeekBuffer(target, offset, length)
        while (bytesRead < length && bytesRead != -1) {
            bytesRead = this.readFromStream(target, offset, length, bytesRead, allowEndOfInput)
        }
        commitBytesRead(bytesRead)
        return bytesRead != -1
    }

    @Throws(IOException::class, InterruptedException::class)
    fun readFully(target: ByteArray, offset: Int, length: Int) {
        this.readFully(target, offset, length, false)
    }

    @Throws(IOException::class, InterruptedException::class)
    fun skip(length: Int): Int {
        var bytesSkipped = skipFromPeekBuffer(length)
        if (bytesSkipped == 0) {
            bytesSkipped =
                this.readFromStream(SCRATCH_SPACE, 0, Math.min(length, SCRATCH_SPACE.size), 0, true)
        }
        commitBytesRead(bytesSkipped)
        return bytesSkipped
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun skipFully(length: Int, allowEndOfInput: Boolean): Boolean {
        var bytesSkipped: Int
        bytesSkipped = skipFromPeekBuffer(length)
        while (bytesSkipped < length && bytesSkipped != -1) {
            bytesSkipped = this.readFromStream(
                SCRATCH_SPACE,
                -bytesSkipped,
                Math.min(length, bytesSkipped + SCRATCH_SPACE.size),
                bytesSkipped,
                allowEndOfInput
            )
        }
        commitBytesRead(bytesSkipped)
        return bytesSkipped != -1
    }

    @Throws(IOException::class, InterruptedException::class)
    fun skipFully(length: Int) {
        this.skipFully(length, false)
    }

    @Throws(IOException::class, InterruptedException::class)
    fun peekFully(target: ByteArray?, offset: Int, length: Int, allowEndOfInput: Boolean): Boolean {
        return if (!this.advancePeekPosition(length, allowEndOfInput)) {
            false
        } else {
            System.arraycopy(
                peekBuffer,
                peekBufferPosition - length,
                target,
                offset,
                length
            )
            true
        }
    }

    @Throws(IOException::class, InterruptedException::class)
    fun peekFully(target: ByteArray?, offset: Int, length: Int) {
        this.peekFully(target, offset, length, false)
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun advancePeekPosition(length: Int, allowEndOfInput: Boolean): Boolean {
        ensureSpaceForPeek(length)
        var bytesPeeked = Math.min(peekBufferLength - peekBufferPosition, length)
        peekBufferLength += length - bytesPeeked
        do {
            if (bytesPeeked >= length) {
                peekBufferPosition += length
                return true
            }
            bytesPeeked = this.readFromStream(
                peekBuffer,
                peekBufferPosition,
                length,
                bytesPeeked,
                allowEndOfInput
            )
        } while (bytesPeeked != -1)
        return false
    }

    @Throws(IOException::class, InterruptedException::class)
    fun advancePeekPosition(length: Int) {
        this.advancePeekPosition(length, false)
    }

    fun resetPeekPosition() {
        peekBufferPosition = 0
    }

    val peekPosition: Long
        get() = position + peekBufferPosition.toLong()

    private fun ensureSpaceForPeek(length: Int) {
        val requiredLength = peekBufferPosition + length
        if (requiredLength > peekBuffer.size) {
            peekBuffer = Arrays.copyOf(peekBuffer, Math.max(peekBuffer.size * 2, requiredLength))
        }
    }

    private fun skipFromPeekBuffer(length: Int): Int {
        val bytesSkipped = Math.min(peekBufferLength, length)
        updatePeekBuffer(bytesSkipped)
        return bytesSkipped
    }

    private fun readFromPeekBuffer(): Int {
        return if (peekBufferLength == 0) {
            0
        } else {
            val bytesRead = peekBuffer[0].toInt()
            updatePeekBuffer(1)
            bytesRead
        }
    }

    private fun readFromPeekBuffer(target: ByteArray, offset: Int, length: Int): Int {
        return if (peekBufferLength == 0) {
            0
        } else {
            val peekBytes = Math.min(peekBufferLength, length)
            System.arraycopy(peekBuffer, 0, target, offset, peekBytes)
            updatePeekBuffer(peekBytes)
            peekBytes
        }
    }

    private fun updatePeekBuffer(bytesConsumed: Int) {
        peekBufferLength -= bytesConsumed
        peekBufferPosition = 0
        System.arraycopy(peekBuffer, bytesConsumed, peekBuffer, 0, peekBufferLength)
    }

    @Throws(InterruptedException::class, IOException::class)
    private fun readFromStream(): Int {
        return if (Thread.interrupted()) {
            throw InterruptedException()
        } else {
            stream.read()
        }
    }

    @Throws(InterruptedException::class, IOException::class)
    private fun readFromStream(
        target: ByteArray,
        offset: Int,
        length: Int,
        bytesAlreadyRead: Int,
        allowEndOfInput: Boolean
    ): Int {
        return if (Thread.interrupted()) {
            throw InterruptedException()
        } else {
            val bytesRead =
                stream.read(target, offset + bytesAlreadyRead, length - bytesAlreadyRead)
            if (bytesRead == -1) {
                if (bytesAlreadyRead == 0 && allowEndOfInput) {
                    -1
                } else {
                    throw EOFException()
                }
            } else {
                bytesAlreadyRead + bytesRead
            }
        }
    }

    private fun commitBytesRead(bytesRead: Int) {
        if (bytesRead != -1) {
            position += bytesRead.toLong()
        }
    }

    companion object {
        private val SCRATCH_SPACE = ByteArray(4096)
    }
}