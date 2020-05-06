package com.android.jesse.biliparser.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
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
import com.android.jesse.biliparser.utils.LogUtils;
import com.android.jesse.biliparser.utils.NetLoadListener;
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
 * @Description:
 * @author: zhangshihao
 * @date: 2020/4/28
 */
public class MoreAnimActivity extends SimpleActivity {
    //TODO:更多需要区分类型解析！！！！
    private static final String TAG = MoreAnimActivity.class.getSimpleName();

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_no_data)
    TextView tv_no_data;

    private String url;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NetLoadListener.getInstance().stopListening();
            if(msg.what == 0){
                if(msg.obj == null){
                    Toast.makeText(mContext, R.string.data_list_is_empty , Toast.LENGTH_SHORT).show();
                    LogUtils.e(TAG+" obj is null");
                    return;
                }
                parseDocument((Document)msg.obj);
            }else if(msg.what == 1){
                waitDialog.dismiss();
                setEmptyState();
            }
        }
    };
    private WaitDialog waitDialog;
    private List<MoreAnimItemBean> itemBeanList = new ArrayList<>();
    private MoreAnimAdapter adapter;

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
        if(getIntent() != null){
            url = getIntent().getStringExtra(Constant.KEY_URL);
            LogUtils.d(TAG+" url = "+url);
        }
        waitDialog = new WaitDialog(mContext,R.style.Dialog_Translucent_Background);
        adapter = new MoreAnimAdapter(mContext,itemBeanList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new OffsetRecyclerDivider(0,SizeUtils.dp2px(15)));
        recyclerView.setAdapter(adapter);
        if(!TextUtils.isEmpty(url)){
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
        }else{
            setEmptyState();
        }
    }

    private void setEmptyState(){
        tv_no_data.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void parseDocument(Document document){
        Element listContainer = document.selectFirst("div.topli");
        if(listContainer == null){
            setEmptyState();
            return;
        }
        Elements liList = listContainer.select("li");
        if(!Utils.isListEmpty(liList)){
            for(int i=0;i<liList.size();i++){
                Element li = liList.get(i);
                Elements aList = li.select("a");
                MoreAnimItemBean itemBean = new MoreAnimItemBean();
                if(!Utils.isListEmpty(aList)){
                    for(int j=0;j<aList.size();j++){
                        Element a = aList.get(j);
                        if(j == 0){
                            itemBean.setType(a.text());
                        }else if(j == 1){
                            itemBean.setTitle(a.text());
                            itemBean.setUrl(Constant.SAKURA_SEARCH_URL.concat(a.attr("href")));
                        }else if(j == 2){
                            itemBean.setSectionCount(a.text());
                        }
                    }
                }else{
                    LogUtils.e(TAG+" alist is empty");
                }
                Element em = li.selectFirst("em");
                itemBean.setUpdateDate(em.text());
                itemBeanList.add(itemBean);
            }
            LogUtils.d(TAG+" itemBeanList : \n"+Arrays.toString(itemBeanList.toArray()));
        }else{
            setEmptyState();
        }
        waitDialog.dismiss();
        if(!Utils.isListEmpty(itemBeanList)){
            adapter.notifyDataSetChanged();
        }else{
            setEmptyState();
        }
    }

}
