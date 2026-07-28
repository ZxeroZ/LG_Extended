package com.android.launcher3.anim;

import android.content.Context;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.launcher3.util.DynamicResource;
import com.android.systemui.plugins.ResourceProvider;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class FlingSpringAnim {
    private static final float FLING_FRICTION = 1.5f;
    private static final float SPRING_DAMPING = 0.8f;
    private static final float SPRING_STIFFNESS = 200.0f;
    private final FlingAnimation mFlingAnim;
    private SpringAnimation mSpringAnim;
    private float mTargetPosition;

    public <K> FlingSpringAnim(final K object, Context context, final FloatPropertyCompat<K> property, float startPosition, float targetPosition, float startVelocity, float minVisChange, float minValue, float maxValue, final float springVelocityFactor, final DynamicAnimation.OnAnimationEndListener onEndListener) {
        ResourceProvider resourceProviderProvider = DynamicResource.provider(context);
        final float f = resourceProviderProvider.getFloat(R.dimen.swipe_up_rect_xy_damping_ratio);
        final float f2 = resourceProviderProvider.getFloat(R.dimen.swipe_up_rect_xy_stiffness);
        FlingAnimation maxValue2 = new FlingAnimation(object, property).setFriction(resourceProviderProvider.getFloat(R.dimen.swipe_up_rect_xy_fling_friction)).setMinimumVisibleChange(minVisChange).setStartVelocity(startVelocity).setMinValue(minValue).setMaxValue(maxValue);
        this.mFlingAnim = maxValue2;
        this.mTargetPosition = targetPosition;
        maxValue2.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: com.android.launcher3.anim.-$$Lambda$FlingSpringAnim$ky5ITErFhnJnGML5-vGXCIWN-28
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f3, float f4) {
                this.f$0.lambda$new$0$FlingSpringAnim(object, property, springVelocityFactor, f2, f, onEndListener, dynamicAnimation, z, f3, f4);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$FlingSpringAnim(Object obj, FloatPropertyCompat floatPropertyCompat, float f, float f2, float f3, DynamicAnimation.OnAnimationEndListener onAnimationEndListener, DynamicAnimation dynamicAnimation, boolean z, float f4, float f5) {
        SpringAnimation spring = new SpringAnimation(obj, (FloatPropertyCompat<Object>) floatPropertyCompat).setStartValue(f4).setStartVelocity(f5 * f).setSpring(new SpringForce(this.mTargetPosition).setStiffness(f2).setDampingRatio(f3));
        this.mSpringAnim = spring;
        spring.addEndListener(onAnimationEndListener);
        this.mSpringAnim.animateToFinalPosition(this.mTargetPosition);
    }

    public float getTargetPosition() {
        return this.mTargetPosition;
    }

    public void updatePosition(float startPosition, float targetPosition) {
        this.mFlingAnim.setMinValue(Math.min(startPosition, targetPosition)).setMaxValue(Math.max(startPosition, targetPosition));
        this.mTargetPosition = targetPosition;
        SpringAnimation springAnimation = this.mSpringAnim;
        if (springAnimation != null) {
            springAnimation.animateToFinalPosition(targetPosition);
        }
    }

    public void start() {
        this.mFlingAnim.start();
    }

    public void end() {
        this.mFlingAnim.cancel();
        if (this.mSpringAnim.canSkipToEnd()) {
            this.mSpringAnim.skipToEnd();
        }
    }
}
