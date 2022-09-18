package com.badradio.nz.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.badradio.nz.Adapter.RadioListAdapter;
import com.badradio.nz.Models.RadioList;
import com.badradio.nz.R;
import com.badradio.nz.metadata.Metadata;
import com.badradio.nz.services.PlaybackStatus;
import com.badradio.nz.services.RadioManager;
import com.badradio.nz.utilities.Tools;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LandingPageActivity extends AppCompatActivity implements Tools.EventListener {

    private RecyclerView rv_stationist;
    private RecyclerView.Adapter mAdapter;
    private List<RadioList> radioLists;
    private static final String URL_DATA = "https://badradio.nz/streamingassets/stations.json";
    private ImageButton img_about;
    private RadioManager radioManager;
    private RelativeLayout nowPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        //Initializing views
        radioManager = RadioManager.with();
        rv_stationist = (RecyclerView) findViewById(R.id.rv_radioList);
        rv_stationist.setHasFixedSize(true);
        rv_stationist.setLayoutManager(new LinearLayoutManager(this));
        radioLists = new ArrayList<>();
        img_about = (ImageButton) findViewById(R.id.img_about);
        nowPlaying = (RelativeLayout) findViewById(R.id.rlt_now_playing);
        nowPlaying.setVisibility(View.INVISIBLE);

        //About buttom(hamburger button)
        img_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingPageActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });


        //NowPlaying Button
        nowPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sending to player activity
                Intent intent = new Intent(LandingPageActivity.this, PlayerActivity.class);
                //sending intent data
                intent.putExtra("type", "nowPlaying");
                startActivity(intent);
            }
        });

        //Checking for permissions.
        requestStoragePermission();

        //Loading stations into recyclerview.
        loadStation();

        //Animation for music bar
        AnimationDrawable animationDrawable = new AnimationDrawable();
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.np1), 80);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.np2), 80);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.np3), 80);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.np4), 80);
        ImageView imageView = (ImageView) findViewById(R.id.imageAnim);
        imageView.setImageDrawable(animationDrawable);

        //Checking whether radio is playing and upating view accordingly
        if (isPlaying()) {

            //Radio playing
            nowPlaying.setVisibility(View.VISIBLE);
            SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
            String song = sharedPreferences.getString("song", "");
            String artist = sharedPreferences.getString("artist", "");
            String station = sharedPreferences.getString("station", "");
            TextView nowPlayingTitle = (TextView) findViewById(R.id.tv_nowPlaying);
            nowPlayingTitle.setText(station + song);
            animationDrawable.start();

        } else {

            //Radio not playing
            nowPlaying.setVisibility(View.GONE);
            animationDrawable.stop();
            TextView nowPlayingTitle = (TextView) findViewById(R.id.tv_nowPlaying);
            nowPlayingTitle.setText("Choose a station above to begin.");

        }

    }

    private boolean isPlaying() {
        return (null != radioManager && null != RadioManager.getService() && RadioManager.getService().isPlaying());
    }

    private void requestStoragePermission() {
        //Checking for permissions
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.MODIFY_AUDIO_SETTINGS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {

                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    private void loadStation() {

        //Getting radio list and loading into recyclerview.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("station");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject o = jsonArray.getJSONObject(i);
                        RadioList radioList = new RadioList(o.getString("name"),
                                o.getString("streamURL"),
                                o.getString("imageURL"),
                                o.getString("desc"),
                                o.getString("longDesc"));


                        radioLists.add(radioList);

                    }
                    mAdapter = new RadioListAdapter(radioLists, getApplicationContext());
                    rv_stationist.setAdapter(mAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onEvent(String status) {

        //Checking status of radio and updating views
        AnimationDrawable animationDrawable = new AnimationDrawable();
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.np1), 80);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.np2), 80);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.np3), 80);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.np4), 80);
        ImageView imageView = (ImageView) findViewById(R.id.imageAnim);
        imageView.setImageDrawable(animationDrawable);

        switch (status) {

            case PlaybackStatus.PLAYING:
                //Playing
                nowPlaying.setVisibility(View.VISIBLE);
                animationDrawable.start();
                break;

            case PlaybackStatus.PAUSED:
                //Paused
                nowPlaying.setVisibility(View.INVISIBLE);
                animationDrawable.stop();
                break;

            case PlaybackStatus.STOPPED:
                //Stopped
                nowPlaying.setVisibility(View.INVISIBLE);
                animationDrawable.stop();
                TextView nowPlayingTitle = (TextView) findViewById(R.id.tv_nowPlaying);
                nowPlayingTitle.setText("Choose a station above to begin.");
                break;

        }

    }

    @Override
    public void onAudioSessionId(Integer i) {

    }

    public void updateMediaInfoFromBackground(String artist, String song, String channel, Bitmap image) {
        //Updating current playing station and song
        TextView nowPlayingTitle = (TextView) findViewById(R.id.tv_nowPlaying);
        if (artist != null) {
            nowPlayingTitle.setText(channel + ":" + song);

        } else if (channel != null) {
            nowPlayingTitle.setText("Now playing " + channel);

        } else {
            nowPlayingTitle.setText("Choose a station above to begin");
        }

    }


    @Override
    public void onMetaDataReceived(Metadata meta, Bitmap image) {

        //Getting currently playing station and song details
        String artist = null;
        String Song = null;
        String channel = null;
        String url = null;
        if (meta != null && meta.getArtist() != null)
            artist = meta.getArtist();
        Song = meta.getSong();
        channel = meta.getStation();
        url = meta.getUrl();
        updateMediaInfoFromBackground(artist, Song, channel, image);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Tools.registerAsListener(this);

    }
}
