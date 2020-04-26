package com.android.jesse.biliparser.activity;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.network.base.SimpleActivity;

/**
 * @Description: 关于我们
 * @author: zhangshihao
 * @date: 2020/4/26
 */
public class AboutUsActivity extends SimpleActivity {

    private static final String TAG = AboutUsActivity.class.getSimpleName();

    @Override
    protected int getLayout() {
        return R.layout.about_us_activity;
    }

    @Override
    protected void initEventAndData() {

    }

    @Override
    protected String getTitleName() {
        return "关于我们";
    }

    @Override
    protected void onBackClick() {
        super.onBackClick();
        finish();
    }
}
