package com.android.jesse.biliparser.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.activity.MoreAnimActivity;
import com.android.jesse.biliparser.activity.MoreFilmActivity;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.network.component.OffsetRecyclerDivider;
import com.android.jesse.biliparser.network.model.bean.FilmRecommendItemBean;
import com.blankj.utilcode.util.SizeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Description: 动漫推荐列表适配器
 * @author: zhangshihao
 * @date: 2020/4/28
 */
public class FilmRecommendAdapter extends RecyclerView.Adapter<FilmRecommendAdapter.ViewHolder>{

    private static final String TAG = FilmRecommendAdapter.class.getSimpleName();

    private List<FilmRecommendItemBean> recommendItemBeanList;
    private Context mContext;

    public FilmRecommendAdapter(Context mContext, List<FilmRecommendItemBean> recommendItemBeanList) {
        this.mContext = mContext;
        this.recommendItemBeanList = recommendItemBeanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.anim_recommend_adapter,null,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        FilmRecommendItemBean itemBean = recommendItemBeanList.get(position);
        viewHolder.tv_type.setText(itemBean.getType());
        viewHolder.tv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,MoreFilmActivity.class);
                intent.putExtra(Constant.KEY_URL,itemBean.getMoreUrl());
                intent.putExtra(Constant.KEY_TYPE_ID,itemBean.getTypeId());
                mContext.startActivity(intent);
            }
        });
        viewHolder.recyclerView.setLayoutManager(new GridLayoutManager(mContext,3));
        viewHolder.recyclerView.addItemDecoration(new OffsetRecyclerDivider(8,SizeUtils.dp2px(15)));
        viewHolder.recyclerView.setAdapter(new FilmRecommendItemAdapter(mContext,itemBean.getDataBeanList()));
    }

    @Override
    public int getItemCount() {
        return recommendItemBeanList==null?0:recommendItemBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_type)
        TextView tv_type;
        @BindView(R.id.tv_more)
        TextView tv_more;
        @BindView(R.id.recyclerView)
        RecyclerView recyclerView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
