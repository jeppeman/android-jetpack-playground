package com.jeppeman.jetpackplayground

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jeppeman.jetpackplayground.common_features.FeatureManager
import com.jeppeman.jetpackplayground.common_features.HomeFeature
import com.jeppeman.jetpackplayground.common_features.VideoFeature
import com.jeppeman.jetpackplayground.common_features.getFeature
import com.jeppeman.jetpackplayground.common_features.installFeature
import com.jeppeman.jetpackplayground.common_features.isFeatureInstalled
import com.jeppeman.jetpackplayground.common.presentation.SingleLiveEvent
import com.jeppeman.jetpackplayground.common.presentation.extensions.mutableLiveDataOf
import com.jeppeman.jetpackplayground.common_features.Feature
import dagger.Lazy
import javax.inject.Inject

interface MainViewModel {
    val featureInstalled: LiveData<Feature.Info>
    val installState: LiveData<FeatureManager.InstallState>
    fun isFeatureInstalled(@IdRes actionId: Int): Boolean
    fun installFeature(@IdRes actionId: Int)
    fun getFeature(@IdRes actionId: Int): Feature<*>
}

class MainViewModelImpl @Inject constructor(
        private val featureManager: FeatureManager,
        private val homeFeatureDependencies: Lazy<HomeFeature.Dependencies>,
        private val videoFeatureDependencies: Lazy<VideoFeature.Dependencies>
) : ViewModel(), MainViewModel {
    override val featureInstalled = SingleLiveEvent<Feature.Info>()
    override val installState = mutableLiveDataOf<FeatureManager.InstallState>()

    override fun installFeature(actionId: Int) {
        val listener: (FeatureManager.InstallState) -> Unit = {state ->
            installState.value = state
            if (state is FeatureManager.InstallState.Installed) {
                featureInstalled.value = state.featureInfo
            }
        }
        when (actionId) {
            R.id.actionHome -> featureManager.installFeature<HomeFeature>(listener)
            R.id.actionVideo -> featureManager.installFeature<VideoFeature>(listener)
        }
    }

    override fun isFeatureInstalled(actionId: Int): Boolean {
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

    override fun getFeature(actionId: Int): Feature<*> {
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