package com.badradio.nz.activity

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.badradio.nz.R
import android.content.Intent
import com.badradio.nz.utilities.Tools
import com.badradio.nz.services.RadioManager
import com.badradio.nz.Models.RadioList
import com.badradio.nz.services.PlaybackStatus
import android.graphics.Bitmap
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import org.json.JSONException
import com.android.volley.toolbox.Volley
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.badradio.nz.Adapter.RadioListAdapter
import com.badradio.nz.metadata.Metadata
import com.karumi.dexter.listener.PermissionRequest
import java.util.ArrayList

// TODO: can be deleted???

class LandingPageActivity : AppCompatActivity(), Tools.EventListener {
    private var rv_stationist: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var radioLists: MutableList<RadioList>? = null
    private var img_about: ImageButton? = null
    private var radioManager: RadioManager? = null
    private var nowPlaying: RelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        //Initializing views
        radioManager = RadioManager.with()
        rv_stationist = findViewById<View>(R.id.rv_radioList) as RecyclerView
        rv_stationist!!.setHasFixedSize(true)
        rv_stationist!!.layoutManager = LinearLayoutManager(this)
        radioLists = ArrayList()
        img_about = findViewById<View>(R.id.img_about) as ImageButton
        nowPlaying = findViewById<View>(R.id.rlt_now_playing) as RelativeLayout
        nowPlaying!!.visibility = View.INVISIBLE

        //About buttom(hamburger button)
        img_about!!.setOnClickListener {
            val intent = Intent(this@LandingPageActivity, AboutActivity::class.java)
            startActivity(intent)
        }


        //NowPlaying Button
        nowPlaying!!.setOnClickListener { //sending to player activity
            val intent = Intent(this@LandingPageActivity, PlayerActivity::class.java)
            //sending intent data
            intent.putExtra("type", "nowPlaying")
            startActivity(intent)
        }

        //Checking for permissions.
        requestStoragePermission()

        //Loading stations into recyclerview.
        loadStation()

        //Animation for music bar
        val animationDrawable = AnimationDrawable()
        animationDrawable.addFrame(resources.getDrawable(R.drawable.np1), 80)
        animationDrawable.addFrame(resources.getDrawable(R.drawable.np2), 80)
        animationDrawable.addFrame(resources.getDrawable(R.drawable.np3), 80)
        animationDrawable.addFrame(resources.getDrawable(R.drawable.np4), 80)
        val imageView = findViewById<View>(R.id.imageAnim) as ImageView
        imageView.setImageDrawable(animationDrawable)

        //Checking whether radio is playing and upating view accordingly
        if (isPlaying) {

            //Radio playing
            nowPlaying!!.visibility = View.VISIBLE
            val sharedPreferences = getSharedPreferences("data", MODE_PRIVATE)
            val song = sharedPreferences.getString("song", "")
            val artist = sharedPreferences.getString("artist", "")
            val station = sharedPreferences.getString("station", "")
            val nowPlayingTitle = findViewById<View>(R.id.tv_nowPlaying) as TextView
            nowPlayingTitle.text = station + song
            animationDrawable.start()
        } else {

            //Radio not playing
            nowPlaying!!.visibility = View.GONE
            animationDrawable.stop()
            val nowPlayingTitle = findViewById<View>(R.id.tv_nowPlaying) as TextView
            nowPlayingTitle.text = "Choose a station above to begin."
        }
    }

    private val isPlaying: Boolean
        private get() = null != radioManager && null != RadioManager.getService() && RadioManager.getService().isPlaying

    private fun requestStoragePermission() {
        //Checking for permissions
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.MODIFY_AUDIO_SETTINGS)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).withErrorListener { error -> Toast.makeText(applicationContext, "Error occurred! $error", Toast.LENGTH_SHORT).show() }
                .onSameThread()
                .check()
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(applicationContext)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", applicationContext.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    private fun loadStation() {

        //Getting radio list and loading into recyclerview.
        val stringRequest = StringRequest(Request.Method.GET, URL_DATA, { response ->
            try {
                val jsonObject = JSONObject(response)
                val jsonArray = jsonObject.getJSONArray("station")
                for (i in 0 until jsonArray.length()) {
                    val o = jsonArray.getJSONObject(i)
                    val radioList = RadioList(o.getString("name"),
                            o.getString("streamURL"),
                            o.getString("imageURL"),
                            o.getString("desc"),
                            o.getString("longDesc"))
                    radioLists!!.add(radioList)
                }
                mAdapter = RadioListAdapter(radioLists, applicationContext)
                rv_stationist!!.adapter = mAdapter
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { error -> Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_LONG).show() }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    override fun onEvent(status: String) {

        //Checking status of radio and updating views
        val animationDrawable = AnimationDrawable()
        animationDrawable.addFrame(resources.getDrawable(R.drawable.np1), 80)
        animationDrawable.addFrame(resources.getDrawable(R.drawable.np2), 80)
        animationDrawable.addFrame(resources.getDrawable(R.drawable.np3), 80)
        animationDrawable.addFrame(resources.getDrawable(R.drawable.np4), 80)
        val imageView = findViewById<View>(R.id.imageAnim) as ImageView
        imageView.setImageDrawable(animationDrawable)
        when (status) {
            PlaybackStatus.PLAYING -> {
                //Playing
                nowPlaying!!.visibility = View.VISIBLE
                animationDrawable.start()
            }
            PlaybackStatus.PAUSED -> {
                //Paused
                nowPlaying!!.visibility = View.INVISIBLE
                animationDrawable.stop()
            }
            PlaybackStatus.STOPPED -> {
                //Stopped
                nowPlaying!!.visibility = View.INVISIBLE
                animationDrawable.stop()
                val nowPlayingTitle = findViewById<View>(R.id.tv_nowPlaying) as TextView
                nowPlayingTitle.text = "Choose a station above to begin."
            }
        }
    }

    override fun onAudioSessionId(i: Int) {}
    fun updateMediaInfoFromBackground(artist: String?, song: String?, channel: String?, image: Bitmap?) {
        //Updating current playing station and song
        val nowPlayingTitle = findViewById<View>(R.id.tv_nowPlaying) as TextView
        if (artist != null) {
            nowPlayingTitle.text = "$channel:$song"
        } else if (channel != null) {
            nowPlayingTitle.text = "Now playing $channel"
        } else {
            nowPlayingTitle.text = "Choose a station above to begin"
        }
    }

    override fun onMetaDataReceived(meta: Metadata, image: Bitmap) {

        //Getting currently playing station and song details
        var artist: String? = null
        var Song: String? = null
        var channel: String? = null
        var url: String? = null
        if (meta != null && meta.artist != null) artist = meta.artist
        Song = meta.song
        channel = meta.station
        url = meta.url
        updateMediaInfoFromBackground(artist, Song, channel, image)
    }

    override fun onStart() {
        super.onStart()
        Tools.registerAsListener(this)
    }

    companion object {
        private const val URL_DATA = "https://badradio.nz/streamingassets/stations.json"
    }
}