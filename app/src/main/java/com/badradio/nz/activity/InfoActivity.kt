package com.badradio.nz.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.badradio.nz.R
import com.badradio.nz.databinding.ActivityInfoBinding

class InfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOkay.setOnClickListener {
            finish()
        }

        // TODO: what about email button?
    }
}