package com.android.jesse.biliparser.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.activity.ChooseSectionActivity;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.network.model.bean.MoreAnimItemBean;
import com.android.jesse.biliparser.network.model.bean.SearchResultBean;
import com.android.jesse.biliparser.utils.Session;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Description: 更多动漫适配器
 * @author: zhangshihao
 * @date: 2020/4/29
 */
public class MoreFilmAdapter extends RecyclerView.Adapter<MoreFilmAdapter.ViewHolder>{

    private static final String TAG = MoreFilmAdapter.class.getSimpleName();

    private Context mContext;
    private List<MoreAnimItemBean> animItemBeanList;

    public MoreFilmAdapter(Context context, List<MoreAnimItemBean> animItemBeanList){
        mContext = context;
        this.animItemBeanList = animItemBeanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.more_anim_adapter,null,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        MoreAnimItemBean itemBean = animItemBeanList.get(position);
        viewHolder.tv_type.setText(itemBean.getType());
        viewHolder.tv_title.setText(itemBean.getTitle());
        viewHolder.tv_section_count.setText(itemBean.getSectionCount());
        viewHolder.tv_update_date.setText(itemBean.getUpdateDate());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchResultBean searchResultBean = new SearchResultBean();
                searchResultBean.setTitle(itemBean.getTitle());
                searchResultBean.setCover("");
                searchResultBean.setUrl(itemBean.getUrl());
                searchResultBean.setSectionCount(itemBean.getSectionCount());
                searchResultBean.setInfos("");
                searchResultBean.setAlias("");
                searchResultBean.setDesc("");
                searchResultBean.setSearchType(Constant.FLAG_SEARCH_ANIM);
                Session.getSession().put(Constant.KEY_RESULT_BEAN,searchResultBean);
                Intent intent = new Intent(mContext,ChooseSectionActivity.class);
                intent.putExtra(Constant.KEY_TITLE,itemBean.getTitle());
                intent.putExtra(Constant.KEY_URL,itemBean.getUrl());
                intent.putExtra(Constant.KEY_SEARCH_TYPE,Constant.FLAG_SEARCH_ANIM);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return animItemBeanList==null?0:animItemBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_type)
        TextView tv_type;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_section_count)
        TextView tv_section_count;
        @BindView(R.id.tv_update_date)
        TextView tv_update_date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
