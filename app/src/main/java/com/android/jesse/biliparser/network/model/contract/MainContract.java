package com.android.jesse.biliparser.network.model.contract;

import com.android.jesse.biliparser.network.base.BasePresenter;
import com.android.jesse.biliparser.network.base.BaseView;

/**
 * @Description:
 * @author: zhangshihao
 * @date: 2020/3/13
 */
public interface MainContract {

    interface View extends BaseView{
        void onGetSearchAnims(String result);
    }

    interface Presenter extends BasePresenter<View>{
        void searchAnims(String keyword);
    }

}
