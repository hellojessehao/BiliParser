package com.android.jesse.biliparser.components;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.jesse.biliparser.R;

/**
 * @ClassName: WaitDialog
 * @Desciption: 等待加载对话框--目前用这个
 * @author: yichaohua
 * @date: 2018-05-14
 */
public class WaitDialog extends Dialog {

    private static final String TAG = "WaitDialog";

    private Context context;

    private View waitView;

    public WaitDialog(@NonNull Context context) {
        super(context);

        this.context = context;

        initView();
    }

    public WaitDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);

        this.context = context;

        initView();
    }

    private void initView() {
        waitView = LayoutInflater.from(context).inflate(R.layout.dialog_waiting, null);

        setCanceledOnTouchOutside(true); // 点击加载框以外的区域
        setContentView(waitView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        /**
         *将显示Dialog的方法封装在这里面
         */
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.PopWindowAnimStyle);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //屏蔽back键
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }else {
            return false;
        }
    }

    public void setMessage(String message){
        if(waitView == null){
            return;
        }
        TextView tipTextView = waitView.findViewById(R.id.tipTextView);
        if(tipTextView != null){
            tipTextView.setText(message);
        }
    }

    public void setMessage(int resId){
        if(waitView == null){
            return;
        }
        TextView tipTextView = waitView.findViewById(R.id.tipTextView);
        if(tipTextView != null){
            tipTextView.setText(resId);
        }
    }

}
