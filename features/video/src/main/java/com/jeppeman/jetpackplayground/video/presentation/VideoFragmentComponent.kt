package com.jeppeman.jetpackplayground.video.presentation

import com.jeppeman.jetpackplayground.common.presentation.di.scopes.FragmentScope
import dagger.BindsInstance
import dagger.Subcomponent
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@FragmentScope
@Subcomponent(modules = [
    VideoFragmentModule::class,
    VideoFragmentModule.Contributor::class,
    AndroidInjectionModule::class
])
interface VideoFragmentComponent : AndroidInjector<VideoFragment> {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance videoFragment: VideoFragment): VideoFragmentComponent
    }
}