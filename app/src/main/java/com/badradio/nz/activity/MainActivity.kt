package com.badradio.nz.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.badradio.nz.R
import android.os.CountDownTimer
import android.content.Intent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Showing splash screen
        object : CountDownTimer(2000, 1000) {
            override fun onFinish() {
                val intent = Intent(baseContext, PlayerActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onTick(millisUntilFinished: Long) {}
        }.start()
    }
}