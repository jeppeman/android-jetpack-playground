package com.jeppeman.jetpackplayground.home.presentation

import android.content.Intent
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.jeppeman.jetpackplayground.common.presentation.BaseFragment
import com.jeppeman.jetpackplayground.common.presentation.extensions.setVisible
import com.jeppeman.jetpackplayground.common_features.FeatureManager
import com.jeppeman.jetpackplayground.common_features.HomeFeature
import com.jeppeman.jetpackplayground.common_features.VideoFeature
import com.jeppeman.jetpackplayground.home.R
import com.jeppeman.jetpackplayground.home.platform.HomeFeatureImpl
import com.jeppeman.jetpackplayground.home.presentation.download.DownloadActivity
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

fun createHomeFragment(): HomeFragment = HomeFragment()

class HomeFragment : BaseFragment<HomeViewModel>() {
    override val layoutRes = R.layout.fragment_home

    @Inject
    override lateinit var viewModel: HomeViewModel
    @Inject
    lateinit var featureManager: FeatureManager

    override fun inject() {
        ((context?.applicationContext as? HomeFeature.InjectionProvider)?.homeFeature as? HomeFeatureImpl)
                ?.homeComponent
                ?.homeFragmentComponentFactory
                ?.create(this)
                ?.inject(this)
    }

    private fun loopAnimation(imageView: ImageView?, animatedVectorDrawable: AnimatedVectorDrawable?) {
        animatedVectorDrawable?.registerAnimationCallback(object : Animatable2.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                animatedVectorDrawable.unregisterAnimationCallback(this)
                context?.let {
                    imageView?.setImageDrawable(null)
                    val newAvd = ContextCompat.getDrawable(it, R.drawable.ic_home_anim) as? AnimatedVectorDrawable
                    imageView?.setImageDrawable(newAvd)
                    loopAnimation(imageView, newAvd)
                }
            }
        })
        animatedVectorDrawable?.start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loopAnimation(homeIcon, homeIcon?.drawable as? AnimatedVectorDrawable)
        view.postDelayed({
            loopAnimation(homeIcon2, homeIcon2?.drawable as? AnimatedVectorDrawable)
        }, 2300)
        videoButton?.setOnClickListener { startActivity(Intent(requireContext(), DownloadActivity::class.java)) }
        if (featureManager.isFeatureInstalled(VideoFeature::class)) {
            videoButton?.setVisible(false)
            homeText?.setText(com.jeppeman.jetpackplayground.R.string.home_text_installed)
        }
    }
}