package com.jeppeman.jetpackplayground.home.platform

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.auto.service.AutoService
import com.jeppeman.jetpackplayground.common_features.HomeFeature
import com.jeppeman.jetpackplayground.home.platform.di.DaggerHomeComponent
import com.jeppeman.jetpackplayground.home.platform.di.HomeComponent
import com.jeppeman.jetpackplayground.home.presentation.HomeActivity
import com.jeppeman.jetpackplayground.home.presentation.createHomeFragment

lateinit var homeComponent: HomeComponent
    private set

@AutoService(HomeFeature::class)
class HomeFeatureImpl : HomeFeature {
    override fun getLaunchIntent(context: Context): Intent {
        return Intent(context, HomeActivity::class.java)
    }

    override fun inject(dependencies: HomeFeature.Dependencies) {
        if (::homeComponent.isInitialized) {
            return
        }

        homeComponent = DaggerHomeComponent.factory()
                .create(dependencies, this)
    }

    override fun getMainScreen(): Fragment = createHomeFragment()
}