package com.jeppeman.jetpackplayground.home.presentation

import android.os.Bundle
import com.jeppeman.jetpackplayground.MainApplication
import com.jeppeman.jetpackplayground.common.presentation.BaseActivity
import com.jeppeman.jetpackplayground.common_features.FeatureManager
import com.jeppeman.jetpackplayground.common_features.HomeFeature
import com.jeppeman.jetpackplayground.common_features.getFeature
import com.jeppeman.jetpackplayground.home.R

class HomeActivity : BaseActivity() {
    private val appComponent get() = (application as MainApplication).appComponent
    private val featureManager: FeatureManager by lazy { appComponent.featureManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (savedInstanceState == null) {
            val feature = featureManager.getFeature<HomeFeature, HomeFeature.Dependencies>(
                    dependencies = appComponent.homeFeatureDependencies
            ) ?: throw IllegalStateException("Could not retrieve video feature")

            supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, feature.getMainScreen())
                    .commit()
        }
    }
}