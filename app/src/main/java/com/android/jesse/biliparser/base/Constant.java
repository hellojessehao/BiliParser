package com.android.jesse.biliparser.base;

import android.os.Environment;

import java.io.File;

/**
 * @Description: 全局变量放置类
 * @author: zhangshihao
 * @date: 2020/3/12
 */
public class Constant {

    //@{url
    public static final String BILI_SEARCH_URL = "https://search.bilibili.com/";//后面拼接上想要搜索的内容 https://search.bilibili.com/all?keyword=
    public static final String SAKURA_SEARCH_URL = "http://www.imomoe.in/";//樱花动漫搜索链接
    public static final String SAKURA_NEXT_PAGE_BASE_URL = "http://www.imomoe.in/search.asp";//樱花动漫下一页基础链接
    public static final String JIJI_SEARCH_URL = "https://www.jijidy.com/index.php?m=vod-search";//吉吉影视搜索接口 搜索词放进wd参数
    public static final String JIJI_BASE_URL = "https://www.jijidy.com/";
    //@}

    //@{thirdpart
    public static final String APPID_BUGLY = "abb0b9eb68";
    public static final String APPKEY_BUGLY = "412a01d5-260b-4d8c-a296-1f73bbff8212";
    //@}

    //@{paths
    public static final String PATH_DATA = App.getInstance().getCacheDir().getAbsolutePath() + File.separator + "data";
    public static final String PATH_CACHE = PATH_DATA + "/NetCache";
    public static final String PATH_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "jesse" + File.separator + "biliparser";
    //@}

    //@{codes
    public final static int NET_REQUEST_OK = 00000;//网络请求成功返回值
    public final static int NET_PARAMS_ERROR = 30000;//参数错误
    public final static int NET_REQUEST_BRUST = 20001;//视频上传网络请求返回值 有分片
    public final static int NET_REQUEST_VIDEO_MERGE = 20002;//视频上传合并
    public final static int GET_SMS_OUT_OF_LIMIT = 80001;//验证码发送次数超过限制
    public final static int GET_SMS_SUCCESS = 80002;//验证码已发送
    public final static int GET_SMS_FAILED = 80003;//验证码发送失败
    public static final int REQUEST_CAMERA_PERMISSION = 103;
    public static final int FLAG_SEARCH_ANIM = 1;
    public static final int FLAG_SEARCH_FILM_TELEVISION = 2;
    //@}

    //@{String常量
    public static final String HEADER_KEY = "url_type";
    public static final String URL_TYPE_SEARCH = "search";
    public static final String INTENT_KEY_SEARCH_RESULT = "intent_key_search_result";
    public static final String KEY_DOCUMENT = "KEY_DOCUMENT";
    public static final String KEY_TITLE = "key_title";
    public static final String KEY_URL = "key_url";
    public static final String KEY_SEARCH_TYPE = "key_search_type";
    public static final String KEY_NEED_WAIT_PARSE = "key_need_wait_parse";
    public static final String USER_AGENT_FORPC = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36";
    public static final String KEY_RESULT_BEAN = "key_result_bean";
    public static final String KEY_CURRENT_INDEX = "key_current_index";//当前集数非序号 不用+1
    public static final String KEY_VIDEO_ID = "key_video_id";
    public static final String SPKEY_SEARCH_HISTORY = "spkey_search_history";
    public static final String SPKEY_SEARCH_TYPE = "spkey_search_type";
    public static final String KEY_SECTION_BEAN_LIST = "key_section_bean_list";
    public static final String SPKEY_IS_SHOULD_VERSION_CHECK = "spkey_is_should_version_check";
    //@}

    //@{BROADCAST ACTIONS
    public static final String ACTION_UPDATE_CURRENT_INDEX = "action_update_current_index";
    //@}
}
