package com.android.jesse.biliparser.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.network.base.SimpleActivity;
import com.android.jesse.biliparser.network.component.OffsetRecyclerDivider;
import com.blankj.utilcode.util.SizeUtils;

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

    @Override
    protected int getLayout() {
        return R.layout.collection_activity;
    }

    @Override
    protected void initEventAndData() {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new OffsetRecyclerDivider(0,SizeUtils.dp2px(15)));

    }

}
