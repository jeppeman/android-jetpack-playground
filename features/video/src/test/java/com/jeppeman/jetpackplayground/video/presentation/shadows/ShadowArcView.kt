package com.jeppeman.jetpackplayground.video.presentation.shadows

import com.jeppeman.jetpackplayground.video.presentation.widget.ArcView
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.annotation.RealObject
import org.robolectric.shadow.api.Shadow.directlyOn
import org.robolectric.shadows.ShadowView

@Implements(ArcView::class)
class ShadowArcView : ShadowView() {
    @RealObject
    private lateinit var realArcView: ArcView
    private val onLoadingCompleteListeners = mutableListOf<() -> Unit>()

    @set:Implementation
    var loading: Boolean = false
        set(value) {
            if (!value) {
                onLoadingCompleteListeners.forEach { listener -> listener() }
            }
            field = value
        }

    @Implementation
    fun registerLoadingCompleteListener(onLoadingComplete: () -> Unit) {
        onLoadingCompleteListeners.add(onLoadingComplete)
        directlyOn(realArcView, ArcView::class.java).registerLoadingCompleteListener(onLoadingComplete)
    }
}