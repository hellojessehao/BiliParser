package com.android.jesse.biliparser.network.util;


import android.text.TextUtils;

import com.android.jesse.biliparser.network.base.BaseView;
import com.android.jesse.biliparser.network.model.http.exception.ApiException;
import com.android.jesse.biliparser.utils.LogUtils;
import com.google.gson.JsonParseException;

import java.net.SocketTimeoutException;

import io.reactivex.subscribers.ResourceSubscriber;
import retrofit2.HttpException;

/**
 * Created by codeest on 2017/2/23.
 */

public abstract class CommonSubscriber<T> extends ResourceSubscriber<T> {
    private BaseView mView;
    private String mErrorMsg;
    private boolean isShowErrorState = true;

    protected CommonSubscriber(BaseView view) {
        this.mView = view;
    }

    protected CommonSubscriber(BaseView view, String errorMsg) {
        this.mView = view;
        this.mErrorMsg = errorMsg;
    }

    protected CommonSubscriber(BaseView view, boolean isShowErrorState) {
        this.mView = view;
        this.isShowErrorState = isShowErrorState;
    }

    protected CommonSubscriber(BaseView view, String errorMsg, boolean isShowErrorState) {
        this.mView = view;
        this.mErrorMsg = errorMsg;
        this.isShowErrorState = isShowErrorState;
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        if (mView == null) {
            return;
        }
        if (mErrorMsg != null && !TextUtils.isEmpty(mErrorMsg)) {
            mView.showErrorMsg(mErrorMsg);
        } else if (e instanceof ApiException) {
            mView.showErrorMsg(e.getMessage());
        } else if (e instanceof HttpException) {
//            if (NetworkUtils.isConnected())
//                mView.showErrorMsg("数据请求异常ヽ(≧Д≦)ノ");
//            else
                mView.showErrorMsg("网络异常，请检查网络是否连接ヽ(≧Д≦)ノ");
        } else if (e instanceof SocketTimeoutException) {
            mView.showErrorMsg("网络不佳，请重新加载！ヽ(≧Д≦)ノ");
        } else if (e instanceof JsonParseException) {
            mView.showErrorMsg("数据解析错误ヽ(≧Д≦)ノ");
        } else {
            mView.showErrorMsg("未知错误ヽ(≧Д≦)ノ");
            LogUtils.d(e.toString());
        }
        if (isShowErrorState) {
            mView.stateError();
        }
        e.printStackTrace();
    }
}
