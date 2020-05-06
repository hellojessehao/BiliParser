package com.android.jesse.biliparser.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jesse.biliparser.BuildConfig;
import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.components.WaitDialog;
import com.android.jesse.biliparser.db.base.DbHelper;
import com.android.jesse.biliparser.network.base.BaseActivity;
import com.android.jesse.biliparser.network.model.bean.VersionCheckBean;
import com.android.jesse.biliparser.network.model.contract.MainContract;
import com.android.jesse.biliparser.network.model.presenter.MainPresenter;
import com.android.jesse.biliparser.network.util.ToastUtil;
import com.android.jesse.biliparser.utils.DialogUtil;
import com.android.jesse.biliparser.utils.LogUtils;
import com.android.jesse.biliparser.utils.NetLoadListener;
import com.android.jesse.biliparser.utils.Session;
import com.android.jesse.biliparser.utils.SharePreferenceUtil;
import com.android.jesse.biliparser.utils.SoftKeyBoardListener;
import com.android.jesse.biliparser.utils.Utils;
import com.android.jesse.biliparser.view.FlowLayout;
import com.android.jesse.biliparser.view.TagAdapter;
import com.android.jesse.biliparser.view.TagFlowLayout;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.upgrade.UpgradeListener;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.et_word)
    EditText et_word;
    @BindView(R.id.rl_search_history_container)
    RelativeLayout rl_search_history_container;
    @BindView(R.id.fl_search_history)
    TagFlowLayout fl_search_history;
    @BindView(R.id.btn_translate)
    Button btn_translate;
    @BindView(R.id.rl_content_container)
    RelativeLayout rl_content_container;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.tv_select_search_type)
    TextView tv_select_search_type;
    @BindView(R.id.tv_appname)
    TextView tv_appname;

    private WaitDialog waitDialog;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                toNextPage(msg);
            }else if(msg.what == 1){
                toNextPage(msg);
            }else if(msg.what == 2){
                NetLoadListener.getInstance().stopListening();
                Toast.makeText(mContext, R.string.net_load_failed, Toast.LENGTH_SHORT).show();
                waitDialog.dismiss();
            }
        }
    };
    private NetLoadListener.Callback callback = new NetLoadListener.Callback() {
        @Override
        public void onNetLoadFailed() {
            mHandler.sendEmptyMessage(2);
        }
    };
    private PopupWindow spinnerPop;
    private Dialog clearHintDialog;
    private int searchType = Constant.FLAG_SEARCH_ANIM;
    private PopupWindow selectTypePop;

    @Override
    protected String getTitleName() {
        return "搜索";
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initEventAndData() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setImageResource(R.mipmap.ic_help);
        iv_right.setVisibility(View.VISIBLE);
        iv_right.setImageResource(R.mipmap.ic_main_menu);
        tv_appname.setTypeface(Typeface.createFromAsset(getAssets(),"font"+File.separator+"yahei_2.ttf"));
        searchType = SharePreferenceUtil.getInt(Constant.SPKEY_SEARCH_TYPE,Constant.FLAG_SEARCH_ANIM);
        if(searchType == Constant.FLAG_SEARCH_ANIM){
            tv_select_search_type.setText("搜动漫");
        }else if(searchType == Constant.FLAG_SEARCH_FILM_TELEVISION){
            tv_select_search_type.setText("搜影视");
        }
        et_word.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //是否是回车键
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    //隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(MainActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //搜索
                    btn_translate.performClick();
                }
                return false;
            }
        });
        initSpinnerPop();
        initSearchTypePop();
        initSearchHistory();
        initKeyboardHeightAutoFit();
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }
        waitDialog = new WaitDialog(mContext, R.style.Dialog_Translucent_Background);
    }

    /**
     * 跳转下一页
     */
    private void toNextPage(Message msg){
        NetLoadListener.getInstance().stopListening();
        waitDialog.dismiss();
        String word = et_word.getText().toString();
        Session.getSession().put(Constant.KEY_DOCUMENT, msg.obj);
        Intent intent = new Intent(mContext, SearchResultDisplayActivity.class);
        intent.putExtra(Constant.KEY_TITLE, word);
        intent.putExtra(Constant.KEY_SEARCH_TYPE,searchType);
        startActivityForResult(intent, 102);
        //将搜索词存缓存
        List<String> searchWordList = getHistoryCacheList();
        String cacheResult = "";//最终存进缓存的字符串
        if (Utils.isListEmpty(searchWordList)) {
            searchWordList = new ArrayList<>();
            searchWordList.add(word);
        } else {
            if (!searchWordList.contains(word)) {
                //只显示最近15个历史搜索词
                if (searchWordList.size() == 15) {
                    searchWordList.remove(searchWordList.size() - 1);
                }
                searchWordList.add(0, word);//新词加到首位
            } else {
                searchWordList.remove(word);
                searchWordList.add(0, word);//新词加到首位
            }
        }
        cacheResult = new Gson().toJson(searchWordList);
        LogUtils.i(TAG + " cacheResult = " + cacheResult);
        SharePreferenceUtil.put(Constant.SPKEY_SEARCH_HISTORY, cacheResult);
    }

    private void initKeyboardHeightAutoFit() {
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
//                int scrollHeight = scrollView.getHeight();
//                int llHeight = rl_content_container.getHeight();
//                float llY = rl_content_container.getY();
//                float raiseHeight = height - (scrollHeight - llHeight - llY);
//                FrameLayout.LayoutParams llParams = (FrameLayout.LayoutParams) rl_content_container.getLayoutParams();
//                llParams.bottomMargin = (int) raiseHeight;
//                rl_content_container.setLayoutParams(llParams);
            }

            @Override
            public void keyBoardHide(int height) {
//                FrameLayout.LayoutParams llParams = (FrameLayout.LayoutParams) rl_content_container.getLayoutParams();
////                llParams.bottomMargin = 0;
////                rl_content_container.setLayoutParams(llParams);
            }
        });

    }

    //测试用，制造15条历史搜索假数据
    private void createFakeHistory() {
        List<String> fakeList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
//            if(i == 0){
//                fakeList.add("爱神的箭发克里斯京东方克拉斯发多久");
//                continue;
//            }
            fakeList.add("fake data " + i);
        }
        SharePreferenceUtil.put(Constant.SPKEY_SEARCH_HISTORY, new Gson().toJson(fakeList));
    }

    private void initSearchHistory() {
//        createFakeHistory();
        List<String> searchWordList = getHistoryCacheList();
        if (Utils.isListEmpty(searchWordList)) {
            rl_search_history_container.setVisibility(View.GONE);
        } else {
            rl_search_history_container.setVisibility(View.VISIBLE);
            TagAdapter<String> tagAdapter = new TagAdapter<String>(searchWordList) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.listory_tag_layout,
                            fl_search_history, false);
                    tv.setText(s);
                    return tv;
                }
            };
            fl_search_history.setAdapter(tagAdapter);
            fl_search_history.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                @Override
                public boolean onTagClick(View view, int position, FlowLayout parent) {
                    et_word.setText(searchWordList.get(position));
                    et_word.setSelection(et_word.length());
                    btn_translate.performClick();
                    return false;
                }
            });
        }
    }

    private List<String> getHistoryCacheList() {
        String searchHistory = SharePreferenceUtil.get(Constant.SPKEY_SEARCH_HISTORY);
        return new Gson().fromJson(searchHistory, new TypeToken<List<String>>() {
        }.getType());
    }

    private void initSearchTypePop(){
        selectTypePop = new PopupWindow(mContext);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.select_search_type_pop, null, false);
        selectTypePop.setContentView(contentView);
        selectTypePop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectTypePop.setAnimationStyle(R.style.WindowStyle);
        selectTypePop.setOutsideTouchable(true);
        selectTypePop.setWidth(SizeUtils.dp2px(55));
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTypePop.dismiss();
                tv_select_search_type.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_arrow_down_deep,0);
                switch (v.getId()){
                    case R.id.tv_anim:
                        searchType = Constant.FLAG_SEARCH_ANIM;
                        tv_select_search_type.setText("搜动漫");
                        et_word.setHint(R.string.et_hint_text_for_anim);
                        SharePreferenceUtil.put(Constant.SPKEY_SEARCH_TYPE,searchType);
                        break;
                    case R.id.tv_film_television:
                        searchType = Constant.FLAG_SEARCH_FILM_TELEVISION;
                        tv_select_search_type.setText("搜影视");
                        et_word.setHint(R.string.et_hint_text_for_films_television);
                        SharePreferenceUtil.put(Constant.SPKEY_SEARCH_TYPE,searchType);
                        break;
                }
            }
        };
        TextView tv_anim = contentView.findViewById(R.id.tv_anim);
        tv_anim.setOnClickListener(onClickListener);
        TextView tv_film_television = contentView.findViewById(R.id.tv_film_television);
        tv_film_television.setOnClickListener(onClickListener);
        selectTypePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                tv_select_search_type.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_arrow_down_deep,0);
            }
        });
    }

    private void initSpinnerPop() {
        spinnerPop = new PopupWindow(mContext);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.main_menu_spinner_pop, null, false);
        spinnerPop.setContentView(contentView);
        spinnerPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spinnerPop.setAnimationStyle(R.style.WindowStyle);
        spinnerPop.setOutsideTouchable(true);
        spinnerPop.setWidth(SizeUtils.dp2px(65));
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerPop.dismiss();
                switch (v.getId()) {
                    case R.id.tv_history:
                        ActivityUtils.startActivity(HistoryVideoActivity.class);
                        break;
                    case R.id.tv_collect:
                        ActivityUtils.startActivity(CollectionActivity.class);
                        break;
                }
            }
        };
        TextView tv_history = contentView.findViewById(R.id.tv_history);
        tv_history.setOnClickListener(onClickListener);
        TextView tv_collect = contentView.findViewById(R.id.tv_collect);
        tv_collect.setOnClickListener(onClickListener);
    }

    @Override
    protected void onBackClick() {
        super.onBackClick();
        startActivity(new Intent(mContext,RookieHelpActivity.class));
    }

    @Override
    protected void onRightClick() {
        super.onRightClick();
        spinnerPop.showAsDropDown(iv_right, 0, -SizeUtils.dp2px(8));
    }

    @OnClick({R.id.btn_translate, R.id.iv_delete,R.id.tv_select_search_type,
    R.id.tv_copyright_statement,R.id.tv_about_us,R.id.btn_commend})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_commend:
                startActivity(new Intent(mContext,RecommendActivity.class));
                break;
            case R.id.tv_about_us:
                startActivity(new Intent(mContext,AboutUsActivity.class));
                break;
            case R.id.tv_copyright_statement:
                DialogUtil.showCopyRightDialog(mContext);
                break;
            case R.id.tv_select_search_type:
                tv_select_search_type.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_arrow_up_deep,0);
                selectTypePop.showAsDropDown(tv_select_search_type,0,SizeUtils.dp2px(3));
                break;
            case R.id.iv_delete:
                clearHintDialog = DialogUtil.showHintDialogForCommonVersion(mContext, "确定要清空搜索历史吗？", "确定", "算了",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clearHintDialog.dismiss();
                                switch (v.getId()) {
                                    case R.id.tv_positive:
                                        SharePreferenceUtil.remove(Constant.SPKEY_SEARCH_HISTORY);
                                        initSearchHistory();
                                        break;
                                }
                            }
                        });
                break;
            case R.id.btn_translate:
                String word = et_word.getText().toString();
                if (TextUtils.isEmpty(word)) {
                    Toast.makeText(this, "搜索内容为空", Toast.LENGTH_SHORT).show();
                    break;
                }
                waitDialog.show();
                NetLoadListener.getInstance().startListening(callback);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            switch (searchType){
                                case Constant.FLAG_SEARCH_ANIM:
                                    Connection connection = Jsoup.connect(Constant.SAKURA_NEXT_PAGE_BASE_URL);
                                    connection.userAgent(Constant.USER_AGENT_FORPC);
                                    connection.data("searchword", word);
                                    connection.postDataCharset("GB2312");//关键中的关键！！
                                    Document document = connection.method(Connection.Method.POST).post();
                                    mHandler.sendMessage(Message.obtain(mHandler, 0, document));
                                    break;
                                case Constant.FLAG_SEARCH_FILM_TELEVISION:
                                    Connection filmConnection = Jsoup.connect(Constant.JIJI_SEARCH_URL);
                                    filmConnection.userAgent(Constant.USER_AGENT_FORPC);
                                    filmConnection.data("wd", word);
                                    Document filmDocument = filmConnection.method(Connection.Method.POST).post();
                                    mHandler.sendMessage(Message.obtain(mHandler, 1, filmDocument));
                                    break;
                            }

                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                            mHandler.sendEmptyMessage(2);
                        }
                    }
                }).start();
                break;
        }
    }

    @Override
    public void onVersionCheck(VersionCheckBean versionCheckBean) {

    }

    @Override
    public void onGetSearchAnims(String result) {

    }

    @Override
    public void showErrorMsg(String msg) {
        super.showErrorMsg(msg);
        waitDialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "请授予必需权限~", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            }
        }
    }

    private long lastMills, currentMills;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            currentMills = System.currentTimeMillis();
            if (lastMills > 0 && currentMills - lastMills <= 1500) {
                finish();
                return true;
            } else {
                Toast.makeText(mContext, "再按一次退出应用", Toast.LENGTH_SHORT).show();
                lastMills = currentMills;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lastMills = 0;
                    }
                }, 1500);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            initSearchHistory();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetLoadListener.getInstance().removeLastCallback();
    }
}
