package com.jeppeman.jetpackplayground.base

import android.content.Context
import androidx.lifecycle.ViewModel
import com.jeppeman.jetpackplayground.R
import com.jeppeman.jetpackplayground.common.presentation.SingleLiveEvent
import com.jeppeman.jetpackplayground.common.presentation.extensions.mutableLiveDataOf
import com.jeppeman.jetpackplayground.common_features.Feature
import com.jeppeman.jetpackplayground.common_features.FeatureManager
import com.jeppeman.jetpackplayground.common_features.HomeFeature
import com.jeppeman.jetpackplayground.common_features.VideoFeature
import com.jeppeman.jetpackplayground.common_features.getFeature
import com.jeppeman.jetpackplayground.common_features.info
import com.jeppeman.jetpackplayground.common_features.isFeatureInstalled
import dagger.Lazy
import javax.inject.Inject

abstract class BaseFeatureNavigatorViewModel : ViewModel() {

    private lateinit var context: Context
    private lateinit var featureManager: FeatureManager
    private lateinit var homeFeatureDependencies: Lazy<HomeFeature.Dependencies>
    private lateinit var videoFeatureDependencies: Lazy<VideoFeature.Dependencies>

    val featureInstalled = SingleLiveEvent<Feature.Info>()
    val installState = mutableLiveDataOf<FeatureManager.InstallState>()

    @Inject
    fun injectMembers(
            context: Context,
            featureManager: FeatureManager,
            homeFeatureDependencies: Lazy<HomeFeature.Dependencies>,
            videoFeatureDependencies: Lazy<VideoFeature.Dependencies>
    ) {
        this.context = context
        this.featureManager = featureManager
        this.homeFeatureDependencies = homeFeatureDependencies
        this.videoFeatureDependencies = videoFeatureDependencies
        featureManager.registerInstallListener(featureInstalled::setValue)
    }

    override fun onCleared() {
        super.onCleared()
        featureManager.unregisterInstallListener(featureInstalled::setValue)
    }

    fun isFeatureInstalled(feature: String): Boolean {
        return when (feature) {
            HomeFeature::class.info(context).id -> {
                featureManager.isFeatureInstalled<HomeFeature>()
            }
            VideoFeature::class.info(context).id -> {
                featureManager.isFeatureInstalled<VideoFeature>()
            }
            else -> false
        }
    }

    fun getFeature(feature: String): Feature<*> {
        return when (feature) {
            HomeFeature::class.info(context).id -> {
                featureManager.getFeature<HomeFeature, HomeFeature.Dependencies>(
                        dependencies = homeFeatureDependencies.get()
                )
            }
            VideoFeature::class.info(context).id -> {
                featureManager.getFeature<VideoFeature, VideoFeature.Dependencies>(
                        dependencies = videoFeatureDependencies.get()
                )
            }
            else -> null
        } ?: throw IllegalArgumentException("Feature not found for action $feature")
    }

    fun isFeatureInstalled(actionId: Int): Boolean {
        return when (actionId) {
            R.id.actionHome -> {
                featureManager.isFeatureInstalled<HomeFeature>()
            }
            R.id.actionVideo -> {
                featureManager.isFeatureInstalled<VideoFeature>()
            }
            else -> false
        }
    }

    fun getFeature(actionId: Int): Feature<*> {
        return when (actionId) {
            R.id.actionHome -> {
                featureManager.getFeature<HomeFeature, HomeFeature.Dependencies>(
                        dependencies = homeFeatureDependencies.get()
                )
            }
            R.id.actionVideo -> {
                featureManager.getFeature<VideoFeature, VideoFeature.Dependencies>(
                        dependencies = videoFeatureDependencies.get()
                )
            }
            else -> null
        } ?: throw IllegalArgumentException("Feature not found for action $actionId")
    }
}