package com.jeppeman.jetpackplayground.applinks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.jeppeman.jetpackplayground.common.presentation.ViewModelFactory
import com.jeppeman.jetpackplayground.common.presentation.ViewModelKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
object AppLinkModule {
    @Provides
    @IntoMap
    @JvmStatic
    @ViewModelKey(AppLinkViewModelImpl::class)
    fun provideAppLinkViewModelIntoMap(appLinkViewModelImpl: AppLinkViewModelImpl): ViewModel =
            appLinkViewModelImpl

    @Provides
    @JvmStatic
    fun provideMainActivityViewModel(
            appLinkActivity: AppLinkActivity,
            viewModelFactory: ViewModelFactory
    ): AppLinkViewModel =
            ViewModelProviders.of(appLinkActivity, viewModelFactory)[AppLinkViewModelImpl::class.java]
}