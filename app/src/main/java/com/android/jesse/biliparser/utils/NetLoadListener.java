package com.android.jesse.biliparser.utils;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 监听网络请求是否超时
 * @author: zhangshihao
 * @date: 2020/4/7
 */
public class NetLoadListener {

    private static final String TAG = NetLoadListener.class.getSimpleName();

    private static NetLoadListener instance;
    private Handler mHandler = new Handler();
    private final int TIMEOUTMILLS = 30*1000;//请求超过这个时间算作失败
    private List<Callback> callBackList = new ArrayList<>();

    public static NetLoadListener getInstance(){
        if(instance == null){
            instance = new NetLoadListener();
        }
        return instance;
    }

    /**
     * 开始监听(网络请求开始时调用)
     */
    public void startListening(){
        mHandler.postDelayed(mRunnable,TIMEOUTMILLS);
    }

    /**
     * 开始监听(网络请求开始时调用)
     */
    public void startListening(Callback callback){
        mHandler.postDelayed(mRunnable,TIMEOUTMILLS);
        addCallback(callback);
    }

    /**
     * 停止监听(网络请求成功时调用)
     */
    public void stopListening(){
        mHandler.removeCallbacks(mRunnable);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(Utils.isListEmpty(callBackList)){
                return;
            }
            for(Callback callback : callBackList){
                callback.onNetLoadFailed();
            }
        }
    };

    public interface Callback{
        void onNetLoadFailed();
    }

    public void addCallback(Callback callback){
        if(callBackList.contains(callback)){
            return;
        }
        callBackList.add(callback);
    }

    public void removeCallback(Callback callback){
        callBackList.remove(callback);
    }

    /**
     * 移除最新加入的一个回调
     */
    public void removeLastCallback(){
        if(Utils.isListEmpty(callBackList)){
            return;
        }
        callBackList.remove(callBackList.size()-1);
    }

}
