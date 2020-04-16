package com.android.jesse.biliparser.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.TextView;
import android.widget.Toast;

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
import com.android.jesse.biliparser.utils.NetLoadListener;
import com.android.jesse.biliparser.utils.ParseUtils;
import com.android.jesse.biliparser.utils.Session;
import com.android.jesse.biliparser.utils.Utils;
import com.blankj.utilcode.util.SizeUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

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
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private List<SearchResultBean> searchResultBeanList;
    private SearchResultAdapter adapter;
    private WaitDialog waitDialog;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                Document nextPageDoc = (Document)msg.obj;
                if(nextPageDoc == null){
                    return;
                }
                parseNextPage(nextPageDoc);
            }
        }
    };
    private int page = 1;
    private int pageSize = 10;
    private String basePageUrl;//基础分页链接
    private List<Integer> lastPageCodeList;//上一页的hashCode集合

    @Override
    protected int getLayout() {
        return R.layout.search_result_display_activity;
    }

    @Override
    protected void onBackClick() {
        finish();
    }

    @Override
    protected void initEventAndData() {
        Document document = (Document) Session.getSession().get(Constant.KEY_DOCUMENT);
        if(getIntent() != null && !TextUtils.isEmpty(getIntent().getStringExtra(Constant.KEY_TITLE))){
            tv_title.setText(getIntent().getStringExtra(Constant.KEY_TITLE));
        }else{
            tv_title.setText("搜索结果展示");
        }
        if(document == null){
            tv_no_data.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            LogUtils.e(TAG+" document is empty");
            return;
        }
        lastPageCodeList = new ArrayList<>();
        waitDialog = new WaitDialog(mContext,R.style.Dialog_Translucent_Background);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new OffsetRecyclerDivider(0,SizeUtils.dp2px(15)));
        parseDocuments(document);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMore();
            }
        });
    }

    //加载更多
    private void loadMore(){
        page++;
        String nextPageUrl = "";
        int pageStartIndex = basePageUrl.indexOf("page=");
        int pageEndIndex = basePageUrl.indexOf("&",pageStartIndex);
        String preString = basePageUrl.substring(0,pageStartIndex+"page=".length());
        String afterString = basePageUrl.substring(pageEndIndex,basePageUrl.length());
        nextPageUrl = preString.concat(page+"").concat(afterString);
        LogUtils.d(TAG+" nextPageUrl = "+nextPageUrl);
        //访问url获取数据
        getData(nextPageUrl);
    }

    private NetLoadListener.Callback callback = new NetLoadListener.Callback() {
        @Override
        public void onNetLoadFailed() {
            Toast.makeText(mContext, R.string.net_load_failed, Toast.LENGTH_SHORT).show();
        }
    };

    private void getData(String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NetLoadListener.getInstance().startListening(callback);
                    Connection connection = Jsoup.connect(url);
                    connection.userAgent(Constant.USER_AGENT_FORPC);
//                    connection.postDataCharset("GB2312");//关键中的关键！！
                    Document document = connection.method(Connection.Method.POST).post();
                    LogUtils.d(TAG + " html = \n" + document.outerHtml());
                    mHandler.sendMessage(Message.obtain(mHandler, 0, document));
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    waitDialog.dismiss();
                    NetLoadListener.getInstance().stopListening();
                }
            }
        }).start();
    }

    //解析下一页数据
    private void parseNextPage(Document document){
        Element baseElement = document.selectFirst("div.pics");
        Elements urlElements = baseElement.select("a[href][title]");
        Elements imgElements = baseElement.select("img[src]");
        Elements liElements = baseElement.getElementsByTag("li");
        if(Utils.isListEmpty(liElements)){
            LogUtils.e(TAG+" no more items");
            refreshLayout.finishLoadMoreWithNoMoreData();
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
        List<SearchResultBean> tempResultBeanList = new ArrayList<>();
        List<Integer> tempCodeList = new ArrayList<>();
        for(int i=0;i<urlElements.size();i++){
            SearchResultBean resultBean = new SearchResultBean();
            Element a = urlElements.get(i);
            resultBean.setUrl(Constant.SAKURA_SEARCH_URL+a.attr("href"));
            resultBean.setCover(imgElements.get(i).attr("src"));
            resultBean.setTitle(imgElements.get(i).attr("alt"));
            resultBean.setAlias(aliasList.get(i).text());
            resultBean.setInfos(infoList.get(i).text());
            resultBean.setDesc(descList.get(i).text());
            tempResultBeanList.add(resultBean);
            String title = resultBean.getTitle();
            if(!TextUtils.isEmpty(title)){
                tempCodeList.add(title.hashCode());
            }
        }
        if(!Utils.isListEmpty(lastPageCodeList) && !Utils.isListEmpty(tempCodeList)){
            if(lastPageCodeList.size() == tempCodeList.size()){
                boolean isEqual = true;
                for(int i=0;i<lastPageCodeList.size();i++){
                    if(lastPageCodeList.get(i).intValue() != tempCodeList.get(i).intValue()){
                        isEqual = false;
                    }
                }
                if(isEqual){//跟上一页是相同的数据
                    refreshLayout.finishLoadMoreWithNoMoreData();
                    LogUtils.e(TAG+" currentPage is same to lastPage,no more datas");
                    return;
                }
            }
        }
        searchResultBeanList.addAll(tempResultBeanList);
        if(urlElements.size() < 10){
            LogUtils.i(TAG+" no more items ");
            refreshLayout.finishLoadMoreWithNoMoreData();
        }
        refreshLayout.finishLoadMore();
        adapter.notifyDataSetChanged();
    }

    private void parseDocuments(Document document){
        waitDialog.show();
        Element baseElement = document.selectFirst("div.pics");
        Elements urlElements = baseElement.select("a[href][title]");
        Elements imgElements = baseElement.select("img[src]");
        Elements liElements = baseElement.getElementsByTag("li");
        try{
            Element pagesElement = document.selectFirst("div.pages");
            basePageUrl = Constant.SAKURA_NEXT_PAGE_BASE_URL.concat(pagesElement.selectFirst("a[href]").attr("href"));
            LogUtils.i(TAG+" basePageUrl : "+basePageUrl);
            if(TextUtils.isEmpty(basePageUrl) || basePageUrl.equals(Constant.SAKURA_NEXT_PAGE_BASE_URL)){
                refreshLayout.setEnableLoadMore(false);
            }else{
                refreshLayout.setEnableLoadMore(true);
            }
        }catch (Exception e){
            e.printStackTrace();
            refreshLayout.setEnableLoadMore(false);
            LogUtils.e(TAG+" 该动漫没有更多分页");
        }
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
        lastPageCodeList.clear();
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
            String title = resultBean.getTitle();
            if(!TextUtils.isEmpty(title)){
                lastPageCodeList.add(title.hashCode());
            }
        }
        waitDialog.dismiss();

        adapter = new SearchResultAdapter(mContext,searchResultBeanList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new SearchResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SearchResultBean resultBean) {
                Session.getSession().put(Constant.KEY_RESULT_BEAN,resultBean);
                Intent intent = new Intent(mContext,ChooseSectionActivity.class);
                intent.putExtra(Constant.KEY_TITLE,resultBean.getTitle());
                intent.putExtra(Constant.KEY_URL,resultBean.getUrl());
                startActivity(intent);
            }
        });
    }

}
