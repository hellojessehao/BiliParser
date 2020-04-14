package com.android.jesse.biliparser.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.db.bean.HistoryVideoBean;
import com.android.jesse.biliparser.network.model.bean.SearchResultBean;
import com.android.jesse.biliparser.utils.GlideUtil;
import com.blankj.utilcode.util.SizeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Description: 搜索结果适配器
 * @author: zhangshihao
 * @date: 2020/3/24
 */
public class HistoryVideoAdapter extends RecyclerView.Adapter<HistoryVideoAdapter.ViewHolder>{

    private static final String TAG = HistoryVideoAdapter.class.getSimpleName();
    private List<HistoryVideoBean> historyVideoBeanList;
    private Context mContext;

    public HistoryVideoAdapter(Context mContext, List<HistoryVideoBean> resultBeanList) {
        this.historyVideoBeanList = resultBeanList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.history_video_adapter,null,false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        HistoryVideoBean historyVideoBean = historyVideoBeanList.get(position);
        GlideUtil.getInstance().loadImg(mContext,historyVideoBean.getCover(),viewHolder.iv_cover);
        viewHolder.tv_title.setText(historyVideoBean.getTitle());
        viewHolder.tv_alias.setText(historyVideoBean.getAlias());
        viewHolder.tv_infos.setText(historyVideoBean.getInfos());
        viewHolder.tv_desc.setTextColor(mContext.getColor(R.color.color_selected_tags));
        viewHolder.tv_desc.setText("已观看到第"+historyVideoBean.getCurrentIndex()+"集");
        if(TextUtils.isEmpty(historyVideoBean.getDate())){
            viewHolder.tv_date.setText("时间未记录");
        }else{
            viewHolder.tv_date.setText(historyVideoBean.getDate());
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(historyVideoBean);
                }
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemLongClick(position,historyVideoBean,viewHolder.itemView);
                }
                return true;
            }
        });
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

}
