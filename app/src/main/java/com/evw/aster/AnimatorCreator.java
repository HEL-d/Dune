package com.evw.aster;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import androidx.core.view.animation.PathInterpolatorCompat;

public class AnimatorCreator {
    private static final int SWEEP_DURATION = 1333;
    private static final int ROTATION_DURATION = 6665;
    private static final float END_ANGLE_MAX = 360;
    private static final float START_ANGLE_MAX = END_ANGLE_MAX - 1;
    private static final int ROTATION_END_ANGLE = 719;

    private AnimatorCreator() {
        // we don't need an instance of this class
    }


    public static Animator create(AnimatorTargetInterface target) {
        AnimatorSet animatorSet = new AnimatorSet();
        Animator startAngleAnimator = createStartAngleAnimator(target);
        Animator sweepAngleAnimator = createSweepAngleAnimator(target);
        Animator rotationAnimator = createAnimationAnimator(target);

        animatorSet.playTogether(startAngleAnimator,
                sweepAngleAnimator,
                rotationAnimator);

        return animatorSet;
    }

    private static Animator createAnimationAnimator(AnimatorTargetInterface target) {
        ObjectAnimator rotateAnimator =
                ObjectAnimator
                        .ofFloat(target, AnimatorTargetInterface.ROTATION,
                                0, ROTATION_END_ANGLE);

        rotateAnimator.setDuration(ROTATION_DURATION);
        rotateAnimator.setRepeatMode(ValueAnimator.RESTART);
        rotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimator.setInterpolator(new LinearInterpolator());

        return rotateAnimator;
    }

    private static Animator createStartAngleAnimator(AnimatorTargetInterface target) {
        ObjectAnimator animator =
                ObjectAnimator
                        .ofFloat(target, target.START_ANGLE,
                                0f, START_ANGLE_MAX);

        animator.setDuration(SWEEP_DURATION);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(createStartInterpolator());

        return animator;
    }

    private static Animator createSweepAngleAnimator(AnimatorTargetInterface target) {
        ObjectAnimator animator
                = ObjectAnimator
                .ofFloat(target,
                        target.END_ANGLE, 0f,
                        END_ANGLE_MAX);

        animator.setDuration(SWEEP_DURATION);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(createEndInterpolator());

        return animator;
    }

    private static Interpolator createStartInterpolator() {
        Path path = new Path();
        path.cubicTo(0.3f, 0f, 0.1f, 0.75f, 0.5f, 0.85f);
        path.lineTo(1f, 1f);
        return PathInterpolatorCompat.create(path);
    }


    private static Interpolator createEndInterpolator() {
        Path path = new Path();
        path.lineTo(0.5f, 0.1f);
        path.cubicTo(0.7f, 0.15f, 0.6f, 0.75f, 1f, 1f);
        return PathInterpolatorCompat.create(path);
    }

    interface AnimatorTargetInterface {

        String START_ANGLE = "startAngle";
        String END_ANGLE = "endAngle";
        String ROTATION = "rotation";

        void setStartAngle(float startAngle);

        void setEndAngle(float endAngle);

        void setRotation(float rotation);
    }
}
