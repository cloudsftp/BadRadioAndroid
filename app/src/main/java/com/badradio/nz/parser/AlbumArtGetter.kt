package com.badradio.nz.parser

import com.badradio.nz.utilities.Tools.getJSONObjectFromUrl
import org.json.JSONException
import com.squareup.picasso.Picasso
import com.badradio.nz.R
import android.graphics.Bitmap
import com.squareup.picasso.Picasso.LoadedFrom
import android.graphics.drawable.Drawable
import android.util.Log
import com.squareup.picasso.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.net.URLEncoder

object AlbumArtGetter {
    fun getImageForQuery(query: String?, callback: AlbumCallback) {

        val jsonObject = getJSONObjectFromUrl("https://itunes.apple.com/search?term=" + URLEncoder.encode(query) + "&media=music&limit=1")
        try {
            if (URLEncoder.encode(query) == "null+null") {
                Log.v("INFO", "No Metadata Received")
            } else if (jsonObject != null && jsonObject.has("results") && jsonObject.getJSONArray("results").length() > 0) {
                val track = jsonObject.getJSONArray("results").getJSONObject(0)
                var url = track.getString("artworkUrl100")
                url = url.replace("100x100bb.jpg", "500x500bb.jpg")

                runBlocking {
                    launch(Dispatchers.Main) {
                        Picasso.get()
                                .load(url)
                                .placeholder(R.drawable.ic_adjust_black_24dp)
                                .into(object : Target {
                                    override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                                        callback.finished(bitmap)
                                    }

                                    override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {
                                        callback.finished(null)
                                    }
                                    override fun onPrepareLoad(placeHolderDrawable: Drawable) {
                                    }
                                })
                    }
                }
            } else {
                Log.v("INFO", "No items in Album Art Request")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        callback.finished(null)
    }

    interface AlbumCallback {
        fun finished(b: Bitmap?)
    }
}