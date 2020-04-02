package com.android.jesse.biliparser.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.network.base.SimpleActivity;
import com.android.jesse.biliparser.utils.LogUtils;
import com.android.jesse.biliparser.utils.Session;
import com.blankj.utilcode.util.ToastUtils;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import cn.jzvd.JZVideoPlayerStandard;


public class VideoPlayActivity extends SimpleActivity {

    private static final String TAG = VideoPlayActivity.class.getSimpleName();

    @BindView(R.id.video_play)
    JZVideoPlayerStandard video_play;
    private boolean isPlayResume;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                video_play.thumbImageView.setImageBitmap(firstFrame);
            }
        }
    };
    private Bitmap firstFrame;
    private String url;
    private String title;

    @Override
    protected int getLayout() {
        return R.layout.activity_video_play;
    }

    @Override
    protected void initEventAndData() {
        video_play.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoPlayActivity.this.finish();
            }
        });
        if (getIntent() != null) {
            url = getIntent().getStringExtra(Constant.KEY_URL);///"http://www.iqiyi.com/v_19rrok4nt0.html";
            //https://api.52wyb.com/webcloud/?v=http://www.iqiyi.com/v_19rrok4nt0.html
            title = getIntent().getStringExtra(Constant.KEY_TITLE);
        }
        video_play.setUp(url, JZVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, title);
        video_play.titleTextView.setText(title);
        setFirstFrameDrawable(url);
        //以下两种自动播放方式
//        video_play.startButton.performClick();
        //video_play.startVideo();
    }

    private void setFirstFrameDrawable(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    LogUtils.d(TAG + "video_url = " + url);
                    mmr.setDataSource(url, new HashMap<String, String>());
                    firstFrame = mmr.getFrameAtTime();
                    mHandler.sendEmptyMessage(0);
                } catch (RuntimeException re) {//有可能url不可用
                    re.printStackTrace();
                    LogUtils.e(TAG + " RuntimeException occur : " + re.getLocalizedMessage() + " \n " + re.getMessage());
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isPlayResume) {
            JZVideoPlayerStandard.goOnPlayOnResume();

            isPlayResume = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        JZVideoPlayerStandard.goOnPlayOnPause();

        isPlayResume = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (video_play != null) {
            video_play.release();
        }
        JZVideoPlayerStandard.releaseAllVideos();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

}
