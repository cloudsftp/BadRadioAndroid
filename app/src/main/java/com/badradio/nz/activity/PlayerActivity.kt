package com.badradio.nz.activity

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.badradio.nz.R
import android.content.Intent
import android.media.AudioManager
import com.badradio.nz.player.RadioManager
import com.badradio.nz.models.RadioList
import com.badradio.nz.player.PlaybackStatus
import android.widget.SeekBar.OnSeekBarChangeListener
import android.graphics.Bitmap
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import org.json.JSONException
import com.android.volley.toolbox.Volley
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.badradio.nz.Config
import com.badradio.nz.databinding.ActivityPlayerBinding
import com.badradio.nz.utilities.ListenersManager
import com.karumi.dexter.listener.PermissionRequest
import java.util.ArrayList

class PlayerActivity : AppCompatActivity(), ListenersManager.EventListener {

    private lateinit var binding: ActivityPlayerBinding

    private var audioManager: AudioManager? = null
    private var radioManager: RadioManager? = null
    private var urlToPlay: String? = null
    private var StationImage: String? = null
    private var StationName: String? = null
    private var StationDesc: String? = null
    private var StationLongDesc: String? = null
    private var current = 0
    private var current2 = 0
    private var radioLists: MutableList<RadioList>? = null
    private val nowPlaying: RelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializing views
        radioManager = RadioManager.with()
        radioLists = ArrayList()

        //Checking for permissions.
        requestStoragePermission()

        //Loading stations into recyclerview.
        loadStation()

        binding.imgAbout.setOnClickListener {
            val intent = Intent(this@PlayerActivity, AboutActivity::class.java)
            startActivity(intent)
        }

        binding.imgBtnInfo.setOnClickListener { //sending to info page
            val intent = Intent(this@PlayerActivity, InfoActivity::class.java)
            startActivity(intent)
        }

        binding.imgBtnPlay.setOnClickListener {
            startStopPlaying()

            //saving station details to show currently playing station
            val pref = applicationContext.getSharedPreferences("Live", 0)
            val editor = pref.edit()
            editor.putString("url", urlToPlay)
            editor.putString("StationImage", StationImage)
            editor.putString("StationName", StationName)
            editor.putString("StationDesc", StationDesc)
            editor.putString("StationLongDesc", StationLongDesc)
            editor.commit()
        }

        binding.imgBtnMute.setOnClickListener { //Muting volume
            volumeMute()
        }

        binding.imgBtnVolumeUp.setOnClickListener { //Setting volume to full
            volumeFull()
        }

        binding.imgBtnStop.setOnClickListener { //stopping radio
            stopPlaying()
        }

        binding.imgBtnShare.setOnClickListener { shareApp(applicationContext) }
    }

    private fun volumeMute() {
        val volumeBar = findViewById<View>(R.id.seekBar) as SeekBar

        //checking whether mute button is on or off
        if (current == 0) {
            binding.imgBtnMute.setImageResource(R.drawable.ic_volume_off_blue_24dp)
            binding.imgBtnVolumeUp.setImageResource(R.drawable.ic_volume_up_black_24dp)

            //mute button in off state
            audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)

            volumeBar.progress = 0
            current = 1
            current2 = 0
        } else if (current == 1) {
            binding.imgBtnMute.setImageResource(R.drawable.ic_volume_off_black_24dp)

            // Mute button in on state
            audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, Config.DEFAULT_VOLUME, 0)
            val volume = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
            volumeBar.max = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            volumeBar.progress = volume
            current = 0
        }
    }

    private fun stopPlaying() {
        radioManager!!.stopServices()

        // Updating button and song and artist textviews
        binding.imgBtnPlay.setImageResource(R.drawable.btnplay)

        binding.tvSongName.text = StationName
        binding.tvArtist.text = StationDesc

        // Changing status as stopped
        val status = PlaybackStatus.STOPPED
        ListenersManager.onEvent(status)
    }

    private fun volumeFull() {
        val volumeBar = findViewById<View>(R.id.seekBar) as SeekBar

        //checking if full volume button is on or off
        if (current2 == 0) {
            binding.imgBtnMute.setImageResource(R.drawable.ic_volume_off_black_24dp)
            binding.imgBtnVolumeUp.setImageResource(R.drawable.ic_volume_up_blue_24dp)

            volumeBar.progress = 100

            //full volume button off
            audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
            current2 = 1
            current = 0
        } else if (current2 == 1) {
            binding.imgBtnVolumeUp.setImageResource(R.drawable.ic_volume_up_black_24dp)

            //full volume button on
            audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, Config.DEFAULT_VOLUME, 0)
            current2 = 0
            audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, Config.DEFAULT_VOLUME, 0)
            val volume = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
            volumeBar.max = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            volumeBar.progress = volume
        }
    }

    private fun initVolumeBar() {

        //initializing volume bar
        val volumeBar = findViewById<View>(R.id.seekBar) as SeekBar

        //getting current volume and updating it on seekbar
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val volume = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        volumeBar.max = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        volumeBar.progress = volume
        volumeBar.max = 15
        if (volume == 0) {
            binding.imgBtnMute.setImageResource(R.drawable.ic_volume_off_blue_24dp)
        } else if (volume == 15) {
            binding.imgBtnVolumeUp.setImageResource(R.drawable.ic_volume_up_blue_24dp)
        } else {
            binding.imgBtnVolumeUp.setImageResource(R.drawable.ic_volume_up_black_24dp)
            binding.imgBtnMute.setImageResource(R.drawable.ic_volume_off_black_24dp)
        }

        //listening to seekbar changes
        volumeBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {

                //setting audio manager volume to current seek bar position volume
                audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                if (progress == 0) {
                    binding.imgBtnMute.setImageResource(R.drawable.ic_volume_off_blue_24dp)
                } else if (progress == 15) {
                    binding.imgBtnVolumeUp.setImageResource(R.drawable.ic_volume_up_blue_24dp)
                } else {
                    binding.imgBtnVolumeUp.setImageResource(R.drawable.ic_volume_up_black_24dp)
                    binding.imgBtnMute.setImageResource(R.drawable.ic_volume_off_black_24dp)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    //checking if currently radio is playing
    private val isPlaying: Boolean
        private get() =//checking if currently radio is playing
            null != radioManager && null != RadioManager.service && RadioManager.service!!.isPlaying

    private fun startStopPlaying() {
        //Start the radio playing
        radioManager!!.playOrPause(urlToPlay)
    }

    fun updateButtons() {
        val tvArtist = findViewById<View>(R.id.tv_Artist) as TextView
        val tvSong = findViewById<View>(R.id.tv_songName) as TextView
        if (isPlaying) {

            //If another stream is playing, show this in the layout
            if (RadioManager.service != null && urlToPlay != null && urlToPlay != RadioManager.service!!.streamUrl) {
                binding.imgBtnPlay.setImageResource(R.drawable.btnplay)
                tvSong.text = StationName
                tvArtist.text = StationDesc
            } else {
                binding.imgBtnPlay.setImageResource(R.drawable.btnpause)
                val sharedPreferences = getSharedPreferences("data", MODE_PRIVATE)
                val song = sharedPreferences.getString("song", "")
                val artist = sharedPreferences.getString("artist", "")
                tvArtist.text = artist
                tvSong.text = song
            }
        } else {
            //If this stream is paused, adjust the buttons accordingly
            binding.imgBtnPlay.setImageResource(R.drawable.btnplay)
            tvSong.text = StationName
            tvArtist.text = StationDesc
        }
    }

    public override fun onStart() {
        super.onStart()
        ListenersManager.registerAsListener(this)
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        ListenersManager.unregisterAsListener(this)
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        radioManager!!.bind(applicationContext)
    }

    @Subscribe
    override fun onEvent(status: String) {
        when (status) {
            PlaybackStatus.LOADING -> {
                binding.imgBtnPlay.setImageResource(R.drawable.btnpause)
                binding.tvSongName.text = "Loading"
            }
            PlaybackStatus.PAUSED   -> binding.imgBtnPlay.setImageResource(R.drawable.btnplay)
            PlaybackStatus.PLAYING  -> binding.imgBtnPlay.setImageResource(R.drawable.btnpause)
            PlaybackStatus.ERROR    -> Toast.makeText(
                this,
                R.string.no_stream,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSongTitle(title: String, artist: String) {
        runOnUiThread {
            binding.tvSongName.text = title
            binding.tvArtist.text = artist
        }
    }

    override fun onAlbumArt(art: Bitmap) {
        runOnUiThread {
            binding.imgStationPlaying.setImageBitmap(art)
        }
    }

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
                    val radioList = RadioList(o.getString("name"),  // TODO: make sure all needed attributes are there
                            o.getString("streamURL"),               // use mochi for json decoding (or gson)
                            o.getString("imageURL"),
                            o.getString("desc"),
                            o.getString("longDesc"))
                    radioLists!!.add(radioList)
                }
                StationName = radioLists!![0].name
                StationDesc = radioLists!![0].desc
                StationImage = radioLists!![0].imageURL
                StationLongDesc = radioLists!![0].longDesc
                urlToPlay = radioLists!![0].streamURL

                //setting station image
                Picasso.get().load(StationImage).into(binding.imgStationPlaying)
                initVolumeBar()
                updateButtons()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { error -> Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_LONG).show() }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("exitDialog()"))
    override fun onBackPressed() {
        exitDialog()
    }

    fun exitDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setIcon(R.mipmap.ic_launcher)
        dialog.setTitle(R.string.app_name)
        dialog.setMessage(resources.getString(R.string.message))
        dialog.setPositiveButton(resources.getString(R.string.quit)) { dialogInterface, i ->
            stopPlaying()
            finish()
        }
        dialog.setNegativeButton(resources.getString(R.string.minimize)) { dialogInterface, i -> minimizeApp() }
        dialog.setNeutralButton(resources.getString(R.string.cancel)) { dialogInterface, i -> }
        dialog.show()
    }

    fun minimizeApp() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    companion object {
        private const val URL_DATA = "https://badradio.nz/streamingassets/stations.json"
        fun shareApp(context: Context) {

            //Sharing app
            val appPackageName = context.packageName
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: https://play.google.com/store/apps/details?id=$appPackageName")
            sendIntent.type = "text/plain"
            sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(sendIntent)
        }
    }
}