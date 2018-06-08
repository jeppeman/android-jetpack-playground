package com.jeppeman.jetpackplayground.di

import com.jeppeman.jetpackplayground.MainActivity
import com.jeppeman.jetpackplayground.MainModule
import com.jeppeman.jetpackplayground.common.presentation.di.scopes.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityContributor {
    @ActivityScope
    @ContributesAndroidInjector(modules = [
        MainModule::class,
        MainModule.Contributor::class
    ])
    fun contributeMainActivity(): MainActivity
}