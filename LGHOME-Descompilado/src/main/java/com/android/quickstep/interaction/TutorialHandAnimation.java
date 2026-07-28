package com.android.quickstep.interaction;

import android.content.Context;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import com.android.quickstep.interaction.TutorialController;
import com.lge.launcher3.R;
import java.time.Duration;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
final class TutorialHandAnimation {
    private static final Duration ANIMATION_START_DELAY = Duration.ofMillis(300);
    private final AnimatedVectorDrawable mGestureAnimation;
    private final ImageView mHandCoachingView;

    TutorialHandAnimation(Context context, View rootView, int resId) {
        this.mHandCoachingView = (ImageView) rootView.findViewById(R.id.gesture_tutorial_fragment_hand_coaching);
        this.mGestureAnimation = (AnimatedVectorDrawable) ContextCompat.getDrawable(context, resId);
    }

    void startLoopedAnimation(TutorialController.TutorialType tutorialType) {
        this.mHandCoachingView.setVisibility(0);
        if (this.mGestureAnimation.isRunning()) {
            stop();
        }
        this.mGestureAnimation.clearAnimationCallbacks();
        this.mGestureAnimation.registerAnimationCallback(new Animatable2.AnimationCallback() { // from class: com.android.quickstep.interaction.TutorialHandAnimation.1
            @Override // android.graphics.drawable.Animatable2.AnimationCallback
            public void onAnimationEnd(Drawable drawable) {
                super.onAnimationEnd(drawable);
                TutorialHandAnimation.this.mGestureAnimation.start();
            }
        });
        start(tutorialType);
    }

    private void start(TutorialController.TutorialType tutorialType) {
        this.mHandCoachingView.setRotationY(tutorialType == TutorialController.TutorialType.LEFT_EDGE_BACK_NAVIGATION ? 180.0f : 0.0f);
        this.mHandCoachingView.setImageDrawable(this.mGestureAnimation);
        ImageView imageView = this.mHandCoachingView;
        final AnimatedVectorDrawable animatedVectorDrawable = this.mGestureAnimation;
        Objects.requireNonNull(animatedVectorDrawable);
        imageView.postDelayed(new Runnable() { // from class: com.android.quickstep.interaction.-$$Lambda$TutorialHandAnimation$jl0Y5z1foHx0fHWXtFr24Ou2UEU
            @Override // java.lang.Runnable
            public final void run() {
                animatedVectorDrawable.start();
            }
        }, ANIMATION_START_DELAY.toMillis());
    }

    void stop() {
        this.mGestureAnimation.clearAnimationCallbacks();
        this.mGestureAnimation.stop();
    }
}
