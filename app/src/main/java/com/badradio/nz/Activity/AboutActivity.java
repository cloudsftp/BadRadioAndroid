package com.badradio.nz.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.badradio.nz.R;

public class AboutActivity extends AppCompatActivity {
    private Button btnWebsite,btnAbout;
    private ImageButton btn_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //Intializing views

        btnAbout=(Button)findViewById(R.id.btnAbout);
        btnWebsite=(Button)findViewById(R.id.btnWebsite);
        btn_close=(ImageButton)findViewById(R.id.btn_close);

        //website button
        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutActivity.this,WebActivity.class);
                startActivity(intent);

            }
        });

        //about button
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AboutActivity.this,WebActivity.class);
                startActivity(intent);

            }
        });

        //close button
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });


    }
}
