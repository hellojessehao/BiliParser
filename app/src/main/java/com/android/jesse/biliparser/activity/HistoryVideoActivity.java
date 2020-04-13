package com.android.jesse.biliparser.activity;

import android.support.v7.widget.RecyclerView;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.adapter.HistoryVideoAdapter;
import com.android.jesse.biliparser.components.WaitDialog;
import com.android.jesse.biliparser.db.bean.HistoryVideoBean;
import com.android.jesse.biliparser.network.base.SimpleActivity;

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

    private List<HistoryVideoBean> historyVideoBeanList;
    private HistoryVideoAdapter adapter;
    private WaitDialog waitDialog;

    @Override
    protected int getLayout() {
        return R.layout.play_history_activity;
    }

    @Override
    protected void initEventAndData() {


        historyVideoBeanList = new ArrayList<>();
        waitDialog = new WaitDialog(mContext,R.style.Dialog_Translucent_Background);
        adapter = new HistoryVideoAdapter(mContext,historyVideoBeanList);
        recyclerView.setAdapter(adapter);
    }

}
