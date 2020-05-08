package com.android.jesse.biliparser.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.adapter.MoreAnimAdapter;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.components.WaitDialog;
import com.android.jesse.biliparser.network.base.SimpleActivity;
import com.android.jesse.biliparser.network.component.OffsetRecyclerDivider;
import com.android.jesse.biliparser.network.model.bean.AnimRecommendItemBean;
import com.android.jesse.biliparser.network.model.bean.MoreAnimItemBean;
import com.android.jesse.biliparser.network.model.bean.SearchResultBean;
import com.android.jesse.biliparser.utils.LogUtils;
import com.android.jesse.biliparser.utils.NetLoadListener;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * @Description:
 * @author: zhangshihao
 * @date: 2020/4/28
 */
public class MoreAnimActivity extends SimpleActivity {
    private static final String TAG = MoreAnimActivity.class.getSimpleName();

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_no_data)
    TextView tv_no_data;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private String url;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NetLoadListener.getInstance().stopListening();
            if (msg.what == 0) {
                if (msg.obj == null) {
                    Toast.makeText(mContext, R.string.data_list_is_empty, Toast.LENGTH_SHORT).show();
                    LogUtils.e(TAG + " obj is null");
                    return;
                }
                parseDocument(typeId, (Document) msg.obj);
            } else if (msg.what == 1) {
                waitDialog.dismiss();
                setEmptyState();
            } else if (msg.what == 2) {
                if (msg.obj == null) {
                    Toast.makeText(mContext, R.string.data_list_is_empty, Toast.LENGTH_SHORT).show();
                    LogUtils.e(TAG + " obj is null " + msg.what);
                    return;
                }
                parseMoreAnim((Document) msg.obj);
            }
        }
    };
    private WaitDialog waitDialog;
    private List<MoreAnimItemBean> itemBeanList = new ArrayList<>();
    private MoreAnimAdapter adapter;
    private int typeId = Constant.ANIM_TYPE_MOST_LAST;//参照Constant里ANIM_xxxx值
    //日漫
    private int page = 1;
    private int pageSize = 10;
    private String basePageUrl;//基础分页链接
    private List<Integer> lastPageCodeList = new ArrayList<>();//上一页的hashCode集合
    private int searchType = Constant.FLAG_SEARCH_ANIM;
    private List<SearchResultBean> searchResultBeanList = new ArrayList<>();

    @Override
    protected String getTitleName() {
        return "更多";
    }

    @Override
    protected void onBackClick() {
        super.onBackClick();
        finish();
    }

    @Override
    protected int getLayout() {
        return R.layout.more_anim_activity;
    }

    private NetLoadListener.Callback callback = new NetLoadListener.Callback() {
        @Override
        public void onNetLoadFailed() {
            mHandler.sendEmptyMessage(1);
        }
    };

    @Override
    protected void initEventAndData() {
        if (getIntent() != null) {
            typeId = getIntent().getIntExtra(Constant.KEY_TYPE_ID, Constant.ANIM_TYPE_MOST_LAST);
            url = getIntent().getStringExtra(Constant.KEY_URL);
            LogUtils.d(TAG + " typeId = " + typeId + " url = " + url);
        }
        waitDialog = new WaitDialog(mContext, R.style.Dialog_Translucent_Background);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new OffsetRecyclerDivider(0, SizeUtils.dp2px(15)));
        switch (typeId) {
            case Constant.ANIM_TYPE_MOST_LAST:
                adapter = new MoreAnimAdapter(mContext, itemBeanList,typeId);
                refreshLayout.setEnableLoadMore(false);//最新动漫不做分页
                if (!TextUtils.isEmpty(url)) {
                    waitDialog.show();
                    NetLoadListener.getInstance().startListening(callback);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Connection connection = Jsoup.connect(url);
                                connection.userAgent(Constant.USER_AGENT_FORPC);
                                Document document = connection.get();
                                mHandler.sendMessage(Message.obtain(mHandler, 0, document));
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                                LogUtils.e(TAG + " ioe : " + ioe.toString());
                                mHandler.sendMessage(Message.obtain(mHandler, 1));
                            }
                        }
                    }).start();
                } else {
                    setEmptyState();
                }
                break;
            case Constant.ANIM_TYPE_JAPEN:
            case Constant.ANIM_TYPE_CHINA:
            case Constant.ANIM_TYPE_AMERICA:
                adapter = new MoreAnimAdapter(mContext, searchResultBeanList,typeId);
                refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                        loadMore();
                    }
                });
                waitDialog.show();
                requestMoreUrl();
                break;
            case Constant.ANIM_TYPE_FILM:
                pageSize = 35;
                recyclerView.setLayoutManager(new GridLayoutManager(mContext,3));
                recyclerView.addItemDecoration(new OffsetRecyclerDivider(8,SizeUtils.dp2px(15)));
                adapter = new MoreAnimAdapter(mContext, searchResultBeanList,typeId);
                refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                        loadMore();
                    }
                });
                if (!TextUtils.isEmpty(url)) {
                    waitDialog.show();
                    NetLoadListener.getInstance().startListening(callback);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Connection connection = Jsoup.connect(url);
                                connection.userAgent(Constant.USER_AGENT_FORPC);
                                Document document = connection.get();
                                mHandler.sendMessage(Message.obtain(mHandler, 0, document));
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                                LogUtils.e(TAG + " ioe : " + ioe.toString());
                                mHandler.sendMessage(Message.obtain(mHandler, 1));
                            }
                        }
                    }).start();
                } else {
                    setEmptyState();
                }
                break;
        }
        recyclerView.setAdapter(adapter);
    }

    private void setEmptyState() {
        tv_no_data.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        refreshLayout.setEnableLoadMore(false);
    }

    private void parseDocument(int typeId, Document document) {
        if (typeId == Constant.ANIM_TYPE_MOST_LAST) {//最新更新里的更多动漫
            Element listContainer = document.selectFirst("div.topli");
            if (listContainer == null) {
                setEmptyState();
                return;
            }
            Elements liList = listContainer.select("li");
            if (!Utils.isListEmpty(liList)) {
                for (int i = 0; i < liList.size(); i++) {
                    Element li = liList.get(i);
                    Elements aList = li.select("a");
                    MoreAnimItemBean itemBean = new MoreAnimItemBean();
                    if (!Utils.isListEmpty(aList)) {
                        for (int j = 0; j < aList.size(); j++) {
                            Element a = aList.get(j);
                            if (j == 0) {
                                itemBean.setType(a.text());
                            } else if (j == 1) {
                                itemBean.setTitle(a.text());
                                itemBean.setUrl(Constant.SAKURA_SEARCH_URL.concat(a.attr("href")));
                            } else if (j == 2) {
                                itemBean.setSectionCount(a.text());
                            }
                        }
                    } else {
                        LogUtils.e(TAG + " alist is empty");
                    }
                    Element em = li.selectFirst("em");
                    itemBean.setUpdateDate(em.text());
                    itemBeanList.add(itemBean);
                }
                LogUtils.d(TAG + " itemBeanList : \n" + Arrays.toString(itemBeanList.toArray()));
            } else {
                setEmptyState();
            }
            waitDialog.dismiss();
            if (!Utils.isListEmpty(itemBeanList)) {
                adapter.notifyDataSetChanged();
            } else {
                setEmptyState();
            }
        } else if (typeId == Constant.ANIM_TYPE_FILM) {//更多动漫电影
            //获取分页链接 /list/1_2.html
            if(TextUtils.isEmpty(basePageUrl)){
                try{
                    Element pagesElement = document.selectFirst("div.pages");
                    basePageUrl = Constant.SAKURA_SEARCH_URL.concat(pagesElement.selectFirst("a[href]").attr("href"));
                    LogUtils.i(TAG+" basePageUrl : "+basePageUrl);
                    if(TextUtils.isEmpty(basePageUrl) || basePageUrl.equals(Constant.SAKURA_SEARCH_URL)){
                        refreshLayout.setEnableLoadMore(false);
                    }else{
                        refreshLayout.setEnableLoadMore(true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    refreshLayout.setEnableLoadMore(false);
                    LogUtils.e(TAG+" 该动漫没有更多分页");
                }
            }
            //解析列表数据
            List<SearchResultBean> tempResultBeanList = new ArrayList<>();
            List<Integer> tempCodeList = new ArrayList<>();
            Element container = document.selectFirst("div#contrainer");
            if(container != null){
                Element imgContainer = container.selectFirst("div.img");
                Elements liList = imgContainer.select("li");
                if(!Utils.isListEmpty(liList)){
                    for(int i=0;i<liList.size();i++){
                        SearchResultBean resultBean = new SearchResultBean();
                        resultBean.setSearchType(searchType);
                        Element li = liList.get(i);
                        Element a = li.selectFirst("a");
                        String url = Constant.SAKURA_SEARCH_URL.concat(a.attr("href"));
                        resultBean.setUrl(url);
                        Element img = a.selectFirst("img");
                        String cover = img.attr("src");
                        resultBean.setCover(cover);
                        String title = img.attr("alt");
                        resultBean.setTitle(title);
                        if(!TextUtils.isEmpty(title)){
                            tempCodeList.add(title.hashCode());
                        }
                        tempResultBeanList.add(resultBean);
                    }
                }else{
                    LogUtils.e(TAG+" lilist is empty");
                    setEmptyState();
                }
            }else{
                LogUtils.e(TAG+" container is null");
                setEmptyState();
            }
            waitDialog.dismiss();
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
            lastPageCodeList.addAll(tempCodeList);
            if(!Utils.isListEmpty(searchResultBeanList)){
                refreshLayout.finishLoadMore();
                adapter.notifyDataSetChanged();
            }else{
                setEmptyState();
            }
            if(tempResultBeanList.size() < pageSize){
                refreshLayout.setEnableLoadMore(false);
            }
        }
    }

    //请求日漫页面数据
    private void requestMoreUrl() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "";
                    switch (typeId){
                        case Constant.ANIM_TYPE_JAPEN:
                            url = Constant.SAKURA_MORE_JP_ANIM_URL;
                            break;
                        case Constant.ANIM_TYPE_CHINA:
                            url = Constant.SAKURA_MORE_CHINA_ANIM_URL;
                            break;
                        case Constant.ANIM_TYPE_AMERICA:
                            url = Constant.SAKURA_MORE_AMERICA_ANIM_URL;
                            break;
                        case Constant.ANIM_TYPE_FILM:

                            break;
                        default:
                            url = Constant.SAKURA_MORE_JP_ANIM_URL;
                            break;
                    }
                    Connection connection = Jsoup.connect(url);
                    connection.userAgent(Constant.USER_AGENT_FORPC);
                    Document document = connection.get();
                    mHandler.sendMessage(Message.obtain(mHandler, 2, document));
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    LogUtils.e(TAG + " ioe : " + ioe.toString());
                    mHandler.sendMessage(Message.obtain(mHandler, 1));
                }
            }
        }).start();
    }

    //日漫加载更多
    private void loadMoreJpAnim(String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = Jsoup.connect(url);
                    connection.userAgent(Constant.USER_AGENT_FORPC);
                    Document document = connection.get();
                    mHandler.sendMessage(Message.obtain(mHandler, 2, document));
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    LogUtils.e(TAG + " ioe : " + ioe.toString());
                    mHandler.sendMessage(Message.obtain(mHandler, 1));
                }
            }
        }).start();
    }

    //解析更多日漫
    private void parseMoreAnim(Document document) {
        //获取分页链接
        if(TextUtils.isEmpty(basePageUrl)){
            try{
                Element pagesElement = document.selectFirst("div.pages");
                basePageUrl = Constant.SAKURA_CATEORY_BASE_URL.concat(pagesElement.selectFirst("a[href]").attr("href"));
                LogUtils.i(TAG+" basePageUrl : "+basePageUrl);
                if(TextUtils.isEmpty(basePageUrl) || basePageUrl.equals(Constant.SAKURA_CATEORY_BASE_URL)){
                    refreshLayout.setEnableLoadMore(false);
                }else{
                    refreshLayout.setEnableLoadMore(true);
                }
            }catch (Exception e){
                e.printStackTrace();
                refreshLayout.setEnableLoadMore(false);
                LogUtils.e(TAG+" 该动漫没有更多分页");
            }
        }
        //获取列表数据
        List<Integer> tempCodeList = new ArrayList<>();
        List<SearchResultBean> tempResultBeanList = new ArrayList<>();
        Elements pics = document.select("div.pics");
        if (!Utils.isListEmpty(pics)) {
            Element picsDiv = pics.get(0);
            Elements liList = picsDiv.select("li");
            if (!Utils.isListEmpty(liList)) {
                for (int i = 0; i < liList.size(); i++) {
                    SearchResultBean resultBean = new SearchResultBean();
                    resultBean.setSearchType(searchType);
                    Element li = liList.get(i);
                    Element a = li.selectFirst("a");
                    String url = Constant.SAKURA_SEARCH_URL.concat(a.attr("href"));
                    resultBean.setUrl(url);
                    Element img = a.selectFirst("img");
                    String cover = img.attr("src");
                    resultBean.setCover(cover);
                    String title = img.attr("alt");
                    resultBean.setTitle(title);
                    if (!TextUtils.isEmpty(title)) {
                        tempCodeList.add(title.hashCode());
                    }
                    Elements spans = li.select("span");
                    if (!Utils.isListEmpty(spans)) {
                        String alias = spans.get(0).text();
                        resultBean.setAlias(alias);
                        String info = spans.get(1).text();
                        resultBean.setInfos(info);
                    }
                    Element p = li.selectFirst("p");
                    String desc = p.text();
                    resultBean.setDesc(desc);
                    tempResultBeanList.add(resultBean);
                }
            } else {
                LogUtils.e(TAG + " list is empty");
            }
            waitDialog.dismiss();
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
            lastPageCodeList.addAll(tempCodeList);
            if (!Utils.isListEmpty(searchResultBeanList)) {
                refreshLayout.finishLoadMore();
                adapter.notifyDataSetChanged();
            } else {
                setEmptyState();
            }
            if(tempResultBeanList.size() < pageSize){
                LogUtils.i(TAG+" no more items ");
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        } else {
            LogUtils.e(TAG + " pics is empty");
            waitDialog.dismiss();
            setEmptyState();
        }
    }

    private void loadMore() {
        page++;
        if (typeId == Constant.ANIM_TYPE_JAPEN ||
                typeId == Constant.ANIM_TYPE_CHINA ||
                typeId == Constant.ANIM_TYPE_AMERICA) {//更多日漫
            String nextPageUrl = "";
            int pageStartIndex = basePageUrl.indexOf("page=");
            int pageEndIndex = basePageUrl.indexOf("&", pageStartIndex);
            String preString = basePageUrl.substring(0, pageStartIndex + "page=".length());
            String afterString = basePageUrl.substring(pageEndIndex, basePageUrl.length());
            nextPageUrl = preString.concat(page + "").concat(afterString);
            LogUtils.d(TAG + " nextPageUrl = " + nextPageUrl);
            //访问url获取数据
            loadMoreJpAnim(nextPageUrl);
        } else if (typeId == Constant.ANIM_TYPE_FILM) {//更多动漫电影
            String nextPageUrl = "";
            int pageStartIndex = basePageUrl.indexOf("1_");
            int pageEndIndex = basePageUrl.indexOf(".html", pageStartIndex);
            String preString = basePageUrl.substring(0, pageStartIndex + "1_".length());
            String afterString = basePageUrl.substring(pageEndIndex, basePageUrl.length());
            nextPageUrl = preString.concat(page + "").concat(afterString);
            LogUtils.d(TAG + " nextPageUrl = " + nextPageUrl);
            //访问url获取数据
            loadMoreAnimMovie(nextPageUrl);
        }
    }

    //加载更多动漫电影
    private void loadMoreAnimMovie(String url){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = Jsoup.connect(url);
                    connection.userAgent(Constant.USER_AGENT_FORPC);
                    Document document = connection.get();
                    mHandler.sendMessage(Message.obtain(mHandler, 0, document));
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    LogUtils.e(TAG + " ioe : " + ioe.toString());
                    mHandler.sendMessage(Message.obtain(mHandler, 1));
                }
            }
        }).start();
    }

    /*

    if(typeId == Constant.ANIM_TYPE_MOST_LAST){//最新更新里的更多动漫

        }else if(typeId == Constant.ANIM_TYPE_JAPEN){//更多日漫

        }else if(typeId == Constant.ANIM_TYPE_CHINA){//更多国漫

        }else if(typeId == Constant.ANIM_TYPE_AMERICA){//更多美漫

        }else if(typeId == Constant.ANIM_TYPE_FILM){//更多动漫电影

        }


    switch (typeId){
            case Constant.ANIM_TYPE_MOST_LAST:

                break;
            case Constant.ANIM_TYPE_JAPEN:

                break;
            case Constant.ANIM_TYPE_CHINA:

                break;
            case Constant.ANIM_TYPE_AMERICA:

                break;
            case Constant.ANIM_TYPE_FILM:

                break;
        }

     */
}
