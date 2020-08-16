package com.jeppeman.jetpackplayground.home.presentation.download

import com.jeppeman.jetpackplayground.di.FragmentContributor
import dagger.BindsInstance
import dagger.Subcomponent
import dagger.android.AndroidInjectionModule

@Subcomponent(modules = [AndroidInjectionModule::class, FragmentContributor::class])
interface DownloadComponent {
    fun inject(downloadActivity: DownloadActivity)

    @Subcomponent.Factory
    interface Factory {
        fun create(
                @BindsInstance downloadActivity: DownloadActivity
        ): DownloadComponent
    }
}