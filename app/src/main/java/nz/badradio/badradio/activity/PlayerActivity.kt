package nz.badradio.badradio.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import nz.badradio.badradio.R
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

        setSupportActionBar(binding.toolbar)

        val ratio = resources.displayMetrics.heightPixels.toFloat() / resources.displayMetrics.widthPixels
        binding.imgAlbumArt.layoutParams.height = (
                (ratio.coerceAtMost(2F) / 2)
                        * resources.displayMetrics.widthPixels
        ).toInt()

        binding.imgBtnPlay.setOnClickListener {
            RadioVM.onPlayPause()
        }

        binding.imgBtnSkip.setOnClickListener {
            RadioVM.onSkip()
        }
/*
        binding.imgBtnShare.setOnClickListener {
            shareApp(applicationContext)
        }
 */
    }

    override fun onResume() {
        super.onResume()
        RadioVM.requestState(this)
        RadioVM.addObserver(this)
    }

    override fun onPause() {
        super.onPause()
        RadioVM.removeObserver(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.player_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.info -> {
                val intent = Intent(this@PlayerActivity, AboutActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }


    override fun onStateChange(state: RadioVMState) {
        runOnUiThread {
            binding.imgBtnPlay.isEnabled = state.enableButtons

            binding.imgBtnPlay.setImageResource(
                if (state.displayPause) {
                    R.drawable.ic_pause
                } else {
                    R.drawable.ic_play
                }
            )

            binding.textSongName.text = state.title
            binding.textArtist.text = state.artist

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