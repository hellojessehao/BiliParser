package com.android.jesse.biliparser.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.adapter.CommonTabFragmentAdapter;
import com.android.jesse.biliparser.fragment.AnimRecommendFragment;
import com.android.jesse.biliparser.fragment.FilmRecommendFragment;
import com.android.jesse.biliparser.network.base.SimpleActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @Description: 推荐页
 * @author: zhangshihao
 * @date: 2020/4/27
 */
public class RecommendActivity extends SimpleActivity {

    private static final String TAG = RecommendActivity.class.getSimpleName();

    @BindView(R.id.tab_layout)
    TabLayout tab_layout;
    @BindView(R.id.view_pager)
    ViewPager view_pager;

    private AnimRecommendFragment animRecommendFragment;
    private FilmRecommendFragment filmRecommendFragment;
    private CommonTabFragmentAdapter commonTabFragmentAdapter;

    @Override
    protected String getTitleName() {
        return "推荐";
    }

    @Override
    protected void onBackClick() {
        super.onBackClick();
        finish();
    }

    @Override
    protected int getLayout() {
        return R.layout.recommend_activity;
    }

    @Override
    protected void initEventAndData() {
        //填充数据
        String[] tabStrArr = {"动漫","影视"};
        for(int i=0;i<tabStrArr.length;i++){
            tab_layout.addTab(tab_layout.newTab(),i==0);
        }
        animRecommendFragment = new AnimRecommendFragment();
        filmRecommendFragment = new FilmRecommendFragment();
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(animRecommendFragment);
        fragmentList.add(filmRecommendFragment);
        commonTabFragmentAdapter = new CommonTabFragmentAdapter(fragmentList,getSupportFragmentManager());
        view_pager.setAdapter(commonTabFragmentAdapter);
        tab_layout.setupWithViewPager(view_pager);
        for(int i=0;i<tabStrArr.length;i++){
            tab_layout.getTabAt(i).setText(tabStrArr[i]);
        }
    }
}
