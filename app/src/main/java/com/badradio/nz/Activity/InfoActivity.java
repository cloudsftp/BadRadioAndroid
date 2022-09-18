package com.badradio.nz.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.badradio.nz.R;

public class InfoActivity extends AppCompatActivity {
    private Button btn_okay,btn_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        btn_okay=(Button)findViewById(R.id.btn_okay);
        btn_email=(Button)findViewById(R.id.btn_email);

        //Okay button
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
