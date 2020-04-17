package com.android.jesse.biliparser.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.network.model.bean.SearchResultBean;
import com.android.jesse.biliparser.utils.GlideUtil;
import com.android.jesse.biliparser.utils.Utils;
import com.blankj.utilcode.util.SizeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Description: 搜索结果适配器
 * @author: zhangshihao
 * @date: 2020/3/24
 */
public class SearchResultAdapter extends RecyclerView.Adapter{

    private static final String TAG = SearchResultAdapter.class.getSimpleName();
    private List<SearchResultBean> resultBeanList;
    private Context mContext;
    private int searchType = Constant.FLAG_SEARCH_ANIM;

    public SearchResultAdapter(Context mContext,List<SearchResultBean> resultBeanList,int searchType) {
        this.resultBeanList = resultBeanList;
        this.mContext = mContext;
        this.searchType = searchType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(searchType == Constant.FLAG_SEARCH_ANIM){
            AnimViewHolder viewHolder = new AnimViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_result_adapter,null,false));
            return viewHolder;
        }else if(searchType == Constant.FLAG_SEARCH_FILM_TELEVISION){
            FilmViewHolder filmViewHolder = new FilmViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_result_adapter_film_holder,null,false));
            return filmViewHolder;
        }else{
            AnimViewHolder viewHolder = new AnimViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_result_adapter,null,false));
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        SearchResultBean searchResultBean = resultBeanList.get(position);
        if(searchType == Constant.FLAG_SEARCH_ANIM){
            AnimViewHolder animViewHolder = (AnimViewHolder)viewHolder;
            GlideUtil.getInstance().loadImg(mContext,searchResultBean.getCover(),animViewHolder.iv_cover);
            animViewHolder.tv_title.setText(searchResultBean.getTitle());
            animViewHolder.tv_alias.setText(searchResultBean.getAlias());
            animViewHolder.tv_infos.setText(searchResultBean.getInfos());
            animViewHolder.tv_desc.setText(searchResultBean.getDesc());
            animViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(searchResultBean);
                    }
                }
            });
        }else if(searchType == Constant.FLAG_SEARCH_FILM_TELEVISION){
            FilmViewHolder filmViewHolder = (FilmViewHolder) viewHolder;
            GlideUtil.getInstance().loadImg(mContext,searchResultBean.getCover(),filmViewHolder.iv_cover);
            filmViewHolder.tv_section_count.setText(searchResultBean.getSectionCount());
            filmViewHolder.tv_title.setText(searchResultBean.getTitle());
            filmViewHolder.tv_desc.setText(searchResultBean.getDesc());
            filmViewHolder.tv_area.setText("地区:"+searchResultBean.getArea());
            filmViewHolder.tv_type.setText(searchResultBean.getType());
            filmViewHolder.tv_publish_time.setText(searchResultBean.getPublishDate());
            filmViewHolder.tv_update_time.setText(searchResultBean.getUpdateDate());
            List<String> directorList = searchResultBean.getDirectorList();
            if(!Utils.isListEmpty(directorList)){
                StringBuilder stringBuilder = new StringBuilder();
                for(int i=0;i<directorList.size();i++){
                    if(i>1){//只显示两个导演名
                        break;
                    }
                    stringBuilder.append(directorList.get(i)+" ");
                }
                filmViewHolder.tv_director.setText("导演:"+stringBuilder.toString());
            }else{
                filmViewHolder.tv_director.setText("导演:未知");
            }
            List<String> actorList = searchResultBean.getActorList();
            if(!Utils.isListEmpty(actorList)){
                StringBuilder stringBuilder = new StringBuilder();
                for(int i=0;i<actorList.size();i++){
                    if(i>1){//只显示两个主演
                        break;
                    }
                    stringBuilder.append(actorList.get(i)+" ");
                }
                filmViewHolder.tv_actor.setText("主演:"+stringBuilder.toString());
            }else{
                filmViewHolder.tv_actor.setText("主演:未知");
            }
            filmViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(searchResultBean);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return resultBeanList==null?0:resultBeanList.size();
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(SearchResultBean resultBean);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class AnimViewHolder extends RecyclerView.ViewHolder{
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
        public AnimViewHolder(@NonNull View itemView) {
            super(itemView);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,SizeUtils.dp2px(150));
            itemView.setLayoutParams(layoutParams);
            ButterKnife.bind(this,itemView);
        }
    }

    class FilmViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_cover)
        ImageView iv_cover;
        @BindView(R.id.tv_section_count)
        TextView tv_section_count;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_area)
        TextView tv_area;
        @BindView(R.id.tv_type)
        TextView tv_type;
        @BindView(R.id.tv_publish_time)
        TextView tv_publish_time;
        @BindView(R.id.tv_update_time)
        TextView tv_update_time;
        @BindView(R.id.tv_director)
        TextView tv_director;
        @BindView(R.id.tv_actor)
        TextView tv_actor;
        @BindView(R.id.tv_desc)
        TextView tv_desc;
        public FilmViewHolder(@NonNull View itemView) {
            super(itemView);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,SizeUtils.dp2px(180));
            itemView.setLayoutParams(layoutParams);
            ButterKnife.bind(this,itemView);
        }
    }

}
