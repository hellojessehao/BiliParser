package com.android.jesse.biliparser.network.di.module;

import com.android.jesse.biliparser.base.App;
import com.android.jesse.biliparser.network.model.DataManager;
import com.android.jesse.biliparser.network.model.http.HttpHelper;
import com.android.jesse.biliparser.network.model.http.RetrofitHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by codeest on 16/8/7.
 */

@Module
public class AppModule {
    private final App application;

    public AppModule(App application) {
        this.application = application;
    }

    @Provides
    @Singleton
    App provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    HttpHelper provideHttpHelper(RetrofitHelper retrofitHelper) {
        return retrofitHelper;
    }

//    @Provides
//    @Singleton
//    PreferencesHelper providePreferencesHelper(ImplPreferencesHelper implPreferencesHelper) {
//        return implPreferencesHelper;
//    }

//    @Provides
//    @Singleton
//    RoomHelper provideRoomHelper() {
//        return new RoomHelper(application);
//    }

//    @Provides
//    @Singleton
//    DBHelper provideDBHelper(RoomHelper roomHelper) {
//        return roomHelper;
//    }

    @Provides
    @Singleton
    DataManager provideDataManager(HttpHelper httpHelper) {
        return new DataManager(httpHelper);
    }
}
