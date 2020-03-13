package com.android.jesse.biliparser.activity;

import android.content.Intent;
import android.text.TextUtils;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.network.base.SimpleActivity;
import com.android.jesse.biliparser.network.model.bean.SearchResultVideoBean;
import com.android.jesse.biliparser.network.util.ToastUtil;
import com.android.jesse.biliparser.utils.LogUtils;
import com.android.jesse.biliparser.utils.ParseUtils;
import com.android.jesse.biliparser.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

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
    protected String getTitleName() {
        return "搜索结果展示";
    }

    @Override
    protected void onBackClick() {
        finish();
    }

    @Override
    protected void initEventAndData() {
        htmlContent = getIntent().getStringExtra(Constant.INTENT_KEY_SEARCH_RESULT);
        if(TextUtils.isEmpty(htmlContent)){
            ToastUtil.show("解析内容为空！");
            finish();
            return;
        }
        Document document = Jsoup.parse(htmlContent);
        Element node1 = document.selectFirst("div.mixin-list");
        Element node2 = node1.selectFirst("ul.video-list");
        Elements videoDataList = node2.select("li.video-item");
        List<SearchResultVideoBean> videoBeanList = ParseUtils.parseSearchResultHtml(videoDataList);
    }
}
