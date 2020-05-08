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
import com.android.jesse.biliparser.network.model.bean.SearchResultBean;
import com.android.jesse.biliparser.utils.GlideUtil;
import com.android.jesse.biliparser.utils.Session;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Description: 更多影视适配器
 * @author: zhangshihao
 * @date: 2020/4/29
 */
public class MoreFilmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = MoreFilmAdapter.class.getSimpleName();

    private Context mContext;
    private List<SearchResultBean> itemBeanList;
    private int typeId;

    public MoreFilmAdapter(Context context, List<SearchResultBean> itemBeanList,int typeId){
        mContext = context;
        this.itemBeanList = itemBeanList;
        this.typeId = typeId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(typeId != Constant.FILM_TYPE_VARIETY){
            return new NormalViewHolder(LayoutInflater.from(mContext).inflate(R.layout.more_film_adapter,null,false));
        }else{
            return new VarietyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.more_film_variety_adapter,null,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if(typeId != Constant.FILM_TYPE_VARIETY){
            NormalViewHolder normalViewHolder = (NormalViewHolder)viewHolder;
            SearchResultBean itemBean = itemBeanList.get(position);
            normalViewHolder.tv_type.setText(itemBean.getType());
            normalViewHolder.tv_title.setText(itemBean.getTitle());
            normalViewHolder.tv_hot_value.setText(itemBean.getHotValue());
            normalViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Session.getSession().put(Constant.KEY_RESULT_BEAN,itemBean);
                    Intent intent = new Intent(mContext,ChooseSectionActivity.class);
                    intent.putExtra(Constant.KEY_TITLE,itemBean.getTitle());
                    intent.putExtra(Constant.KEY_URL,itemBean.getUrl());
                    intent.putExtra(Constant.KEY_SEARCH_TYPE,itemBean.getSearchType());
                    mContext.startActivity(intent);
                }
            });
        }else{
            VarietyViewHolder varietyViewHolder = (VarietyViewHolder) viewHolder;
            SearchResultBean dataBean = itemBeanList.get(position);
            GlideUtil.getInstance().loadImg(mContext,dataBean.getCover(),varietyViewHolder.iv_cover);
            varietyViewHolder.tv_title.setText(dataBean.getTitle());
            varietyViewHolder.tv_section_count.setText(dataBean.getSectionCount());
            varietyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Session.getSession().put(Constant.KEY_RESULT_BEAN,dataBean);
                    Intent intent = new Intent(mContext,ChooseSectionActivity.class);
                    intent.putExtra(Constant.KEY_TITLE,dataBean.getTitle());
                    intent.putExtra(Constant.KEY_URL,dataBean.getUrl());
                    intent.putExtra(Constant.KEY_SEARCH_TYPE,dataBean.getSearchType());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemBeanList==null?0:itemBeanList.size();
    }

    public class VarietyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_cover)
        ImageView iv_cover;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_section_count)
        TextView tv_section_count;
        public VarietyViewHolder(@NonNull View itemView) {
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

    public class NormalViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_type)
        TextView tv_type;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_hot_value)
        TextView tv_hot_value;
        public NormalViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
