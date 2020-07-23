package com.jeppeman.jetpackplayground.installdialog

import android.content.Context
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jeppeman.globallydynamic.globalsplitinstall.GlobalSplitInstallSessionState
import com.jeppeman.jetpackplayground.common.presentation.extensions.mutableLiveDataOf
import com.jeppeman.jetpackplayground.common_features.*
import javax.inject.Inject

interface InstallDialogViewModel {
    val installState: LiveData<FeatureManager.InstallState>
    val missingSplitsInstallState: LiveData<GlobalSplitInstallSessionState>
    fun installFeature(feature: String)
    fun installFeature(@IdRes actionId: Int)
    fun installMissingSplits()
}

class InstallDialogViewModelImpl @Inject constructor(
        private val featureManager: FeatureManager,
        private val context: Context
) : ViewModel(), InstallDialogViewModel {
    override val installState = mutableLiveDataOf<FeatureManager.InstallState>()
    override val missingSplitsInstallState = mutableLiveDataOf<GlobalSplitInstallSessionState>()

    override fun installMissingSplits() {
        featureManager.installMissingSplits(missingSplitsInstallState::setValue)
    }

    override fun installFeature(actionId: Int) {
        val listener: (FeatureManager.InstallState) -> Unit = { state ->
            installState.value = state
        }
        when (actionId) {
            HomeFeature::class.info(context).actionId -> featureManager.installFeature<HomeFeature>(listener)
            VideoFeature::class.info(context).actionId -> featureManager.installFeature<VideoFeature>(listener)
        }
    }

    override fun installFeature(feature: String) {
        val listener: (FeatureManager.InstallState) -> Unit = { state ->
            installState.value = state
        }
        when (feature) {
            HomeFeature::class.info(context).id -> featureManager.installFeature<HomeFeature>(listener)
            VideoFeature::class.info(context).id -> featureManager.installFeature<VideoFeature>(listener)
        }
    }
}