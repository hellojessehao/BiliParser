package com.android.jesse.biliparser.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.activity.ChooseSectionActivity;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.network.component.OffsetRecyclerDivider;
import com.android.jesse.biliparser.network.model.bean.AnimRecommendItemBean;
import com.android.jesse.biliparser.network.model.bean.SearchResultBean;
import com.android.jesse.biliparser.utils.GlideUtil;
import com.android.jesse.biliparser.utils.LogUtils;
import com.android.jesse.biliparser.utils.Session;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Description: 动漫推荐列表条目列表适配器
 * @author: zhangshihao
 * @date: 2020/4/28
 */
public class AnimRecommendItemAdapter extends RecyclerView.Adapter<AnimRecommendItemAdapter.ViewHolder>{

    private static final String TAG = AnimRecommendItemAdapter.class.getSimpleName();

    private List<AnimRecommendItemBean.DataBean> dataBeanList;
    private Context mContext;

    public AnimRecommendItemAdapter(Context mContext, List<AnimRecommendItemBean.DataBean> dataBeanList) {
        this.mContext = mContext;
        this.dataBeanList = dataBeanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.anim_recommend_item_adapter,null,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        AnimRecommendItemBean.DataBean dataBean = dataBeanList.get(position);
        GlideUtil.getInstance().loadImg(mContext,dataBean.getCover(),viewHolder.iv_cover);
        viewHolder.tv_title.setText(dataBean.getTitle());
        viewHolder.tv_section_count.setText(dataBean.getSectionCount());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchResultBean searchResultBean = new SearchResultBean();
                searchResultBean.setTitle(dataBean.getTitle());
                searchResultBean.setCover(dataBean.getCover());
                searchResultBean.setUrl(dataBean.getUrl());
                searchResultBean.setSectionCount(dataBean.getSectionCount());
                searchResultBean.setInfos("");
                searchResultBean.setAlias("");
                searchResultBean.setDesc("");
                searchResultBean.setSearchType(Constant.FLAG_SEARCH_ANIM);
                Session.getSession().put(Constant.KEY_RESULT_BEAN,searchResultBean);
                Intent intent = new Intent(mContext,ChooseSectionActivity.class);
                intent.putExtra(Constant.KEY_TITLE,dataBean.getTitle());
                intent.putExtra(Constant.KEY_URL,dataBean.getUrl());
                intent.putExtra(Constant.KEY_SEARCH_TYPE,Constant.FLAG_SEARCH_ANIM);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataBeanList==null?0:dataBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_cover)
        ImageView iv_cover;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_section_count)
        TextView tv_section_count;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            int columnCount = 3;//3列
            int dividerWidth = 10;//两列之间的间距 dp
            int dividerLeftRight = 10;//与屏幕左右两边的间距总和 dp
            float width = (float)(ScreenUtils.getScreenWidth() - columnCount*SizeUtils.dp2px(dividerWidth) - SizeUtils.dp2px(dividerLeftRight))/3;
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams((int)width,ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(layoutParams);
            ButterKnife.bind(this,itemView);
        }
    }

}
