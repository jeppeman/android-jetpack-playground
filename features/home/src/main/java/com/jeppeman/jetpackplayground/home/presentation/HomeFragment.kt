package com.jeppeman.jetpackplayground.home.presentation

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.jeppeman.jetpackplayground.common.presentation.BaseFragment
import com.jeppeman.jetpackplayground.home.R
import com.jeppeman.jetpackplayground.home.platform.homeComponent
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

fun createHomeFragment(): HomeFragment = HomeFragment()

class HomeFragment : BaseFragment<HomeViewModel>() {
    override val layoutRes = R.layout.fragment_home
    @Inject
    override lateinit var viewModel: HomeViewModel

    override fun inject() {
        homeComponent.homeFragmentComponentFactory
                .create(this)
                .inject(this)
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
    }
}