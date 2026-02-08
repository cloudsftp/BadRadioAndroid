package nz.badradio.badradio.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import nz.badradio.badradio.BuildConfig
import nz.badradio.badradio.R
import nz.badradio.badradio.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout()
            )
            v.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right,
                bottom = bars.bottom,
            )
            WindowInsetsCompat.CONSUMED
        }

        setSupportActionBar(binding.toolbar.root)
        binding.toolbar.root.apply {
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                finish()
            }
        }

        binding.websiteButton.setOnClickListener {
            openWebsite(resources.getString(R.string.website_url))
        }
        binding.discordButton.setOnClickListener {
            openWebsite(resources.getString(R.string.discord_url))
        }

        binding.appVersionTextView.text = String.format(
            resources.getString(R.string.version_name),
            BuildConfig.VERSION_NAME
        )

        binding.shareButton.setOnClickListener { shareApp() }
        binding.betaButton.setOnClickListener {
            openWebsite(String.format(
                resources.getString(R.string.beta_link),
                packageName
            ))
        }

        binding.privacyButton.setOnClickListener {
            openWebsite(resources.getString(R.string.privacy_policy_url))
        }
    }

    private fun openWebsite(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun shareApp() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, String.format(
                resources.getString(R.string.share_app_text),
                packageName
            ))
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        startActivity(sendIntent)
    }
}