package com.evw.aster;

import android.animation.Animator;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

public class GlowProgressbar extends View implements AnimatorCreator.AnimatorTargetInterface {

    private static final float BODY_STROKE_WIDTH = 7f;
    private static final float GLOWING_MULTIPLIER = 2;
    private static final float BLUR_MULTIPLIER = 1.5f;


    private float mPaddingPx;
    private Paint mBodyPaint;
    private Paint mGlowingPaint;
    private RectF mBounds;

    private float mStartAngle;
    private float mEndAngle = 270;

    private float mRotation;

    private Animator mAnimator;

    public GlowProgressbar(Context context) {
        super(context);
        init();
    }

    public GlowProgressbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GlowProgressbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!mAnimator.isRunning()) {
            mAnimator.start();
        }

        float start = mRotation + mStartAngle;
        float sweep = mEndAngle - mStartAngle;

        canvas.drawArc(mBounds, start, sweep, false, mGlowingPaint);
        canvas.drawArc(mBounds, start, sweep, false, mBodyPaint);
    }

    //region AnimatorTargetInterface implementation
    @Override
    public void setStartAngle(float startAngle) {
        mStartAngle = startAngle;
        invalidate();
    }

    @Override
    public void setEndAngle(float endAngle) {
        mEndAngle = endAngle;
        invalidate();
    }

    @Override
    public void setRotation(float rotation) {
        mRotation = rotation;
        invalidate();
    }
    //endregion

    @Override
    protected void onSizeChanged(int newW, int newH, int oldw, int oldh) {
        super.onSizeChanged(newW, newH, oldw, oldh);

        mBounds.set(mPaddingPx, mPaddingPx,
                newW - mPaddingPx, newH - mPaddingPx);
    }

    private static float convertDpToPixel(float valueDp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueDp,
                displayMetrics);
    }

    private static Paint createBodyPaint(Context context, float strokeWidth) {
        int bodyColor = ResourcesCompat
                .getColor(context.getResources(), R.color.Aster_neo, null);

        Paint bodyPaint = new Paint();
        bodyPaint.setAntiAlias(true);
        bodyPaint.setColor(bodyColor);
        bodyPaint.setStrokeWidth(strokeWidth);
        bodyPaint.setStyle(Paint.Style.STROKE);
        bodyPaint.setStrokeJoin(Paint.Join.ROUND);
        bodyPaint.setStrokeCap(Paint.Cap.ROUND);
        return bodyPaint;
    }

    private static Paint createGlowingPaint(Context context,
                                            float strokeWidth,
                                            float blurMaskRadius) {
        int glowColor = ResourcesCompat
                .getColor(context.getResources(), R.color.Aster_neo, null);

        Paint glowingPaint = new Paint();
        glowingPaint.setAntiAlias(true);
        glowingPaint.setColor(glowColor);
        glowingPaint.setMaskFilter(new BlurMaskFilter(blurMaskRadius, BlurMaskFilter.Blur.NORMAL));
        glowingPaint.setStrokeWidth(strokeWidth);
        glowingPaint.setStyle(Paint.Style.STROKE);
        glowingPaint.setStrokeJoin(Paint.Join.ROUND);
        glowingPaint.setStrokeCap(Paint.Cap.ROUND);
        return glowingPaint;
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        float bodyStrokeWidthPx = convertDpToPixel(BODY_STROKE_WIDTH, getContext());

        float glowStrokeWidthPx = bodyStrokeWidthPx * GLOWING_MULTIPLIER;
        float blurRadiusPx = bodyStrokeWidthPx * BLUR_MULTIPLIER;

        mPaddingPx = glowStrokeWidthPx / 2 + blurRadiusPx;

        mBodyPaint = createBodyPaint(getContext(), bodyStrokeWidthPx);
        mGlowingPaint = createGlowingPaint(getContext(), glowStrokeWidthPx,
                blurRadiusPx);

        mBounds = new RectF();

        mAnimator = createAnimator();
    }

    private Animator createAnimator() {
        return AnimatorCreator.create(this);
    }
}
