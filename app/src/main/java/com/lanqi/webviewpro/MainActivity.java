package com.lanqi.webviewpro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lanqi.webviewpro.widget.DefaultToggledFullscreenCallback;
import com.lanqi.webviewpro.widget.LQWebChromeClient;
import com.lanqi.webviewpro.widget.LQWebClient;
import com.lanqi.webviewpro.widget.WebViewSetting;

public class MainActivity extends Activity {
    private WebView webView;
    private FrameLayout videoView;
    private LQWebChromeClient webChromeClient;
    private LQWebClient webClient;
    private ProgressBar propressbar;
    private TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();


        webView = WebViewSetting.init(webView);
        webClient = new LQWebClient();
//        显示标题
        webClient.setPagefinish(new LQWebClient.PageFinishedListener() {
            @Override
            public void pageFinished(WebView view, String url) {
                if (TextUtils.isEmpty(view.getTitle())) {
                    titleView.setText("网页");
                }else {
                    titleView.setText(view.getTitle());
                }
            }
        });
        webView.setWebViewClient(webClient);

        webChromeClient = new LQWebChromeClient(webView, videoView);
        webChromeClient.setProgressBar(propressbar);
        webChromeClient.setOnToggledFullscreen(new DefaultToggledFullscreenCallback(this) {
            @Override
            public void toggledFullscreen(boolean fullscreen) {
                super.toggledFullscreen(fullscreen);
                if (fullscreen) {
                    titleView.setVisibility(View.GONE);
                } else {
                    titleView.setVisibility(View.VISIBLE);
                }
            }
        });
        webView.setWebChromeClient(webChromeClient);
        webView.loadUrl("https://www.baidu.com");
    }

    private void initView() {
        titleView = (TextView) findViewById(R.id.titleView);
        propressbar = (ProgressBar) findViewById(R.id.propressbar);
        webView = (WebView) findViewById(R.id.webview);
        videoView = (FrameLayout) findViewById(R.id.videoview);
    }

    @Override
    protected void onResume() {
        webView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        webView.onPause();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (!webChromeClient.onBackPressed()) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }
}
