package com.android.jesse.biliparser.network.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.base.App;
import com.android.jesse.biliparser.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by codeest on 16/8/11.
 * 无MVP的activity基类
 */

public abstract class SimpleActivity extends SupportActivity {

    protected Activity mContext;
    private Unbinder mUnBinder;
    protected ImageView iv_back;
    protected TextView tv_title;
    protected ImageView iv_right;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        initTitleBar();
        Utils.setDarkStatusBar(this);
        mUnBinder = ButterKnife.bind(this);
        mContext = this;
        onViewCreated();
        App.getInstance().addActivity(this);
        initEventAndData();
    }

    private void initTitleBar(){
        if(findViewById(R.id.iv_back) != null){
            iv_back = findViewById(R.id.iv_back);
            iv_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackClick();
                }
            });
            tv_title = findViewById(R.id.tv_title);
            tv_title.setText(getTitleName());
        }
        if(findViewById(R.id.iv_right) != null){
            iv_right = findViewById(R.id.iv_right);
            iv_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRightClick();
                }
            });
        }
    }

    /**
     * 右侧按钮点击事件
     */
    protected void onRightClick(){

    }

    /**
     * back按钮点击事件
     */
    protected void onBackClick(){

    };

    protected String getTitleName(){
        return "搜索";
    };

    protected void setToolBar(Toolbar toolbar, String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressedSupport();
            }
        });
    }

    protected void onViewCreated() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getInstance().removeActivity(this);
        mUnBinder.unbind();
        System.gc();
    }

    protected abstract int getLayout();

    protected abstract void initEventAndData();
}
