package com.android.jesse.biliparser.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.adapter.ChooseSectionAdapter;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.components.WaitDialog;
import com.android.jesse.biliparser.db.base.DbHelper;
import com.android.jesse.biliparser.db.bean.HistoryVideoBean;
import com.android.jesse.biliparser.network.base.SimpleActivity;
import com.android.jesse.biliparser.network.component.OffsetRecyclerDivider;
import com.android.jesse.biliparser.network.model.bean.SearchResultBean;
import com.android.jesse.biliparser.network.model.bean.SectionBean;
import com.android.jesse.biliparser.utils.DateUtil;
import com.android.jesse.biliparser.utils.GlideUtil;
import com.android.jesse.biliparser.utils.LogUtils;
import com.android.jesse.biliparser.utils.NetLoadListener;
import com.android.jesse.biliparser.utils.Session;
import com.android.jesse.biliparser.utils.Utils;
import com.blankj.utilcode.util.SizeUtils;
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
 * @Description: 动漫选集界面
 * @author: zhangshihao
 * @date: 2020/3/25
 */
public class ChooseSectionActivity extends SimpleActivity {

    private static final String TAG = ChooseSectionActivity.class.getSimpleName();

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_no_data)
    TextView tv_no_data;
    @BindView(R.id.iv_cover)
    ImageView iv_cover;
    @BindView(R.id.tv_alias)
    TextView tv_alias;
    @BindView(R.id.tv_area)
    TextView tv_area;
    @BindView(R.id.tv_type)
    TextView tv_type;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_tags)
    TextView tv_tags;
    @BindView(R.id.tv_indexes)
    TextView tv_indexes;
    @BindView(R.id.tv_update_info)
    TextView tv_update_info;

    private ChooseSectionAdapter adapter;
    private String url;
    private WaitDialog waitDialog;
    private List<SectionBean> sectionBeanList;
    private int currentIndex = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NetLoadListener.getInstance().stopListening();
            parseDocument((Document) msg.obj);
        }
    };
    private NetLoadListener.Callback callback = new NetLoadListener.Callback() {
        @Override
        public void onNetLoadFailed() {
            waitDialog.dismiss();
            Toast.makeText(mContext, R.string.net_load_failed, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected int getLayout() {
        return R.layout.choose_section_activity;
    }

    @Override
    protected void onBackClick() {
        super.onBackClick();
        finish();
    }

    @Override
    protected void initEventAndData() {
        if(getIntent() != null){
            String title = getIntent().getStringExtra(Constant.KEY_TITLE);
            if(!TextUtils.isEmpty(title)){
                tv_title.setText(title);
            }
            url = getIntent().getStringExtra(Constant.KEY_URL);
            LogUtils.i(TAG+" getUrl : "+url);
            currentIndex = getIntent().getIntExtra(Constant.KEY_CURRENT_INDEX,0);
            LogUtils.i(TAG+" currentIndex = "+currentIndex);
        }
        sectionBeanList = new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(mContext,4));
        recyclerView.addItemDecoration(new OffsetRecyclerDivider(SizeUtils.dp2px(10),SizeUtils.dp2px(10)));
        waitDialog = new WaitDialog(mContext,R.style.Dialog_Translucent_Background);
        adapter = new ChooseSectionAdapter(mContext,sectionBeanList);
        adapter.setOnItemClickListener(new ChooseSectionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, SectionBean sectionBean) {
                if(currentIndex > 0){
                    currentIndex = position + 1;
                    tv_indexes.setText("已观看到第"+currentIndex+"集");
                }
                SearchResultBean searchResultBean = (SearchResultBean) Session.getSession().request(Constant.KEY_RESULT_BEAN);
                HistoryVideoBean historyVideoBean = new HistoryVideoBean();
                historyVideoBean.setCurrentIndex(position+1);
                historyVideoBean.setVideoId(searchResultBean.getTitle().hashCode());
                LogUtils.i(TAG+" videoId = "+searchResultBean.getTitle().hashCode());
                historyVideoBean.setAlias(searchResultBean.getAlias());
                historyVideoBean.setTitle(searchResultBean.getTitle());
                historyVideoBean.setUrl(searchResultBean.getUrl());
                historyVideoBean.setInfos(searchResultBean.getInfos());
                historyVideoBean.setDesc(searchResultBean.getDesc());
                historyVideoBean.setCover(searchResultBean.getCover());
                historyVideoBean.setDate(DateUtil.getDefaultTime());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Long> idList = DbHelper.getInstance().insertHistoryVideo(historyVideoBean);
                        LogUtils.i(TAG+" idList = "+Arrays.toString(idList.toArray()));
                        if(!Utils.isListEmpty(idList)){
                            Intent intent = new Intent(Constant.ACTION_UPDATE_CURRENT_INDEX);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                        }
                    }
                }).start();

                Intent intent = new Intent(mContext,BaseWebActivity.class);
                intent.putExtra(Constant.KEY_TITLE,sectionBean.getTitle());
                intent.putExtra(Constant.KEY_URL,sectionBean.getUrl());
                intent.putExtra(Constant.KEY_NEED_WAIT_PARSE,false);
                mContext.startActivity(intent);
//                Intent intent = new Intent(mContext,VideoPlayActivity.class);
//                intent.putExtra(Constant.KEY_TITLE,sectionBean.getTitle());
//                intent.putExtra(Constant.KEY_URL,sectionBean.getUrl());
//                mContext.startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        requestDocument();
    }

    /**
     * 根据url请求到html并解析成document
     */
    private void requestDocument(){
        if(TextUtils.isEmpty(url)){
            tv_no_data.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            LogUtils.e(TAG+" url is empty");
            return;
        }
        waitDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    NetLoadListener.getInstance().startListening(callback);
                    Connection connection = Jsoup.connect(url);
                    connection.userAgent(Constant.USER_AGENT_FORPC);
//                    connection.data("searchword",word);
//                    connection.postDataCharset("GB2312");//关键中的关键！！
                    Document document = connection.method(Connection.Method.GET).get();
                    LogUtils.d(TAG+" html = \n"+document.outerHtml());
                    mHandler.sendMessage(Message.obtain(mHandler, 0, document));
                }catch (IOException ioe){
                    ioe.printStackTrace();
                    waitDialog.dismiss();
                    NetLoadListener.getInstance().stopListening();
                }
            }
        }).start();
    }

    //解析document并填充数据到页面
    private void parseDocument(Document document){
        //介绍信息
        Element wholeData = document.selectFirst("div.fire.l");
        Element img = wholeData.selectFirst("img[src][alt]");
        String cover = img.attr("src");
        LogUtils.d(TAG+" cover = "+cover);
        GlideUtil.getInstance().loadImg(mContext,cover,iv_cover);
        Element infos = wholeData.selectFirst("div.alex");
        Elements ps = infos.select("p");
        Element p0 = ps.get(0);
        Element p1 = ps.get(1);
        String alias = p0.text();
        String updateInfo = p1.text();
        tv_alias.setText(alias);
        tv_update_info.setText(updateInfo);
        Element infoList = wholeData.selectFirst("div.alex");
        Elements spans = infoList.select("span");
        String area = spans.get(0).text();
        tv_area.setText(area);
        Element spanType = spans.get(1);
        Elements aTypes = spanType.select("a");
        StringBuilder type = new StringBuilder();
        for(Element aType : aTypes){
            type.append(aType.text()+" ");
        }
        tv_type.setText("类型："+type.toString());
        String time = spans.get(2).selectFirst("a").text();
        tv_time.setText("年代："+time);
        StringBuilder tags = new StringBuilder();
        Elements aTags = spans.get(3).select("a");
        for(Element aTag : aTags){
            tags.append(aTag.text()+" ");
        }
        tv_tags.setText("标签："+tags.toString());
        if(currentIndex > 0){
            tv_indexes.setTextColor(getColor(R.color.color_selected_tags));
            tv_indexes.setText("已观看到第"+currentIndex+"集");
        }else{
            String indexes = spans.get(4).selectFirst("a").text();
            tv_indexes.setText("索引："+indexes);
        }
        //选集列表
        Elements aList = document.selectFirst("div.movurl").select("a[title][href][target]");
        for(int i=0;i<aList.size();i++){
            SectionBean sectionBean = new SectionBean();
            sectionBean.setTitle(aList.get(i).attr("title"));
            sectionBean.setUrl(Constant.SAKURA_SEARCH_URL+aList.get(i).attr("href"));
            sectionBeanList.add(sectionBean);
        }
        adapter.notifyDataSetChanged();
        waitDialog.dismiss();
    }

}
