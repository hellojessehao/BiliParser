package com.android.jesse.biliparser.network.util;


import com.android.jesse.biliparser.base.Constant;
import com.android.jesse.biliparser.network.model.bean.ResponseBaseBean;
import com.android.jesse.biliparser.network.model.http.exception.ApiException;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by codeest on 2016/8/3.
 */
public class RxUtil {

    /**
     * 统一线程处理
     *
     * @param <T>
     * @return
     */
    public static <T> FlowableTransformer<T, T> rxSchedulerHelper() {    //compose简化线程
        return new FlowableTransformer<T, T>() {
            @Override
            public Flowable<T> apply(Flowable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 统一返回结果处理
     *
     * @param <T>
     * @return
     */
    public static <T> FlowableTransformer<ResponseBaseBean<T>, T> handleSearchResult() {   //compose判断结果
        return new FlowableTransformer<ResponseBaseBean<T>, T>() {
            @Override
            public Flowable<T> apply(Flowable<ResponseBaseBean<T>> httpResponseFlowable) {
                return httpResponseFlowable.flatMap(new Function<ResponseBaseBean<T>, Flowable<T>>() {
                    @Override
                    public Flowable<T> apply(ResponseBaseBean<T> tResponseBaseBean) {
                        if (tResponseBaseBean.getErrorCode() == Constant.NET_REQUEST_OK) {
                            if (tResponseBaseBean.getData() != null)
                                return createData(tResponseBaseBean.getData());
                            else
                                return Flowable.empty();
                        } else {
                            return Flowable.error(new ApiException(tResponseBaseBean.getMsg()));
                        }
                    }
                });
            }
        };
    }

    /**
     * 生成Flowable
     *
     * @param <T>
     * @return
     */
    public static <T> Flowable<T> createData(final T t) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> emitter) throws Exception {
                try {
                    emitter.onNext(t);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, BackpressureStrategy.BUFFER);
    }
}
