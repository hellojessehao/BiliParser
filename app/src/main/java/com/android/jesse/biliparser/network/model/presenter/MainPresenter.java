package com.android.jesse.biliparser.network.model.presenter;


import android.text.TextUtils;

import com.android.jesse.biliparser.network.base.RxPresenter;
import com.android.jesse.biliparser.network.model.DataManager;
import com.android.jesse.biliparser.network.model.contract.MainContract;
import com.android.jesse.biliparser.network.util.CommonSubscriber;
import com.android.jesse.biliparser.network.util.RxUtil;
import com.android.jesse.biliparser.utils.LogUtils;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @Description:
 * @author: zhangshihao
 * @date: 2020/3/13
 */
public class MainPresenter extends RxPresenter<MainContract.View> implements MainContract.Presenter {

    private static final String TAG = MainPresenter.class.getSimpleName();

    private DataManager dataManager;

    @Inject
    public MainPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void searchAnims(String keyword) {
        addSubscribe(dataManager.searchAnims(keyword)
                .compose(RxUtil.rxSchedulerHelper())
                .subscribeWith(new CommonSubscriber<ResponseBody>(mView) {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String result = responseBody.string();
                            mView.onGetSearchAnims(result);
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                            LogUtils.e(TAG + " ioe : " + ioe.toString());
                        }
                    }
                }));
    }
}
