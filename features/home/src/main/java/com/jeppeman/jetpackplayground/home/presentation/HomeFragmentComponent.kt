package com.jeppeman.jetpackplayground.home.presentation

import com.jeppeman.jetpackplayground.common.presentation.di.scopes.FragmentScope
import dagger.BindsInstance
import dagger.Subcomponent
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@FragmentScope
@Subcomponent(modules = [
    HomeFragmentModule::class,
    AndroidInjectionModule::class
])
interface HomeFragmentComponent : AndroidInjector<HomeFragment> {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance homeFragment: HomeFragment): HomeFragmentComponent
    }
}