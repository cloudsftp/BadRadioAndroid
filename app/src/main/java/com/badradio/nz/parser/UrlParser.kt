package com.badradio.nz.parser

import android.annotation.SuppressLint
import android.util.Log
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.util.*

object UrlParser {
    @SuppressLint("DefaultLocale") // Does not matter, no text but URL
    fun getUrl(url: String): String? {
        val mUrl = url.uppercase(Locale.getDefault())
        if (mUrl.endsWith(".FLAC")) {
            return url
        } else if (mUrl.endsWith(".MP3")) {
            return url
        } else if (mUrl.endsWith(".WAV")) {
            return url
        } else if (mUrl.endsWith(".M4A")) {
            return url
        } else if (mUrl.endsWith(".PLS")) {
            return url
            /**
             * Since our new player can handle PLS directly, no need to parse.
             * PLSParser mPls = new PLSParser();
             * LinkedList<String> urls = mPls.getRawUrl(url);
             * if(urls.size()>0)
             * {
             * return urls.get(0);
             * }</String> */
        } else if (mUrl.endsWith(".M3U")) {
            val mM3u = M3UParser()
            val urls = mM3u.getRawUrl(url)
            if (urls!!.size > 0) {
                return urls[0]
            }
        } else if (mUrl.endsWith(".ASX")) //|| url.toUpperCase().endsWith("WMA_UK_CONCRETE"))
        {
            val mAsx = ASXParser()
            val urls = mAsx.getRawUrl(url)
            if (urls!!.size > 0) {
                return urls[0]
            }
        } else {
            val conn = getConnection(url)
            if (conn != null) {
                val mContentDisposition = conn.getHeaderField("Content-Disposition")
                Log.v("INFO", "Requesting: " + url + " Headers: " + conn.headerFields)
                var mContentType = conn.contentType
                if (mContentType != null) {
                    mContentType = mContentType.uppercase(Locale.getDefault())
                }
                if (mContentDisposition != null && mContentDisposition.uppercase(Locale.getDefault()).endsWith("M3U")) {
                    val m3u = M3UParser()
                    val urls = m3u.getRawUrl(conn)
                    if (urls!!.size > 0) {
                        return urls.first
                    }
                } else if (mContentType != null && mContentType.contains("AUDIO/X-SCPLS")) {
                    return url
                    /** Since our new player can handle PLS directly, no need to parse.
                     *
                     * PLSParser pls = new PLSParser();
                     * LinkedList<String> urls = pls.getRawUrl(conn);
                     * if(urls.size()> 0)
                     * {
                     * return urls.getFirst();
                     * }</String> */
                } else if (mContentType != null && mContentType.contains("VIDEO/X-MS-ASF")) {
                    val asx = ASXParser()
                    var urls = asx.getRawUrl(url)
                    if (urls!!.size > 0) {
                        return urls[0]
                    }
                    val pls = PLSParser()
                    urls = pls.getRawUrl(url)
                    if (urls.size > 0) {
                        return urls[0]
                    }
                } else if (mContentType != null && mContentType.contains("AUDIO/MPEG")) {
                    return url
                } else if (mContentType != null && mContentType.contains("AUDIO/X-MPEGURL")) {
                    val m3u = M3UParser()
                    val urls = m3u.getRawUrl(url)
                    if (urls!!.size > 0) {
                        return urls[0]
                    }
                } else {
                    //not found
                }
            }
        }
        return url
    }

    private fun getConnection(url: String): URLConnection? {
        val mUrl: URLConnection
        try {
            mUrl = URL(url).openConnection()
            return mUrl
        } catch (e: MalformedURLException) {
        } catch (e: IOException) {
        }
        return null
    }
}