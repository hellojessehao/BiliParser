package com.android.jesse.biliparser.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.db.bean.HistoryVideoBean;
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
public class HistoryVideoAdapter extends RecyclerView.Adapter{

    private static final String TAG = HistoryVideoAdapter.class.getSimpleName();
    private List<HistoryVideoBean> historyVideoBeanList;
    private Context mContext;

    public HistoryVideoAdapter(Context mContext, List<HistoryVideoBean> resultBeanList) {
        this.historyVideoBeanList = resultBeanList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == Constant.FLAG_SEARCH_ANIM){
            ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.history_video_adapter,null,false));
            return viewHolder;
        }else if(i == Constant.FLAG_SEARCH_FILM_TELEVISION){
            FilmViewHolder filmViewHolder = new FilmViewHolder(LayoutInflater.from(mContext).inflate(R.layout.history_video_adapter_holder_films,
                    null,false));
            return filmViewHolder;
        }else{
            ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.history_video_adapter,null,false));
            return viewHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(Utils.isListEmpty(historyVideoBeanList)){
            return Constant.FLAG_SEARCH_ANIM;
        }

        HistoryVideoBean videoBean = historyVideoBeanList.get(position);
        return videoBean.getSearchType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        HistoryVideoBean historyVideoBean = historyVideoBeanList.get(position);
        if(historyVideoBean.getSearchType() == Constant.FLAG_SEARCH_ANIM){
            ViewHolder animViewHolder = (ViewHolder) viewHolder;
            GlideUtil.getInstance().loadImg(mContext,historyVideoBean.getCover(),animViewHolder.iv_cover);
            animViewHolder.tv_title.setText(historyVideoBean.getTitle());
            animViewHolder.tv_alias.setText(historyVideoBean.getAlias());
            animViewHolder.tv_infos.setText(historyVideoBean.getInfos());

            animViewHolder.tv_desc.setTextColor(mContext.getColor(R.color.color_selected_tags));
            if(historyVideoBean.getCurrentIndex() <= 0){
                animViewHolder.tv_desc.setText("还没有观看过噢~");
            }else{
                animViewHolder.tv_desc.setText("已观看到第"+historyVideoBean.getCurrentIndex()+"集");
            }
            if(TextUtils.isEmpty(historyVideoBean.getDate())){
                animViewHolder.tv_date.setText("时间未记录");
            }else{
                animViewHolder.tv_date.setText(historyVideoBean.getDate());
            }
            animViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(historyVideoBean);
                    }
                }
            });
            animViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemLongClick(animViewHolder.getAdapterPosition(),historyVideoBean,animViewHolder.itemView);
                    }
                    return true;
                }
            });
        }else if(historyVideoBean.getSearchType() == Constant.FLAG_SEARCH_FILM_TELEVISION){
            FilmViewHolder filmViewHolder = (FilmViewHolder) viewHolder;
            GlideUtil.getInstance().loadImg(mContext,historyVideoBean.getCover(),filmViewHolder.iv_cover);
            filmViewHolder.tv_section_count.setText(historyVideoBean.getSectionCount());
            filmViewHolder.tv_title.setText(historyVideoBean.getTitle());
            String date = "";
            if(TextUtils.isEmpty(historyVideoBean.getDate())){
                date = "时间未记录";
            }else{
                date = historyVideoBean.getDate();
            }
            //@{设置观看到第几集和观看日期
            String desc = "";
            if(historyVideoBean.getCurrentIndex() <= 0){
                desc = "还没有观看过噢~";
            }else{
                desc = "已观看到第"+historyVideoBean.getCurrentIndex()+"集";
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            SpannableString descString = new SpannableString(desc);
            descString.setSpan(new ForegroundColorSpan(mContext.getColor(R.color.color_selected_tags)),0,desc.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            descString.setSpan(new AbsoluteSizeSpan(SizeUtils.sp2px(14)),0,desc.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableString dateString = new SpannableString(date);
            dateString.setSpan(new ForegroundColorSpan(mContext.getColor(R.color.color_most_shallow)),0,date.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            dateString.setSpan(new AbsoluteSizeSpan(SizeUtils.sp2px(10)),0,date.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(descString).append("\n").append(dateString);
            filmViewHolder.tv_desc.setText(spannableStringBuilder);
            //@}
            filmViewHolder.tv_area.setText("地区:"+historyVideoBean.getArea());
            filmViewHolder.tv_type.setText(historyVideoBean.getType());
            filmViewHolder.tv_publish_time.setText(historyVideoBean.getPublishDate());
            filmViewHolder.tv_update_time.setText(historyVideoBean.getUpdateDate());
            List<String> directorList = historyVideoBean.getDirectorList();
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
            List<String> actorList = historyVideoBean.getActorList();
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
                        onItemClickListener.onItemClick(historyVideoBean);
                    }
                }
            });
            filmViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemLongClick(filmViewHolder.getAdapterPosition(),historyVideoBean,filmViewHolder.itemView);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return historyVideoBeanList==null?0:historyVideoBeanList.size();
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(HistoryVideoBean videoBean);
        void onItemLongClick(int position,HistoryVideoBean videoBean,View itemView);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
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
        @BindView(R.id.tv_date)
        TextView tv_date;
        public ViewHolder(@NonNull View itemView) {
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
