package com.badradio.nz.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.badradio.nz.Adapter.RadioListAdapter;
import com.badradio.nz.Config;
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
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PlayerActivity extends AppCompatActivity implements Tools.EventListener {
    private ImageView img_Station;
    private ImageButton img_Back, img_mute, img_volume, img_play, img_stop, img_share, img_info;
    private ProgressBar progressBar;
    private AudioManager audioManager;


    private RadioManager radioManager;
    private String urlToPlay;
    private String StationImage;
    private String StationName;
    private String StationDesc;
    private String StationLongDesc;

    private int current = 0;
    private int current2 = 0;

    private List<RadioList> radioLists;
    private static final String URL_DATA = "https://badradio.nz/streamingassets/stations.json";
    private ImageButton img_about;
    private RelativeLayout nowPlaying;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //initializing views

        radioManager = RadioManager.with();
        img_Station = (ImageView) findViewById(R.id.img_station_playing);
        img_info = (ImageButton) findViewById(R.id.imgBtn_info);
        img_share = (ImageButton) findViewById(R.id.imgBtn_share);
        img_play = (ImageButton) findViewById(R.id.imgBtn_play);
        img_stop = (ImageButton) findViewById(R.id.imgBtn_stop);
        img_mute = (ImageButton) findViewById(R.id.imgBtn_mute);
        img_volume = (ImageButton) findViewById(R.id.imgBtn_volumeUp);
        radioLists = new ArrayList<>();
        img_about = (ImageButton) findViewById(R.id.img_about);

        //Checking for permissions.
        requestStoragePermission();

        //Loading stations into recyclerview.
        loadStation();

        //About buttom(hamburger button)
        img_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        //Info page button
        img_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sending to info page
                Intent intent = new Intent(PlayerActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });

        //Play or pause button
        img_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopPlaying();

                //saving station details to show currently playing station
                SharedPreferences pref = getApplicationContext().getSharedPreferences("Live", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("url", urlToPlay);
                editor.putString("StationImage", StationImage);
                editor.putString("StationName", StationName);
                editor.putString("StationDesc", StationDesc);
                editor.putString("StationLongDesc", StationLongDesc);
                editor.commit();


            }
        });


        //Mute button
        img_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Muting volume
                volumeMute();
            }
        });

        //Volume full button
        img_volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setting volume to full
                volumeFull();
            }
        });

        //Stop Button
        img_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stopping radio
                stopPlaying();
            }
        });


        //Share button
        img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp(getApplicationContext());
            }
        });



    }

    private void volumeMute() {

        SeekBar volumeBar = (SeekBar) findViewById(R.id.seekBar);

        //checking whether mute button is on or off

        if (current == 0) {

            //mute button in off state
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            img_mute.setImageResource(R.drawable.ic_volume_off_blue_24dp);
            img_volume.setImageResource(R.drawable.ic_volume_up_black_24dp);
            volumeBar.setProgress(0);
            current = 1;
            current2 = 0;
        } else if (current == 1) {

            //mute button in on state

            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Config.DEFAULT_VOLUME, 0);
            int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            volumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeBar.setProgress(volume);
            img_mute.setImageResource(R.drawable.ic_volume_off_black_24dp);
            current = 0;


        }


    }

    private void stopPlaying() {

        //Stopiing radio service
        radioManager.stopServices();

        //updating button and song and artist textviews
        img_play.setImageResource(R.drawable.btnplay);
        TextView tvArtist = (TextView) findViewById(R.id.tv_Artist);
        TextView tvSong = (TextView) findViewById(R.id.tv_songName);
        tvSong.setText(StationName);
        tvArtist.setText(StationDesc);

        //Changing status as stopped
        String status = PlaybackStatus.STOPPED;
        Tools.onEvent(status);
    }

    private void volumeFull() {

        SeekBar volumeBar = (SeekBar) findViewById(R.id.seekBar);

        //checking if full volume button is on or off

        if (current2 == 0) {

            //full volume button off
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            img_mute.setImageResource(R.drawable.ic_volume_off_black_24dp);
            img_volume.setImageResource(R.drawable.ic_volume_up_blue_24dp);
            volumeBar.setProgress(100);
            current2 = 1;
            current = 0;
        } else if (current2 == 1) {

            //full volume button on
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Config.DEFAULT_VOLUME, 0);
            img_volume.setImageResource(R.drawable.ic_volume_up_black_24dp);
            current2 = 0;
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Config.DEFAULT_VOLUME, 0);
            int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            volumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeBar.setProgress(volume);

        }


    }

    public static void shareApp(Context context) {

        //Sharing app
        final String appPackageName = context.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: https://play.google.com/store/apps/details?id=" + appPackageName);
        sendIntent.setType("text/plain");
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(sendIntent);
    }

    private void initVolumeBar() {

        //initializing volume bar
        SeekBar volumeBar = (SeekBar) findViewById(R.id.seekBar);

        //getting current volume and updating it on seekbar
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setProgress(volume);
        volumeBar.setMax(15);
        if (volume==0){
            img_mute.setImageResource(R.drawable.ic_volume_off_blue_24dp);
        }else if (volume==15){
            img_volume.setImageResource(R.drawable.ic_volume_up_blue_24dp);
        }else {
            img_volume.setImageResource(R.drawable.ic_volume_up_black_24dp);
            img_mute.setImageResource(R.drawable.ic_volume_off_black_24dp);
        }

        //listening to seekbar changes
        volumeBar.setOnSeekBarChangeListener(new AppCompatSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                //setting audio manager volume to current seek bar position volume
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

                if (progress==0){
                    img_mute.setImageResource(R.drawable.ic_volume_off_blue_24dp);
                }else if (progress==15){
                    img_volume.setImageResource(R.drawable.ic_volume_up_blue_24dp);
                }else {
                    img_volume.setImageResource(R.drawable.ic_volume_up_black_24dp);
                    img_mute.setImageResource(R.drawable.ic_volume_off_black_24dp);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private boolean isPlaying() {
        //checking if currently radio is playing
        return (null != radioManager && null != RadioManager.getService() && RadioManager.getService().isPlaying());
    }

    private void startStopPlaying() {
        //Start the radio playing
        radioManager.playOrPause(urlToPlay);

    }

    public void updateButtons() {
        TextView tvArtist = (TextView) findViewById(R.id.tv_Artist);
        TextView tvSong = (TextView) findViewById(R.id.tv_songName);
        if (isPlaying()) {

            //If another stream is playing, show this in the layout
            if (RadioManager.getService() != null && urlToPlay != null && !urlToPlay.equals(RadioManager.getService().getStreamUrl())) {
                img_play.setImageResource(R.drawable.btnplay);
                tvSong.setText(StationName);
                tvArtist.setText(StationDesc);
            } else {
                img_play.setImageResource(R.drawable.btnpause);
                SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                String song = sharedPreferences.getString("song", "");
                String artist = sharedPreferences.getString("artist", "");
                tvArtist.setText(artist);
                tvSong.setText(song);

            }
        } else {
            //If this stream is paused, adjust the buttons accordingly
            img_play.setImageResource(R.drawable.btnplay);
            tvSong.setText(StationName);
            tvArtist.setText(StationDesc);

        }
    }

    //@param info - the text to be updated. Giving a null string will hide the info.
    public void updateMediaInfoFromBackground(String artist, String song, Bitmap image) {
        TextView nowPlayingTitle = (TextView) findViewById(R.id.tv_songName);
        TextView nowPlaying = (TextView) findViewById(R.id.tv_Artist);

        //checking if data is null else updating artist ,song and album art
        if (artist != null) {
            nowPlaying.setText(artist);
            nowPlayingTitle.setText(song);
        } else if (artist == null) {
            nowPlayingTitle.setVisibility(View.VISIBLE);
            nowPlayingTitle.setText(StationName);
            nowPlaying.setVisibility(View.VISIBLE);
            nowPlaying.setText(StationDesc);
        }

        if (image != null) {
            img_Station.setImageBitmap(image);
        } else {
            Picasso.get().load(StationImage).into(img_Station);
        }

    }

    @Override
    public void onStart() {

        super.onStart();
        Tools.registerAsListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {

        super.onStop();
        Tools.unregisterAsListener(this);
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        radioManager.bind(getApplicationContext());
    }

    @Override
    public void onMetaDataReceived(Metadata meta, Bitmap image) {
        //Update the mediainfo shown above the controls
        String artist = null;
        String Song = null;
        if (meta != null && meta.getArtist() != null)
            artist = meta.getArtist();
        Song = meta.getSong();
        updateMediaInfoFromBackground(artist, Song, image);
    }


    @Subscribe
    public void onEvent(String status) {

        //getting status and updating textviews and buttons accordingly

        switch (status) {

            case PlaybackStatus.LOADING:
                // loading
                img_play.setImageResource(R.drawable.btnpause);
                TextView nowPlayingTitle = (TextView) findViewById(R.id.tv_songName);
                nowPlayingTitle.setText("Loading");
                break;

            case PlaybackStatus.PAUSED:
                //Paused
                img_play.setImageResource(R.drawable.btnplay);
                break;

            case PlaybackStatus.PLAYING:
                //Playing
                img_play.setImageResource(R.drawable.btnpause);
                break;

            case PlaybackStatus.ERROR:
                //Error
                Toast.makeText(this, R.string.no_stream, Toast.LENGTH_SHORT).show();
                break;

        }


    }

    @Override
    public void onAudioSessionId(Integer i) {

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

                    StationName = radioLists.get(0).getName();
                    StationDesc = radioLists.get(0).getDesc();
                    StationImage = radioLists.get(0).getImageURL();
                    StationLongDesc = radioLists.get(0).getLongDesc();
                    urlToPlay = radioLists.get(0).getStreamURL();

                    //setting station image
                    Picasso.get().load(StationImage).into(img_Station);
                    if (isPlaying()) {
                        onAudioSessionId(RadioManager.getService().getAudioSessionId());
                    }

                    initVolumeBar();
                    updateButtons();

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
    public void onBackPressed() {
        exitDialog();
    }

    public void exitDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(getResources().getString(R.string.message));
        dialog.setPositiveButton(getResources().getString(R.string.quit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                stopPlaying();
                finish();
            }
        });

        dialog.setNegativeButton(getResources().getString(R.string.minimize), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                minimizeApp();
            }
        });

        dialog.setNeutralButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    public void minimizeApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}










