package com.jeppeman.jetpackplayground.home.presentation

import androidx.lifecycle.ViewModelProviders
import com.jeppeman.jetpackplayground.common.presentation.di.scopes.FragmentScope
import dagger.Module
import dagger.Provides

@Module
object HomeFragmentModule {
    @JvmStatic
    @Provides
    @FragmentScope
    fun provideHomeFragment(videoFragment: HomeFragment): HomeViewModel =
            ViewModelProviders.of(videoFragment)[HomeViewModel::class.java]
}