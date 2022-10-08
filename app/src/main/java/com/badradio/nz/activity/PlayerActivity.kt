package com.badradio.nz.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.badradio.nz.R
import android.content.Intent
import com.badradio.nz.player.RadioManager
import com.badradio.nz.databinding.ActivityPlayerBinding
import com.badradio.nz.player.PlayerState
import com.badradio.nz.utilities.PlayerStateObserver

class PlayerActivity : AppCompatActivity(), PlayerStateObserver {
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

    /*
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

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        runOnUiThread {
            binding.textSongName.text = mediaMetadata.title
            binding.textArtist.text = mediaMetadata.artist
        }
    }

     */

    private fun togglePlayer() {
        RadioManager.onPlay()
    }

    private var latestPlaybackState: Boolean? = null

    override fun onStateChange(state: PlayerState) {
        runOnUiThread {
            binding.imgAlbumArt.setImageBitmap(state.art)

            binding.textSongName.text = state.metadata.title
            binding.textArtist.text = state.metadata.artist

            if (latestPlaybackState != state.playing) {
                val res = if (state.playing) {
                    R.drawable.ic_pause_white
                } else {
                    R.drawable.ic_play_white
                }

                binding.imgBtnPlay.setImageResource(res)

                latestPlaybackState = state.playing
            }
        }
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

    private val tag = "PlayerActivity"
}