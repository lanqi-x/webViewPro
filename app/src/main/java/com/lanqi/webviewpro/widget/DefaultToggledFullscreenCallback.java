package com.lanqi.webviewpro.widget;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Blue.K on 2016/7/27.
 */
public class DefaultToggledFullscreenCallback implements LQWebChromeClient.ToggledFullscreenCallback {
    private boolean fullscreen;
    private Activity activity;
    private boolean hidestatusBar = true;

    public DefaultToggledFullscreenCallback(Activity activity) {
        this.activity = activity;
    }

    public boolean isHidestatusBar() {
        return hidestatusBar;
    }

    public void setHidestatusBar(boolean hidestatusBar) {
        this.hidestatusBar = hidestatusBar;
    }

    @Override
    public void toggledFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        if (fullscreen) {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            if (hidestatusBar) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            }
            attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            attrs.flags |= Window.FEATURE_NO_TITLE;
            activity.getWindow().setAttributes(attrs);
            if (android.os.Build.VERSION.SDK_INT >= 14) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            }
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        } else {

            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            if (hidestatusBar) {
                attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            }
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            activity.getWindow().setAttributes(attrs);
            if (android.os.Build.VERSION.SDK_INT >= 14) {
                //noinspection all
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//改为竖屏
        }
    }

    public boolean isFullscreen() {
        return fullscreen;
    }
}
