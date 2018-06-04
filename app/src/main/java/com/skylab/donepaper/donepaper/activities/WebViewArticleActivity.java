package com.skylab.donepaper.donepaper.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.activities.abstracted.AbstractBaseAcitivity;

public class WebViewArticleActivity extends AbstractBaseAcitivity {

    private ProgressBar webProgress;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webProgress.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_web_view_article;
    }

    @Override
    protected void initViews() {

        webProgress = (ProgressBar)findViewById(R.id.web_article_progress);

        webView = (WebView) findViewById(R.id.web_article);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webProgress.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                webProgress.setVisibility(View.GONE);
                showNotiDialog("Error", description);
            }
        });

        webView.loadUrl(getIntent().getExtras().getString("Url"));
    }

    @Override
    public void onClick(View v) {

    }
}
