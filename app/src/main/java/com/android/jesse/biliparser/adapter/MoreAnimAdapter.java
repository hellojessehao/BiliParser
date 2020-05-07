package com.android.jesse.biliparser.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.activity.ChooseSectionActivity;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.network.model.bean.AnimRecommendItemBean;
import com.android.jesse.biliparser.network.model.bean.MoreAnimItemBean;
import com.android.jesse.biliparser.network.model.bean.SearchResultBean;
import com.android.jesse.biliparser.utils.GlideUtil;
import com.android.jesse.biliparser.utils.Session;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Description: 更多动漫适配器
 * @author: zhangshihao
 * @date: 2020/4/29
 */
public class MoreAnimAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = MoreAnimAdapter.class.getSimpleName();

    private Context mContext;
    private List<MoreAnimItemBean> animItemBeanList;
    private List<SearchResultBean> searchResultBeanList;
    private int typeId;

    public MoreAnimAdapter(Context context,List dataBeanList,int typeId){
        mContext = context;
        this.typeId = typeId;
        switch (typeId){
            case Constant.ANIM_TYPE_MOST_LAST:
                animItemBeanList = (List<MoreAnimItemBean>)dataBeanList;
            case Constant.ANIM_TYPE_JAPEN:
            case Constant.ANIM_TYPE_CHINA:
            case Constant.ANIM_TYPE_AMERICA:
            case Constant.ANIM_TYPE_FILM:
                searchResultBeanList = (List<SearchResultBean>)dataBeanList;
                break;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (typeId){
            case Constant.ANIM_TYPE_MOST_LAST:
                return new LastAnimViewHolder(LayoutInflater.from(mContext).inflate(R.layout.more_anim_adapter,null,false));
            case Constant.ANIM_TYPE_JAPEN:
            case Constant.ANIM_TYPE_CHINA:
            case Constant.ANIM_TYPE_AMERICA:
                return new MoreJpAnimViewHolder(LayoutInflater.from(mContext).inflate(R.layout.more_jp_anim_adapter,null,false));
            case Constant.ANIM_TYPE_FILM:
                return new MoreAnimMovieViewHolder(LayoutInflater.from(mContext).inflate(R.layout.more_anim_movie_adapter,null,false));
        }
        return new LastAnimViewHolder(LayoutInflater.from(mContext).inflate(R.layout.more_anim_adapter,null,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if(typeId == Constant.ANIM_TYPE_MOST_LAST){//最新更新里的更多动漫
            MoreAnimItemBean itemBean = animItemBeanList.get(position);
            LastAnimViewHolder lastAnimViewHolder = (LastAnimViewHolder) viewHolder;
            lastAnimViewHolder.tv_type.setText(itemBean.getType());
            lastAnimViewHolder.tv_title.setText(itemBean.getTitle());
            lastAnimViewHolder.tv_section_count.setText(itemBean.getSectionCount());
            lastAnimViewHolder.tv_update_date.setText(itemBean.getUpdateDate());
            lastAnimViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
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
        }else if(typeId == Constant.ANIM_TYPE_JAPEN ||
                typeId == Constant.ANIM_TYPE_CHINA ||
                typeId == Constant.ANIM_TYPE_AMERICA){//更多日漫 国漫 美漫
            SearchResultBean searchResultBean = searchResultBeanList.get(position);
            MoreJpAnimViewHolder moreJpAnimViewHolder = (MoreJpAnimViewHolder)viewHolder;
            GlideUtil.getInstance().loadImg(mContext,searchResultBean.getCover(),moreJpAnimViewHolder.iv_cover);
            moreJpAnimViewHolder.tv_title.setText(searchResultBean.getTitle());
            moreJpAnimViewHolder.tv_alias.setText(searchResultBean.getAlias());
            moreJpAnimViewHolder.tv_infos.setText(searchResultBean.getInfos());
            moreJpAnimViewHolder.tv_desc.setText(searchResultBean.getDesc());
            moreJpAnimViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Session.getSession().put(Constant.KEY_RESULT_BEAN,searchResultBean);
                    Intent intent = new Intent(mContext,ChooseSectionActivity.class);
                    intent.putExtra(Constant.KEY_TITLE,searchResultBean.getTitle());
                    intent.putExtra(Constant.KEY_URL,searchResultBean.getUrl());
                    intent.putExtra(Constant.KEY_SEARCH_TYPE,searchResultBean.getSearchType());
                    mContext.startActivity(intent);
                }
            });
        }else if(typeId == Constant.ANIM_TYPE_FILM){//更多动漫电影
            MoreAnimMovieViewHolder moreAnimMovieViewHolder = (MoreAnimMovieViewHolder)viewHolder;
            SearchResultBean resultBean = searchResultBeanList.get(position);
            GlideUtil.getInstance().loadImg(mContext,resultBean.getCover(),moreAnimMovieViewHolder.iv_cover);
            moreAnimMovieViewHolder.tv_title.setText(resultBean.getTitle());
            moreAnimMovieViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchResultBean searchResultBean = new SearchResultBean();
                    searchResultBean.setTitle(resultBean.getTitle());
                    searchResultBean.setCover(resultBean.getCover());
                    searchResultBean.setUrl(resultBean.getUrl());
                    searchResultBean.setSectionCount("");
                    searchResultBean.setInfos("");
                    searchResultBean.setAlias("");
                    searchResultBean.setDesc("");
                    searchResultBean.setSearchType(Constant.FLAG_SEARCH_ANIM);
                    Session.getSession().put(Constant.KEY_RESULT_BEAN,searchResultBean);
                    Intent intent = new Intent(mContext,ChooseSectionActivity.class);
                    intent.putExtra(Constant.KEY_TITLE,resultBean.getTitle());
                    intent.putExtra(Constant.KEY_URL,resultBean.getUrl());
                    intent.putExtra(Constant.KEY_SEARCH_TYPE,Constant.FLAG_SEARCH_ANIM);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        switch (typeId){
            case Constant.ANIM_TYPE_MOST_LAST:
                return animItemBeanList==null?0:animItemBeanList.size();
            case Constant.ANIM_TYPE_JAPEN:
            case Constant.ANIM_TYPE_CHINA:
            case Constant.ANIM_TYPE_AMERICA:
            case Constant.ANIM_TYPE_FILM:
                return searchResultBeanList==null?0:searchResultBeanList.size();
        }
        return animItemBeanList==null?0:animItemBeanList.size();
    }

    public class MoreAnimMovieViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_cover)
        ImageView iv_cover;
        @BindView(R.id.tv_title)
        TextView tv_title;
        public MoreAnimMovieViewHolder(@NonNull View itemView) {
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

    class MoreJpAnimViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_cover)
        ImageView iv_cover;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_alias)
        TextView tv_alias;
        @BindView(R.id.tv_infos)
        TextView tv_infos;
        @BindView(R.id.tv_desc)
        TextView tv_desc;
        public MoreJpAnimViewHolder(@NonNull View itemView) {
            super(itemView);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,SizeUtils.dp2px(150));
            itemView.setLayoutParams(layoutParams);
            ButterKnife.bind(this,itemView);
        }
    }

    public class LastAnimViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_type)
        TextView tv_type;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_section_count)
        TextView tv_section_count;
        @BindView(R.id.tv_update_date)
        TextView tv_update_date;
        public LastAnimViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
