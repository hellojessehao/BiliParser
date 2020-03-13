package com.android.jesse.biliparser.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.network.base.BaseActivity;
import com.android.jesse.biliparser.network.model.contract.MainContract;
import com.android.jesse.biliparser.network.model.presenter.MainPresenter;
import com.android.jesse.biliparser.utils.LogUtils;
import com.android.jesse.biliparser.utils.Utils;
import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.et_word)
    EditText et_word;
    @BindView(R.id.tv_result)
    TextView tv_result;

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
        tv_result.setText(result);
    }

}
