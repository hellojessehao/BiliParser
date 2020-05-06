package com.android.jesse.biliparser.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.activity.ChooseSectionActivity;
import com.android.jesse.biliparser.adapter.FilmRecommendAdapter;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.components.WaitDialog;
import com.android.jesse.biliparser.network.base.SimpleFragment;
import com.android.jesse.biliparser.network.component.OffsetRecyclerDivider;
import com.android.jesse.biliparser.network.model.bean.AnimRecommendBannerBean;
import com.android.jesse.biliparser.network.model.bean.FilmRecommendBannerBean;
import com.android.jesse.biliparser.network.model.bean.FilmRecommendItemBean;
import com.android.jesse.biliparser.network.model.bean.SearchResultBean;
import com.android.jesse.biliparser.utils.GlideUtil;
import com.android.jesse.biliparser.utils.LogUtils;
import com.android.jesse.biliparser.utils.NetLoadListener;
import com.android.jesse.biliparser.utils.Session;
import com.android.jesse.biliparser.utils.Utils;
import com.blankj.utilcode.util.SizeUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

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
 * @Description: 动漫推荐页
 * @author: zhangshihao
 * @date: 2020/4/27
 */
public class FilmRecommendFragment extends SimpleFragment {

    private static final String TAG = FilmRecommendFragment.class.getSimpleName();

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.banner)
    Banner mBanner;
    @BindView(R.id.tv_no_data)
    TextView tv_no_data;

    private WaitDialog waitDialog;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                NetLoadListener.getInstance().stopListening();
                if(msg.obj == null){
                    Toast.makeText(mActivity, R.string.data_is_null_hint , Toast.LENGTH_SHORT).show();
                    LogUtils.e(TAG+" document is null");
                    return;
                }
                Document document = (Document) msg.obj;
                try{
                    parseDocument(document);
                }catch (Exception e){
                    e.printStackTrace();
                    LogUtils.e(TAG+" e : "+e.toString());
                }
            }else if(msg.what == 1){
                Toast.makeText(mContext, R.string.net_load_failed, Toast.LENGTH_SHORT).show();
                waitDialog.dismiss();
            }
        }
    };
    private List<FilmRecommendBannerBean> bannerBeanList = new ArrayList<>();
    private List<FilmRecommendItemBean> filmRecommendItemBeanList = new ArrayList<>();
    private FilmRecommendAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.anim_recommend_fragment;
    }

    private NetLoadListener.Callback callback = new NetLoadListener.Callback() {
        @Override
        public void onNetLoadFailed() {
            mHandler.sendEmptyMessage(1);
        }
    };

    @Override
    protected void initEventAndData() {
        waitDialog = new WaitDialog(mContext,R.style.Dialog_Translucent_Background);
        initBanner();
        initRecyclerView();
        waitDialog.show();
        NetLoadListener.getInstance().startListening(callback);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Connection connection = Jsoup.connect(Constant.JIJI_BASE_URL);
                    connection.userAgent(Constant.USER_AGENT_FORPC);
                    Document document = connection.get();
                    mHandler.sendMessage(Message.obtain(mHandler, 0, document));
                }catch (IOException ioe){
                    ioe.printStackTrace();
                    LogUtils.e(TAG+" ioe : "+ioe.toString());
                }
            }
        }).start();
    }

    private void initRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new OffsetRecyclerDivider(0,SizeUtils.dp2px(20)));
        adapter = new FilmRecommendAdapter(mContext,filmRecommendItemBeanList);
        recyclerView.setAdapter(adapter);
    }

    private void initBanner() {
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        mBanner.setImageLoader(new GlideImageLoader());
        mBanner.setOnBannerListener(onBannerListener);
    }

    private OnBannerListener onBannerListener = new OnBannerListener() {
        @Override
        public void OnBannerClick(int position) {
            if(Utils.isListEmpty(bannerBeanList)){
                LogUtils.e(TAG+" OnBannerClick : bannerBeanList is empty");
                return;
            }
            FilmRecommendBannerBean bannerBean = bannerBeanList.get(position);
            SearchResultBean searchResultBean = new SearchResultBean();
            searchResultBean.setTitle(bannerBean.getTitle());
            searchResultBean.setCover(bannerBean.getCover());
            searchResultBean.setUrl(bannerBean.getUrl());
            searchResultBean.setSearchType(Constant.FLAG_SEARCH_FILM_TELEVISION);
            Session.getSession().put(Constant.KEY_RESULT_BEAN,searchResultBean);
            Intent intent = new Intent(mContext,ChooseSectionActivity.class);
            intent.putExtra(Constant.KEY_TITLE,bannerBean.getTitle());
            intent.putExtra(Constant.KEY_URL,bannerBean.getUrl());
            intent.putExtra(Constant.KEY_SEARCH_TYPE,Constant.FLAG_SEARCH_FILM_TELEVISION);
            mContext.startActivity(intent);
        }
    };

    private void setEmptyState(){
        tv_no_data.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    /**
     * Banner自定义图片加载器
     */
    private class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            GlideUtil.getInstance().loadOriImg(context, (String) path, imageView);
        }
    }

    private void parseDocument(Document document){
        //banner填充
        Element bannerContainer = document.selectFirst("ul.focus-bg");
        if(bannerContainer != null){
            Elements liList = bannerContainer.select("li");
            if(!Utils.isListEmpty(liList)){
                List<String> imgList = new ArrayList<>();
                List<String> titleList = new ArrayList<>();
                for(int i=0;i<liList.size();i++){
                    FilmRecommendBannerBean bannerBean = new FilmRecommendBannerBean();
                    Element li = liList.get(i);
                    String style = li.attr("style");
                    if(!TextUtils.isEmpty(style)){
                        String cover = style.substring(style.indexOf("(")+1,style.indexOf(")"));
                        bannerBean.setCover(cover);
                        imgList.add(cover);
                    }
                    Element a = li.selectFirst("a");
                    if(a != null){
                        String url = Constant.JIJI_BASE_URL.concat(a.attr("href"));
                        bannerBean.setUrl(url);
                        String title = a.attr("title");
                        bannerBean.setTitle(title);
                        titleList.add(title);
                    }
                    bannerBeanList.add(bannerBean);
                }
                if(!Utils.isListEmpty(imgList) && !Utils.isListEmpty(titleList)){
                    mBanner.setImages(imgList);
                    mBanner.setBannerTitles(titleList);
                    mBanner.start();
                }else{
                    LogUtils.e(TAG+" bannerBeanList is empty");
                }
            }else{
                LogUtils.e(TAG+" liList is empty");
            }
        }else{
           LogUtils.e(TAG+" bannerContainer is null");
        }
        //RecyclerView填充
        //插入推荐数据@{
        Element recommendContainer = document.selectFirst("ul.mx_list_ul");
        FilmRecommendItemBean recommendItemBean = new FilmRecommendItemBean();
        Elements tabList = document.select("div.tab-a.box-lett-title");//更多的链接都从这个列表里获取
        if(!Utils.isListEmpty(tabList)){
            Element recommendTab = tabList.get(0);
            Element more = recommendTab.selectFirst("div.more");
            Element a = more.selectFirst("a[href]");
            recommendItemBean.setMoreUrl(Constant.JIJI_BASE_URL.concat(a.attr("href")));
        }else{
            LogUtils.e(TAG+" recommendTab is null");
        }
        if(recommendContainer != null){
            Elements recommendAList = recommendContainer.select("a.star-img");
            List<FilmRecommendItemBean.DataBean> dataBeanList = new ArrayList<>();
            if(!Utils.isListEmpty(recommendAList)){
                for(int i=0;i<recommendAList.size();i++){
                    FilmRecommendItemBean.DataBean dataBean = new FilmRecommendItemBean.DataBean();
                    Element a = recommendAList.get(i);
                    Element img = a.selectFirst("img");
                    String url = Constant.JIJI_BASE_URL.concat(a.attr("href"));
                    dataBean.setUrl(url);
                    String title = a.attr("title");
                    dataBean.setTitle(title);
                    String cover = img.attr("src");
                    dataBean.setCover(cover);
                    dataBeanList.add(dataBean);
                }
            }else{
                LogUtils.e(TAG+" recommendAList is empty");
            }
            recommendItemBean.setDataBeanList(dataBeanList);
        }
        filmRecommendItemBeanList.add(recommendItemBean);
        //@}
        //@{插入电影数据
        FilmRecommendItemBean filmItemBean = new FilmRecommendItemBean();
        if(!Utils.isListEmpty(tabList)){
            Element div = document.selectFirst("div.up-nav");
            if(div != null){
                Element span = div.selectFirst("span");
                Element a = span.selectFirst("a");
                filmItemBean.setMoreUrl(Constant.JIJI_BASE_URL.concat(a.attr("href")));
            }else{
                LogUtils.e(TAG+" div is null");
            }
        }else{
            LogUtils.e(TAG+" recommendTab is null");
        }
        Element filmLiContainer = document.selectFirst("ul#con_tbmov_2");
        if(filmLiContainer != null){
            Elements liList = filmLiContainer.select("li");
            List<FilmRecommendItemBean.DataBean> dataBeanList = new ArrayList<>();
            if(!Utils.isListEmpty(liList)){
                for(int i=0;i<liList.size();i++){
                    FilmRecommendItemBean.DataBean dataBean = new FilmRecommendItemBean.DataBean();
                    Element li = liList.get(i);
                    Element a = li.selectFirst("a");
                    Element img = a.selectFirst("img");
                    String url = Constant.JIJI_BASE_URL.concat(a.attr("href"));
                    dataBean.setUrl(url);
                    String cover = img.attr("src");
                    dataBean.setCover(cover);
                    String title = img.attr("alt");
                    dataBean.setTitle(title);
                    dataBeanList.add(dataBean);
                }
            }else{
                LogUtils.e(TAG+" liList is empty");
            }
            filmItemBean.setDataBeanList(dataBeanList);
        }else{
            LogUtils.e(TAG+" filmLiContainer is null");
        }
        filmRecommendItemBeanList.add(filmItemBean);
        //@}
        //@{插入电视剧数据
        FilmRecommendItemBean televisionItemBean = new FilmRecommendItemBean();
        Elements divs = document.select("div.up-nav");
        Element div = null;
        if(!Utils.isListEmpty(divs)){
            div = divs.get(1);
            Element span = div.selectFirst("span");
            Element a = span.selectFirst("a");
            televisionItemBean.setMoreUrl(Constant.JIJI_BASE_URL.concat(a.attr("href")));
        }else{
            LogUtils.e(TAG+" divs is empty");
        }
        if(div != null){
            Element ul = document.selectFirst("ul#con_tb_2");
            List<FilmRecommendItemBean.DataBean> dataBeanList = new ArrayList<>();
            if(ul != null){
                Elements liList = ul.select("li");
                if(!Utils.isListEmpty(liList)){
                    for(int i=0;i<liList.size();i++){
                        FilmRecommendItemBean.DataBean dataBean = new FilmRecommendItemBean.DataBean();
                        Element li = liList.get(i);
                        Element a = li.selectFirst("a");
                        Element img = a.selectFirst("img");
                        String url = Constant.JIJI_BASE_URL.concat(a.attr("href"));
                        dataBean.setUrl(url);
                        String cover = img.attr("src");
                        dataBean.setCover(cover);
                        String title = img.attr("alt");
                        dataBean.setTitle(title);
                        dataBeanList.add(dataBean);
                    }
                }
            }else{
                LogUtils.e(TAG+" ul is null");
            }
            televisionItemBean.setDataBeanList(dataBeanList);
        }
        filmRecommendItemBeanList.add(televisionItemBean);
        //@}
        //@{插入综艺数据
        FilmRecommendItemBean varietyItemBean = new FilmRecommendItemBean();
        Element varietyTab = tabList.get(4);
        if(varietyTab != null){
            Element more = varietyTab.selectFirst("div.more");
            Element a = more.selectFirst("a");
            String moreUrl = Constant.JIJI_BASE_URL.concat(a.attr("href"));
            varietyItemBean.setMoreUrl(moreUrl);
        }
        Element ul = document.selectFirst("ul#con_zy_1");
        List<FilmRecommendItemBean.DataBean> dataBeanList = new ArrayList<>();
        if(ul != null){
            Elements liList = ul.select("li");
            if(!Utils.isListEmpty(liList)){
                for(int i=0;i<liList.size();i++){
                    FilmRecommendItemBean.DataBean dataBean = new FilmRecommendItemBean.DataBean();
                    Element li = liList.get(i);
                    Element a = li.selectFirst("a");
                    Element img = a.selectFirst("img");
                    String url = Constant.JIJI_BASE_URL.concat(a.attr("href"));
                    dataBean.setUrl(url);
                    String cover = img.attr("src");
                    dataBean.setCover(cover);
                    String title = img.attr("alt");
                    dataBean.setTitle(title);
                    dataBeanList.add(dataBean);
                }
            }
        }else{
            LogUtils.e(TAG+" ul is null");
        }
        varietyItemBean.setDataBeanList(dataBeanList);
        filmRecommendItemBeanList.add(varietyItemBean);
        //@}
        if(!Utils.isListEmpty(filmRecommendItemBeanList)){
            for(int i=0;i<filmRecommendItemBeanList.size();i++){
                FilmRecommendItemBean itemBean = filmRecommendItemBeanList.get(i);
                itemBean.setTypeId(i);
                itemBean.setType(getResources().getStringArray(R.array.film_type_array)[i]);
            }
            recyclerView.setVisibility(View.VISIBLE);
            tv_no_data.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }else{
            setEmptyState();
        }
        waitDialog.dismiss();
    }

}
