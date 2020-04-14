package com.android.jesse.biliparser.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.components.WaitDialog;
import com.android.jesse.biliparser.network.base.BaseActivity;
import com.android.jesse.biliparser.network.model.contract.MainContract;
import com.android.jesse.biliparser.network.model.presenter.MainPresenter;
import com.android.jesse.biliparser.network.util.ToastUtil;
import com.android.jesse.biliparser.utils.LogUtils;
import com.android.jesse.biliparser.utils.NetLoadListener;
import com.android.jesse.biliparser.utils.Session;
import com.android.jesse.biliparser.utils.Utils;
import com.blankj.utilcode.util.ActivityUtils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.et_word)
    EditText et_word;
    @BindView(R.id.tv_result)
    TextView tv_result;

    private WaitDialog waitDialog;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                NetLoadListener.getInstance().stopListening();
                waitDialog.dismiss();
                Session.getSession().put(Constant.KEY_DOCUMENT,msg.obj);
                ActivityUtils.startActivity(SearchResultDisplayActivity.class);
            }
        }
    };
    private NetLoadListener.Callback callback = new NetLoadListener.Callback() {
        @Override
        public void onNetLoadFailed() {
            Toast.makeText(mContext, R.string.net_load_failed, Toast.LENGTH_SHORT).show();
            waitDialog.dismiss();
        }
    };

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
        iv_back.setVisibility(View.GONE);
        iv_right.setVisibility(View.VISIBLE);

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }
        waitDialog = new WaitDialog(mContext, R.style.Dialog_Translucent_Background);
    }


    @Override
    protected void onRightClick() {
        super.onRightClick();
        ActivityUtils.startActivity(HistoryVideoActivity.class);
    }

    @OnClick({R.id.btn_translate})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_translate:
                String word = et_word.getText().toString();
                if (TextUtils.isEmpty(word)) {
                    Toast.makeText(this, "搜索内容为空", Toast.LENGTH_SHORT).show();
                    break;
                }
                waitDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            NetLoadListener.getInstance().startListening(callback);
                            Connection connection = Jsoup.connect("http://www.imomoe.in/search.asp");
                            connection.userAgent(Constant.USER_AGENT_FORPC);
                            connection.data("searchword",word);
                            connection.postDataCharset("GB2312");//关键中的关键！！
                            Document document = connection.method(Connection.Method.POST).post();
                            LogUtils.d(TAG+" html = \n"+document.outerHtml());
                            mHandler.sendMessage(Message.obtain(mHandler, 0, document));
                        }catch (IOException ioe){
                            ioe.printStackTrace();
                            waitDialog.dismiss();
                            NetLoadListener.getInstance().stopListening();
                        }
                    }
                }).start();
                break;
        }
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
    protected void onDestroy() {
        super.onDestroy();
        NetLoadListener.getInstance().removeLastCallback();
    }
}
