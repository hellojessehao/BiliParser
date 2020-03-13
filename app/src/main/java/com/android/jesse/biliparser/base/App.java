package com.android.jesse.biliparser.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.android.jesse.biliparser.network.component.InitializeService;
import com.android.jesse.biliparser.network.di.component.AppComponent;
import com.android.jesse.biliparser.network.di.component.DaggerAppComponent;
import com.android.jesse.biliparser.network.di.module.AppModule;
import com.android.jesse.biliparser.network.di.module.HttpModule;
import com.android.jesse.biliparser.utils.CrashHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @author: zhangshihao
 * @date: 2020/3/12
 */
public class App extends Application implements Application.ActivityLifecycleCallbacks {

    public static Context mContext;
    public static App app;
    public static AppComponent appComponent;
    private Set<Activity> allActivities;

    public static int SCREEN_WIDTH = -1;
    public static int SCREEN_HEIGHT = -1;
    public static float DIMEN_RATE = -1.0F;
    public static int DIMEN_DPI = -1;
    private int countActivity = 0;//前台界面数

    public static synchronized App getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
        app = this;
        //初始化屏幕宽高
        getScreenSize();
        //在子线程中完成其他初始化
        InitializeService.start(this);
        //init
        //初始化崩溃监听器，崩溃日志将存于 mnt/sdcard/crash 目录下
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

        registerActivityLifecycleCallbacks(this);//注册APP生命周期监听
    }

    public static App getApp() {
        return app;
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void addActivity(Activity act) {
        if (allActivities == null) {
            allActivities = new HashSet<>();
        }
        allActivities.add(act);
    }

    public void removeActivity(Activity act) {
        if (allActivities != null) {
            allActivities.remove(act);
        }
    }

    public void removeOtherActivity(Class c) {
        if (allActivities != null) {
            synchronized (allActivities) {
                for (Activity act : allActivities) {
                    if (!act.getClass().equals(c))
                        act.finish();
                }
            }
        }
    }

    public void exitApp() {
        if (allActivities != null) {
            synchronized (allActivities) {
                for (Activity act : allActivities) {
                    act.finish();
                }
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public void getScreenSize() {
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        display.getMetrics(dm);
        DIMEN_RATE = dm.density / 1.0F;
        DIMEN_DPI = dm.densityDpi;
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;
        if (SCREEN_WIDTH > SCREEN_HEIGHT) {
            int t = SCREEN_HEIGHT;
            SCREEN_HEIGHT = SCREEN_WIDTH;
            SCREEN_WIDTH = t;
        }
    }

    public static AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(app))
                    .httpModule(new HttpModule())
                    .build();
        }
        return appComponent;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (countActivity == 0) {
            //进入前台操作
        }
        countActivity++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        countActivity--;
        if (countActivity <= 0) {
            //进入后台操作
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
