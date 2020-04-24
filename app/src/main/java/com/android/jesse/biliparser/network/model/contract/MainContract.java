package com.android.jesse.biliparser.network.model.contract;

import com.android.jesse.biliparser.network.base.BasePresenter;
import com.android.jesse.biliparser.network.base.BaseView;
import com.android.jesse.biliparser.network.model.bean.VersionCheckBean;

import java.util.Map;

import okhttp3.RequestBody;

/**
 * @Description:
 * @author: zhangshihao
 * @date: 2020/3/13
 */
public interface MainContract {

    interface View extends BaseView{
        void onGetSearchAnims(String result);
        void onVersionCheck(VersionCheckBean versionCheckBean);
    }

    interface Presenter extends BasePresenter<View>{
        void searchAnims(String keyword);
        void versionCheck(Map<String, RequestBody> map);
    }

}
