package com.android.jesse.biliparser.activity;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.network.base.SimpleActivity;

/**
 * @Description: 新手帮助页面
 * @author: zhangshihao
 * @date: 2020/4/26
 */
public class RookieHelpActivity extends SimpleActivity {

    private static final String TAG = RookieHelpActivity.class.getSimpleName();

    @Override
    protected String getTitleName() {
        return "新手帮助";
    }

    @Override
    protected int getLayout() {
        return R.layout.rookie_help_activity;
    }

    @Override
    protected void initEventAndData() {

    }

    @Override
    protected void onBackClick() {
        super.onBackClick();
        finish();
    }
}
