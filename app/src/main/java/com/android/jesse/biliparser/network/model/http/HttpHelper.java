package com.android.jesse.biliparser.network.model.http;

import com.android.jesse.biliparser.network.model.bean.ResponseBaseBean;
import com.android.jesse.biliparser.network.model.bean.VersionCheckBean;

import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.http.Query;

/**
 * @author: Est <codeest.dev@gmail.com>
 * @date: 2017/4/21
 * @description:
 */

public interface HttpHelper {

    Flowable<ResponseBody> searchAnims(@Query("searchword") String keyword);//根据keyword查询动漫

    Flowable<VersionCheckBean> versionCheck(Map<String,RequestBody> map);

}
