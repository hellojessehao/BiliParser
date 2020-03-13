package com.android.jesse.biliparser.network.di.component;

import android.app.Activity;

import com.android.jesse.biliparser.activity.MainActivity;
import com.android.jesse.biliparser.network.di.module.ActivityModule;
import com.android.jesse.biliparser.network.di.scope.ActivityScope;

import dagger.Component;


/**
 * Created by yu_yo on 2018/5/24.
 */

@ActivityScope
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    Activity getActivity();

    void inject(MainActivity mainActivity);

}
