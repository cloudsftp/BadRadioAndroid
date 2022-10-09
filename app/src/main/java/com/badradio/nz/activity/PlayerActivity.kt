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
        RadioManager.addObserver(this)
    }

    override fun onPause() {
        super.onPause()
        RadioManager.removeObserver(this)
    }

    private var isPlaying: Boolean = false

    private fun togglePlayer() {
        if (isPlaying) {
            RadioManager.onPause()
        } else {
            RadioManager.onPlay()
        }
    }


    override fun onStateChange(state: PlayerState) {
        runOnUiThread {
            binding.imgAlbumArt.setImageBitmap(state.art)

            binding.textSongName.text = state.metadata.title
            binding.textArtist.text = state.metadata.artist

            if (isPlaying != state.playing) {
                val res = if (state.playing) {
                    R.drawable.pause_circle
                } else {
                    R.drawable.play_circle
                }

                binding.imgBtnPlay.setImageResource(res)

                isPlaying = state.playing
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
}