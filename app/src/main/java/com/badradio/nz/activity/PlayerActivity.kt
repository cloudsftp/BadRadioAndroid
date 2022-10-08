package com.badradio.nz.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.badradio.nz.R
import android.content.Intent
import android.util.Log
import com.badradio.nz.player.RadioManager
import com.badradio.nz.databinding.ActivityPlayerBinding
import com.google.android.exoplayer2.Player

class PlayerActivity : AppCompatActivity(), Player.Listener {
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RadioManager.bind(applicationContext)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgAbout.setOnClickListener {
            val intent = Intent(this@PlayerActivity, AboutActivity::class.java)
            startActivity(intent)
        }

        binding.imgBtnPlay.setOnClickListener {
            togglePlayer()
        }

        binding.imgBtnInfo.setOnClickListener {
            val intent = Intent(this@PlayerActivity, InfoActivity::class.java)
            startActivity(intent)
        }
        binding.imgBtnShare.setOnClickListener { shareApp(applicationContext) }
    }

    override fun onResume() {
        super.onResume()
        RadioManager.addListener(this)
    }

    override fun onPause() {
        super.onPause()
        RadioManager.removeListener(this)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        val res = if (isPlaying) {
            R.drawable.ic_pause_white
        } else {
            R.drawable.ic_play_white
        }

        runOnUiThread {
            binding.imgBtnPlay.setImageResource(res)
        }
    }

    private fun togglePlayer() {
        RadioManager.onPlay()
    }

    /*
    override fun onStateChange(state: PlayerState) {
        runOnUiThread {
            binding.imgAlbumArt.setImageBitmap(state.art)

            binding.textSongName.text = state.metadata.title
            binding.textArtist.text = state.metadata.artist

            if (playbackState != state.playback) {
                when (state.playback) {
                    PlaybackState.NOT_READY -> {
                        binding.imgBtnPlay.setImageResource(R.drawable.ic_play_white)
                        binding.imgBtnPlay.imageAlpha = 50
                    }
                    PlaybackState.PAUSED -> {
                        binding.imgBtnPlay.setImageResource(R.drawable.ic_play_white)
                        binding.imgBtnPlay.imageAlpha = 1000
                    }
                    PlaybackState.PLAYING -> {
                        binding.imgBtnPlay.setImageResource(R.drawable.ic_pause_white)
                        binding.imgBtnPlay.imageAlpha = 1000
                    }
                }

                playbackState = state.playback
            }
        }
    }
     */

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

    private val tag = "PlayerActivity"
}