package com.skylab.donepaper.donepaper.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.activities.abstracted.AbstractBaseAcitivity;

public class TermsActivity extends AbstractBaseAcitivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_terms;
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.terms_activity_toolbar);
        this.setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        webView = (WebView)findViewById(R.id.web_terms);

        webView.getSettings().setDisplayZoomControls(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                webView.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.loadUrl("javascript:(function(){" +
                        "document.querySelector('header').innerHTML = ''; " +
                        "document.querySelector('footer').innerHTML = ''; " +
                        "document.querySelector('.entry-header').innerHTML = '';" +
                        "document.querySelector('#lang_sel_footer').innerHTML = '';" +
                        "document.querySelector('#secondary').innerHTML = '';" +
                        "document.querySelector('.content-left-wrap').style.cssText='padding-top:0px';" +
                        "})()");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webView.post(new Runnable() {
                            @Override
                            public void run() {
                                webView.setVisibility(View.VISIBLE);

                            }
                        });
                    }

                }, 75);
            }
        });

        webView.loadUrl("https://www.donepaper.com/terms/");
    }

    @Override
    public void onClick(View v) {

    }
}
