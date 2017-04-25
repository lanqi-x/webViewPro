package com.lanqi.webviewpro.widget;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lanqi.webviewpro.R;


/**
 * Created by Blue.K on 2015/12/7.
 */
public class LQWebClient extends WebViewClient {


    /**
     * 页面加载完毕事件
     */
    private PageFinishedListener pagefinish;
    /**
     * 加载新页面事件
     */
    private LoadNewUrlListener loadNewUrlListener;
    /**
     * 页面是否加载结束，针对android5.0以下而做
     */
    private boolean pageIsFinished;
    /**
     * 是否需要显示自定义错误页
     */
    private boolean customizeErrorPage = true;


    /**
     * 点击网页中按钮时，让其还在原页面打开
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("http")) {
            if (loadNewUrlListener != null) {
                if (!loadNewUrlListener.loadNewUrl(view, url)) {
                    view.loadUrl(url);
                }
            } else {
                view.loadUrl(url);
            }
        } else {
//            非http和https请求丢给系统处理
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                view.getContext().startActivity(intent);
            } catch (Exception e) {
//                没有安装对应的应用会抛异常
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean isCustomizeErrorPage() {
        return customizeErrorPage;
    }

    public void setCustomizeErrorPage(boolean customizeErrorPage) {
        this.customizeErrorPage = customizeErrorPage;
    }

    //当网页加载完毕时调用


    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//      5.0以下在加载失败时onPageStarted会调用两次，而onReceivedError只在第一次加载时调用，
//        而onPageFinished是在onPageStarted调用后直接调用onPageFinished
        if (pageIsFinished) {
            loadError = false;
        }

        pageIsFinished = false;
        super.onPageStarted(view, url, favicon);
    }

    /**
     * 页面加载完成事件
     */
    public interface PageFinishedListener {
        void pageFinished(WebView view, String url);
    }

    public void setPagefinish(PageFinishedListener pagefinish) {
        this.pagefinish = pagefinish;
    }

    public LoadNewUrlListener getLoadNewUrlListener() {
        return loadNewUrlListener;
    }

    public void setLoadNewUrlListener(LoadNewUrlListener loadNewUrlListener) {
        this.loadNewUrlListener = loadNewUrlListener;
    }

    public interface LoadNewUrlListener {
        boolean loadNewUrl(WebView view, String url);
    }

    /**
     * 页面加载结束
     *
     * @param view
     * @param url
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        hideErrorPage(view);
        if (pagefinish != null) {
            pagefinish.pageFinished(view, url);
        }
        pageIsFinished = true;
    }

    /**
     * 加载错误
     *
     * @param view
     * @param request
     * @param error
     */
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        showErrorPage(view);
    }

    /**
     * 加载错误
     *
     * @param view
     * @param errorCode
     * @param description
     * @param failingUrl
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        showErrorPage(view);
    }

    private View mErrorView;
    private boolean loadError;

    /**
     * 显示错误页
     *
     * @param webView
     */
    protected void showErrorPage(final WebView webView) {
        if (customizeErrorPage) {
            ViewGroup viewParent = (ViewGroup) webView.getParent();
            if (mErrorView == null) {
                mErrorView = View.inflate(webView.getContext(), R.layout.view_webview_error, null);
                mErrorView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        webView.reload();
                    }
                });
            }
            int i = viewParent.indexOfChild(webView);
            if (viewParent.indexOfChild(mErrorView) == -1) {
                ViewGroup.LayoutParams lp = webView.getLayoutParams();
                webView.setVisibility(View.GONE);
                viewParent.addView(mErrorView, i, lp);
            }
            loadError = true;
        }
    }

    /****
     * 隐藏错误页
     */
    protected void hideErrorPage(final WebView webView) {
        if (customizeErrorPage && mErrorView != null) {
            if (!loadError) {
                ViewGroup viewParent = (ViewGroup) mErrorView.getParent();
                if (viewParent != null) {
                    int i = viewParent.indexOfChild(mErrorView);
                    if (i != -1) {
                        viewParent.removeView(mErrorView);
                    }
                }
                loadError = false;
                mErrorView = null;
//              防止较卡的手机出现系统自带的错误页
                webView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webView.setVisibility(View.VISIBLE);
                    }
                }, 200);
            }
        }
    }
}
