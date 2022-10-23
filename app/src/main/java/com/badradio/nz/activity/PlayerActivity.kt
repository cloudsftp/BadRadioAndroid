package com.badradio.nz.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.badradio.nz.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.badradio.nz.player.RadioManager
import com.badradio.nz.databinding.ActivityPlayerBinding
import com.badradio.nz.player.PlaybackStatus
import com.badradio.nz.player.PlayerState
import com.badradio.nz.utilities.PlayerStateObserver

class PlayerActivity : AppCompatActivity(), PlayerStateObserver {
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RadioManager.bind(applicationContext)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ratio = resources.displayMetrics.heightPixels.toFloat() / resources.displayMetrics.widthPixels
        binding.imgAlbumArt.layoutParams.height = (
                (ratio.coerceAtMost(2F) / 2)
                        * resources.displayMetrics.widthPixels
        ).toInt()

        binding.imgAbout.setOnClickListener {
            val intent = Intent(this@PlayerActivity, AboutActivity::class.java)
            startActivity(intent)
        }

        binding.imgBtnPlay.setOnClickListener {
            togglePlayer()
        }

        binding.imgBtnStop.setOnClickListener {
            RadioManager.onStop()
        }

        binding.imgBtnShare.setOnClickListener {
            shareApp(applicationContext)
        }
    }

    override fun onResume() {
        super.onResume()
        RadioManager.addObserver(this)
    }

    override fun onPause() {
        super.onPause()
        RadioManager.removeObserver(this)
    }

    private var displayPlayButton: Boolean = true

    private fun togglePlayer() {
        if (displayPlayButton) {
            RadioManager.onPlay()
        } else {
            RadioManager.onPause()
        }
    }


    override fun onStateChange(state: PlayerState) {
        runOnUiThread {
            updateAlbumArt(state.art)

            binding.textSongName.text = state.metadata.title
            binding.textArtist.text = state.metadata.artist

            displayPlayButton = state.playbackStatus != PlaybackStatus.PLAYING

            val res = if (displayPlayButton) {
                R.drawable.ic_play_btn
            } else {
                R.drawable.ic_pause_btn
            }

            binding.imgBtnPlay.setImageResource(res)
        }
    }

    private fun updateAlbumArt(art: Bitmap?) {
        var toDisplay = art
        if (toDisplay == null) {
            toDisplay = BitmapFactory.decodeResource(resources, R.drawable.badradio)
        }

        binding.imgAlbumArt.setImageBitmap(toDisplay)
    }

    private fun shareApp(context: Context) {
        val appPackageName = context.packageName
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out the App at: https://play.google.com/store/apps/details?id=$appPackageName")
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(sendIntent)
    }
}