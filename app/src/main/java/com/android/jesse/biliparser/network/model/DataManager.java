package com.android.jesse.biliparser.network.model;

import com.android.jesse.biliparser.network.model.bean.ResponseBaseBean;
import com.android.jesse.biliparser.network.model.bean.VersionCheckBean;
import com.android.jesse.biliparser.network.model.http.HttpHelper;

import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author: Est <codeest.dev@gmail.com>
 * @date: 2017/4/21
 * @desciption:
 */

public class DataManager implements HttpHelper {

    HttpHelper mHttpHelper;

    public DataManager(HttpHelper httpHelper) {
        mHttpHelper = httpHelper;
    }

    @Override
    public Flowable<ResponseBody> searchAnims(String keyword) {
        return mHttpHelper.searchAnims(keyword);
    }

    @Override
    public Flowable<VersionCheckBean> versionCheck(Map<String, RequestBody> map) {
        return mHttpHelper.versionCheck(map);
    }
}
