package com.android.jesse.biliparser.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.components.WaitDialog;
import com.android.jesse.biliparser.network.base.SimpleActivity;
import com.android.jesse.biliparser.utils.LogUtils;
import com.android.jesse.biliparser.utils.Utils;
import com.blankj.utilcode.util.SizeUtils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
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

    private boolean enableProgressBar = false;//是否显示进度条
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                mWebView.setVisibility(View.GONE);
                tv_debug.setText((String)msg.obj);
            }
        }
    };

    @Override
    protected int getLayout() {
        return R.layout.base_web_activity;
    }

    @Override
    protected void initEventAndData() {
        Utils.setDarkStatusBar(mContext);
        initWebView();

        if(getIntent() != null){
            title = getIntent().getStringExtra(Constant.KEY_TITLE);
            url = getIntent().getStringExtra(Constant.KEY_URL);
            needWaitParse = getIntent().getBooleanExtra(Constant.KEY_NEED_WAIT_PARSE,false);
            waitDialog = new WaitDialog(mContext,R.style.Dialog_Translucent_Background);
            if(!TextUtils.isEmpty(title)){
                tv_title.setText(title);
            }
            if(!TextUtils.isEmpty(url) && !needWaitParse){
                waitDialog.show();
                mWebView.loadUrl(url);
            }
            if(needWaitParse){
                parseHtmlFromUrl();
            }
        }
    }

    @JavascriptInterface
    private void initWebView() {
        WebSettings mWebSettings = mWebView.getSettings();
        mWebView.addJavascriptInterface(new CustomScript(),"customScript");
//        mWebSettings.setUserAgentString(Constant.USER_AGENT_FORPC);
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
            if(isFinishing()){
                return true;
            }
            view.loadUrl(url);
            LogUtils.d(TAG+" url : "+url);
            return true;
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            if(isFinishing()){
                return;
            }
            try{
                AssetManager assetManager = getAssets();
                InputStream inputStream = assetManager.open("parser/pureVideo.js");
//                InputStream inputStream = assetManager.open("parser/removeAds.js");
                StringBuilder stringBuilder = new StringBuilder();
                int len = 0;
                byte[] buf = new byte[4096];
                while((len=inputStream.read(buf))!=-1){
                    stringBuilder.append(new String(buf,0,len));
                }
                String js = stringBuilder.toString();
                mWebView.loadUrl(js);
                String setHeightJS = String.format(getResources().getString(R.string.set_video_height_js),400);
                mWebView.loadUrl(setHeightJS);
                inputStream.close();
            }catch (IOException ioe){
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
            if(isFinishing()){
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
        Log.i(TAG, "onLeftBackward()");

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
            LogUtils.i(TAG+" 横屏");
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            LogUtils.i(TAG+" 竖屏");
        }
    }

    private void parseHtmlFromUrl(){
        waitDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Connection connection = Jsoup.connect(url);
                    connection.userAgent(Constant.USER_AGENT_FORPC);
//                    connection.data("searchword",word);
//                    connection.postDataCharset("GB2312");//关键中的关键！！
                    Document document = connection.method(Connection.Method.GET).get();
                    LogUtils.d(TAG+" html = \n"+document.outerHtml());
                    mHandler.sendMessage(Message.obtain(mHandler, 0, document));
                }catch (IOException ioe){
                    ioe.printStackTrace();
                    waitDialog.dismiss();
                }
            }
        }).start();
    }

    final class CustomScript{

        @JavascriptInterface
        public void log(String html){
            LogUtils.d(TAG+" html : \n"+html);
        }

        @JavascriptInterface
        public void onJSLoadComplete(){
            LogUtils.d(TAG+" onJSLoadComplete...");
            waitDialog.dismiss();
            mWebView.setVisibility(View.VISIBLE);
            tv_no_data.setVisibility(View.GONE);
        }

    }

    private void parseDocument(Document document){
        String pureVideoUrl = document.selectFirst("div.player").selectFirst("iframe#play2").attr("src");
        LogUtils.d(TAG+" pureVideoUrl : "+pureVideoUrl);
        mWebView.loadUrl(pureVideoUrl);
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

    @OnClick({R.id.iv_back,})
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
