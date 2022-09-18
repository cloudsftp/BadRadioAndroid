package com.badradio.nz.parser;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.badradio.nz.R;
import com.badradio.nz.utilities.Tools;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class AlbumArtGetter {

    public static String getImageForQuery(final String query, final AlbumCallback callback, final Context context) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... unused) {
                JSONObject jsonObject = Tools.getJSONObjectFromUrl("https://itunes.apple.com/search?term=" + URLEncoder.encode(query) + "&media=music&limit=1");
                try {
                    if (URLEncoder.encode(query).equals("null+null")) {
                        Log.v("INFO", "No Metadata Received");
                    } else if (jsonObject != null && jsonObject.has("results") && jsonObject.getJSONArray("results").length() > 0) {
                        JSONObject track = jsonObject.getJSONArray("results").getJSONObject(0);
                        String url = track.getString("artworkUrl100");
                        return url.replace("100x100bb.jpg", "500x500bb.jpg");
                    } else {
                        Log.v("INFO", "No items in Album Art Request");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(final String imageUrl) {
                if (imageUrl != null) {
                    Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_adjust_black_24dp)
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                    callback.finished(bitmap);
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }


                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
                } else {
                    callback.finished(null);
                }
            }
        }.execute();


        return null;
    }

    public interface AlbumCallback {
        void finished(Bitmap b);
    }
}

