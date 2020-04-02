package com.android.jesse.biliparser.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.TextView;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.adapter.SearchResultAdapter;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.components.WaitDialog;
import com.android.jesse.biliparser.network.base.SimpleActivity;
import com.android.jesse.biliparser.network.component.OffsetRecyclerDivider;
import com.android.jesse.biliparser.network.model.bean.SearchResultBean;
import com.android.jesse.biliparser.network.model.bean.SearchResultVideoBean;
import com.android.jesse.biliparser.network.util.ToastUtil;
import com.android.jesse.biliparser.utils.LogUtils;
import com.android.jesse.biliparser.utils.ParseUtils;
import com.android.jesse.biliparser.utils.Session;
import com.android.jesse.biliparser.utils.Utils;
import com.blankj.utilcode.util.SizeUtils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * @Description: 搜索结果展示页面
 * @author: zhangshihao
 * @date: 2020/3/13
 */
public class SearchResultDisplayActivity extends SimpleActivity {

    private static final String TAG = SearchResultDisplayActivity.class.getSimpleName();

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_no_data)
    TextView tv_no_data;

    private List<SearchResultBean> searchResultBeanList;
    private SearchResultAdapter adapter;
    private WaitDialog waitDialog;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

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
        Document document = (Document) Session.getSession().get(Constant.KEY_DOCUMENT);
        if(document == null){
            tv_no_data.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            LogUtils.e(TAG+" document is empty");
            return;
        }
        waitDialog = new WaitDialog(mContext,R.style.Dialog_Translucent_Background);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new OffsetRecyclerDivider(0,SizeUtils.dp2px(15)));
        parseDocuments(document);
    }

    private void parseDocuments(Document document){
        waitDialog.show();
        Element baseElement = document.selectFirst("div.pics");
        Elements urlElements = baseElement.select("a[href][title]");
        Elements imgElements = baseElement.select("img[src]");
        Elements liElements = baseElement.getElementsByTag("li");
        if(Utils.isListEmpty(liElements)){
            tv_no_data.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            waitDialog.dismiss();
            LogUtils.e(TAG+" no search result find");
            return;
        }
        List<Element> aliasList = new ArrayList<>();
        List<Element> infoList = new ArrayList<>();
        List<Element> descList = new ArrayList<>();
        for(Element li : liElements){
            Element firstSpan = li.selectFirst("span");
            aliasList.add(firstSpan);
            Element secondSpan = li.after(firstSpan).selectFirst("span");
            infoList.add(secondSpan);
            Element p = li.selectFirst("p");
            descList.add(p);
        }
        searchResultBeanList = new ArrayList<>();
        for(int i=0;i<urlElements.size();i++){
            SearchResultBean resultBean = new SearchResultBean();
            Element a = urlElements.get(i);
            resultBean.setUrl(Constant.SAKURA_SEARCH_URL+a.attr("href"));
            resultBean.setCover(imgElements.get(i).attr("src"));
            resultBean.setTitle(imgElements.get(i).attr("alt"));
            resultBean.setAlias(aliasList.get(i).text());
            resultBean.setInfos(infoList.get(i).text());
            resultBean.setDesc(descList.get(i).text());
            searchResultBeanList.add(resultBean);
            LogUtils.d(TAG+" \n"+resultBean.toString());
        }
        waitDialog.dismiss();

        adapter = new SearchResultAdapter(mContext,searchResultBeanList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new SearchResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SearchResultBean resultBean) {
                Intent intent = new Intent(mContext,ChooseSectionActivity.class);
                intent.putExtra(Constant.KEY_TITLE,resultBean.getTitle());
                intent.putExtra(Constant.KEY_URL,resultBean.getUrl());
                startActivity(intent);
            }
        });
    }

}
