package com.badradio.nz.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.badradio.nz.R
import android.content.Intent
import android.media.AudioManager
import com.badradio.nz.player.RadioManager
import com.badradio.nz.player.PlaybackState
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.badradio.nz.Config
import com.badradio.nz.databinding.ActivityPlayerBinding
import com.badradio.nz.player.PlayerState
import com.badradio.nz.utilities.PlayerStateObserver

class PlayerActivity : AppCompatActivity(), PlayerStateObserver {

    private lateinit var binding: ActivityPlayerBinding

    private var audioManager: AudioManager? = null
    private var StationName: String? = null
    private var StationDesc: String? = null
    private var current = 0
    private var current2 = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgAbout.setOnClickListener {
            val intent = Intent(this@PlayerActivity, AboutActivity::class.java)
            startActivity(intent)
        }

        binding.imgBtnInfo.setOnClickListener { //sending to info page
            val intent = Intent(this@PlayerActivity, InfoActivity::class.java)
            startActivity(intent)
        }

        binding.imgBtnPlay.setOnClickListener {
        }


        binding.imgBtnShare.setOnClickListener { shareApp(applicationContext) }
    }


    private fun stopPlaying() {

        // Updating button and song and artist textviews
        binding.imgBtnPlay.setImageResource(R.drawable.btnplay)

        binding.tvSongName.text = StationName
        binding.tvArtist.text = StationDesc

        // Changing status as stopped
        // ListenersManager.onEvent(status)
    }

    /*
    //checking if currently radio is playing
    private val isPlaying: Boolean
        private get() =//checking if currently radio is playing
            null != RadioManager.service && RadioManager.service!!.isPlaying


    fun updateButtons() {
        val tvArtist = findViewById<View>(R.id.tv_Artist) as TextView
        val tvSong = findViewById<View>(R.id.tv_songName) as TextView
        if (isPlaying) {

            //If another stream is playing, show this in the layout
            if (RadioManager.service != null) {
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
     */

    public override fun onStart() {
        super.onStart()
        // ListenersManager.registerAsListener(this)
    }

    public override fun onStop() {
        super.onStop()
        // ListenersManager.unregisterAsListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        RadioManager.bind(applicationContext)
    }

    /*fun onEvent(status: PlayerState) {
        when (status) {
            PlayerState.LOADING -> {
                binding.imgBtnPlay.setImageResource(R.drawable.btnpause)
                binding.tvSongName.text = "Loading"
            }
            PlayerState.PAUSED   -> binding.imgBtnPlay.setImageResource(R.drawable.btnplay)
            PlayerState.PLAYING  -> binding.imgBtnPlay.setImageResource(R.drawable.btnpause)
            PlayerState.ERROR    -> Toast.makeText(
                this,
                R.string.no_stream,
                Toast.LENGTH_SHORT
            ).show()
            else -> {
                // dont do anythin? TODO: rethink
            }
        }
    }*/

/*
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

    override fun onStateChange(state: PlaybackState) {
        // TODO("Not yet implemented")
    } */

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

    override fun onStateChange(state: PlayerState) {
        // TODO("Not yet implemented")
    }
}