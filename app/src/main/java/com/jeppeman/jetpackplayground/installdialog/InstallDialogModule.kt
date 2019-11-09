package com.jeppeman.jetpackplayground.installdialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.jeppeman.jetpackplayground.common.presentation.ViewModelFactory
import com.jeppeman.jetpackplayground.common.presentation.ViewModelKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
object InstallDialogModule {
    @Provides
    @JvmStatic
    @IntoMap
    @ViewModelKey(InstallDialogViewModelImpl::class)
    fun provideIntoViewModelFactory(
            installDialogViewModelImpl: InstallDialogViewModelImpl
    ): ViewModel = installDialogViewModelImpl

    @Provides
    @JvmStatic
    fun provideInstallDialogViewModel(
            installDialogFragment: InstallDialogFragment,
            viewModelFactory: ViewModelFactory
    ): InstallDialogViewModel = ViewModelProviders.of(
            installDialogFragment,
            viewModelFactory
    )[InstallDialogViewModelImpl::class.java]
}