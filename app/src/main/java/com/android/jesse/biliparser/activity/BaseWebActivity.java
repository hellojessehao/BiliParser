package com.android.jesse.biliparser.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.components.WaitDialog;
import com.android.jesse.biliparser.db.base.DbHelper;
import com.android.jesse.biliparser.db.bean.HistoryVideoBean;
import com.android.jesse.biliparser.network.base.SimpleActivity;
import com.android.jesse.biliparser.network.model.bean.SearchResultBean;
import com.android.jesse.biliparser.network.model.bean.SectionBean;
import com.android.jesse.biliparser.network.util.ToastUtil;
import com.android.jesse.biliparser.utils.DateUtil;
import com.android.jesse.biliparser.utils.LogUtils;
import com.android.jesse.biliparser.utils.Session;
import com.android.jesse.biliparser.utils.Utils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ClassName: BaseWebActivity
 * @Desciption: webview基础activity
 * @author: zhangshihao
 * @date: 2019/8/14
 */
public class BaseWebActivity extends SimpleActivity implements View.OnClickListener {

    private static final String TAG = "BaseWebActivity";

    @BindView(R.id.base_web_webview)
    protected WebView mWebView;
    @BindView(R.id.tv_no_data)
    TextView tv_no_data;
    @BindView(R.id.base_web_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_title)
    protected TextView tv_title;
    @BindView(R.id.titleBar)
    protected LinearLayout titleBar;
    @BindView(R.id.iv_back)
    protected ImageView iv_back;
    @BindView(R.id.base_content_view)
    protected FrameLayout contentView;
    @BindView(R.id.tv_debug)
    TextView tv_debug;

    protected String title;
    protected String url;
    private boolean needWaitParse = false;
    private WaitDialog waitDialog;
    private final int TIMEOUT_MILLS = 60 * 1000;
    private int searchType = Constant.FLAG_SEARCH_ANIM;
    private PopupWindow spinnerPop;
    private List<SectionBean> sectionBeanList;
    private int currentIndex = 0;//当前集数序号
    private final int NO_SWITCH = 0;
    private final int SWITCH_TO_LAST = 1;//切换上集
    private final int SWITCH_TO_NEXT = 2; //切换上集
    private final int SWITCH_TO_X = 3;//跳集
    private int FLAG_SWITCH_SECTION = NO_SWITCH;//没有换集
    private Dialog jumpSectionDialog;
    private int tempJumpIndex = -1;
    private String switchUrl;//换集url

    private boolean enableProgressBar = false;//是否显示进度条
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mWebView.setVisibility(View.GONE);
                tv_debug.setText((String) msg.obj);
            } else if (msg.what == 2) {
                waitDialog.dismiss();
                ToastUtil.shortShow("加载超时，可继续等待或退出重试~");
            } else if (msg.what == 3) {
                contentView.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
                tv_no_data.setVisibility(View.GONE);
            } else if (msg.what == 4) {
                contentView.setVisibility(View.GONE);
                mWebView.setVisibility(View.GONE);
                tv_no_data.setText("暂不支持该视频的播放，看看其它的吧~");
                tv_no_data.setVisibility(View.VISIBLE);
            } else if (msg.what == 5) {
                switch (FLAG_SWITCH_SECTION) {
                    case NO_SWITCH:
                        //do nothing
                        break;
                    case SWITCH_TO_LAST:
                        currentIndex--;
                        tv_title.setText("第" + (currentIndex + 1) + "集");
                        FLAG_SWITCH_SECTION = NO_SWITCH;
                        updateSectionIndex();
                        break;
                    case SWITCH_TO_NEXT:
                        currentIndex++;
                        tv_title.setText("第" + (currentIndex + 1) + "集");
                        FLAG_SWITCH_SECTION = NO_SWITCH;
                        updateSectionIndex();
                        break;
                    case SWITCH_TO_X:
                        currentIndex = tempJumpIndex;
                        tv_title.setText("第" + (currentIndex + 1) + "集");
                        FLAG_SWITCH_SECTION = NO_SWITCH;
                        updateSectionIndex();
                        break;
                }
            }
        }
    };

    @Override
    protected int getLayout() {
        return R.layout.base_web_activity;
    }

    @Override
    protected void onRightClick() {
        super.onRightClick();
        spinnerPop.showAsDropDown(iv_right, 0, -SizeUtils.dp2px(12));
    }

    @Override
    protected void initEventAndData() {
        Utils.setDarkStatusBar(mContext);
        initSpinnerPop();
        initJumpSectionDialog();
        sectionBeanList = (List<SectionBean>) Session.getSession().get(Constant.KEY_SECTION_BEAN_LIST);
        if (Utils.isListEmpty(sectionBeanList)) {
            LogUtils.e(TAG + " sectionBeanList is empty");
            iv_right.setVisibility(View.GONE);
        } else {
            iv_right.setVisibility(View.VISIBLE);
            iv_right.setImageResource(R.mipmap.ic_menu);
        }
        if (getIntent() != null) {
            title = getIntent().getStringExtra(Constant.KEY_TITLE);
            url = getIntent().getStringExtra(Constant.KEY_URL);
            LogUtils.i(TAG + " url = " + url + " ,title = " + title);
            currentIndex = getIntent().getIntExtra(Constant.KEY_CURRENT_INDEX, 0);
            LogUtils.d(TAG + " currentIndex = " + currentIndex);
            needWaitParse = getIntent().getBooleanExtra(Constant.KEY_NEED_WAIT_PARSE, false);
            waitDialog = new WaitDialog(mContext, R.style.Dialog_Translucent_Background);
            searchType = getIntent().getIntExtra(Constant.KEY_SEARCH_TYPE, Constant.FLAG_SEARCH_ANIM);
            if (searchType == Constant.FLAG_SEARCH_FILM_TELEVISION) {
                waitDialog.setMessage(R.string.films_waiting_hint);
            }
            initWebView();
            if (!TextUtils.isEmpty(title)) {
                tv_title.setText(title);
            }
            if (!TextUtils.isEmpty(url) && !needWaitParse) {
                waitDialog.show();
                contentView.setVisibility(View.VISIBLE);
                mWebView.loadUrl(url);
                mHandler.postDelayed(timeoutRunnable, TIMEOUT_MILLS);
            }
            if (needWaitParse) {
                parseHtmlFromUrl();
            }
        }
    }

    //刷新数据库中的集数信息
    private void updateSectionIndex() {
        SearchResultBean searchResultBean = (SearchResultBean) Session.getSession().request(Constant.KEY_RESULT_BEAN);
        if(searchResultBean == null || TextUtils.isEmpty(searchResultBean.getTitle())){
            LogUtils.e(TAG+" updateSectionIndex : searchResultBean is null or title is empty");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtils.i(TAG+" updateSectionIndex : title = "+searchResultBean.getTitle()+" ,currentIndex = "+currentIndex);
                int videoId = searchResultBean.getTitle().hashCode();
                LogUtils.i(TAG + " updateSectionIndex : videoId = " + videoId);
                //历史
                int id = DbHelper.getInstance().updateIndexByVideoId(currentIndex + 1, videoId);
                if (id >= 0) {
                    Intent intent = new Intent(Constant.ACTION_UPDATE_CURRENT_INDEX);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    LogUtils.i(TAG + " updateIndexByVideoId success");
                }else{
                    LogUtils.e(TAG + " updateIndexByVideoId failed");
                }
                //收藏
                int result = DbHelper.getInstance().updateCollectionIndexByVideoId(currentIndex + 1, videoId);
                if (result > 0) {
                    LogUtils.i(TAG + " updateCollectionIndexByVideoId success");
                } else {
                    LogUtils.e(TAG + " updateCollectionIndexByVideoId failed");
                }
            }
        }).start();
    }

    private void initJumpSectionDialog() {
        jumpSectionDialog = new Dialog(mContext, R.style.Dialog_Translucent_Background);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.webview_jump_section_dialog, null, false);
        jumpSectionDialog.setContentView(contentView);
        EditText et_section_num = contentView.findViewById(R.id.et_section_num);
        TextView tv_positive = contentView.findViewById(R.id.tv_positive);
        TextView tv_negative = contentView.findViewById(R.id.tv_negative);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = et_section_num.getText().toString();
                switch (v.getId()) {
                    case R.id.tv_positive:
                        if (TextUtils.isEmpty(number)) {
                            LogUtils.e(TAG + " jump number is null");
                            Toast.makeText(BaseWebActivity.this, "请输入跳转集数~", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        tempJumpIndex = Integer.valueOf(number) - 1;
                        LogUtils.d(TAG + " tempJumpIndex = " + tempJumpIndex);
                        if (tempJumpIndex > (sectionBeanList.size() - 1) || tempJumpIndex < 0) {
                            tempJumpIndex = -1;
                            Toast.makeText(BaseWebActivity.this, "请输入正确的数值~", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        SectionBean jumpSectionBean = sectionBeanList.get(tempJumpIndex);
                        LogUtils.d(TAG + " jumpUrl = " + jumpSectionBean.getUrl());
                        switchUrl = jumpSectionBean.getUrl();
                        mWebView.loadUrl(switchUrl);
                        FLAG_SWITCH_SECTION = SWITCH_TO_X;
                        jumpSectionDialog.dismiss();
                        waitDialog.show();
                        mHandler.postDelayed(timeoutRunnable, TIMEOUT_MILLS);
                        break;
                    case R.id.tv_negative:
                        jumpSectionDialog.dismiss();
                        break;
                }
            }
        };
        tv_positive.setOnClickListener(onClickListener);
        tv_negative.setOnClickListener(onClickListener);
        Window window = jumpSectionDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(30);
            window.setAttributes(layoutParams);
        }
        jumpSectionDialog.setCanceledOnTouchOutside(true);
    }

    private void initSpinnerPop() {
        spinnerPop = new PopupWindow(mContext);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.webview_menu_spinner_pop, null, false);
        spinnerPop.setContentView(contentView);
        spinnerPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spinnerPop.setAnimationStyle(R.style.WindowStyle);
        spinnerPop.setOutsideTouchable(true);
        spinnerPop.setWidth(SizeUtils.dp2px(65));
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerPop.dismiss();
                LogUtils.d(TAG+" initSpinnerPop --> onclick : currentIndex = "+currentIndex);
                switch (v.getId()) {
                    case R.id.tv_last:
                        if (currentIndex <= 0) {
                            Toast.makeText(BaseWebActivity.this, "这好像是第一集~", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        FLAG_SWITCH_SECTION = SWITCH_TO_LAST;
                        SectionBean lastSectionBean = sectionBeanList.get(currentIndex - 1);
                        LogUtils.d(TAG + " lastUrl = " + lastSectionBean.getUrl());
                        switchUrl = lastSectionBean.getUrl();
                        mWebView.loadUrl(switchUrl);
                        waitDialog.show();
                        mHandler.postDelayed(timeoutRunnable, TIMEOUT_MILLS);
                        break;
                    case R.id.tv_next:
                        if (currentIndex >= (sectionBeanList.size() - 1)) {
                            Toast.makeText(BaseWebActivity.this, "已经是最后一集了~", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        FLAG_SWITCH_SECTION = SWITCH_TO_NEXT;
                        SectionBean nextSectionBean = sectionBeanList.get(currentIndex + 1);
                        LogUtils.d(TAG + " nextUrl = " + nextSectionBean.getUrl());
                        switchUrl = nextSectionBean.getUrl();
                        mWebView.loadUrl(nextSectionBean.getUrl());
                        waitDialog.show();
                        mHandler.postDelayed(timeoutRunnable, TIMEOUT_MILLS);
                        break;
                    case R.id.tv_jump:
                        jumpSectionDialog.show();
                        break;
                }
            }
        };
        TextView tv_last = contentView.findViewById(R.id.tv_last);
        tv_last.setOnClickListener(onClickListener);
        TextView tv_next = contentView.findViewById(R.id.tv_next);
        tv_next.setOnClickListener(onClickListener);
        TextView tv_jump = contentView.findViewById(R.id.tv_jump);
        tv_jump.setOnClickListener(onClickListener);
    }

    private void resetWebView() {
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.clearMatches();
        mWebView.clearFormData();
        mWebView.clearSslPreferences();
    }

    private Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(2);//30s内js未执行完成视为超时
        }
    };

    @JavascriptInterface
    private void initWebView() {
        WebSettings mWebSettings = mWebView.getSettings();
        mWebView.addJavascriptInterface(new CustomScript(), "customScript");
        if (searchType == Constant.FLAG_SEARCH_FILM_TELEVISION) {
            mWebSettings.setUserAgentString(Constant.USER_AGENT_FORPC);
        }
        mWebSettings.setSupportZoom(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");
        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setBlockNetworkImage(false);//解决图片不显示
        mWebSettings.setTextZoom(100);//设置默认缩放比例，防止网页跟随系统字体大小变化
        //待定项目@{
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSettings.setDomStorageEnabled(true);// 必须保留，否则无法播放优酷视频，其他的OK
        try {
            if (Build.VERSION.SDK_INT >= 16) {
                Class<?> clazz = mWebSettings.getClass();
                Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(mWebSettings, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWebSettings.setPluginState(WebSettings.PluginState.ON);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        //@}
        //调用JS方法.安卓版本大于17,加上注解 @JavascriptInterface
        mWebSettings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setWebViewClient(webViewClient);
    }

    WebViewClient webViewClient = new WebViewClient() {

        /**
         * 多页面在同一个WebView中打开，就是不新建activity或者调用系统浏览器打开
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (isFinishing()) {
                return true;
            }
            LogUtils.d(TAG + " shouldOverrideUrlLoading : url = " + url);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            LogUtils.i(TAG + " onPageFinished...");
            if (isFinishing()) {
                return;
            }
//            以下省略代码用于不执行脚本直接播放时使用
//            mHandler.removeCallbacks(timeoutRunnable);
//            waitDialog.dismiss();
//            mHandler.sendEmptyMessage(3);
            try {
                AssetManager assetManager = getAssets();
                InputStream inputStream = null;
                if (searchType == Constant.FLAG_SEARCH_ANIM) {
                    inputStream = assetManager.open("parser/pureVideo.js");
                } else if (searchType == Constant.FLAG_SEARCH_FILM_TELEVISION) {
                    inputStream = assetManager.open("parser/pureVideo_films_pc_2.js");
//                    inputStream = assetManager.open("parser/pureVideo_films_pc.js");
//                    inputStream = assetManager.open("parser/pureVideo_films.js");
                }
//                InputStream inputStream = assetManager.open("parser/removeAds.js");
                StringBuilder stringBuilder = new StringBuilder();
                int len = 0;
                byte[] buf = new byte[4096];
                while ((len = inputStream.read(buf)) != -1) {
                    stringBuilder.append(new String(buf, 0, len));
                }
                String js = stringBuilder.toString();
                mWebView.loadUrl(js);
                if (searchType == Constant.FLAG_SEARCH_ANIM) {
                    String setHeightJS = String.format(getResources().getString(R.string.set_video_height_js), 400);
                    mWebView.loadUrl(setHeightJS);
                } else if (searchType == Constant.FLAG_SEARCH_FILM_TELEVISION) {
//                    String setHeightJS = String.format(getResources().getString(R.string.set_video_height_js_films_pc),ScreenUtils.getScreenWidth(),
//                            SizeUtils.dp2px(400));
//                    mWebView.loadUrl(setHeightJS);
                }
                inputStream.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            if (TextUtils.isEmpty(title)) {
                String title = webView.getTitle();
                if (!TextUtils.isEmpty(title)) {
                    tv_title.setText(title);
                }
            }
        }
    };

    WebChromeClient webChromeClient = new WebChromeClient() {

        CustomViewCallback mCallback;

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            fullScreen();

            titleBar.setVisibility(View.GONE);
            mWebView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
            contentView.addView(view);
            mCallback = callback;
            super.onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            fullScreen();

            titleBar.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.VISIBLE);
            contentView.setVisibility(View.GONE);
            contentView.removeAllViews();
            super.onHideCustomView();
        }

        @Override
        public void onProgressChanged(WebView webView, int newProgress) {
            super.onProgressChanged(webView, newProgress);
            if (isFinishing()) {
                return;
            }
            if (enableProgressBar) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    progressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    progressBar.setProgress(newProgress);//设置进度值
                }
            }

        }
    };

    protected void onLeftBackward() {
        LogUtils.d(TAG+" onLeftBackward : currentIndex = "+currentIndex);
        Intent intent = new Intent();
        intent.putExtra(Constant.KEY_CURRENT_INDEX,currentIndex+1);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }
    }

    private void fullScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            LogUtils.i(TAG + " 横屏");
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            LogUtils.i(TAG + " 竖屏");
        }
    }

    private void parseHtmlFromUrl() {
        waitDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = Jsoup.connect(url);
                    connection.userAgent(Constant.USER_AGENT_FORPC);
//                    connection.data("searchword",word);
//                    connection.postDataCharset("GB2312");//关键中的关键！！
                    Document document = connection.method(Connection.Method.GET).get();
                    LogUtils.d(TAG + " html = \n" + document.outerHtml());
                    mHandler.sendMessage(Message.obtain(mHandler, 0, document));
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    waitDialog.dismiss();
                }
            }
        }).start();
    }

    final class CustomScript {

        @JavascriptInterface
        public void log(String html) {
            if(!TextUtils.isEmpty(html)){
                LogUtils.d(TAG + " log : \n" + html);
            }else{
                LogUtils.i(TAG+" log : log is empty");
            }
        }

        @JavascriptInterface
        public void onJSLoadComplete() {
            LogUtils.d(TAG + " onJSLoadComplete...");
            mHandler.sendEmptyMessage(5);
            mHandler.removeCallbacks(timeoutRunnable);
            waitDialog.dismiss();
            mHandler.sendEmptyMessage(3);
        }

        @JavascriptInterface
        public void onVideoNotExist() {//TODO:暂未实现
            LogUtils.e(TAG + " onVideoNotExist : 暂不支持该视频的播放，看看其它的吧");
            mHandler.removeCallbacks(timeoutRunnable);
            waitDialog.dismiss();
            mHandler.sendEmptyMessage(4);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            goBack();
            onLeftBackward();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.iv_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onLeftBackward();
                break;
            default:
                break;
        }
    }

    private void goBack() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            onLeftBackward();
        }
    }



    public boolean isEnableProgressBar() {
        return enableProgressBar;
    }

    public void setEnableProgressBar(boolean enableProgressBar) {
        this.enableProgressBar = enableProgressBar;
    }

}
