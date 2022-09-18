package com.badradio.nz.Activity;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.badradio.nz.R;

public class WebActivity extends AppCompatActivity {
    private WebView wbView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);


        //Intializing views
        wbView = (WebView) findViewById(R.id.webView);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        int color = 0xE91E63;
        progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        progressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);


        //intializing webview and loading url
        wbView.getSettings().setJavaScriptEnabled(true);
        wbView.loadUrl("http://badradio.nz");
        wbView.measure(100, 100);
        wbView.getSettings().setUseWideViewPort(true);
        wbView.getSettings().setLoadWithOverviewMode(true);
        wbView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                //removing progressbar when site is loaded.
                    progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }
        });
    }
}

