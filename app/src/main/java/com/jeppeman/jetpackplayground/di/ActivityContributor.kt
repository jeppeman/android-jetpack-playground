package com.jeppeman.jetpackplayground.di

import com.jeppeman.jetpackplayground.MainActivity
import com.jeppeman.jetpackplayground.MainModule
import com.jeppeman.jetpackplayground.applinks.AppLinkActivity
import com.jeppeman.jetpackplayground.applinks.AppLinkModule
import com.jeppeman.jetpackplayground.common.presentation.di.scopes.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityContributor {
    @ActivityScope
    @ContributesAndroidInjector(modules = [
        MainModule::class,
        FragmentContributor::class
    ])
    fun contributeMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [
        AppLinkModule::class,
        FragmentContributor::class
    ])
    fun contributeAppLinkActivity(): AppLinkActivity
}