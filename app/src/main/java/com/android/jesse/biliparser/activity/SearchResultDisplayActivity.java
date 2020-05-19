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
import com.android.jesse.biliparser.utils.SharePreferenceUtil;
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
                NetLoadListener.getInstance().stopListening();
                Document nextPageDoc = (Document)msg.obj;
                if(nextPageDoc == null){
                    return;
                }
                parseNextPage(nextPageDoc);
            }else if(msg.what == 1){
                Toast.makeText(mContext, R.string.net_load_failed, Toast.LENGTH_SHORT).show();
            }
        }
    };
    private int page = 1;
    private int pageSize = 10;
    private String basePageUrl;//基础分页链接
    private List<Integer> lastPageCodeList;//上一页的hashCode集合
    private int searchType = Constant.FLAG_SEARCH_ANIM;

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
        searchType = getIntent().getIntExtra(Constant.KEY_SEARCH_TYPE,1);
        LogUtils.d(TAG+" searchType = "+searchType);
        if(document == null){
            tv_no_data.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            LogUtils.e(TAG+" document is empty");
            return;
        }
        lastPageCodeList = new ArrayList<>();
        waitDialog = new WaitDialog(mContext,R.style.Dialog_Translucent_Background);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        if(searchType == Constant.FLAG_SEARCH_ANIM){
            recyclerView.addItemDecoration(new OffsetRecyclerDivider(0,SizeUtils.dp2px(15)));
        }else if(searchType == Constant.FLAG_SEARCH_FILM_TELEVISION){
            recyclerView.addItemDecoration(new OffsetRecyclerDivider(0,SizeUtils.dp2px(20)));
        }
        parseDocuments(document);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMore();
            }
        });
        if(SharePreferenceUtil.getInt(Constant.SPKEY_ENTER_APP_COUNT) <= 1){
            ToastUtil.show(R.string.hint_longclick_expand_desc);
        }
    }

    //加载更多
    private void loadMore(){
        page++;
        String nextPageUrl = "";
        if(searchType == Constant.FLAG_SEARCH_ANIM){
            int pageStartIndex = basePageUrl.indexOf("page=");
            int pageEndIndex = basePageUrl.indexOf("&",pageStartIndex);
            String preString = basePageUrl.substring(0,pageStartIndex+"page=".length());
            String afterString = basePageUrl.substring(pageEndIndex,basePageUrl.length());
            nextPageUrl = preString.concat(page+"").concat(afterString);
        }else if(searchType == Constant.FLAG_SEARCH_FILM_TELEVISION){
            int pageStartIndex = basePageUrl.indexOf("pg-");
            int pageEndIndex = basePageUrl.indexOf("-wd",pageStartIndex);
            String preString = basePageUrl.substring(0,pageStartIndex+"pg-".length());
            String afterString = basePageUrl.substring(pageEndIndex,basePageUrl.length());
            nextPageUrl = preString.concat(page+"").concat(afterString);
        }
        LogUtils.d(TAG+" nextPageUrl = "+nextPageUrl);
        //访问url获取数据
        getData(nextPageUrl);
    }

    private NetLoadListener.Callback callback = new NetLoadListener.Callback() {
        @Override
        public void onNetLoadFailed() {
            mHandler.sendEmptyMessage(1);
        }
    };

    private void getData(String url){
        NetLoadListener.getInstance().startListening(callback);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
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
        if(searchType == Constant.FLAG_SEARCH_ANIM){
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
            if(urlElements.size() < pageSize){
                LogUtils.i(TAG+" no more items ");
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }else if(searchType == Constant.FLAG_SEARCH_FILM_TELEVISION){
            Element listContainerElement = document.selectFirst("ul.serach-ul");
            if(listContainerElement == null || listContainerElement.childrenSize() == 0){
                refreshLayout.finishLoadMoreWithNoMoreData();
                LogUtils.e(TAG+" next page is no more film datas");
                return;
            }
            Elements sectionListElements = listContainerElement.select("li");
            LogUtils.i(TAG+" sctionListElements size = "+sectionListElements.size());
            for(int i=0;i<sectionListElements.size();i++){
                SearchResultBean resultBean = new SearchResultBean();
                Element sectionElement = sectionListElements.get(i);
                //解析a标签@{
                Element aTag = sectionElement.selectFirst("a.list-img");
                String url = Constant.JIJI_BASE_URL.concat(aTag.attr("href"));
                resultBean.setUrl(url);
                String title = aTag.attr("title");
                resultBean.setTitle(title);
                Element imgTag = aTag.selectFirst("img.loading");
                String cover = imgTag.attr("src");
                resultBean.setCover(cover);
                Element scoreElement = aTag.selectFirst("label.score");
                String score = scoreElement.text();
                resultBean.setScore(score);
                Element sectionCountElement = aTag.selectFirst("label.title");
                String sectionCount = sectionCountElement.text();
                resultBean.setSectionCount(sectionCount);
                //@}
                //解析p标签列表@{
                Elements pList = sectionElement.select("p");
                if(pList.size() > 0){
                    //导演列表
                    Elements directorAList = pList.get(0).select("a");
                    if(directorAList != null && directorAList.size() > 0){
                        List<String> directorList = new ArrayList<>();
                        for(int k=0;k<directorAList.size();k++){
                            String director = directorAList.get(k).text();
                            directorList.add(director);
                        }
                        resultBean.setDirectorList(directorList);
                    }
                    //演员列表
                    Elements actorAList = pList.get(1).select("a");
                    if(actorAList != null && actorAList.size() > 0){
                        List<String> actorList = new ArrayList<>();
                        for(int j=0;j<actorAList.size();j++){
                            String actor = actorAList.get(j).text();
                            actorList.add(actor);
                        }
                        resultBean.setActorList(actorList);
                    }
                    //type
                    String type = pList.get(2).text().replaceAll("\"","");
                    resultBean.setType(type);
                    //area
                    Element areaA = pList.get(3).selectFirst("a");
                    if(areaA != null){
                        String area = areaA.text();
                        resultBean.setArea(area);
                    }
                    //上映时间
                    String publishDate = pList.get(4).text().replaceAll("\"","");
                    resultBean.setPublishDate(publishDate);
                    //更新时间
                    String updateDate = pList.get(5).text().replaceAll("\"","");
                    resultBean.setUpdateDate(updateDate);
                    //剧情介绍
                    String desc = pList.get(6).text().replaceAll("\"","");
                    resultBean.setDesc(desc);
                }
                //@}
                LogUtils.d(TAG+" "+resultBean.toString());
                searchResultBeanList.add(resultBean);
                if(!TextUtils.isEmpty(title)){
                    lastPageCodeList.add(title.hashCode());
                }
            }
        }
        refreshLayout.finishLoadMore();
        adapter.notifyDataSetChanged();
    }

    private void parseDocuments(Document document){
        waitDialog.show();
        searchResultBeanList = new ArrayList<>();
        lastPageCodeList.clear();
        if(searchType == Constant.FLAG_SEARCH_ANIM){
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
        }else if(searchType == Constant.FLAG_SEARCH_FILM_TELEVISION){
            Element pageElement = document.selectFirst("div.page");
            if(pageElement != null){
                Element pageA = pageElement.selectFirst("a[target]");
                if(pageA == null){
                    refreshLayout.setEnableLoadMore(false);
                    LogUtils.e(TAG+" no more pages");
                }else{
                    basePageUrl = Constant.JIJI_BASE_URL.concat(pageA.attr("href"));
                    LogUtils.i(TAG+" basePageUrl = "+basePageUrl);
                    if(TextUtils.isEmpty(basePageUrl) || basePageUrl.equals(Constant.JIJI_BASE_URL)){
                        refreshLayout.setEnableLoadMore(false);
                    }else{
                        refreshLayout.setEnableLoadMore(true);
                    }
                }
            }
            Element listContainerElement = document.selectFirst("ul.serach-ul");
            if(listContainerElement == null || listContainerElement.childrenSize() == 0){
                tv_no_data.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                waitDialog.dismiss();
                LogUtils.e(TAG+" no film datas");
                return;
            }
            Elements sectionListElements = listContainerElement.select("li");
            LogUtils.i(TAG+" sctionListElements size = "+sectionListElements.size());
            for(int i=0;i<sectionListElements.size();i++){
                SearchResultBean resultBean = new SearchResultBean();
                Element sectionElement = sectionListElements.get(i);
                //解析a标签@{
                Element aTag = sectionElement.selectFirst("a.list-img");
                String url = Constant.JIJI_BASE_URL.concat(aTag.attr("href"));
                resultBean.setUrl(url);
                String title = aTag.attr("title");
                resultBean.setTitle(title);
                Element imgTag = aTag.selectFirst("img.loading");
                String cover = imgTag.attr("src");
                resultBean.setCover(cover);
                Element scoreElement = aTag.selectFirst("label.score");
                String score = scoreElement.text();
                resultBean.setScore(score);
                Element sectionCountElement = aTag.selectFirst("label.title");
                String sectionCount = sectionCountElement.text();
                resultBean.setSectionCount(sectionCount);
                //@}
                //解析p标签列表@{
                Elements pList = sectionElement.select("p");
                if(pList.size() > 0){
                    //导演列表
                    Elements directorAList = pList.get(0).select("a");
                    if(directorAList != null && directorAList.size() > 0){
                        List<String> directorList = new ArrayList<>();
                        for(int k=0;k<directorAList.size();k++){
                            String director = directorAList.get(k).text();
                            directorList.add(director);
                        }
                        resultBean.setDirectorList(directorList);
                    }
                    //演员列表
                    Elements actorAList = pList.get(1).select("a");
                    if(actorAList != null && actorAList.size() > 0){
                        List<String> actorList = new ArrayList<>();
                        for(int j=0;j<actorAList.size();j++){
                            String actor = actorAList.get(j).text();
                            actorList.add(actor);
                        }
                        resultBean.setActorList(actorList);
                    }
                    //type
                    String type = pList.get(2).text().replaceAll("\"","");
                    resultBean.setType(type);
                    //area
                    Element areaA = pList.get(3).selectFirst("a");
                    if(areaA != null){
                        String area = areaA.text();
                        resultBean.setArea(area);
                    }
                    //上映时间
                    String publishDate = pList.get(4).text().replaceAll("\"","");
                    resultBean.setPublishDate(publishDate);
                    //更新时间
                    String updateDate = pList.get(5).text().replaceAll("\"","");
                    resultBean.setUpdateDate(updateDate);
                    //剧情介绍
                    String desc = pList.get(6).text().replaceAll("\"","");
                    resultBean.setDesc(desc);
                }
                //@}
                LogUtils.d(TAG+" "+resultBean.toString());
                searchResultBeanList.add(resultBean);
                if(!TextUtils.isEmpty(title)){
                    lastPageCodeList.add(title.hashCode());
                }
            }
        }
        waitDialog.dismiss();

        adapter = new SearchResultAdapter(mContext,searchResultBeanList,searchType);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new SearchResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SearchResultBean resultBean) {
                Session.getSession().put(Constant.KEY_RESULT_BEAN,resultBean);
                Intent intent = new Intent(mContext,ChooseSectionActivity.class);
                intent.putExtra(Constant.KEY_TITLE,resultBean.getTitle());
                intent.putExtra(Constant.KEY_URL,resultBean.getUrl());
                intent.putExtra(Constant.KEY_SEARCH_TYPE,searchType);
                startActivity(intent);
            }
        });
    }

}
