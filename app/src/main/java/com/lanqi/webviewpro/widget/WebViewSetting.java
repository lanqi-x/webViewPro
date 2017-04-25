package com.lanqi.webviewpro.widget;

import android.webkit.WebSettings;
import android.webkit.WebView;
/**
 * Created by Blue.K on 2016/3/14.
 */
public class WebViewSetting {

    public static <T extends WebView> T init(T wb) {
        WebSettings set = wb.getSettings();
        String ua = set.getUserAgentString();
        set.setUserAgentString(ua+";my mark");//修改浏览器标识
        set.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 开启 DOM storage API 功能
        set.setDomStorageEnabled(true);
        set.setSupportZoom(true);
        set.setJavaScriptEnabled(true);
        set.setCacheMode(WebSettings.LOAD_DEFAULT);
        set.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        set.setLoadWithOverviewMode(true);
        return wb;
    }
}