package com.android.jesse.biliparser.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.jesse.biliparser.network.base.BaseActivity;


/**
 * @ClassName: ScreenManager
 * @Desciption: 屏幕管理类
 * @author: yichaohua
 * @date: 2017-12-29
 */
public class ScreenManager {

    private static final String TAG = "ScreenManager";

    private static ScreenManager instance;

    public synchronized static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    /**
     * 窗口全屏
     */
    public void setFullScreen(boolean isChange,BaseActivity mActivity) {
        if(!isChange){
            return;
        }
        mActivity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * 沉浸状态栏
     */
    public void setStatusBar(boolean isChange,Activity mActivity) {
        if (!isChange){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            mActivity.getWindow().setStatusBarColor(mActivity.getResources().getColor(R.color.window_status_bar));
            // 透明状态栏
            Window window = mActivity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    /*| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION*/
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public boolean setDeepStatusBar(boolean isChange,Activity mActivity) {
        if (!isChange){
            return false;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // 透明状态栏
            Window window = mActivity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    /*| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION*/
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

            //设置状态栏文字颜色及图标为深色
            mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            return true;
        }else {
            return false;
        }
    }

    public void setStatusBar(boolean isChange,int colorResId,Activity mActivity) {
        if (!isChange){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mActivity.getWindow().setStatusBarColor(mActivity.getResources().getColor(colorResId));

        }
    }

    /**
     * 旋转屏幕
     **/
    public void setScreenRoate(boolean isChange, BaseActivity mActivity) {
        if (!isChange) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 获取状态栏高度
     **/
    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
