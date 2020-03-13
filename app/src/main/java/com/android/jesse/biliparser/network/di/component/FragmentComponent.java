package com.android.jesse.biliparser.network.di.component;

import android.app.Activity;

import com.android.jesse.biliparser.network.di.module.FragmentModule;
import com.android.jesse.biliparser.network.di.scope.FragmentScope;

import dagger.Component;

/**
 * Created by yu_yo on 2018/5/24.
 */

@FragmentScope
@Component(dependencies = AppComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {

    Activity getActivity();

}
