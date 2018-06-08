package com.jeppeman.jetpackplayground.video.presentation.detail

import android.content.Context
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * An adapted version of [FloatingActionButton.Behavior] to work on a container of multiple
 * [FloatingActionButton]s
 */
class FabContainerBehavior(context: Context, attributeSet: AttributeSet) : CoordinatorLayout.Behavior<ViewGroup>(context, attributeSet) {

    private lateinit var tmpRect: Rect

    private fun updateFabVisibilityForAppBarLayout(parent: CoordinatorLayout, appBarLayout: AppBarLayout, child: ViewGroup): Boolean {
        if (!::tmpRect.isInitialized) {
            this.tmpRect = Rect()
        }

        val rect = requireNotNull(this.tmpRect)
        getDescendantRect(parent, appBarLayout, rect)
        (0 until child.childCount)
                .map(child::getChildAt)
                .filterIsInstance(FloatingActionButton::class.java)
                .filter { fab -> fab.tag != TAG_FAB_EXCLUDED }
                .forEach { fab ->
                    if (rect.bottom <= appBarLayout.minimumHeightForVisibleOverlappingContent) {
                        fab.hide()
                    } else {
                        fab.show()
                    }
                }

        return true
    }

    override fun onAttachedToLayoutParams(lp: CoordinatorLayout.LayoutParams) {
        if (lp.dodgeInsetEdges == 0) {
            lp.dodgeInsetEdges = 80
        }
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: ViewGroup, dependency: View): Boolean {
        if (dependency is AppBarLayout) {
            this.updateFabVisibilityForAppBarLayout(parent, dependency, child)
        }

        return false
    }


    override fun onLayoutChild(parent: CoordinatorLayout, child: ViewGroup, layoutDirection: Int): Boolean {
        val dependencies = parent.getDependencies(child)
        var i = 0

        val count = dependencies.size
        while (i < count) {
            val dependency = dependencies[i] as View
            if (dependency is AppBarLayout) {
                if (this.updateFabVisibilityForAppBarLayout(parent, dependency, child)) {
                    break
                }
            }
            ++i
        }

        parent.onLayoutChild(child, layoutDirection)
        return true
    }

    companion object {
        const val TAG_FAB_EXCLUDED = "fab_excluded"
        private val matrix = ThreadLocal<Matrix>()
        private val rectF = ThreadLocal<RectF>()

        private fun offsetDescendantRect(parent: ViewGroup, descendant: View, rect: Rect) {
            var m = matrix.get()
            if (m == null) {
                m = Matrix()
                matrix.set(m)
            } else {
                m.reset()
            }

            offsetDescendantMatrix(parent, descendant, m)
            var rectF = rectF.get()
            if (rectF == null) {
                rectF = RectF()
                rectF.set(rectF)
            }

            rectF.set(rect)
            m.mapRect(rectF)
            rect.set((rectF.left + 0.5f).toInt(), (rectF.top + 0.5f).toInt(), (rectF.right + 0.5f).toInt(), (rectF.bottom + 0.5f).toInt())
        }

        private fun getDescendantRect(parent: ViewGroup, descendant: View, out: Rect) {
            out.set(0, 0, descendant.width, descendant.height)
            offsetDescendantRect(parent, descendant, out)
        }

        private fun offsetDescendantMatrix(target: ViewParent, view: View, m: Matrix) {
            val parent = view.parent
            if (parent is View && parent !== target) {
                val vp = parent as View
                offsetDescendantMatrix(target, vp, m)
                m.preTranslate((-vp.scrollX).toFloat(), (-vp.scrollY).toFloat())
            }

            m.preTranslate(view.left.toFloat(), view.top.toFloat())
            if (!view.matrix.isIdentity) {
                m.preConcat(view.matrix)
            }
        }
    }
}