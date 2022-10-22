package com.badradio.nz.activity

import androidx.appcompat.app.AppCompatActivity
import android.webkit.WebView
import android.widget.ProgressBar
import android.os.Bundle
import com.badradio.nz.R
import android.graphics.PorterDuff
import android.webkit.WebViewClient
import android.view.View
import com.badradio.nz.databinding.ActivityWebBinding

class WebActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val color = 0xE91E63 // TODO: remove hard code
        binding.progressBar.indeterminateDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        binding.progressBar.progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)


        binding.webView.settings.javaScriptEnabled = true // TODO: necessary?
        binding.webView.loadUrl("https://badradio.nz")
        binding.webView.measure(100, 100)
        binding.webView.settings.useWideViewPort = true
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                binding.progressBar.visibility = View.GONE
            }

            @Deprecated("Deprecated in Java")
            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {}
        }
    }
}