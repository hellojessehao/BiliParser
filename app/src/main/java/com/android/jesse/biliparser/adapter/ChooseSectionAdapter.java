package com.android.jesse.biliparser.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.network.model.bean.SectionBean;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Description: 选择集数适配器
 * @author: zhangshihao
 * @date: 2020/3/25
 */
public class ChooseSectionAdapter extends RecyclerView.Adapter<ChooseSectionAdapter.ViewHolder>{

    private static final String TAG = ChooseSectionAdapter.class.getSimpleName();

    private Context mContext;
    private List<SectionBean> sectionBeanList;

    public ChooseSectionAdapter(Context mContext,List<SectionBean> sectionBeanList) {
        this.mContext = mContext;
        this.sectionBeanList = sectionBeanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.choose_section_adapter,null,false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        SectionBean sectionBean = sectionBeanList.get(position);
        viewHolder.tv_section.setText(sectionBean.getTitle());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(position,sectionBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return sectionBeanList==null?0:sectionBeanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_section)
        TextView tv_section;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position,SectionBean sectionBean);
    }

}
