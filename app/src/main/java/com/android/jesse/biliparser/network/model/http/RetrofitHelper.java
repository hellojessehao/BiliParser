package com.android.jesse.biliparser.network.model.http;


import com.android.jesse.biliparser.network.model.bean.ResponseBaseBean;
import com.android.jesse.biliparser.network.model.bean.VersionCheckBean;
import com.android.jesse.biliparser.network.model.http.api.MainApi;

import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by codeest on 2016/8/3.
 */
public class RetrofitHelper implements HttpHelper {
    private MainApi mMainApiService;

    @Inject
    public RetrofitHelper(MainApi mMainApiService) {
        this.mMainApiService = mMainApiService;
    }

    @Override
    public Flowable<ResponseBody> searchAnims(String keyword) {
        return mMainApiService.searchAnims(keyword);
    }

    @Override
    public Flowable<VersionCheckBean> versionCheck(Map<String, RequestBody> map) {
        return mMainApiService.versionCheck(map);
    }
}
