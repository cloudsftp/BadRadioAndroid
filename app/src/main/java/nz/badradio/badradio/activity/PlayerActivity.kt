package nz.badradio.badradio.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import nz.badradio.badradio.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import nz.badradio.badradio.radio.RadioManager
import nz.badradio.badradio.databinding.ActivityPlayerBinding
import nz.badradio.badradio.radio_viewmodel.RadioVM
import nz.badradio.badradio.radio_viewmodel.RadioVMObserver
import nz.badradio.badradio.radio_viewmodel.RadioVMState

class PlayerActivity : AppCompatActivity(), RadioVMObserver {
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RadioVM.initialize(applicationContext)

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
            RadioVM.onPlayPause()
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
        RadioVM.addObserver(this)
    }

    override fun onPause() {
        super.onPause()
        RadioVM.removeObserver(this)
    }

    private fun togglePlayer() {
        RadioVM.onPlayPause()
    }

    override fun onStateChange(state: RadioVMState) {
        runOnUiThread {
            binding.imgBtnPlay.isEnabled = state.enableButtons

            binding.imgBtnPlay.setImageResource(
                if (state.displayPause) {
                    R.drawable.ic_pause_btn
                } else {
                    R.drawable.ic_play_btn
                }
            )

            binding.textSongName.text = state.title ?: resources.getText(R.string.default_song_name)
            binding.textArtist.text = state.artist ?: resources.getText(R.string.default_artist)

            updateAlbumArt(state.art)
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