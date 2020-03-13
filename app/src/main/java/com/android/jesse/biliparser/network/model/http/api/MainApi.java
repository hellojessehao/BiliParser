package com.android.jesse.biliparser.network.model.http.api;


import com.android.jesse.biliparser.network.model.bean.ResponseBaseBean;

import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * Created by yu_yo on 2018/5/24.
 */

public interface MainApi {

    @Headers({"url_type:search"})
    @GET("all?")
    Flowable<ResponseBody> searchAnims(@Query("keyword") String keyword);//根据keyword查询动漫

}
