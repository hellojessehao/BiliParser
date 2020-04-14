package com.android.jesse.biliparser.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.adapter.HistoryVideoAdapter;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.components.WaitDialog;
import com.android.jesse.biliparser.db.base.DbHelper;
import com.android.jesse.biliparser.db.bean.HistoryVideoBean;
import com.android.jesse.biliparser.network.base.SimpleActivity;
import com.android.jesse.biliparser.network.component.OffsetRecyclerDivider;
import com.android.jesse.biliparser.network.model.bean.SearchResultBean;
import com.android.jesse.biliparser.network.util.ToastUtil;
import com.android.jesse.biliparser.utils.DialogUtil;
import com.android.jesse.biliparser.utils.LogUtils;
import com.android.jesse.biliparser.utils.Session;
import com.android.jesse.biliparser.utils.Utils;
import com.blankj.utilcode.util.SizeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @Description: 播放历史页面
 * @author: zhangshihao
 * @date: 2020/4/8
 */
public class HistoryVideoActivity extends SimpleActivity {

    private static final String TAG = HistoryVideoActivity.class.getSimpleName();

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_no_data)
    TextView tv_no_data;

    private List<HistoryVideoBean> historyVideoBeanList;
    private HistoryVideoAdapter adapter;
    private WaitDialog waitDialog;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                waitDialog.dismiss();
                tv_no_data.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                historyVideoBeanList.clear();
                historyVideoBeanList.addAll((List<HistoryVideoBean>)msg.obj);
                adapter.notifyDataSetChanged();
            }else if(msg.what == 1){
                historyVideoBeanList.remove(popPosition);
                adapter.notifyItemRemoved(popPosition);
            }else if(msg.what == 2){
                historyVideoBeanList.clear();
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.GONE);
                tv_no_data.setVisibility(View.VISIBLE);
            }
        }
    };
    private BroadcastReceiver sectionChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getData();
        }
    };
    private PopupWindow longClickPop;
    private int popPosition;
    private HistoryVideoBean popVideoBean;
    private PopupWindow spinnerPop;
    private Dialog clearHintDialog;

    @Override
    protected String getTitleName() {
        return "观看历史";
    }

    @Override
    protected void onBackClick() {
        super.onBackClick();
        finish();
    }

    @Override
    protected void onRightClick() {
        super.onRightClick();
        spinnerPop.showAsDropDown(iv_right,0,-SizeUtils.dp2px(12));
    }

    @Override
    protected int getLayout() {
        return R.layout.play_history_activity;
    }

    @Override
    protected void initEventAndData() {
        waitDialog = new WaitDialog(mContext,R.style.Dialog_Translucent_Background);
        waitDialog.show();
        initLongClickPop();
        initSpinnerPop();
        iv_right.setVisibility(View.VISIBLE);
        iv_right.setImageResource(R.mipmap.ic_menu);
        historyVideoBeanList = new ArrayList<>();
        adapter = new HistoryVideoAdapter(mContext,historyVideoBeanList);
        adapter.setOnItemClickListener(new HistoryVideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HistoryVideoBean videoBean) {
                SearchResultBean searchResultBean = new SearchResultBean();
                searchResultBean.setDesc(videoBean.getDesc());
                searchResultBean.setInfos(videoBean.getInfos());
                searchResultBean.setAlias(videoBean.getAlias());
                searchResultBean.setTitle(videoBean.getTitle());
                searchResultBean.setCover(videoBean.getCover());
                searchResultBean.setUrl(videoBean.getUrl());
                Session.getSession().put(Constant.KEY_RESULT_BEAN,searchResultBean);
                Intent intent = new Intent(mContext,ChooseSectionActivity.class);
                intent.putExtra(Constant.KEY_TITLE,videoBean.getTitle());
                intent.putExtra(Constant.KEY_URL,videoBean.getUrl());
                intent.putExtra(Constant.KEY_CURRENT_INDEX,videoBean.getCurrentIndex());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position,HistoryVideoBean videoBean,View itemView) {
                popPosition = position;
                popVideoBean = videoBean;
                int width = itemView.getWidth();
                int height = itemView.getHeight();
                longClickPop.showAsDropDown(itemView,width/2-SizeUtils.dp2px(40),-(height/2),Gravity.CENTER);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new OffsetRecyclerDivider(0,SizeUtils.dp2px(15)));
        recyclerView.setAdapter(adapter);
        getData();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(sectionChangeReceiver,new IntentFilter(Constant.ACTION_UPDATE_CURRENT_INDEX));
    }

    private void initSpinnerPop(){
        spinnerPop = new PopupWindow(mContext);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.history_video_spinner_pop,null,false);
        spinnerPop.setContentView(contentView);
        spinnerPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spinnerPop.setAnimationStyle(R.style.WindowStyle);
        spinnerPop.setOutsideTouchable(true);
        spinnerPop.setWidth(SizeUtils.dp2px(45));
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerPop.dismiss();
                switch (v.getId()){
                    case R.id.tv_clear:
                        if(Utils.isListEmpty(historyVideoBeanList)){
                            ToastUtil.shortShow("你在为难我胖虎");
                            break;
                        }
                        clearHintDialog = DialogUtil.showHintDialogForCommonVersion(mContext, "确定要清空观看记录吗？", "确定",
                                "算了", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        clearHintDialog.dismiss();
                                        switch (v.getId()){
                                            case R.id.tv_positive:
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        int result = DbHelper.getInstance().clear();
                                                        LogUtils.i(TAG+" result = "+result);
                                                        mHandler.sendMessage(Message.obtain(mHandler,2));
                                                    }
                                                }).start();
                                                break;
                                        }
                                    }
                                });
                        break;
                }
            }
        };
        TextView tv_clear = contentView.findViewById(R.id.tv_clear);
        tv_clear.setOnClickListener(onClickListener);
    }

    private void initLongClickPop(){
        longClickPop = new PopupWindow(mContext);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.history_video_long_click_pop,null,false);
        longClickPop.setContentView(contentView);
        longClickPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        longClickPop.setAnimationStyle(R.style.WindowStyle);
        longClickPop.setOutsideTouchable(true);
        longClickPop.setWidth(SizeUtils.dp2px(80));
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_collect:
                        Toast.makeText(mContext, "暂未开发", Toast.LENGTH_SHORT).show();
                        longClickPop.dismiss();
                        break;
                    case R.id.tv_delete:
                        longClickPop.dismiss();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int result = DbHelper.getInstance().deleteByVideoId(popVideoBean.getVideoId());
                                LogUtils.i(TAG+" result = "+result);
                                if(result > 0){
                                    mHandler.sendMessage(Message.obtain(mHandler,1));
                                }else{
                                    LogUtils.e(TAG+" delete item failed in local db");
                                }
                            }
                        }).start();
                        break;
                }
            }
        };
        TextView tv_collect = contentView.findViewById(R.id.tv_collect);
        TextView tv_delete = contentView.findViewById(R.id.tv_delete);
        tv_collect.setOnClickListener(onClickListener);
        tv_delete.setOnClickListener(onClickListener);
    }

    private void getData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<HistoryVideoBean> tempVideoBeanList = DbHelper.getInstance().queryAll();
                LogUtils.i(TAG+" tempVideoBeanList size = "+tempVideoBeanList.size());
                if(Utils.isListEmpty(tempVideoBeanList)){
                    waitDialog.dismiss();
                    tv_no_data.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    return;
                }
                mHandler.sendMessage(Message.obtain(mHandler,0,tempVideoBeanList));
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(sectionChangeReceiver);
    }
}
