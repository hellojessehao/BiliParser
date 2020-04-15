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
import com.android.jesse.biliparser.db.bean.CollectionBean;
import com.android.jesse.biliparser.db.bean.HistoryVideoBean;
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
public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder>{

    private static final String TAG = CollectionAdapter.class.getSimpleName();
    private List<CollectionBean> collectionBeanList;
    private Context mContext;

    public CollectionAdapter(Context mContext, List<CollectionBean> collectionBeanList) {
        this.collectionBeanList = collectionBeanList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.collection_adapter,null,false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        CollectionBean collectionBean = collectionBeanList.get(position);
        GlideUtil.getInstance().loadImg(mContext,collectionBean.getCover(),viewHolder.iv_cover);
        viewHolder.tv_title.setText(collectionBean.getTitle());
        viewHolder.tv_alias.setText(collectionBean.getAlias());
        viewHolder.tv_infos.setText(collectionBean.getInfos());
        viewHolder.tv_desc.setTextColor(mContext.getColor(R.color.color_selected_tags));
        if(collectionBean.getCurrentIndex() <= 0){
            viewHolder.tv_desc.setText("还没有观看过噢~");
        }else{
            viewHolder.tv_desc.setText("已观看到第"+collectionBean.getCurrentIndex()+"集");
        }
        if(TextUtils.isEmpty(collectionBean.getDate())){
            viewHolder.tv_date.setText("时间未记录");
        }else{
            viewHolder.tv_date.setText(collectionBean.getDate());
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(collectionBean);
                }
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemLongClick(viewHolder.getAdapterPosition(),collectionBean,viewHolder.itemView);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return collectionBeanList==null?0:collectionBeanList.size();
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(CollectionBean collectionBean);
        void onItemLongClick(int position, CollectionBean collectionBean, View itemView);
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
