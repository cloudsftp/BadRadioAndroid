package nz.badradio.badradio.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import nz.badradio.badradio.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import nz.badradio.badradio.player.RadioManager
import nz.badradio.badradio.databinding.ActivityPlayerBinding
import nz.badradio.badradio.player.PlaybackStatus
import nz.badradio.badradio.player.PlayerState
import nz.badradio.badradio.utilities.PlayerStateObserver

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

    private fun togglePlayer() {
        RadioManager.onPlayPause()
    }


    override fun onStateChange(state: PlayerState) {
        runOnUiThread {
            updateAlbumArt(state.art)

            binding.textSongName.text = state.metadata.title
            binding.textArtist.text = state.metadata.artist

            binding.imgBtnPlay.isEnabled = state.playbackStatus != PlaybackStatus.LOADING

            binding.imgBtnPlay.setImageResource(
                when(state.playbackStatus) {
                    PlaybackStatus.LOADING      -> R.drawable.ic_pause_btn // TODO: replace w/ loading icon
                    PlaybackStatus.NOT_PLAYING  -> R.drawable.ic_play_btn
                    PlaybackStatus.PLAYING      -> R.drawable.ic_pause_btn
                }
            )
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