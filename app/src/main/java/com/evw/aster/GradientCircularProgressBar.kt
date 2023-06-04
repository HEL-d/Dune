package com.evw.aster

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat

class GradientCircularProgressBar : View {
    private var mPaddingPx = 0f
    private var mBodyStrokeWidthPx = 0f
    private var mGlowStrokeWidthPx = 0f
    private var mPaintBody: Paint? = null
    private var mPaintGlow: Paint? = null
    private var mRect: RectF? = null
    private var mBodyGradient: SweepGradient? = null
    private lateinit var mBodyGradientFromToColors: IntArray
    private lateinit var mGradientFromToPositions: FloatArray
    private var mGlowGradient: SweepGradient? = null
    private lateinit var mGlowGradientFromToColors: IntArray

    //additional offset caused by rendered cup at start
    private var mCupAdditionalOffset = 0
    private var mRotationAnimator: ObjectAnimator? = null
    private val rotation = 0f

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        if (!mRotationAnimator!!.isStarted) {
            mRotationAnimator!!.start()
        }
        canvas.rotate(rotation, (width / 2).toFloat(), (height / 2).toFloat())
        val left = mPaddingPx
        val top = mPaddingPx
        val right = width - mPaddingPx
        val bottom = height - mPaddingPx
        mRect!![left, top, right] = bottom
        canvas.drawArc(
            mRect!!,
            mCupAdditionalOffset.toFloat(),
            BODY_LENGTH.toFloat(),
            false,
            mPaintGlow!!
        )
        canvas.drawArc(
            mRect!!,
            mCupAdditionalOffset.toFloat(),
            BODY_LENGTH.toFloat(),
            false,
            mPaintBody!!
        )
    }

    override fun onSizeChanged(newWidth: Int, newHeight: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(newWidth, newHeight, oldw, oldh)
        val centerX = (newWidth / 2).toFloat()
        val centerY = (newHeight / 2).toFloat()
        mBodyGradient = SweepGradient(
            centerX, centerY,
            mBodyGradientFromToColors, mGradientFromToPositions
        )
        mGlowGradient = SweepGradient(
            centerX, centerY,
            mGlowGradientFromToColors, mGradientFromToPositions
        )
        mPaintBody!!.shader = mBodyGradient
        mPaintGlow!!.shader = mGlowGradient
        val cupRadius = mPaddingPx
        val arcRadius = newWidth / 2f - mPaddingPx
        mCupAdditionalOffset = computeOffset(cupRadius, arcRadius)
    }

    private fun init() {
        //disable hardware acceleration
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        val bodyColor = ResourcesCompat.getColor(
            resources,
            R.color.Aster_neo, null
        )
        val glowColor = ResourcesCompat.getColor(
            resources,
            R.color.Aster_neo, null
        )
        val displayMetrics = resources.displayMetrics
        mBodyStrokeWidthPx = TypedValue
            .applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                BODY_STROKE_WIDTH.toFloat(),
                displayMetrics
            )
        mPaddingPx = TypedValue
            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, PADDING.toFloat(), displayMetrics)
        mGlowStrokeWidthPx = TypedValue
            .applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                GLOW_STROKE_WIDTH.toFloat(),
                displayMetrics
            )
        mRect = RectF()
        mPaintBody = Paint()
        mPaintBody!!.isAntiAlias = true
        mPaintBody!!.color = bodyColor
        mPaintBody!!.strokeWidth = mBodyStrokeWidthPx
        mPaintBody!!.style = Paint.Style.STROKE
        mPaintBody!!.strokeJoin = Paint.Join.ROUND
        mPaintBody!!.strokeCap = Paint.Cap.ROUND
        mPaintGlow = Paint()
        mPaintGlow!!.set(mPaintBody)
        mPaintGlow!!.color = glowColor
        mPaintGlow!!.strokeWidth = mGlowStrokeWidthPx
        mPaintGlow!!.maskFilter = BlurMaskFilter(mBodyStrokeWidthPx, BlurMaskFilter.Blur.NORMAL)
        mBodyGradientFromToColors = intArrayOf(Color.TRANSPARENT, bodyColor)
        mGradientFromToPositions = floatArrayOf(0f, NORMALIZED_GRADIENT_LENGTH)
        mGlowGradientFromToColors = intArrayOf(Color.TRANSPARENT, glowColor)
        mRotationAnimator = createRotateAnimator()
    }

    private fun createRotateAnimator(): ObjectAnimator {
        val rotateAnimator = ObjectAnimator.ofFloat(
            this,
            ROTATION_PROPERTY_NAME, 0f, ROTATION_LENGTH.toFloat()
        )
        rotateAnimator.duration = ROTATION_DURATION.toLong()
        rotateAnimator.interpolator = LinearInterpolator()
        rotateAnimator.repeatMode = ValueAnimator.RESTART
        rotateAnimator.repeatCount = ValueAnimator.INFINITE
        return rotateAnimator
    }

    companion object {
        private const val BODY_STROKE_WIDTH = 12
        private const val GLOW_STROKE_WIDTH = BODY_STROKE_WIDTH * 3

        // padding has to be the half size of the stroke width plus blur filter radius
        private const val PADDING = GLOW_STROKE_WIDTH / 2 + BODY_STROKE_WIDTH
        private const val BODY_LENGTH = 270
        private const val NORMALIZED_GRADIENT_LENGTH = BODY_LENGTH / 720f
        private const val ROTATION_DURATION = 1200
        private const val ROTATION_LENGTH = 360
        private const val ROTATION_PROPERTY_NAME = "rotation"
        private fun computeOffset(capRadius: Float, arcRadius: Float): Int {
            val sinus = capRadius / arcRadius
            val degreeRad = Math.asin(sinus.toDouble())
            val degree = Math.toDegrees(degreeRad)
            return Math.ceil(degree).toInt()
        }
    }
}