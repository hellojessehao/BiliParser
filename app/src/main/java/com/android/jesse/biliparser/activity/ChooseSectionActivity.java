package com.android.jesse.biliparser.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.adapter.ChooseSectionAdapter;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.components.WaitDialog;
import com.android.jesse.biliparser.db.base.DbHelper;
import com.android.jesse.biliparser.db.bean.CollectionBean;
import com.android.jesse.biliparser.db.bean.HistoryVideoBean;
import com.android.jesse.biliparser.network.base.SimpleActivity;
import com.android.jesse.biliparser.network.component.OffsetRecyclerDivider;
import com.android.jesse.biliparser.network.model.bean.SearchResultBean;
import com.android.jesse.biliparser.network.model.bean.SectionBean;
import com.android.jesse.biliparser.network.util.ToastUtil;
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
import java.util.Collections;
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
    private boolean showIndex = false;//是否显示当前观看到第几集
    private String title;
    private int collectFlag = 0;//0未收藏 1已收藏
    private int searchType = Constant.FLAG_SEARCH_ANIM;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                NetLoadListener.getInstance().stopListening();
                parseDocument((Document) msg.obj);
            }else if(msg.what == 1){
                if(msg.obj == null){
                    collectFlag = 0;
                    iv_right.setImageResource(R.mipmap.ic_collect);
                }else{
                    collectFlag = 1;
                    iv_right.setImageResource(R.mipmap.ic_collected);
                }
            }else if(msg.what == 2){
                Toast.makeText(mContext, "收藏失败,请重新进入此页面~", Toast.LENGTH_SHORT).show();
            }else if(msg.what == 3){
                Toast.makeText(mContext, "收藏失败,请重试~", Toast.LENGTH_SHORT).show();
            }else if(msg.what == 4){
                collectFlag = 1;
                iv_right.setImageResource(R.mipmap.ic_collected);
                Toast.makeText(mContext, "已收藏", Toast.LENGTH_SHORT).show();
            }else if(msg.what == 5){
                collectFlag = 0;
                iv_right.setImageResource(R.mipmap.ic_collect);
                Toast.makeText(mContext, "已取消", Toast.LENGTH_SHORT).show();
            }else if(msg.what == 6){
                Toast.makeText(mContext, "请重试", Toast.LENGTH_SHORT).show();
            }else if(msg.what == 7){
                waitDialog.dismiss();
                Toast.makeText(mContext, R.string.net_load_failed, Toast.LENGTH_SHORT).show();
            }
        }
    };
    private NetLoadListener.Callback callback = new NetLoadListener.Callback() {
        @Override
        public void onNetLoadFailed() {
            mHandler.sendEmptyMessage(7);
        }
    };

    @Override
    protected int getLayout() {
        return R.layout.choose_section_activity;
    }

    @Override
    protected void onBackClick() {
        super.onBackClick();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onRightClick() {
        super.onRightClick();
        switch (collectFlag){
            case 0://未收藏
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SearchResultBean searchResultBean = (SearchResultBean) Session.getSession().request(Constant.KEY_RESULT_BEAN);
                        if(searchResultBean == null){
                            mHandler.sendEmptyMessage(2);
                            return;
                        }
                        CollectionBean collectionBean = new CollectionBean();
                        collectionBean.setCurrentIndex(currentIndex);
                        collectionBean.setVideoId(searchResultBean.getTitle().hashCode());
                        LogUtils.i(TAG+" videoId = "+searchResultBean.getTitle().hashCode());
                        collectionBean.setAlias(searchResultBean.getAlias());
                        collectionBean.setTitle(searchResultBean.getTitle());
                        collectionBean.setUrl(searchResultBean.getUrl());
                        collectionBean.setInfos(searchResultBean.getInfos());
                        collectionBean.setDesc(searchResultBean.getDesc());
                        collectionBean.setCover(searchResultBean.getCover());
                        collectionBean.setDate(DateUtil.getDefaultTime());
                        //@{新增字段
                        collectionBean.setSectionCount(searchResultBean.getSectionCount());
                        collectionBean.setDirectorList(searchResultBean.getDirectorList());
                        collectionBean.setActorList(searchResultBean.getActorList());
                        collectionBean.setType(searchResultBean.getType());
                        collectionBean.setArea(searchResultBean.getArea());
                        collectionBean.setPublishDate(searchResultBean.getPublishDate());
                        collectionBean.setUpdateDate(searchResultBean.getUpdateDate());
                        collectionBean.setScore(searchResultBean.getScore());
                        collectionBean.setSearchType(searchType);
                        //@}
                        List<Long> result = DbHelper.getInstance().insertCollection(collectionBean);
                        if(Utils.isListEmpty(result)){
                            mHandler.sendEmptyMessage(3);
                        }else{
                            mHandler.sendEmptyMessage(4);
                        }
                    }
                }).start();
                break;
            case 1://已收藏
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(TextUtils.isEmpty(title)){
                            return;
                        }
                        int videoId = title.hashCode();
                        LogUtils.i(TAG+" videoId = "+videoId);
                        int result = DbHelper.getInstance().deleteCollectionByVideoId(videoId);
                        if(result > 0){
                            mHandler.sendEmptyMessage(5);
                        }else{
                            mHandler.sendEmptyMessage(6);
                        }
                    }
                }).start();
                break;
        }
    }

    @Override
    protected void initEventAndData() {
        if(getIntent() != null){
            title = getIntent().getStringExtra(Constant.KEY_TITLE);
            if(!TextUtils.isEmpty(title)){
                tv_title.setText(title);
            }
            url = getIntent().getStringExtra(Constant.KEY_URL);
            LogUtils.i(TAG+" getUrl : "+url);
            currentIndex = getIntent().getIntExtra(Constant.KEY_CURRENT_INDEX,0);
            LogUtils.i(TAG+" currentIndex = "+currentIndex);
            if(currentIndex > 0){
                showIndex = true;
            }else{
                showIndex = false;
            }
            searchType = getIntent().getIntExtra(Constant.KEY_SEARCH_TYPE,Constant.FLAG_SEARCH_ANIM);
            LogUtils.i(TAG+" searchType = "+searchType);
        }
        initCollectState();
        sectionBeanList = new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(mContext,4));
        recyclerView.addItemDecoration(new OffsetRecyclerDivider(SizeUtils.dp2px(10),SizeUtils.dp2px(10)));
        waitDialog = new WaitDialog(mContext,R.style.Dialog_Translucent_Background);
        adapter = new ChooseSectionAdapter(mContext,sectionBeanList);
        adapter.setOnItemClickListener(new ChooseSectionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, SectionBean sectionBean) {
                currentIndex = position + 1;
                if(showIndex){
                    tv_indexes.setText("已观看到第"+currentIndex+"集");
                }
                SearchResultBean searchResultBean = (SearchResultBean) Session.getSession().request(Constant.KEY_RESULT_BEAN);
                HistoryVideoBean historyVideoBean = new HistoryVideoBean();
                historyVideoBean.setCurrentIndex(currentIndex);
                historyVideoBean.setVideoId(searchResultBean.getTitle().hashCode());
                LogUtils.i(TAG+" videoId = "+searchResultBean.getTitle().hashCode());
                historyVideoBean.setAlias(searchResultBean.getAlias());
                historyVideoBean.setTitle(searchResultBean.getTitle());
                historyVideoBean.setUrl(searchResultBean.getUrl());
                historyVideoBean.setInfos(searchResultBean.getInfos());
                historyVideoBean.setDesc(searchResultBean.getDesc());
                historyVideoBean.setCover(searchResultBean.getCover());
                historyVideoBean.setDate(DateUtil.getDefaultTime());
                //@{新增字段
                historyVideoBean.setSectionCount(searchResultBean.getSectionCount());
                historyVideoBean.setDirectorList(searchResultBean.getDirectorList());
                historyVideoBean.setActorList(searchResultBean.getActorList());
                historyVideoBean.setType(searchResultBean.getType());
                historyVideoBean.setArea(searchResultBean.getArea());
                historyVideoBean.setPublishDate(searchResultBean.getPublishDate());
                historyVideoBean.setUpdateDate(searchResultBean.getUpdateDate());
                historyVideoBean.setScore(searchResultBean.getScore());
                historyVideoBean.setSearchType(searchType);
                //@}

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Long> idList = DbHelper.getInstance().insertHistoryVideo(historyVideoBean);
                        LogUtils.i(TAG+" idList = "+Arrays.toString(idList.toArray()));
                        if(!Utils.isListEmpty(idList)){
                            Intent intent = new Intent(Constant.ACTION_UPDATE_CURRENT_INDEX);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                        }
                        if(collectFlag == 1 && !TextUtils.isEmpty(title)){
                            int videoId = title.hashCode();
                            LogUtils.i(TAG+" videoId = "+videoId);
                            int result = DbHelper.getInstance().updateCollectionIndexByVideoId(currentIndex,videoId);
                            if(result > 0){
                                LogUtils.i(TAG+" updateCollectionIndexByVideoId success");
                            }else{
                                LogUtils.e(TAG+" updateCollectionIndexByVideoId failed");
                            }
                        }
                    }
                }).start();
                Session.getSession().put(Constant.KEY_SECTION_BEAN_LIST,sectionBeanList);
                Intent intent = new Intent(mContext,PlayWebActivity.class);
                intent.putExtra(Constant.KEY_TITLE,sectionBean.getTitle());
                intent.putExtra(Constant.KEY_URL,sectionBean.getUrl());
                intent.putExtra(Constant.KEY_NEED_WAIT_PARSE,false);
                intent.putExtra(Constant.KEY_SEARCH_TYPE,searchType);
                intent.putExtra(Constant.KEY_CURRENT_INDEX,position);
                mContext.startActivityForResult(intent,105);
//                Intent intent = new Intent(mContext,VideoPlayActivity.class);
//                intent.putExtra(Constant.KEY_TITLE,sectionBean.getTitle());
//                intent.putExtra(Constant.KEY_URL,sectionBean.getUrl());
//                mContext.startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        requestDocument();
    }

    //初始化收藏按钮状态
    private void initCollectState(){
        if(TextUtils.isEmpty(title)){
            return;
        }
        iv_right.setVisibility(View.VISIBLE);
        int videoId = title.hashCode();
        LogUtils.i(TAG+" videoId = "+videoId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                CollectionBean collectionBean = DbHelper.getInstance().queryCollectionByVideoId(videoId);
                mHandler.sendMessage(Message.obtain(mHandler,1,collectionBean));
            }
        }).start();
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
        NetLoadListener.getInstance().startListening(callback);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Connection connection = Jsoup.connect(url);
                    connection.userAgent(Constant.USER_AGENT_FORPC);
//                    connection.data("searchword",word);
//                    connection.postDataCharset("GB2312");//关键中的关键！！
                    Document document = connection.method(Connection.Method.GET).get();
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
        SearchResultBean searchResultBean = (SearchResultBean) Session.getSession().request(Constant.KEY_RESULT_BEAN);
        if(searchResultBean == null){
            LogUtils.e(TAG+" searchResultBean is null,put new one");
            searchResultBean = new SearchResultBean();
        }
        if(searchType == Constant.FLAG_SEARCH_ANIM){
            //介绍信息
            Element wholeData = document.selectFirst("div.fire.l");
            Element img = wholeData.selectFirst("img[src][alt]");
            String cover = img.attr("src");
            GlideUtil.getInstance().loadImg(mContext,cover,iv_cover);
            if(TextUtils.isEmpty(searchResultBean.getCover())){
                searchResultBean.setCover(cover);
            }
            Element infos = wholeData.selectFirst("div.alex");
            Elements ps = infos.select("p");
            Element p0 = ps.get(0);
            Element p1 = ps.get(1);
            String alias = p0.text();
            String updateInfo = p1.text();
            tv_alias.setText(alias);
            if(TextUtils.isEmpty(searchResultBean.getAlias())){
                searchResultBean.setAlias(alias);
            }
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
            if(TextUtils.isEmpty(searchResultBean.getInfos())){
                searchResultBean.setInfos(updateInfo+" "+type.toString());
            }
            String time = spans.get(2).selectFirst("a").text();
            tv_time.setText("年代："+time);
            StringBuilder tags = new StringBuilder();
            Elements aTags = spans.get(3).select("a");
            for(Element aTag : aTags){
                tags.append(aTag.text()+" ");
            }
            tv_tags.setText("标签："+tags.toString());
            if(showIndex){
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
        }else if(searchType == Constant.FLAG_SEARCH_FILM_TELEVISION){
            Element imgContainElement = document.selectFirst("div.vod-n-img");
            if(imgContainElement != null){
                Element imgTag = imgContainElement.selectFirst("img.loading");
                String cover = imgTag.attr("src");
                GlideUtil.getInstance().loadImg(mContext,cover,iv_cover);
                if(TextUtils.isEmpty(searchResultBean.getCover())){
                    searchResultBean.setCover(cover);
                }
            }
            Element infoContainerElement = document.selectFirst("div.vod-n-l");
            String title = infoContainerElement.selectFirst("h1").text();
            tv_alias.setText(title);
            if(TextUtils.isEmpty(searchResultBean.getAlias())){
                searchResultBean.setAlias(title);
            }
            Element areaP = infoContainerElement.selectFirst("p.vw20");
            Element areaA = areaP.selectFirst("a");
            String area = areaA.text();
            tv_area.setText("地区："+area);
            if(TextUtils.isEmpty(searchResultBean.getArea())){
                searchResultBean.setArea(area);
            }
            Element typeP = infoContainerElement.selectFirst("p.vw60");
            String type = typeP.text().replaceAll("\"","");
            tv_type.setText(type);
            searchResultBean.setType(type);//解决更多影视类型不对问题
            if(TextUtils.isEmpty(searchResultBean.getType())){
                searchResultBean.setType(type);
            }
            Element versionP = infoContainerElement.selectFirst("p.vw38");
            tv_time.setText(versionP.text().replaceAll("\"",""));
            tv_tags.setVisibility(View.GONE);//播放量需要加载js才能出来
            Element actorP = infoContainerElement.selectFirst("p.v-zy");
            if(showIndex){
                tv_indexes.setTextColor(getColor(R.color.color_selected_tags));
                tv_indexes.setText("已观看到第"+currentIndex+"集");
            }else{
                Elements actorAList = actorP.select("a[href]");
                if(actorAList != null && actorAList.size() > 0){
                    StringBuilder stringBuilder = new StringBuilder();
                    for(int i=0;i<actorAList.size();i++){
                        if(i>1){//只取两个演员
                            break;
                        }
                        stringBuilder.append(actorAList.get(i).text()+" ");
                    }
                    tv_indexes.setText("主演："+stringBuilder.toString());
                    if(Utils.isListEmpty(searchResultBean.getActorList())){
                        List<String> actorList = new ArrayList<>();
                        for(int j=0;j<actorAList.size();j++){
                            String actor = actorAList.get(j).text();
                            actorList.add(actor);
                        }
                        searchResultBean.setActorList(actorList);
                    }
                }
            }
            Element directorP = infoContainerElement.selectFirst("p.vw30");
            if(directorP != null && Utils.isListEmpty(searchResultBean.getDirectorList())){
                Elements directorAList = directorP.select("a[href]");
                if(directorAList != null && directorAList.size() > 0) {
                    List<String> directorList = new ArrayList<>();
                    for (int i = 0; i < directorAList.size(); i++) {
                        if (i > 1) {//只取两个导演
                            break;
                        }
                        directorList.add(directorAList.get(i).text());
                    }
                    searchResultBean.setDirectorList(directorList);
                }
            }
            if(TextUtils.isEmpty(searchResultBean.getPublishDate())){
                Element publishP = infoContainerElement.selectFirst("p.ptxt");
                Element publishA = publishP.selectFirst("a[href]");
                searchResultBean.setPublishDate("上映:"+publishA.text());
            }
            if(TextUtils.isEmpty(searchResultBean.getUpdateDate())){
                Element updateP = infoContainerElement.select("p.vw30").get(2);
                searchResultBean.setUpdateDate(updateP.text().replaceAll("\"",""));
            }

            Element descP = infoContainerElement.selectFirst("p.v-js");
            String desc = descP.text().replaceAll("\"","");
            tv_update_info.setText(desc);
            if(TextUtils.isEmpty(searchResultBean.getDesc())){
                searchResultBean.setDesc(desc);
            }

            Element sectionListElement = document.selectFirst("ul.player_list");
            if(sectionListElement == null){
                tv_no_data.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                LogUtils.e(TAG+" 没有选集信息");
                waitDialog.dismiss();
                return;
            }
            Elements aList = sectionListElement.select("a[href]");
            if(aList == null || aList.size() == 0){
                LogUtils.e(TAG+" 选集列表下没有a标签");
                return;
            }
            for(int i=0;i<aList.size();i++){
                SectionBean sectionBean = new SectionBean();
                sectionBean.setTitle(aList.get(i).attr("title"));
                String url = Constant.JIJI_BASE_URL.concat(aList.get(i).attr("href"));
                sectionBean.setUrl(url);
                sectionBeanList.add(sectionBean);
            }
            Collections.reverse(sectionBeanList);
        }
        Session.getSession().put(Constant.KEY_RESULT_BEAN,searchResultBean);
        adapter.notifyDataSetChanged();
        waitDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 106){
            if(data != null){
                ToastUtil.show("加载超时，请检查网络后重试~\n当前看到第"+data.getIntExtra(Constant.KEY_CURRENT_INDEX,currentIndex)+"集");
            }else{
                ToastUtil.show("加载超时，请检查网络后重试~");
            }
        }else if(requestCode == 105 && showIndex){
            tv_indexes.setText("已观看到第"+data.getIntExtra(Constant.KEY_CURRENT_INDEX,currentIndex)+"集");
        }
    }
}
