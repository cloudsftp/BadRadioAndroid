package com.badradio.nz.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.badradio.nz.R
import android.content.Intent
import android.graphics.BitmapFactory
import com.badradio.nz.player.RadioManager
import com.badradio.nz.player.PlaybackState
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.badradio.nz.databinding.ActivityPlayerBinding
import com.badradio.nz.metadata.SongMetadata
import com.badradio.nz.player.PlayerState
import com.badradio.nz.utilities.PlayerStateObserver

class PlayerActivity : AppCompatActivity(), PlayerStateObserver {
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RadioManager.bind(applicationContext)
        tryRegisterAsPlayerStateObserver()

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onStateChange(
            PlayerState(
                PlaybackState.NOT_READY,
                SongMetadata("24/7 Phonk Radio", "BADRADIO"),
                BitmapFactory.decodeResource(resources, R.drawable.badradio)
            )
        )

        binding.imgAbout.setOnClickListener {
            val intent = Intent(this@PlayerActivity, AboutActivity::class.java)
            startActivity(intent)
        }

        binding.imgBtnPlay.setOnClickListener {
            RadioManager.onPlay()
        }

        /*binding.imgBtnInfo.setOnClickListener {
            val intent = Intent(this@PlayerActivity, InfoActivity::class.java)
            startActivity(intent)
        } */
        //binding.imgBtnShare.setOnClickListener { shareApp(applicationContext) }
    }

    private fun tryRegisterAsPlayerStateObserver() {
        if (RadioManager.ready) {
            RadioManager.registerPlayerStateObserver(this)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                tryRegisterAsPlayerStateObserver()
            }, 100)
        }
    }

    private val lastPlaybackState = PlaybackState.NOT_READY

    override fun onStateChange(state: PlayerState) {
        runOnUiThread {
            binding.imgAlbumArt.setImageBitmap(state.art)

            binding.textSongName.text = state.metadata.title
            binding.textArtist.text = state.metadata.artist
        }
    }

    public override fun onStart() {
        super.onStart()
        // ListenersManager.registerAsListener(this)
    }

    public override fun onStop() {
        super.onStop()
        // ListenersManager.unregisterAsListener(this)
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
        dialog.setPositiveButton(resources.getString(R.string.quit)) { _, _ ->
            onPause()
            finish()
        }
        dialog.setNegativeButton(resources.getString(R.string.minimize)) { _, _ -> minimizeApp() }
        dialog.setNeutralButton(resources.getString(R.string.cancel)) { _, _ -> }
        dialog.show()
    }

    private fun minimizeApp() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    /*
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
    } */
}