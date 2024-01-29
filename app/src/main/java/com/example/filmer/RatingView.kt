package com.example.filmer

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import kotlin.math.min

class RatingView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    private val oval = RectF()

    private var radius: Float = 0f
    private var centerX: Float = 0f
    private var centerY: Float = 0f

    private var stroke = 10f

    private var progress = 0
    private var animStrokeProgress = 0

    private var scaleSize = 40f

    private lateinit var strokePaint: Paint
    private lateinit var digitPaint: Paint
    private lateinit var circlePaint: Paint

    private var isAnimated = false
    private lateinit var progressAnimation: ValueAnimator

    init {
        val a = context.theme.obtainStyledAttributes(attributeSet, R.styleable.RatingView, 0, 0)
        try {
            stroke = a.getFloat(R.styleable.RatingView_stroke, stroke)
            progress = a.getInt(R.styleable.RatingView_progress, progress)
        } finally {
            a.recycle()
        }

        scaleSize = min(width, height).toFloat()
        initPaint()

        progressAnimation = ValueAnimator.ofInt(0, progress).apply {
            duration = 800
            interpolator = OvershootInterpolator()
            addUpdateListener {
                animStrokeProgress = it.animatedValue as Int
                invalidate()
            }
        }
    }

    private fun initPaint() {

        strokePaint = Paint().apply {
            color = getPaintColor(progress)
            style = Paint.Style.STROKE
            strokeWidth = stroke
            isAntiAlias = true
        }
        digitPaint = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 2f
            textSize = scaleSize
            typeface = Typeface.SANS_SERIF
            color = getPaintColor(progress)
            isAntiAlias = true
        }
        circlePaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.DKGRAY
        }
    }

    private fun getPaintColor(progress: Int): Int = when (progress) {
        in 0..25 -> Color.parseColor("#e84258")
        in 26..50 -> Color.parseColor("#fd8060")
        in 51..75 -> Color.parseColor("#fee191")
        else -> Color.parseColor("#b0d8a4")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = if (width > height) {
            height.div(2f)
        } else {
            width.div(2f)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val chosenWidth = chooseDimension(widthMode, widthSize)
        val chosenHeight = chooseDimension(heightMode, heightSize)

        val minSide = min(chosenWidth, chosenHeight)
        centerX = minSide.div(2f)
        centerY = minSide.div(2f)

        setMeasuredDimension(minSide, minSide)
    }

    private fun chooseDimension(mode: Int, size: Int) =
        when (mode) {
            MeasureSpec.AT_MOST, MeasureSpec.EXACTLY -> size
            else -> 300
        }

    private fun drawRating(canvas: Canvas) {
        val scale = radius * 0.8f
        canvas.save()
        canvas.translate(centerX, centerY)
        oval.set(0f - scale, 0f - scale, scale, scale)
        canvas.drawCircle(0f, 0f, radius, circlePaint)
        canvas.drawArc(oval, -90f, convertProgressToDegrees(animStrokeProgress), false, strokePaint)
        canvas.restore()
    }

    private fun convertProgressToDegrees(progress: Int): Float = progress * 3.6f

    private fun drawText(canvas: Canvas) {
        val message = String.format("%.1f", progress / 10f)
        val widths = FloatArray(message.length)
        var advance = 0f

        digitPaint.getTextWidths(message, widths)
        for (width in widths) advance += width

        canvas.drawText(message, centerX - advance / 2, centerY + advance / 4, digitPaint)
    }

    override fun onDraw(canvas: Canvas) {
        if (!isAnimated) {
            progressAnimation.setIntValues(0, progress)
            progressAnimation.start()
            isAnimated = true
        }

        drawRating(canvas)
        drawText(canvas)
    }

    fun setProgress(pr: Int) {
        progress = pr
        initPaint()
        invalidate()
    }

}