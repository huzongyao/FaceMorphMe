package com.hzy.face.morphme.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.StringUtils;
import com.hzy.face.morphme.R;
import com.hzy.face.morphme.consts.RouterHub;
import com.hzy.face.morphme.utils.ActionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = RouterHub.WEBVIEW_ACTIVITY)
public class WebViewActivity extends AppCompatActivity {
    public static final String EXTRA_URL = "url";

    @BindView(R.id.web_view_web)
    WebView mWebViewWeb;
    @BindView(R.id.web_view_progress)
    ProgressBar mWebViewProgress;

    public static void startUrl(String url) {
        ARouter.getInstance().build(RouterHub.WEBVIEW_ACTIVITY)
                .withString(EXTRA_URL, url).navigation();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String mInitUrl = intent.getStringExtra(EXTRA_URL);
        if (!StringUtils.isTrimEmpty(mInitUrl)) {
            setContentView(R.layout.activity_web_view);
            ButterKnife.bind(this);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            setupWebView();
            if (!StringUtils.isEmpty(mInitUrl)) {
                mWebViewWeb.loadUrl(mInitUrl);
            }
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebViewWeb.canGoBack()) {
            mWebViewWeb.goBack();
            return;
        }
        super.onBackPressed();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        mWebViewProgress.setMax(100);
        WebSettings webSettings = mWebViewWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(true);
        if (NetworkUtils.isConnected()) {
            webSettings.setCacheMode(WebSettings.LOAD_NORMAL);
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        mWebViewWeb.setWebViewClient(getWebViewClient());
        mWebViewWeb.setWebChromeClient(getWebChromeClient());
        mWebViewWeb.setDownloadListener(getDownloadListener());
    }

    private DownloadListener getDownloadListener() {
        return (url, userAgent, contentDisposition, mimetype, contentLength) ->
                ActionUtils.startViewAction(url);
    }

    private WebViewClient getWebViewClient() {
        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http")) {
                    view.loadUrl(url);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        };
    }

    private WebChromeClient getWebChromeClient() {
        return new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 0 && newProgress < 100) {
                    mWebViewProgress.setProgress(newProgress);
                    mWebViewProgress.setVisibility(View.VISIBLE);
                } else {
                    mWebViewProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                setTitle(title);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_refresh:
                mWebViewWeb.reload();
                return true;
            case R.id.menu_open_out:
                ActionUtils.startViewAction(mWebViewWeb.getUrl());
                return true;
            case R.id.menu_exit:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWebViewWeb != null)
            mWebViewWeb.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWebViewWeb != null)
            mWebViewWeb.onResume();
    }

    @Override
    public void onDestroy() {
        if (mWebViewWeb != null) {
            mWebViewWeb.loadDataWithBaseURL(null, "",
                    "text/html", "utf-8", null);
            mWebViewWeb.clearHistory();
            mWebViewWeb.destroy();
        }
        super.onDestroy();
    }
}
