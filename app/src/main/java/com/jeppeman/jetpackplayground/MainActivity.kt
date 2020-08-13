package com.jeppeman.jetpackplayground

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jeppeman.globallydynamic.globalsplitinstall.GlobalSplitInstallConfirmResult
import com.jeppeman.jetpackplayground.common.presentation.AppUiContainer
import com.jeppeman.jetpackplayground.common.presentation.extensions.observe
import com.jeppeman.jetpackplayground.common_features.Feature
import com.jeppeman.jetpackplayground.installdialog.MISSING_SPLITS_INSTALL_REQUEST_CODE
import com.jeppeman.jetpackplayground.installdialog.createInstallDialogFragment
import com.jeppeman.jetpackplayground.installdialog.createMissingSplitsInstallDialogFragment
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

    private fun dismissInstallDialog() {
        supportFragmentManager.findFragmentByTag("install")?.let {
            supportFragmentManager.beginTransaction()
                    .remove(it)
                    .commit()
        }
    }

    private fun dismissInstallMissingSplitsDialog() {
        supportFragmentManager.findFragmentByTag("missingSplitsInstall")?.let {
            supportFragmentManager.beginTransaction()
                    .remove(it)
                    .commit()
        }
    }

    private fun launchInstallDialog(@IdRes actionId: Int) {
        createInstallDialogFragment(actionId).show(supportFragmentManager, "install")
    }

    private fun launchMissingSplitsDialog() {
        createMissingSplitsInstallDialogFragment().show(supportFragmentManager, "missingSplitsInstall")
    }

    private fun featureInstalled(featureInfo: Feature.Info) {
        bottomNavigation?.menu?.removeItem(R.id.actionVideo)
        bottomNavigation?.menu?.removeItem(R.id.actionHome)
        bottomNavigation?.inflateMenu(R.menu.navigation_full)
        bottomNavigation?.selectedItemId = featureInfo.actionId
    }

    private fun missingSplitsInstalled() {
        bottomNavigation?.menu?.removeItem(R.id.actionHome)
        bottomNavigation?.inflateMenu(R.menu.navigation_only_home)
        goToFeatureEntryPoint(R.id.actionHome)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == bottomNavigation?.selectedItemId) {
            Toast.makeText(this, "You are already on \"${item.title}\"", Toast.LENGTH_SHORT).show()
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
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            if (!mainViewModel.isFeatureInstalled(R.id.actionVideo)) {
                bottomNavigation.menu.removeItem(R.id.actionVideo)
            }
            if (mainViewModel.isFeatureInstalled(R.id.actionHome)) {
                goToFeatureEntryPoint(R.id.actionHome)
            } else {
                bottomNavigation.menu.removeItem(R.id.actionHome)
                launchMissingSplitsDialog()
            }
        }

        mainViewModel.featureInstalled.observe(this, ::featureInstalled)
        mainViewModel.missingSplitsInstalled.observe(this, ::missingSplitsInstalled)

        bottomNavigation?.setOnNavigationItemSelectedListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MISSING_SPLITS_INSTALL_REQUEST_CODE
                && data?.hasExtra(GlobalSplitInstallConfirmResult.EXTRA_RESULT) == true) {
            val confirmResult = data.getIntExtra(
                    GlobalSplitInstallConfirmResult.EXTRA_RESULT,
                    GlobalSplitInstallConfirmResult.RESULT_DENIED
            )
            dismissInstallMissingSplitsDialog()
            if (confirmResult == GlobalSplitInstallConfirmResult.RESULT_CONFIRMED) {
                launchMissingSplitsDialog()
            }
        }
    }
}