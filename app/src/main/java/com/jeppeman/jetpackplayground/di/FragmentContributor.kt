package com.jeppeman.jetpackplayground.di

import com.jeppeman.jetpackplayground.common.presentation.di.scopes.FragmentScope
import com.jeppeman.jetpackplayground.installdialog.InstallDialogFragment
import com.jeppeman.jetpackplayground.installdialog.InstallDialogModule
import com.jeppeman.jetpackplayground.installdialog.MissingSplitsInstallDialogFragment
import com.jeppeman.jetpackplayground.installdialog.MissingSplitsInstallDialogModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FragmentContributor {
    @ContributesAndroidInjector(modules = [InstallDialogModule::class])
    @FragmentScope
    fun contributeInstallDialogFragment(): InstallDialogFragment

    @ContributesAndroidInjector(modules = [MissingSplitsInstallDialogModule::class])
    @FragmentScope
    fun contributeMissingSplitsInstallDialogFragment(): MissingSplitsInstallDialogFragment
}