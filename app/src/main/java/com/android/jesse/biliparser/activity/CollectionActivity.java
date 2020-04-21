package com.android.jesse.biliparser.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.adapter.CollectionAdapter;
import com.android.jesse.biliparser.adapter.HistoryVideoAdapter;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.components.WaitDialog;
import com.android.jesse.biliparser.db.base.DbHelper;
import com.android.jesse.biliparser.db.bean.CollectionBean;
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
 * @Description: 收藏页面
 * @author: zhangshihao
 * @date: 2020/4/14
 */
public class CollectionActivity extends SimpleActivity {

    private static final String TAG = CollectionActivity.class.getSimpleName();

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_no_data)
    TextView tv_no_data;

    private List<CollectionBean> collectionBeanList;
    private CollectionAdapter collectionAdapter;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                waitDialog.dismiss();
                recyclerView.setVisibility(View.VISIBLE);
                tv_no_data.setVisibility(View.GONE);
                collectionBeanList.clear();
                collectionBeanList.addAll((List<CollectionBean>)msg.obj);
                collectionAdapter.notifyDataSetChanged();
            }else if(msg.what == 1){
                waitDialog.dismiss();
                tv_no_data.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }else if(msg.what == 2){
                collectionBeanList.clear();
                collectionAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.GONE);
                tv_no_data.setVisibility(View.VISIBLE);
            }else if(msg.what == 3){
                collectionBeanList.remove(popPosition);
                collectionAdapter.notifyItemRemoved(popPosition);
                if(Utils.isListEmpty(collectionBeanList)){
                    tv_no_data.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else{
                    tv_no_data.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        }
    };
    private PopupWindow longClickPop;
    private PopupWindow spinnerPop;
    private Dialog clearHintDialog;
    private int popPosition;
    private CollectionBean popCollectionBean;
    private WaitDialog waitDialog;

    @Override
    protected int getLayout() {
        return R.layout.collection_activity;
    }

    @Override
    protected String getTitleName() {
        return "收藏夹";
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
    protected void initEventAndData() {
        waitDialog = new WaitDialog(mContext,R.style.Dialog_Translucent_Background);
        iv_right.setVisibility(View.VISIBLE);
        iv_right.setImageResource(R.mipmap.ic_menu);
        initLongClickPop();
        initSpinnerPop();

        collectionBeanList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new OffsetRecyclerDivider(0,SizeUtils.dp2px(15)));
        collectionAdapter = new CollectionAdapter(mContext,collectionBeanList);
        collectionAdapter.setOnItemClickListener(new CollectionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CollectionBean collectionBean) {
                SearchResultBean searchResultBean = new SearchResultBean();
                searchResultBean.setDesc(collectionBean.getDesc());
                searchResultBean.setInfos(collectionBean.getInfos());
                searchResultBean.setAlias(collectionBean.getAlias());
                searchResultBean.setTitle(collectionBean.getTitle());
                searchResultBean.setCover(collectionBean.getCover());
                searchResultBean.setUrl(collectionBean.getUrl());
                //@{新增字段
                searchResultBean.setSectionCount(collectionBean.getSectionCount());
                searchResultBean.setDirectorList(collectionBean.getDirectorList());
                searchResultBean.setActorList(collectionBean.getActorList());
                searchResultBean.setType(collectionBean.getType());
                searchResultBean.setArea(collectionBean.getArea());
                searchResultBean.setPublishDate(collectionBean.getPublishDate());
                searchResultBean.setUpdateDate(collectionBean.getUpdateDate());
                searchResultBean.setScore(collectionBean.getScore());
                searchResultBean.setSearchType(collectionBean.getSearchType());
                //@}
                Session.getSession().put(Constant.KEY_RESULT_BEAN,searchResultBean);
                Intent intent = new Intent(mContext,ChooseSectionActivity.class);
                intent.putExtra(Constant.KEY_TITLE,collectionBean.getTitle());
                intent.putExtra(Constant.KEY_URL,collectionBean.getUrl());
                intent.putExtra(Constant.KEY_CURRENT_INDEX,collectionBean.getCurrentIndex());
                intent.putExtra(Constant.KEY_SEARCH_TYPE,collectionBean.getSearchType());
                startActivityForResult(intent,101);
            }

            @Override
            public void onItemLongClick(int position,CollectionBean collectionBean,View itemView) {
                popPosition = position;
                popCollectionBean = collectionBean;
                int width = itemView.getWidth();
                int height = itemView.getHeight();
                longClickPop.showAsDropDown(itemView,width/2-SizeUtils.dp2px(40),-(height/2),Gravity.CENTER);
            }
        });
        recyclerView.setAdapter(collectionAdapter);
        getData();
    }

    private void initSpinnerPop(){
        spinnerPop = new PopupWindow(mContext);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.collection_spinner_pop,null,false);
        spinnerPop.setContentView(contentView);
        spinnerPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spinnerPop.setAnimationStyle(R.style.WindowStyle);
        spinnerPop.setOutsideTouchable(true);
        spinnerPop.setWidth(SizeUtils.dp2px(65));
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerPop.dismiss();
                switch (v.getId()){
                    case R.id.tv_clear:
                        if(Utils.isListEmpty(collectionBeanList)){
                            ToastUtil.shortShow("你在为难我胖虎");
                            break;
                        }
                        clearHintDialog = DialogUtil.showHintDialogForCommonVersion(mContext, "确定要清空收藏夹吗？", "确定",
                                "算了", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        clearHintDialog.dismiss();
                                        switch (v.getId()){
                                            case R.id.tv_positive:
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        int result = DbHelper.getInstance().clearCollection();
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
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.collection_long_click_pop,null,false);
        longClickPop.setContentView(contentView);
        longClickPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        longClickPop.setAnimationStyle(R.style.WindowStyle);
        longClickPop.setOutsideTouchable(true);
        longClickPop.setWidth(SizeUtils.dp2px(80));
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_delete:
                        longClickPop.dismiss();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if(popCollectionBean == null){
                                    return;
                                }
                                int result = DbHelper.getInstance().deleteCollectionByVideoId(popCollectionBean.getVideoId());
                                LogUtils.i(TAG+" result = "+result);
                                if(result > 0){
                                    mHandler.sendMessage(Message.obtain(mHandler,3));
                                }else{
                                    LogUtils.e(TAG+" delete item failed in local db");
                                }
                            }
                        }).start();
                        break;
                }
            }
        };
        TextView tv_delete = contentView.findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(onClickListener);
    }

    private void getData(){
        waitDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CollectionBean> collectionBeans = DbHelper.getInstance().queryAllCollection();
                if(Utils.isListEmpty(collectionBeans)){
                    mHandler.sendEmptyMessage(1);
                    return;
                }
                mHandler.sendMessage(Message.obtain(mHandler,0,collectionBeans));
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101){
            getData();
        }
    }
}
