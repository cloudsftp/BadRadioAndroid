package com.badradio.nz.Activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.badradio.nz.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Showing splash screen

        new CountDownTimer(2000, 1000) {

            @Override
            public void onFinish() {
                Intent intent = new Intent(getBaseContext(), PlayerActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onTick(long millisUntilFinished) {

            }
        }.start();
    }
}
