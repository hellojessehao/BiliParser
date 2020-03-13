package com.android.jesse.biliparser.activity;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.network.base.BaseActivity;
import com.android.jesse.biliparser.network.model.contract.MainContract;
import com.android.jesse.biliparser.network.model.presenter.MainPresenter;
import com.android.jesse.biliparser.utils.LogUtils;
import com.android.jesse.biliparser.utils.Utils;
import com.blankj.utilcode.util.ActivityUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.et_word)
    EditText et_word;
    @BindView(R.id.tv_result)
    TextView tv_result;

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
    }

    @OnClick({R.id.btn_translate})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_translate:
                String word = et_word.getText().toString();
                if(TextUtils.isEmpty(word)){
                    Toast.makeText(this, "搜索内容为空", Toast.LENGTH_SHORT).show();
                    break;
                }
                mPresenter.searchAnims(word);
                break;
        }
    }

    @Override
    public void onGetSearchAnims(String result) {
        LogUtils.d(TAG+" onGetSearchAnims : result = \n"+result);
        if(!TextUtils.isEmpty(result)){
            Intent intent = new Intent(mContext,SearchResultDisplayActivity.class);
            intent.putExtra(Constant.INTENT_KEY_SEARCH_RESULT,result);
            startActivity(intent);
        }
    }

    private long lastMills,currentMills;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            currentMills = System.currentTimeMillis();
            if(lastMills > 0 && currentMills - lastMills <= 1500){
                finish();
                return true;
            }else{
                Toast.makeText(mContext, "再按一次退出应用", Toast.LENGTH_SHORT).show();
                lastMills = currentMills;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lastMills = 0;
                    }
                },1500);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
