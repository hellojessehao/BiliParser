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
import com.android.jesse.biliparser.adapter.MoreFilmAdapter;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.components.WaitDialog;
import com.android.jesse.biliparser.network.base.SimpleActivity;
import com.android.jesse.biliparser.network.component.OffsetRecyclerDivider;
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
 * @Description: 更多影视
 * @author: zhangshihao
 * @date: 2020/4/28
 */
public class MoreFilmActivity extends SimpleActivity {

    private static final String TAG = MoreFilmActivity.class.getSimpleName();

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
                try{
                    parseDocument((Document) msg.obj);
                }catch (Exception e){
                    LogUtils.e(TAG+" e : "+e.toString());
                    waitDialog.dismiss();
                    Toast.makeText(mContext, R.string.net_load_failed, Toast.LENGTH_SHORT).show();
                }
            } else if (msg.what == 1) {
                waitDialog.dismiss();
                setEmptyState();
            }
        }
    };
    private WaitDialog waitDialog;
    private int typeId = Constant.FILM_TYPE_RECOMMEND;
    private List<SearchResultBean> searchResultBeanList = new ArrayList<>();
    private MoreFilmAdapter adapter;
    //只有综艺需要分页
    private int page = 1;
    private int pageSize = 24;
    private String basePageUrl;//基础分页链接
    private List<Integer> lastPageCodeList = new ArrayList<>();//上一页的hashCode集合
    private int searchType = Constant.FLAG_SEARCH_FILM_TELEVISION;

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
        return R.layout.more_film_activity;
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
            url = getIntent().getStringExtra(Constant.KEY_URL);
            typeId = getIntent().getIntExtra(Constant.KEY_TYPE_ID, Constant.FILM_TYPE_RECOMMEND);
            LogUtils.d(TAG + " url = " + url + " ,typeId = " + typeId);
        }
        waitDialog = new WaitDialog(mContext, R.style.Dialog_Translucent_Background);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new OffsetRecyclerDivider(0, SizeUtils.dp2px(15)));
        if (typeId == Constant.FILM_TYPE_VARIETY) {//只有综艺需要分页处理
            recyclerView.setLayoutManager(new GridLayoutManager(mContext,3));
            recyclerView.addItemDecoration(new OffsetRecyclerDivider(8,SizeUtils.dp2px(15)));
            refreshLayout.setEnableLoadMore(true);
            refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    loadMore();
                }
            });
        }
        adapter = new MoreFilmAdapter(mContext, searchResultBeanList, typeId);
        recyclerView.setAdapter(adapter);
        getData(url);
    }

    private void getData(String url) {
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
    }

    private void parseDocument(Document document) throws Exception{
        List<SearchResultBean> tempResultBeanList = new ArrayList<>();
        List<Integer> tempCodeList = new ArrayList<>();//分页用
        if (typeId == Constant.FILM_TYPE_RECOMMEND) {
            Elements leftDivList = document.select("div.top-item.aleft");
            if (!Utils.isListEmpty(leftDivList)) {
                for (int i = 0; i < leftDivList.size(); i++) {
                    Element div = leftDivList.get(i);
                    Elements ulList = div.select("ul.top-list");
                    if (Utils.isListEmpty(ulList)) {
                        LogUtils.e(TAG + " ulList is empty : i = " + i);
                        continue;
                    }
                    for (int j = 0; j < ulList.size(); j++) {
                        Element ul = ulList.get(j);
                        Elements aList = ul.select("a[href]");
                        if (Utils.isListEmpty(aList)) {
                            LogUtils.e(TAG + " aList is empty : j = " + j);
                            continue;
                        }
                        for (int k = 0; k < aList.size(); k++) {
                            SearchResultBean resultBean = new SearchResultBean();
                            if (i == 0) {
                                resultBean.setType("电视剧");
                            } else if (i == 1) {
                                resultBean.setType("电影");
                            } else if (i == 2) {
                                resultBean.setType("动漫");
                            } else {
                                resultBean.setType("综艺");
                            }
                            resultBean.setSearchType(searchType);
                            Element a = aList.get(k);
                            String url = Constant.JIJI_BASE_URL.concat(a.attr("href"));
                            resultBean.setUrl(url);
                            String title = a.attr("title");
                            resultBean.setTitle(title);
                            Element span = a.selectFirst("span.score");
                            String hotValue = span.text();
                            resultBean.setHotValue(hotValue);
                            tempResultBeanList.add(resultBean);
                        }
                    }
                }
                searchResultBeanList.addAll(tempResultBeanList);
                if (!Utils.isListEmpty(searchResultBeanList)) {
                    adapter.notifyDataSetChanged();
                } else {
                    setEmptyState();
                }
            } else {
                LogUtils.e(TAG + " leftDivList is empty");
                setEmptyState();
            }
        } else if (typeId == Constant.FILM_TYPE_FILM ||
                typeId == Constant.FILM_TYPE_TELEVISION) {
            Elements leftDivList = document.select("div.top-item");//左右两边列表都取
            if (!Utils.isListEmpty(leftDivList)) {
                for (int i = 0; i < leftDivList.size(); i++) {
                    Element div = leftDivList.get(i);
                    Elements ulList = div.select("ul.top-list");
                    if (Utils.isListEmpty(ulList)) {
                        LogUtils.e(TAG + " ulList is empty : i = " + i);
                        continue;
                    }
                    for (int j = 0; j < ulList.size(); j++) {
                        Element ul = ulList.get(j);
                        Elements aList = ul.select("a[href]");
                        if (Utils.isListEmpty(aList)) {
                            LogUtils.e(TAG + " aList is empty : j = " + j);
                            continue;
                        }
                        for (int k = 0; k < aList.size(); k++) {
                            SearchResultBean resultBean = new SearchResultBean();
                            if (i == 0) {
                                resultBean.setType("总排行");
                            } else if (i == 1) {
                                resultBean.setType("周排行");
                            }
                            resultBean.setSearchType(searchType);
                            Element a = aList.get(k);
                            String url = Constant.JIJI_BASE_URL.concat(a.attr("href"));
                            resultBean.setUrl(url);
                            String title = a.attr("title");
                            resultBean.setTitle(title);
                            Element span = a.selectFirst("span.score");
                            String hotValue = span.text();
                            resultBean.setHotValue(hotValue);
                            //去重
                            boolean isExist = false;
                            if (!Utils.isListEmpty(tempResultBeanList)) {
                                for (int n = 0; n < tempResultBeanList.size(); n++) {
                                    SearchResultBean tempResultBean = tempResultBeanList.get(n);
                                    if (TextUtils.isEmpty(tempResultBean.getTitle())) {
                                        continue;
                                    }
                                    if (tempResultBean.getTitle().equals(title)) {
                                        isExist = true;
                                        break;
                                    }
                                }
                            }
                            if (!isExist) {
                                tempResultBeanList.add(resultBean);
                            }
                        }
                    }
                }
                searchResultBeanList.addAll(tempResultBeanList);
                if (!Utils.isListEmpty(searchResultBeanList)) {
                    adapter.notifyDataSetChanged();
                } else {
                    setEmptyState();
                }
            } else {
                LogUtils.e(TAG + " leftDivList is empty");
                setEmptyState();
            }
        } else if (typeId == Constant.FILM_TYPE_VARIETY) {
            //获取分页链接
            if(TextUtils.isEmpty(basePageUrl)){
                try {
                    Element pagesElement = document.selectFirst("div.page");
                    basePageUrl = Constant.JIJI_BASE_URL.concat(pagesElement.selectFirst("a[href]").attr("href"));
                    LogUtils.i(TAG + " basePageUrl : " + basePageUrl);
                    if (TextUtils.isEmpty(basePageUrl) || basePageUrl.equals(Constant.JIJI_BASE_URL)) {
                        refreshLayout.setEnableLoadMore(false);
                    } else {
                        refreshLayout.setEnableLoadMore(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    refreshLayout.setEnableLoadMore(false);
                    LogUtils.e(TAG + " 该动漫没有更多分页");
                }
            }
            //获取列表数据
            Element ul = document.selectFirst("ul.list_module_img");
            if (ul != null) {
                Elements liList = ul.select("li");
                if (!Utils.isListEmpty(liList)) {
                    for (int i = 0; i < liList.size(); i++) {
                        SearchResultBean resultBean = new SearchResultBean();
                        resultBean.setSearchType(searchType);
                        Element li = liList.get(i);
                        Element a = li.selectFirst("a");
                        String url = Constant.JIJI_BASE_URL.concat(a.attr("href"));
                        resultBean.setUrl(url);
                        String title = a.attr("title");
                        resultBean.setTitle(title);
                        Element img = a.selectFirst("img");
                        String cover = img.attr("src");
                        resultBean.setCover(cover);
                        Element label = a.selectFirst("label.title");
                        String sectionCount = label.text().trim();
                        resultBean.setSectionCount(sectionCount);
                        Element actorP = li.selectFirst("p.actor");
                        String actor = actorP.text().replaceAll("\"", "").replaceAll(",", " ");
                        if(actor.contains(":")){
                            String[] arr = actor.split(":");
                            if(arr.length > 1){
                                actor = arr[1];
                            }
                        }
                        if(actor.contains("主演:")){
                            actor = "不详";
                        }
                        resultBean.setActorList(Arrays.asList(actor));
                        Element typeP = li.selectFirst("p.type");
                        String type = typeP.text().replaceAll("\"", "");
                        resultBean.setType(type);
                        Element publishP = li.selectFirst("p.showtime");
                        String publishDate = publishP.text().replaceAll("\"", "");
                        resultBean.setPublishDate(publishDate);
                        Element upP = li.selectFirst("p.up");
                        String updateDate = upP.text().replaceAll("\"", "");
                        resultBean.setUpdateDate(updateDate);
                        Element descP = li.selectFirst("p.plot");
                        String desc = descP.text().trim().replaceAll("\"", "");
                        resultBean.setDesc(desc);
                        tempResultBeanList.add(resultBean);
                        if (!TextUtils.isEmpty(title)) {
                            tempCodeList.add(title.hashCode());
                        }
                    }
                }
            } else {
                LogUtils.e(TAG + " ul is null");
                setEmptyState();
            }
            if (!Utils.isListEmpty(lastPageCodeList) && !Utils.isListEmpty(tempCodeList)) {
                if (lastPageCodeList.size() == tempCodeList.size()) {
                    boolean isEqual = true;
                    for (int i = 0; i < lastPageCodeList.size(); i++) {
                        if (lastPageCodeList.get(i).intValue() != tempCodeList.get(i).intValue()) {
                            isEqual = false;
                        }
                    }
                    if (isEqual) {//跟上一页是相同的数据
                        refreshLayout.finishLoadMoreWithNoMoreData();
                        LogUtils.e(TAG + " currentPage is same to lastPage,no more datas");
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
            if (tempResultBeanList.size() < pageSize) {
                LogUtils.i(TAG + " no more items ");
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }
        waitDialog.dismiss();
    }

    private void loadMore() {
        //https://www.jijidy.com/index.php?m=vod-list-id-10-pg-1-order--by-time-class-0-year-0-letter--area--lang-.html
        page++;
        String nextPageUrl = "";
        int pageStartIndex = basePageUrl.indexOf("pg-");
        int pageEndIndex = basePageUrl.indexOf("-order", pageStartIndex);
        String preString = basePageUrl.substring(0, pageStartIndex + "pg-".length());
        String afterString = basePageUrl.substring(pageEndIndex, basePageUrl.length());
        nextPageUrl = preString.concat(page + "").concat(afterString);
        LogUtils.d(TAG + " nextPageUrl = " + nextPageUrl);
        //访问url获取数据
        getData(nextPageUrl);
    }

    private void setEmptyState() {
        waitDialog.dismiss();
        refreshLayout.setEnableLoadMore(false);
        tv_no_data.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

}
