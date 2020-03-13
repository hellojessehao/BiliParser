package com.android.jesse.biliparser.network.di.component;

import com.android.jesse.biliparser.base.App;
import com.android.jesse.biliparser.network.di.module.AppModule;
import com.android.jesse.biliparser.network.di.module.HttpModule;
import com.android.jesse.biliparser.network.model.DataManager;
import com.android.jesse.biliparser.network.model.http.RetrofitHelper;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by yu_yo on 2018/5/24.
 */
@Singleton
@Component(modules = {AppModule.class, HttpModule.class})
public interface AppComponent {

    App getContext();  // 提供App的Context

    DataManager getDataManager(); //数据中心

    RetrofitHelper retrofitHelper();  //提供http的帮助类
}
