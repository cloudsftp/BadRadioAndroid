package com.badradio.nz.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.badradio.nz.R
import android.content.Intent
import com.badradio.nz.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnWebsite.setOnClickListener {
            val intent = Intent(this@AboutActivity, WebActivity::class.java)
            startActivity(intent)
        }

        binding.btnAbout.setOnClickListener {
            val intent = Intent(this@AboutActivity, WebActivity::class.java)
            startActivity(intent)
        }

        binding.btnClose.setOnClickListener {
            finish()
        }
    }
}