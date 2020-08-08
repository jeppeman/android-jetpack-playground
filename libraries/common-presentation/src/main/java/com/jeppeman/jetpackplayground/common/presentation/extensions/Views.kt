package com.jeppeman.jetpackplayground.common.presentation.extensions

import android.animation.ObjectAnimator
import android.graphics.SurfaceTexture
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.jeppeman.jetpackplayground.common.presentation.GlideApp
import kotlin.math.roundToInt

fun ImageView.setImageUrl(imageUrl: String?) {
    if (imageUrl == null) {
        setImageDrawable(null)
    } else {
        GlideApp.with(context)
                .load(imageUrl)
                .into(this)
    }
}

fun CollapsingToolbarLayout.setCollapsingToolbarFont(@FontRes resId: Int) {
    val typeface = ResourcesCompat.getFont(context, resId)
    apply {
        setCollapsedTitleTypeface(typeface)
        setExpandedTitleTypeface(typeface)
    }
}

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

private fun findViewWithTransitionNameTraversal(view: View, transitionName: String): View? {
    if (view.transitionName == transitionName) {
        return view
    }

    if (view is ViewGroup) {
        return (0 until view.childCount)
                .asSequence()
                .map(view::getChildAt)
                .mapNotNull { child -> child.findViewWithTransitionName(transitionName) }
                .firstOrNull()
    }

    return null
}

fun View.findViewWithTransitionName(transitionName: String): View? {
    return findViewWithTransitionNameTraversal(this, transitionName)
}

fun ProgressBar.animateBetween(from: Int, to: Int, duration: Long) {
    val animator = ObjectAnimator.ofInt(this, "progress", from, to)
    animator.interpolator = AccelerateInterpolator()
    animator.duration = duration
    animator.start()
}

fun TextView.animateProgress(from: Int, to: Int, duration: Long) {
    val animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            val total = to - from
            val current = from + total * interpolatedTime
            text = "${current.roundToInt()}%"
        }
    }

    this.clearAnimation()
    this.animation = animation
    animation.duration = duration
    animation.start()
}

inline fun TextureView.onSurfaceAvailable(crossinline onAvailable: (SurfaceTexture?) -> Unit) {
    this.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture) = true

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            onAvailable(surface)
        }
    }
}