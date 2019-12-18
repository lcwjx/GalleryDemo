package com.wjx.gallery.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wjx.gallery.R;
import com.wjx.gallery.utils.StatusBarUtils;

/**
 * @ProjectName :GalleryDemo
 * @Description :
 * @Author chen.li.o
 * @DATE 2019-12-10
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
    }

    /**
     * Sets status bar.
     */
    protected void setStatusBar() {
        setDarkStatusIcon(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            StatusBarUtils.setColorNoTranslucent(this, getResources().getColor(R.color.gallery_white_ffffff));
        } else {
            StatusBarUtils
                    .setColor(this, getResources().getColor(R.color.gallery_white_ffffff), StatusBarUtils.DEFAULT_STATUS_BAR_ALPHA);
        }
        //处理小米魅族状态栏问题
        StatusBarUtils.MIUISetStatusBarLightMode(getWindow(), true);
        StatusBarUtils.FlymeSetStatusBarLightMode(getWindow(), true);
    }

    /**
     * Sets dark status icon.
     *
     * @param bDark the b dark
     */
    public void setDarkStatusIcon(boolean bDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            View decorView = getWindow().getDecorView();
            int vis = decorView.getSystemUiVisibility();
            if (bDark) {
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(vis);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
