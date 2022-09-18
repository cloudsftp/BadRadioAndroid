package com.badradio.nz.parser

import kotlin.Throws
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.util.*

class ASXParser {
    fun getRawUrl(url: String): LinkedList<String>? {
        val murls: LinkedList<String>? = null
        try {
            return getRawUrl(getConnection(url))
        } catch (e: MalformedURLException) {
        } catch (e: IOException) {
        }
        return murls
    }

    fun getRawUrl(conn: URLConnection): LinkedList<String> {
        val br: BufferedReader
        var murl: String? = null
        var murls: LinkedList<String>? = null
        murls = LinkedList()
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
        //murls.add(conn.getURL().toString());
        return murls
    }

    private fun parseLine(line: String?): String? {
        if (line == null) {
            return null
        }
        var trimmed = line.trim { it <= ' ' }
        if (trimmed.startsWith("<ref href=\"")) {
            trimmed = trimmed.replace("<ref href=\"", "")
            trimmed = trimmed.replace("/>", "").trim { it <= ' ' }
            if (trimmed.endsWith("\"")) {
                trimmed = trimmed.replace("\"", "")
                Log.v("INFO", "ASX: $trimmed")
                return trimmed
            }
        }
        return ""
    }

    @Throws(IOException::class)
    private fun getConnection(url: String): URLConnection {
        return URL(url).openConnection()
    }
}