package com.android.jesse.biliparser.activity;

import android.content.Intent;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.network.base.SimpleActivity;

/**
 * @Description: 搜索结果展示页面
 * @author: zhangshihao
 * @date: 2020/3/13
 */
public class SearchResultDisplayActivity extends SimpleActivity {

    private static final String TAG = SearchResultDisplayActivity.class.getSimpleName();

    private String htmlContent;

    @Override
    protected int getLayout() {
        return R.layout.search_result_display_activity;
    }

    @Override
    protected void initEventAndData() {
        Intent intent = getIntent();
        if(intent != null){
            htmlContent = intent.getStringExtra(Constant.INTENT_KEY_SEARCH_RESULT);
        }
    }
}
