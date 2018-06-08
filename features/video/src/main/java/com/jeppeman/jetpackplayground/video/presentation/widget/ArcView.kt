package com.jeppeman.jetpackplayground.video.presentation.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StyleRes
import androidx.core.animation.doOnEnd
import com.jeppeman.jetpackplayground.video.R
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class ArcView : View {
    private var isRunningLastLoadingIteration = false
    private var isDetached = false
    private val onLoadingCompleteListeners = mutableListOf<() -> Unit>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    var startAngle: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    var sweepAngle: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    var strokeWidth: Float = 0f
        set(value) {
            field = value
            paint.strokeWidth = value
            invalidate()
        }
    var tailLength: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    var noseLength: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    var strokeColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            paint.color = value
            invalidate()
        }
    var loading: Boolean = false
        set(value) {
            field = value
            if (value && !isAnimating) {
                runLoadingAnimation()
            }
        }
    var loadingEndAngle: Float = 0f
    var loadingEndRotation: Float = 0f
    var isAnimating = false
        private set

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet, @StyleRes defStyle: Int) : super(context, attributeSet, defStyle) {
        init(attributeSet)
    }

    private fun init(attributeSet: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ArcView)
        startAngle = typedArray.getFloat(R.styleable.ArcView_startAngle, 0f)
        sweepAngle = typedArray.getFloat(R.styleable.ArcView_sweepAngle, 0f)
        loadingEndAngle = typedArray.getFloat(R.styleable.ArcView_loadingEndAngle, 0f)
        loadingEndRotation = typedArray.getFloat(R.styleable.ArcView_loadingEndRotation, 0f)
        strokeWidth = typedArray.getDimensionPixelSize(R.styleable.ArcView_strokeWidth, 0).toFloat()
        strokeColor = typedArray.getColor(R.styleable.ArcView_strokeColor, Color.TRANSPARENT)
        tailLength = typedArray.getDimensionPixelSize(R.styleable.ArcView_tailLength, 0).toFloat()
        noseLength = typedArray.getDimensionPixelSize(R.styleable.ArcView_noseLength, 0).toFloat()
        typedArray.recycle()
    }

    private fun afterLoadingFinished() {
        onLoadingCompleteListeners.forEach { listener -> listener() }
        isRunningLastLoadingIteration = false
        startAngle = loadingEndAngle
        rotation = loadingEndRotation
        isAnimating = false
    }

    private fun startAnimation(nextRotation: Float, nextStartAngle: Float, nextSweepAngle: Float) {
        val arcView = this
        AnimatorSet().apply {
            duration = 750
            doOnEnd {
                runLoadingAnimation()
            }

            playTogether(
                    ObjectAnimator.ofFloat(arcView, "rotation", rotation, nextRotation),
                    ObjectAnimator.ofFloat(arcView, "sweepAngle", sweepAngle, nextSweepAngle),
                    ObjectAnimator.ofFloat(arcView, "startAngle", startAngle, nextStartAngle)
            )
            start()
        }
    }

    private fun runLastLoadingIteration() {
        var nextZeroRotation = rotation.roundToInt()
        while (nextZeroRotation % 360 != 0) {
            nextZeroRotation++
        }
        var nextZeroAngle = startAngle.roundToInt()
        while (nextZeroAngle % 360 != 0) {
            nextZeroAngle++
        }
        startAnimation(
                nextRotation = nextZeroRotation + loadingEndRotation,
                nextStartAngle = nextZeroAngle + loadingEndAngle,
                nextSweepAngle = if (sweepAngle > 0f) 0f else 360f
        )
        isRunningLastLoadingIteration = true
    }

    private fun runOrdinaryLoadingIteration() {
        val nextRotation = rotation + 180 + Math.random().toFloat() * 40
        val nextStartAngle = startAngle + 360
        val nextSweepAngle = if (sweepAngle > 0) 0f else 360f
        startAnimation(nextRotation, nextStartAngle, nextSweepAngle)
    }

    private fun runLoadingAnimation() {
        isAnimating = true
        when {
            isDetached -> return
            isRunningLastLoadingIteration -> afterLoadingFinished()
            !loading -> runLastLoadingIteration()
            else -> runOrdinaryLoadingIteration()
        }
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = desiredSize
            if (specMode == MeasureSpec.AT_MOST) {
                result = result.coerceAtMost(specSize)
            }
        }

        return result
    }

    fun registerLoadingCompleteListener(onLoadingComplete: () -> Unit) {
        onLoadingCompleteListeners.add(onLoadingComplete)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isDetached = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        setMeasuredDimension(measureDimension(desiredWidth, widthMeasureSpec),
                measureDimension(desiredHeight, heightMeasureSpec))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val halfStrokeWidth = strokeWidth / 2f
        val aspect = width / height.toFloat()
        val arcRight = (if (aspect < 1) width.toFloat() else width / aspect) - halfStrokeWidth
        val arcBottom = (if (aspect < 1) height * aspect else height.toFloat()) - halfStrokeWidth
        val half = width / 2f
        val startAngleRadians = Math.toRadians(startAngle.toDouble())
        val endAngleRadians = Math.toRadians(startAngle + sweepAngle.toDouble())

        // Calculate tail
        val tailX = (half + cos(startAngleRadians) * half).toFloat()
        val tailY = (half + sin(startAngleRadians) * half).toFloat()
        val tailAngle = Math.toRadians(-(180 - (90 + startAngle)).toDouble())
        val tailX2 = (tailX + cos(tailAngle) * tailLength).toFloat()
        val tailY2 = (tailY + sin(tailAngle) * tailLength).toFloat()
        val tailOffsetX = (cos(startAngleRadians) * halfStrokeWidth).toFloat()
        val tailOffsetY = (sin(startAngleRadians) * halfStrokeWidth).toFloat()

        // Calculate nose
        val noseX = (half + cos(endAngleRadians) * half).toFloat()
        val noseY = (half + sin(endAngleRadians) * half).toFloat()
        val noseAngle = Math.toRadians(-(180 - (90 + startAngle + sweepAngle)).toDouble())
        val noseX2 = (noseX - cos(noseAngle) * noseLength).toFloat()
        val noseY2 = (noseY - sin(noseAngle) * noseLength).toFloat()
        val noseOffsetX = (cos(endAngleRadians) * halfStrokeWidth).toFloat()
        val noseOffsetY = (sin(endAngleRadians) * halfStrokeWidth).toFloat()

        canvas?.apply {
            // Draw arc
            drawArc(
                    halfStrokeWidth,
                    halfStrokeWidth,
                    arcRight,
                    arcBottom,
                    startAngle,
                    sweepAngle,
                    false,
                    paint
            )

            // Draw nose
            drawLine(
                    noseX - noseOffsetX,
                    noseY - noseOffsetY,
                    noseX2 - noseOffsetX,
                    noseY2 - noseOffsetY,
                    paint
            )

            // Draw tail
            drawLine(
                    tailX - tailOffsetX,
                    tailY - tailOffsetY,
                    tailX2 - tailOffsetX,
                    tailY2 - tailOffsetY,
                    paint
            )
        }
    }
}