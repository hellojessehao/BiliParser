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

    @Headers({
            "url_type:search",
            "Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            "Accept-Encoding:gzip, deflate",
            "Accept-Language:zh-CN,zh;q=0.9,und;q=0.8",
            "Cache-Control:max-age=0",
            "Connection:keep-alive",
            "Content-Length:35",
            "Content-Type:application/x-www-form-urlencoded",
            "Cookie:UM_distinctid=170ec8a2ff8384-011bba6fd97575-6701b35-1fa400-170ec8a2ff9c6e; bdshare_firstime=1584516444658; CNZZDATA1277354711=826245-1584512684-null%7C1584512684; CNZZDATA1277627309=159405575-1584515797-null%7C1584515797; CNZZDATA1260742008=44623999-1584515707-https%253A%252F%252Fwww.baidu.com%252F%7C1584683046; first_h=1584684856652; Hm_lvt_38c112aee0c8dc4d8d4127bb172cc197=1584516444,1584684856,1584684870; __music_index__=2; ASPSESSIONIDSSBQQRCB=EOBLNJCCKFKHJJLDJCCEELAO; count_m=1; Hm_lpvt_38c112aee0c8dc4d8d4127bb172cc197=1584687467; count_h=6; first_m=1584687469512",
            "Host:www.imomoe.in",
            "Origin:http://www.imomoe.in",
            "Upgrade-Insecure-Requests:1"
    })
    @POST("search.asp")
    Flowable<ResponseBody> searchAnims(@Query("searchword") String keyword);//根据keyword查询动漫

}
