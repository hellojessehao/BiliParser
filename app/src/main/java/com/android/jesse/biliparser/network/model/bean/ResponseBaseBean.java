package com.android.jesse.biliparser.network.model.bean;

/**
 * @Description:
 * @author: yuyoucheng
 * @date: 2019/5/14
 */
public class ResponseBaseBean<T> {
    private int errorCode;
    private String msg;
    private T data;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
