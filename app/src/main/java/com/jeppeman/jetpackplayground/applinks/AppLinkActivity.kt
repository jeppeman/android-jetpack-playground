package com.jeppeman.jetpackplayground.applinks

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.jeppeman.jetpackplayground.common.presentation.extensions.observe
import com.jeppeman.jetpackplayground.common_features.Feature
import com.jeppeman.jetpackplayground.installdialog.createInstallDialogFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class AppLinkActivity : AppCompatActivity(), HasAndroidInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var appLinkViewModel: AppLinkViewModel
    @Inject
    lateinit var handler: Handler

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    private fun launchFeature(feature: String) {
        val launchIntent = appLinkViewModel.getFeature(feature).getLaunchIntent(this)
        startActivity(launchIntent)
        finish()
    }

    private fun onFeatureInstalled(featureInfo: Feature.Info) {
        handler.postDelayed({
            launchFeature(featureInfo.id)
        }, 500)
    }

    private fun handleAppLink(uri: Uri) {
        AndroidInjection.inject(this)
        val feature = uri.pathSegments.firstOrNull()
        if (feature != null) {
            appLinkViewModel.featureInstalled.observe(this, ::onFeatureInstalled)
            if (appLinkViewModel.isFeatureInstalled(feature)) {
                launchFeature(feature)
            } else {
                createInstallDialogFragment(feature).show(supportFragmentManager, "install")
            }
        } else {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.data?.let(::handleAppLink) ?: finish()
    }
}