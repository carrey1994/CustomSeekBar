package com.example.viewdragplay

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.animation.*
import android.widget.FrameLayout
import androidx.customview.widget.ViewDragHelper


class CustomDragView : FrameLayout {

    private lateinit var viewDragHelper: ViewDragHelper

    private var start = 0
    private var end = 0

    init {
        minimumHeight = 300

    }


    private val fadeIn = AlphaAnimation(0f, 1f).apply {
        interpolator = DecelerateInterpolator() //add this
        duration = 500
    }

    private val fadeOut = AlphaAnimation(1f, 0f).apply {
        interpolator = AccelerateInterpolator() //and this
        startOffset = 500
        duration = 500
    }


    constructor(context: Context) : super(context) {
        setupDragHelper()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setupDragHelper()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setupDragHelper()
    }


//    constructor(context: Context?) : super(context) {
//        setupDragHelper()
//    }
//
//    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
//        setupDragHelper()
//    }
//
//    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
//        context,
//        attrs,
//        defStyleAttr
//    ) {
//        setupDragHelper()
//    }

    private val pointerView: PointerView
    private val toastView: ToastView
    private val frameLayout: FrameLayout
    private val highLightBar: HighLightBar

    init {
        setupDragHelper()
        val backgroundBar = BackgroundBar(context)
        addView(backgroundBar)

        highLightBar = HighLightBar(context)
        addView(highLightBar)

        pointerView = PointerView(context)

        toastView = ToastView(context)
        toastView.visibility = View.GONE

        frameLayout = FrameLayout(context)
        frameLayout.addView(pointerView)
        frameLayout.addView(toastView)

        addView(frameLayout)

    }

    private fun setupDragHelper() {
        viewDragHelper = ViewDragHelper.create(this, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                if (child is FrameLayout) {
                    toastView.visibility = View.VISIBLE
                }
                return child is FrameLayout
            }

            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                var finalLeft = left
                if (child is FrameLayout) {
                    if (left < 0)
                        finalLeft = 0
                    else if (left > width - 200)
                        finalLeft = width - 200
                    Log.e("OnProgress===", "$finalLeft   ${width - 200f}===")
                    val progress = (finalLeft / (width - 200f)) * 100

                    toastView.progress = progress
                    toastView.invalidate()

                    highLightBar.barLength = finalLeft
                    highLightBar.invalidate()


                }
                return finalLeft
            }

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                return 0
            }

        })
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev!!)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val animation = AnimationSet(false)

        if (event?.action == ACTION_UP) {
            animation.addAnimation(fadeOut)
            toastView.animation = animation
            toastView.startAnimation(animation)
            toastView.visibility = View.INVISIBLE
        } else if (event?.action == ACTION_DOWN) {
            animation.addAnimation(fadeIn)
            toastView.animation = animation
            toastView.startAnimation(animation)
        }

        viewDragHelper.processTouchEvent(event!!)
        return true
    }


    inner class BackgroundBar : View {
        constructor(context: Context?) : super(context)
        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
        constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
        )

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 10f
            color = Color.parseColor("#D3D0CB")
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 20f
            color = Color.parseColor("#D3D0CB")
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        @SuppressLint("DrawAllocation")
        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)

            canvas?.drawLine(0f + 100f, height / 2f, width - 100f, height / 2f, paint)

            val part = (width - 200) / 10
            for (i in 1..10) {
                canvas?.drawPoint((100 + i * part).toFloat(), height / 2f, dotPaint)
            }

        }

        override fun isFocused(): Boolean = false

//        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//            setMeasuredDimension(width - 200, 10)
//        }
    }

    inner class PointerView : View {
        constructor(context: Context?) : super(context)
        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
        constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
        )

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 66F
            color = Color.parseColor("#111D4A")
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        private val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 33F
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        @SuppressLint("DrawAllocation")
        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
            canvas?.drawPoint(0f + 100f, height / 2f, paint)
            canvas?.drawPoint(0f + 100f, height / 2f, whitePaint)
        }
    }

    inner class ToastView : View {
        constructor(context: Context?) : super(context)
        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
        constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
        )

        var progress = 0f

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 3F
            color = Color.parseColor("#B42419")
            style = Paint.Style.FILL
            strokeCap = Paint.Cap.ROUND
        }

        private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            strokeCap = Paint.Cap.ROUND
            textSize = 50f
            textAlign = Paint.Align.CENTER
        }

        @SuppressLint("DrawAllocation")
        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
            val path = Path()

            val x = 50f

            path.moveTo(100f, height / 2f - 220f)
            path.cubicTo(
                100f - 2 * x,
                height / 2f - 220f,
                100 - 2 * x,
                height / 2f - 120f,
                100f,
                height / 2f - 35f
            )

            path.moveTo(100f, height / 2f - 220f)
            path.cubicTo(
                100f + 2 * x,
                height / 2f - 220f,
                100f + 2 * x,
                height / 2f - 120f,
                100f,
                height / 2f - 35f
            )

            canvas?.drawPath(path, paint)
            canvas?.drawText("${progress.toInt()}%", 100f, height / 2f - 125f, textPaint)
        }

    }

    inner class HighLightBar : View {
        constructor(context: Context?) : super(context)
        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
        constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
        )

        var barLength = 0
        private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 20f
            color = Color.parseColor("#2E5266")
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }


        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
            canvas?.drawLine(0f + 100f, height / 2f, barLength + 100f, height / 2f, paint)

        }

        override fun isFocused(): Boolean = false

    }

}