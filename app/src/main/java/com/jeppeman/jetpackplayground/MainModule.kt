package com.jeppeman.jetpackplayground

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.jeppeman.jetpackplayground.common.presentation.ViewModelFactory
import com.jeppeman.jetpackplayground.common.presentation.ViewModelKey
import com.jeppeman.jetpackplayground.common.presentation.di.scopes.FragmentScope
import com.jeppeman.jetpackplayground.installdialog.InstallDialogFragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
object MainModule {
    @Provides
    @IntoMap
    @JvmStatic
    @ViewModelKey(MainViewModelImpl::class)
    fun provideMainViewModelIntoMap(mainActivityViewModel: MainViewModelImpl): ViewModel =
            mainActivityViewModel

    @Provides
    @JvmStatic
    fun provideMainViewModel(
            mainActivity: MainActivity,
            viewModelFactory: ViewModelFactory
    ): MainViewModel =
            ViewModelProviders.of(mainActivity, viewModelFactory)[MainViewModelImpl::class.java]
}