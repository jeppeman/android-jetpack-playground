package com.jeppeman.jetpackplayground.installdialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.jeppeman.jetpackplayground.MainViewModel
import com.jeppeman.jetpackplayground.R
import com.jeppeman.jetpackplayground.common.presentation.extensions.animateBetween
import com.jeppeman.jetpackplayground.common.presentation.extensions.animateProgress
import com.jeppeman.jetpackplayground.common.presentation.extensions.observe
import com.jeppeman.jetpackplayground.common_features.FeatureManager
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.install_feature_dialog.*
import javax.inject.Inject


fun createInstallDialogFragment(): InstallDialogFragment = InstallDialogFragment()

class InstallDialogFragment : DialogFragment() {
    @Inject
    lateinit var mainViewModel: MainViewModel

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
        view?.postDelayed(::dismiss, 500)
    }

    private fun handleFailedState(state: FeatureManager.InstallState.Failed) {
        progressText?.text = getString(R.string.install_dialog_failed, state.code.toString())
    }

    private fun handleUserConfirmationRequired(state: FeatureManager.InstallState.RequiresUserConfirmation) {
        activity?.startIntentSender(state.sender, null, 0, 0, 0)
    }

    private fun onInstallStateChanged(state: FeatureManager.InstallState?) {
        if (state != null) {
            dialog?.setTitle(getString(R.string.install_dialog_title, state.featureInfo.name))
            installDialogContainer?.transitionToEnd()
        }

        when (state) {
            is FeatureManager.InstallState.RequiresUserConfirmation -> handleUserConfirmationRequired(state)
            is FeatureManager.InstallState.Downloading -> handleDownloadingState(state)
            is FeatureManager.InstallState.Installing -> handleInstallingState(state)
            is FeatureManager.InstallState.Installed -> handleInstalledState(state)
            is FeatureManager.InstallState.Failed -> handleFailedState(state)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.install_feature_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.installState.observe(viewLifecycleOwner, ::onInstallStateChanged)

        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)
        dialog?.setCanceledOnTouchOutside(false)
    }
}