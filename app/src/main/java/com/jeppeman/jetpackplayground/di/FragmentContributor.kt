package com.jeppeman.jetpackplayground.di

import com.jeppeman.jetpackplayground.common.presentation.di.scopes.FragmentScope
import com.jeppeman.jetpackplayground.installdialog.InstallDialogFragment
import com.jeppeman.jetpackplayground.installdialog.InstallDialogModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FragmentContributor {
    @ContributesAndroidInjector(modules = [InstallDialogModule::class])
    @FragmentScope
    fun contributeInstallDialogFragment(): InstallDialogFragment
}