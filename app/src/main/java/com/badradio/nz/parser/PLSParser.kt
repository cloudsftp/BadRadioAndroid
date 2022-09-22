package com.badradio.nz.parser

import kotlin.Throws
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.util.*

class PLSParser {
    fun getRawUrl(url: String): LinkedList<String> {
        val murls = LinkedList<String>()
        try {
            return getRawUrl(getConnection(url))
        } catch (e: MalformedURLException) {
        } catch (e: IOException) {
        }
        murls.add(url)
        return murls
    }

    fun getRawUrl(conn: URLConnection): LinkedList<String> {
        val br: BufferedReader
        var murl: String? = null
        val murls = LinkedList<String>()
        try {
            br = BufferedReader(InputStreamReader(conn.getInputStream()))
            while (true) {
                try {
                    val line = br.readLine() ?: break
                    murl = parseLine(line)
                    if (murl != null && murl != "") {
                        murls.add(murl)
                    }
                } catch (e: IOException) {
                }
            }
        } catch (e: MalformedURLException) {
        } catch (e: IOException) {
        }
        murls.add(conn.url.toString())
        return murls
    }

    private fun parseLine(line: String?): String? {
        if (line == null) {
            return null
        }
        val trimmed = line.trim { it <= ' ' }
        return if (trimmed.indexOf("http") >= 0) {
            trimmed.substring(trimmed.indexOf("http"))
        } else ""
    }

    @Throws(IOException::class)
    private fun getConnection(url: String): URLConnection {
        return URL(url).openConnection()
    }
}