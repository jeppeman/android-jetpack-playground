package com.jeppeman.jetpackplayground.installdialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.jeppeman.globallydynamic.globalsplitinstall.GlobalSplitInstallErrorCode
import com.jeppeman.globallydynamic.globalsplitinstall.GlobalSplitInstallErrorCodeHelper
import com.jeppeman.globallydynamic.globalsplitinstall.GlobalSplitInstallSessionState
import com.jeppeman.globallydynamic.globalsplitinstall.GlobalSplitInstallSessionStatus
import com.jeppeman.jetpackplayground.R
import com.jeppeman.jetpackplayground.common.presentation.extensions.animateBetween
import com.jeppeman.jetpackplayground.common.presentation.extensions.animateProgress
import com.jeppeman.jetpackplayground.common.presentation.extensions.observe
import com.jeppeman.jetpackplayground.common_features.FeatureManager
import com.jeppeman.jetpackplayground.common_features.progress
import com.jeppeman.jetpackplayground.isHuawei
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.install_feature_dialog.*
import javax.inject.Inject

private const val ARG_FEATURE_ID = "com.jeppeman.jetpackplayground.ARG_FEATURE_ID"
private const val ARG_FEATURE_ACTION_ID = "com.jeppeman.jetpackplayground.ARG_FEATURE_ACTION_ID"

const val INSTALL_REQUEST_CODE = 123

fun createInstallDialogFragment(feature: String): InstallDialogFragment = InstallDialogFragment().apply {
    arguments = bundleOf(ARG_FEATURE_ID to feature)
}

fun createInstallDialogFragment(feature: Int): InstallDialogFragment = InstallDialogFragment().apply {
    arguments = bundleOf(ARG_FEATURE_ACTION_ID to feature)
}

class InstallDialogFragment : DialogFragment() {
    @Inject
    lateinit var installDialogViewModel: InstallDialogViewModel

    val dismissListeners = mutableListOf<() -> Unit>()

    private fun progressTo(to: Int) {
        progressValueText?.animateProgress(loader?.progress ?: 0, to, 300)
        loader?.animateBetween(loader?.progress ?: 0, to, 333)
    }

    private fun handleDownloadingState(state: FeatureManager.InstallState.Downloading) {
        progressText?.text = getString(R.string.install_dialog_step_downloading)
        progressTo(state.progress)
    }

    private fun handleInstallingState(state: FeatureManager.InstallState.Installing) {
        progressText?.text = getString(R.string.install_dialog_installing)
        progressTo(state.progress)
    }

    private fun handleInstalledState(state: FeatureManager.InstallState.Installed) {
        progressText?.text = getString(R.string.install_dialog_installed)
        progressTo(100)
        dismissDelayed()
    }

    private fun dismissDelayed() {
        view?.postDelayed({
            if (fragmentManager != null) {
                dismiss()
            }
        }, 500)
    }

    private fun handleFailedState(state: FeatureManager.InstallState.Failed) {
        progressText?.text = getString(
                R.string.install_dialog_failed,
                state.message
        )
    }

    private fun handleUserConfirmationRequired(state: FeatureManager.InstallState.RequiresUserConfirmation) {
        activity?.let { state.startConfirmationActivity(it, INSTALL_REQUEST_CODE) }
    }

    private fun onInstallStateChanged(state: FeatureManager.InstallState?) {
        if (state != null) {
            if (isHuawei()
                    && state is FeatureManager.InstallState.Failed
                    && (state.code == GlobalSplitInstallErrorCode.MODULE_UNAVAILABLE
                            || state.code == GlobalSplitInstallErrorCode.INTERNAL_ERROR)) {
                installDialogContainer?.setPadding(10, installDialogContainer.paddingTop, installDialogContainer.paddingRight, 10)
                installDialogContainer?.loadLayoutDescription(R.xml.install_feature_dialog_scene_huawei)
                installDialogContainer?.transitionToEnd()
                dismissButton?.setOnClickListener { dismiss() }
                dialog?.setTitle("App Gallery")
//                loader?.setVisible(false)
//                progressValueText?.setVisible(false)
//                indeterminateLoader?.setVisible(false)
//                progressText?.setVisible(true)
                progressText?.text = "The Video module is only available for download on Huawei App Gallery, " +
                        "when the app has been published you will be available to download the module."
                return
            } else {
                dialog?.setTitle(getString(R.string.install_dialog_title, state.featureInfo.name))
                installDialogContainer?.transitionToEnd()
            }
        }

        when (state) {
            is FeatureManager.InstallState.RequiresUserConfirmation -> handleUserConfirmationRequired(state)
            is FeatureManager.InstallState.Downloading -> handleDownloadingState(state)
            is FeatureManager.InstallState.Installing -> handleInstallingState(state)
            is FeatureManager.InstallState.Installed -> handleInstalledState(state)
            is FeatureManager.InstallState.Failed -> handleFailedState(state)
            is FeatureManager.InstallState.Canceled -> dismissDelayed()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListeners.forEach { it() }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)

        if (savedInstanceState == null) {
            val featureId = arguments?.getString(ARG_FEATURE_ID)
            val featureActionId = arguments?.getInt(ARG_FEATURE_ACTION_ID)
            when {
                featureId != null -> installDialogViewModel.installFeature(featureId)
                featureActionId != null -> installDialogViewModel.installFeature(featureActionId)
                else -> dismiss()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.install_feature_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        installDialogViewModel.installState.observe(viewLifecycleOwner, ::onInstallStateChanged)

        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)
        dialog?.setCanceledOnTouchOutside(false)
    }
}

fun createMissingSplitsInstallDialogFragment() = MissingSplitsInstallDialogFragment()

const val MISSING_SPLITS_INSTALL_REQUEST_CODE = 1234

class MissingSplitsInstallDialogFragment : DialogFragment() {
    @Inject
    lateinit var installDialogViewModel: InstallDialogViewModel
    @Inject
    lateinit var featureManager: FeatureManager

    private fun progressTo(to: Int) {
        progressValueText?.animateProgress(loader?.progress ?: 0, to, 300)
        loader?.animateBetween(loader?.progress ?: 0, to, 333)
    }

    private fun handleDownloadingState(state: GlobalSplitInstallSessionState) {
        progressText?.text = getString(R.string.install_dialog_step_downloading)
        progressTo(state.progress())
    }

    private fun handleInstallingState(state: GlobalSplitInstallSessionState) {
        progressText?.text = getString(R.string.install_dialog_installing)
        progressTo(state.progress())
    }

    private fun handleInstalledState(state: GlobalSplitInstallSessionState) {
        progressText?.text = getString(R.string.install_dialog_installed)
        progressTo(100)
        dismissDelayed()
    }

    private fun dismissDelayed() {
        view?.postDelayed({
            if (fragmentManager != null) {
                dismiss()
            }
        }, 500)
    }

    private fun handleFailedState(state: GlobalSplitInstallSessionState) {
        progressText?.text = getString(
                R.string.install_dialog_failed,
                GlobalSplitInstallErrorCodeHelper.getErrorDescription(state.errorCode()) ?: ""
        )
    }

    private fun handleUserConfirmationRequired(state: GlobalSplitInstallSessionState) {
        featureManager.startConfirmationDialogForResult(activity!!, state, MISSING_SPLITS_INSTALL_REQUEST_CODE)
    }

    private fun onInstallStateChanged(state: GlobalSplitInstallSessionState?) {
        if (state != null) {
            dialog?.setTitle(getString(R.string.missing_splits_install_dialog_title))
            installDialogContainer?.transitionToEnd()
        }

        when (state?.status()) {
            GlobalSplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> handleUserConfirmationRequired(state)
            GlobalSplitInstallSessionStatus.DOWNLOADING -> handleDownloadingState(state)
            GlobalSplitInstallSessionStatus.INSTALLING -> handleInstallingState(state)
            GlobalSplitInstallSessionStatus.INSTALLED -> handleInstalledState(state)
            GlobalSplitInstallSessionStatus.FAILED -> handleFailedState(state)
            GlobalSplitInstallSessionStatus.CANCELED -> dismissDelayed()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)

        if (savedInstanceState == null) {
            installDialogViewModel.installMissingSplits()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.install_feature_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        installDialogViewModel.missingSplitsInstallState.observe(viewLifecycleOwner, ::onInstallStateChanged)

        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)
        dialog?.setCanceledOnTouchOutside(false)
    }
}
