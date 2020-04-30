package com.android.jesse.biliparser.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.activity.ChooseSectionActivity;
import com.android.jesse.biliparser.adapter.AnimRecommendAdapter;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.components.WaitDialog;
import com.android.jesse.biliparser.network.base.SimpleFragment;
import com.android.jesse.biliparser.network.component.OffsetRecyclerDivider;
import com.android.jesse.biliparser.network.model.bean.AnimRecommendBannerBean;
import com.android.jesse.biliparser.network.model.bean.AnimRecommendItemBean;
import com.android.jesse.biliparser.network.model.bean.SearchResultBean;
import com.android.jesse.biliparser.network.util.ToastUtil;
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
public class AnimRecommendFragment extends SimpleFragment {

    private static final String TAG = AnimRecommendFragment.class.getSimpleName();

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.banner)
    Banner mBanner;
    @BindView(R.id.tv_no_data)
    TextView tv_no_data;

    private WaitDialog waitDialog;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                NetLoadListener.getInstance().stopListening();
                if (msg.obj == null) {
                    Toast.makeText(mActivity, R.string.data_is_null_hint, Toast.LENGTH_SHORT).show();
                    LogUtils.e(TAG + " document is null");
                    return;
                }
                Document document = (Document) msg.obj;
                parseDocument(document);
            } else if (msg.what == 1) {
                Toast.makeText(mContext, R.string.net_load_failed, Toast.LENGTH_SHORT).show();
                waitDialog.dismiss();
                tv_no_data.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        }
    };
    private List<AnimRecommendBannerBean> bannerBeanList = new ArrayList<>();
    private List<AnimRecommendItemBean> recommendItemBeanList = new ArrayList<>();
    private AnimRecommendAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.anim_recommend_fragment;
    }

    private NetLoadListener.Callback callback = new NetLoadListener.Callback() {
        @Override
        public void onNetLoadFailed() {
            mHandler.sendMessage(Message.obtain(mHandler, 1));
        }
    };

    @Override
    protected void initEventAndData() {
        waitDialog = new WaitDialog(mContext, R.style.Dialog_Translucent_Background);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new OffsetRecyclerDivider(0, SizeUtils.dp2px(20)));
        adapter = new AnimRecommendAdapter(mContext, recommendItemBeanList);
        recyclerView.setAdapter(adapter);
        initBanner();
        waitDialog.show();
        NetLoadListener.getInstance().startListening(callback);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = Jsoup.connect(Constant.SAKURA_SEARCH_URL);
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
            AnimRecommendBannerBean bannerBean = bannerBeanList.get(position);
            SearchResultBean searchResultBean = new SearchResultBean();
            searchResultBean.setTitle(bannerBean.getTitle());
            searchResultBean.setCover(bannerBean.getCover());
            searchResultBean.setUrl(bannerBean.getUrl());
            searchResultBean.setSectionCount(bannerBean.getSectionCount());
            searchResultBean.setInfos("");
            searchResultBean.setAlias("");
            searchResultBean.setDesc("");
            searchResultBean.setSearchType(Constant.FLAG_SEARCH_ANIM);
            Session.getSession().put(Constant.KEY_RESULT_BEAN,searchResultBean);
            Intent intent = new Intent(mContext,ChooseSectionActivity.class);
            intent.putExtra(Constant.KEY_TITLE,bannerBean.getTitle());
            intent.putExtra(Constant.KEY_URL,bannerBean.getUrl());
            intent.putExtra(Constant.KEY_SEARCH_TYPE,Constant.FLAG_SEARCH_ANIM);
            mContext.startActivity(intent);
        }
    };

    /**
     * Banner自定义图片加载器
     */
    private class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            GlideUtil.getInstance().loadOriImg(context, (String) path, imageView);
        }
    }

    private void parseDocument(Document document) {
        //banner填充
        Element bannerContainer = document.selectFirst("div.hero-wrap");
        Elements bannerLiList = bannerContainer.select("li");
        if (!Utils.isListEmpty(bannerLiList)) {
            for (int i = 0; i < bannerLiList.size(); i++) {
                AnimRecommendBannerBean bannerBean = new AnimRecommendBannerBean();
                Element li = bannerLiList.get(i);
                Element a = li.selectFirst("a");
                if (a != null) {
                    bannerBean.setUrl(a.attr("href"));
                    bannerBean.setTitle(a.attr("title"));
                    Element img = a.selectFirst("img");
                    if (img != null) {
                        bannerBean.setCover(img.attr("src"));
                    }
                }
                Element em = li.selectFirst("em");
                if (em != null) {
                    bannerBean.setSectionCount(em.text());
                }
                bannerBeanList.add(bannerBean);
            }
            if (!Utils.isListEmpty(bannerBeanList)) {
                List<String> bannerImgList = new ArrayList<>();
                List<String> bannerTitleList = new ArrayList<>();
                for(int i=0;i<bannerBeanList.size();i++){
                    AnimRecommendBannerBean bannerBean = bannerBeanList.get(i);
                    bannerImgList.add(bannerBean.getCover());
                    bannerTitleList.add("["+bannerBean.getSectionCount()+"]"+bannerBean.getTitle());
                }
                mBanner.setImages(bannerImgList);
                mBanner.setBannerTitles(bannerTitleList);
                mBanner.start();
            }
        } else {
            LogUtils.e(TAG + " banner list is empty");
            Toast.makeText(mActivity, R.string.banner_data_load_failed, Toast.LENGTH_SHORT).show();
        }
        //recyclerView填充
        Element listContainer = document.selectFirst("div#contrainer");
        Elements tameDivs = listContainer.select("div.tame");
        Elements imgDivs = listContainer.select("div.imgs");
        if (tameDivs != null && tameDivs.size() > 0 && imgDivs != null && imgDivs.size() > 0) {
            for (int i = 0; i < tameDivs.size() && i < imgDivs.size(); i++) {
                AnimRecommendItemBean itemBean = new AnimRecommendItemBean();
                List<AnimRecommendItemBean.DataBean> dataBeanList = new ArrayList<>();
                Element tame = tameDivs.get(i);
                Element aTag = tame.selectFirst("a[href]");
                if (aTag != null) {
                    itemBean.setMoreUrl(Constant.SAKURA_SEARCH_URL.concat(aTag.attr("href")));
                }
                itemBean.setTypeId(i);
                itemBean.setType(getResources().getStringArray(R.array.anim_type_array)[i]);

                Element imgs = imgDivs.get(i);
                Elements liList = imgs.select("li");
                if (liList != null && liList.size() > 0) {
                    for (int j = 0; j < liList.size(); j++) {
                        AnimRecommendItemBean.DataBean dataBean = new AnimRecommendItemBean.DataBean();
                        Element li = liList.get(j);
                        Element imgTag = li.selectFirst("img[src]");
                        if (imgTag != null) {
                            dataBean.setCover(imgTag.attr("src"));
                            dataBean.setTitle(imgTag.attr("alt"));
                        }
                        Element a = li.selectFirst("a[href]");
                        if (a != null) {
                            dataBean.setUrl(Constant.SAKURA_SEARCH_URL.concat(a.attr("href")));
                        }
                        Elements pList = li.select("p");
                        if (pList != null && pList.size() > 0) {
                            Element lastP = pList.get(pList.size() - 1);
                            dataBean.setSectionCount(lastP.text());
                        }
                        dataBeanList.add(dataBean);
                    }
                }
                itemBean.setDataBeanList(dataBeanList);
                recommendItemBeanList.add(itemBean);
            }
            tv_no_data.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        } else {
            LogUtils.e(TAG + " tameDivs or imgDivs is empty");
            Toast.makeText(mActivity, R.string.data_list_is_empty, Toast.LENGTH_SHORT).show();
            tv_no_data.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        waitDialog.dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        mBanner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        mBanner.stopAutoPlay();
    }

}
