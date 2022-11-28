package nz.badradio.badradio.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import nz.badradio.badradio.R
import nz.badradio.badradio.databinding.ActivityPlayerBinding
import nz.badradio.badradio.viewmodel.RadioVM
import nz.badradio.badradio.viewmodel.RadioVMObserver
import nz.badradio.badradio.viewmodel.RadioVMState

class PlayerActivity : AppCompatActivity(), RadioVMObserver {
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RadioVM.initialize(applicationContext)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.root)
        binding.toolbar.root.apply {
            setNavigationIcon(R.drawable.ic_info)
            setNavigationOnClickListener { openAbout() }
        }

        val ratio = resources.displayMetrics.heightPixels.toFloat() / resources.displayMetrics.widthPixels
        binding.imgAlbumArt.layoutParams.height = (
                (ratio.coerceAtMost(2F) / 2)
                        * resources.displayMetrics.widthPixels
        ).toInt()

        binding.imageButtonPlayPause.setOnClickListener {
            RadioVM.onPlayPause(applicationContext)
        }

        binding.imageButtonGoLive.setOnClickListener {
            RadioVM.onGoLive(applicationContext)
        }
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
        when (item.itemId) {
            R.id.info               -> openAbout()
            R.id.stop_service       -> RadioVM.stopService()
            else                    -> super.onContextItemSelected(item)
        }

        return true
    }

    override fun onStateChange(state: RadioVMState) {
        runOnUiThread {
            binding.imageButtonPlayPause.setImageResource(
                if (state.displayPause) {
                    R.drawable.ic_pause
                } else {
                    R.drawable.ic_play
                }
            )

            binding.imageButtonGoLive.setImageResource(
                if (state.displayLive) {
                    R.drawable.ic_radio_button_checked
                } else {
                    R.drawable.ic_radio_button_unchecked
                }
            )

            binding.imageButtonPlayPause.isEnabled = state.enablePlayPauseButton
            binding.imageButtonGoLive.isEnabled = !state.displayLive

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

    private fun openAbout() {
        val intent = Intent(this@PlayerActivity, AboutActivity::class.java)
        startActivity(intent)
    }
}