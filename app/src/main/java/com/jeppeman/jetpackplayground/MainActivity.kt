package com.jeppeman.jetpackplayground

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jeppeman.jetpackplayground.common.presentation.AppUiContainer
import com.jeppeman.jetpackplayground.common.presentation.extensions.observe
import com.jeppeman.jetpackplayground.common_features.Feature
import com.jeppeman.jetpackplayground.installdialog.createInstallDialogFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

private val TAG_TOP_FRAGMENT = "${MainActivity::class.java.name}.TOP_FRAGMENT"

class MainActivity : AppCompatActivity(), HasAndroidInjector, AppUiContainer, BottomNavigationView.OnNavigationItemSelectedListener {
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var handler: Handler

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    override fun enterFullscreen() {
        window?.decorView?.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        bottomNavigation?.visibility = View.GONE
    }

    override fun exitFullscreen() {
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        bottomNavigation?.visibility = View.VISIBLE
    }

    private fun goToFeatureEntryPoint(@IdRes actionId: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        when (actionId) {
            R.id.actionHome -> transaction.setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_right)
            R.id.actionVideo -> transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
        }

        // Instead of replacing we hide the current, add the next and then remove the previous
        // in order to have child fragments be part of the animation
        val topFragment = supportFragmentManager.findFragmentByTag(TAG_TOP_FRAGMENT)
        val fragment = mainViewModel.getFeature(actionId).getMainScreen()
        transaction.add(R.id.fragmentContainer, fragment, TAG_TOP_FRAGMENT)
        if (topFragment != null) {
            transaction.hide(topFragment).commit()
            handler.postDelayed({
                supportFragmentManager.beginTransaction().remove(topFragment).commit()
            }, resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())
        } else {
            transaction.commit()
        }
    }

    private fun launchInstallDialog(@IdRes actionId: Int) {
        createInstallDialogFragment(actionId).show(supportFragmentManager, "install")
    }

    private fun featureInstalled(featureInfo: Feature.Info) {
        bottomNavigation?.selectedItemId = featureInfo.actionId
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == bottomNavigation?.selectedItemId) {
            return false
        }

        val isInstalled = mainViewModel.isFeatureInstalled(item.itemId)

        return if (!isInstalled) {
            launchInstallDialog(item.itemId)
            false
        } else {
            goToFeatureEntryPoint(item.itemId)
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidInjection.inject(this)

        if (savedInstanceState == null) {
            goToFeatureEntryPoint(R.id.actionHome)
        }

        mainViewModel.featureInstalled.observe(this, ::featureInstalled)

        bottomNavigation?.setOnNavigationItemSelectedListener(this)
    }
}