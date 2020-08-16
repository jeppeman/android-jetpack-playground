package com.jeppeman.jetpackplayground.home.presentation.download

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jeppeman.globallydynamic.globalsplitinstall.GlobalSplitInstallConfirmResult
import com.jeppeman.jetpackplayground.R
import com.jeppeman.jetpackplayground.common_features.HomeFeature
import com.jeppeman.jetpackplayground.home.platform.HomeFeatureImpl
import com.jeppeman.jetpackplayground.installdialog.INSTALL_REQUEST_CODE
import com.jeppeman.jetpackplayground.installdialog.createInstallDialogFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class DownloadActivity : AppCompatActivity(), HasAndroidInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    private fun dismissInstallDialog() {
        supportFragmentManager.findFragmentByTag("install")?.let {
            supportFragmentManager.beginTransaction()
                    .remove(it)
                    .commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ((applicationContext as? HomeFeature.InjectionProvider)?.homeFeature as? HomeFeatureImpl)
                ?.homeComponent
                ?.downloadComponentFactory
                ?.create(this)
                ?.inject(this)
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            createInstallDialogFragment(R.id.actionVideo).apply {
                dismissListeners.add { finish() }
                show(supportFragmentManager, "install")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INSTALL_REQUEST_CODE
                && data?.hasExtra(GlobalSplitInstallConfirmResult.EXTRA_RESULT) == true) {
            val confirmResult = data.getIntExtra(
                    GlobalSplitInstallConfirmResult.EXTRA_RESULT,
                    GlobalSplitInstallConfirmResult.RESULT_DENIED
            )
            dismissInstallDialog()
            if (confirmResult == GlobalSplitInstallConfirmResult.RESULT_CONFIRMED) {
                createInstallDialogFragment(R.id.actionVideo).apply {
                    dismissListeners.add { finish() }
                    show(supportFragmentManager, "install")
                }
            }
        }
    }
}