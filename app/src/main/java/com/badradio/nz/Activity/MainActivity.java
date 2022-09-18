package com.badradio.nz.Activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.badradio.nz.Activity.LandingPageActivity;
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
