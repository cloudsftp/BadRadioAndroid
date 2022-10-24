package com.badradio.badradio.player

import android.net.Uri
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener

class StreamDataSource(streamURL: String) : DataSource {

    private val uri = Uri.parse(streamURL)

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        TODO("Not yet implemented")
    }

    override fun addTransferListener(transferListener: TransferListener) {
        TODO("Not yet implemented")
    }

    override fun open(dataSpec: DataSpec): Long {
        TODO("Not yet implemented")
    }

    override fun getUri(): Uri? {
        return uri
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}