package com.lanqi.webviewpro.widget;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class LQWebChromeClient extends WebChromeClient implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
//    全屏切换的回调
    public interface ToggledFullscreenCallback {
        void toggledFullscreen(boolean fullscreen);
    }

    /**
     * 全屏播放时需要隐藏的view
     */
    private View noVideoView;
    /**
     * 全屏播放时显示的地方
     */
    private ViewGroup videoView;
    /**
     * 全屏播放缓冲时的显示动画
     */
    private View loadingView;

    /**
     * 是否全屏播放视频
     */
    private boolean isVideoFullscreen;
    /**
     * webView的视频播放View
     */
    private FrameLayout videoViewContainer;
    /**
     * 全屏切换的回调
     */
    private CustomViewCallback videoViewCallback;
    /**
     * 网页加载进度
     */
    private ProgressBar progressBar;

    private ToggledFullscreenCallback toggledFullscreenCallback;

    @SuppressWarnings("unused")
    public LQWebChromeClient() {
    }

    @SuppressWarnings("unused")
    public LQWebChromeClient(View activityNonVideoView, ViewGroup activityVideoView) {
        this.noVideoView = activityNonVideoView;
        this.videoView = activityVideoView;
        this.loadingView = null;
        this.isVideoFullscreen = false;
    }

    @SuppressWarnings("unused")
    public LQWebChromeClient(View activityNonVideoView, ViewGroup activityVideoView, View loadingView) {
        this.noVideoView = activityNonVideoView;
        this.videoView = activityVideoView;
        this.loadingView = loadingView;
        this.isVideoFullscreen = false;
    }

    public boolean isVideoFullscreen() {
        return isVideoFullscreen;
    }

    @SuppressWarnings("unused")
    public void setOnToggledFullscreen(ToggledFullscreenCallback callback) {
        this.toggledFullscreenCallback = callback;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (videoView == null) {
            super.onShowCustomView(view, callback);
        } else {
            if (view instanceof FrameLayout) {
                FrameLayout frameLayout = (FrameLayout) view;
                View focusedChild = frameLayout.getFocusedChild();

//                更改状态
                this.isVideoFullscreen = true;
                this.videoViewContainer = frameLayout;
                this.videoViewCallback = callback;

//                隐藏布局
                noVideoView.setVisibility(View.INVISIBLE);
//                将网页视频的View ，加入显示的地方
                videoView.addView(videoViewContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                videoView.setVisibility(View.VISIBLE);

//                添加视频播放事件
                if (focusedChild instanceof android.widget.VideoView) {
                    // android.widget.VideoView (typically API level <11)
                    android.widget.VideoView videoView = (android.widget.VideoView) focusedChild;

                    // Handle all the required events
                    videoView.setOnPreparedListener(this);
                    videoView.setOnCompletionListener(this);
                    videoView.setOnErrorListener(this);
                }

                // Notify full-screen change
                if (toggledFullscreenCallback != null) {
                    toggledFullscreenCallback.toggledFullscreen(true);
                }
            }else {
                super.onShowCustomView(view, callback);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) // Available in API level 14+, deprecated in API level 18+
    {
        onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        if (videoView == null) {
            super.onHideCustomView();
        } else {
            if (isVideoFullscreen) {
                videoView.setVisibility(View.INVISIBLE);
                videoView.removeView(videoViewContainer);
                noVideoView.setVisibility(View.VISIBLE);

                // Call back (only in API level <19, because in API level 19+ with chromium webview it crashes)
                if (videoViewCallback != null && !videoViewCallback.getClass().getName().contains(".chromium.")) {
                    videoViewCallback.onCustomViewHidden();
                }

                isVideoFullscreen = false;
                videoViewContainer = null;
                videoViewCallback = null;

                if (toggledFullscreenCallback != null) {
                    toggledFullscreenCallback.toggledFullscreen(false);
                }
            }else {
                super.onHideCustomView();
            }
        }
    }

//    缓冲加载的回调方法
    @Override
    public View getVideoLoadingProgressView()
    {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
            return loadingView;
        } else {
            return super.getVideoLoadingProgressView();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }

    /**视频播放完毕
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp)
    {
        onHideCustomView();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        return false;
    }

    /**退出全屏播放
     * @return
     */
    @SuppressWarnings("unused")
    public boolean onBackPressed() {
        if (isVideoFullscreen) {
            onHideCustomView();
            return true;
        } else {
            return false;
        }
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    /**设置进度条
     * @param progressBar
     */
    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
        this.progressBar.setMax(100);
        this.progressBar.setProgress(0);
    }

    /**页面加载进度处理
     * @param view
     * @param newProgress
     */
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        handle.removeMessages(1);
        if (progressBar != null) {
            if (progressBar.getProgress() == 0) {
                progressBar.setVisibility(View.VISIBLE);
            }
            for (int i = this.progressBar.getProgress(); i <= newProgress; i++) {
                Message message = new Message();
                message.what = 1;
                message.obj = i;
                handle.sendMessageDelayed(message, i * 5);
            }
        }
        super.onProgressChanged(view, newProgress);
    }

    private Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressBar.setProgress((int) msg.obj);
            if ((int) msg.obj == 100) {
                progressBar.setVisibility(View.GONE);
                progressBar.setProgress(0);
            }
        }
    };
}
