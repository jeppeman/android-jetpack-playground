package com.jeppeman.jetpackplayground.home.platform

import androidx.fragment.app.Fragment
import com.jeppeman.jetpackplayground.common_features.HomeFeature
import com.jeppeman.jetpackplayground.home.platform.di.DaggerHomeComponent
import com.jeppeman.jetpackplayground.home.platform.di.HomeComponent
import com.jeppeman.jetpackplayground.home.presentation.createHomeFragment

lateinit var homeComponent: HomeComponent
    private set

class HomeFeatureImpl : HomeFeature {
    override fun inject(dependencies: HomeFeature.Dependencies) {
        homeComponent = DaggerHomeComponent.factory()
                .create(dependencies, this)
    }

    override fun getEntryPoint(): Fragment = createHomeFragment()
}