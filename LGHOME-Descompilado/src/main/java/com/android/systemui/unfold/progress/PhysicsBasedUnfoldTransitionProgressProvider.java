package com.android.systemui.unfold.progress;

import android.util.Log;
import android.util.MathUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import com.android.systemui.unfold.updates.FoldStateProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: PhysicsBasedUnfoldTransitionProgressProvider.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\b\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003:\u0001)B\r\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\fH\u0016J\u0018\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u00102\u0006\u0010\u0019\u001a\u00020\bH\u0002J\b\u0010\u001a\u001a\u00020\u0015H\u0016J4\u0010\u001b\u001a\u00020\u00152\u0012\u0010\u001c\u001a\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\u001d0\u001d2\u0006\u0010\u001e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u001f\u001a\u00020\u0010H\u0016J\u0010\u0010 \u001a\u00020\u00152\u0006\u0010!\u001a\u00020\"H\u0016J\u0010\u0010#\u001a\u00020\u00152\u0006\u0010$\u001a\u00020\u0010H\u0016J\b\u0010%\u001a\u00020\u0015H\u0002J\u0010\u0010&\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\fH\u0016J\u0010\u0010'\u001a\u00020\u00152\u0006\u0010(\u001a\u00020\u0010H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u0010@BX\u0082\u000e¢\u0006\b\n\u0000\"\u0004\b\u0012\u0010\u0013¨\u0006*"}, d2 = {"Lcom/android/systemui/unfold/progress/PhysicsBasedUnfoldTransitionProgressProvider;", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;", "Lcom/android/systemui/unfold/updates/FoldStateProvider$FoldUpdatesListener;", "Landroidx/dynamicanimation/animation/DynamicAnimation$OnAnimationEndListener;", "foldStateProvider", "Lcom/android/systemui/unfold/updates/FoldStateProvider;", "(Lcom/android/systemui/unfold/updates/FoldStateProvider;)V", "isAnimatedCancelRunning", "", "isTransitionRunning", "listeners", "", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider$TransitionProgressListener;", "springAnimation", "Landroidx/dynamicanimation/animation/SpringAnimation;", "value", "", "transitionProgress", "setTransitionProgress", "(F)V", "addCallback", "", "listener", "cancelTransition", "endValue", "animate", "destroy", "onAnimationEnd", "animation", "Landroidx/dynamicanimation/animation/DynamicAnimation;", "canceled", "velocity", "onFoldUpdate", "update", "", "onHingeAngleUpdate", "angle", "onStartTransition", "removeCallback", "startTransition", "startValue", "AnimationProgressProperty", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class PhysicsBasedUnfoldTransitionProgressProvider implements UnfoldTransitionProgressProvider, FoldStateProvider.FoldUpdatesListener, DynamicAnimation.OnAnimationEndListener {
    private final FoldStateProvider foldStateProvider;
    private boolean isAnimatedCancelRunning;
    private boolean isTransitionRunning;
    private final List<UnfoldTransitionProgressProvider.TransitionProgressListener> listeners;
    private final SpringAnimation springAnimation;
    private float transitionProgress;

    public PhysicsBasedUnfoldTransitionProgressProvider(FoldStateProvider foldStateProvider) {
        Intrinsics.checkNotNullParameter(foldStateProvider, "foldStateProvider");
        this.foldStateProvider = foldStateProvider;
        SpringAnimation springAnimation = new SpringAnimation(this, AnimationProgressProperty.INSTANCE);
        springAnimation.addEndListener(this);
        this.springAnimation = springAnimation;
        this.listeners = new ArrayList();
        foldStateProvider.addCallback(this);
        foldStateProvider.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void setTransitionProgress(float f) {
        if (this.isTransitionRunning) {
            Iterator<T> it = this.listeners.iterator();
            while (it.hasNext()) {
                ((UnfoldTransitionProgressProvider.TransitionProgressListener) it.next()).onTransitionProgress(f);
            }
        }
        this.transitionProgress = f;
    }

    @Override // com.android.systemui.unfold.UnfoldTransitionProgressProvider
    public void destroy() {
        this.foldStateProvider.stop();
    }

    @Override // com.android.systemui.unfold.updates.FoldStateProvider.FoldUpdatesListener
    public void onHingeAngleUpdate(float angle) {
        if (!this.isTransitionRunning || this.isAnimatedCancelRunning) {
            return;
        }
        this.springAnimation.animateToFinalPosition(MathUtils.saturate(angle / 165.0f));
    }

    @Override // com.android.systemui.unfold.updates.FoldStateProvider.FoldUpdatesListener
    public void onFoldUpdate(int update) {
        if (update != 1) {
            if (update == 2) {
                startTransition(0.0f);
                if (this.foldStateProvider.isFinishedOpening()) {
                    cancelTransition(1.0f, true);
                }
            } else if (update == 3 || update == 4) {
                if (this.isTransitionRunning) {
                    cancelTransition(1.0f, true);
                }
            } else if (update == 5) {
                cancelTransition(0.0f, false);
            }
        } else if (this.isTransitionRunning) {
            if (this.isAnimatedCancelRunning) {
                this.isAnimatedCancelRunning = false;
            }
        } else {
            startTransition(1.0f);
        }
        Log.d("PhysicsBasedUnfoldTransitionProgressProvider", Intrinsics.stringPlus("onFoldUpdate = ", Integer.valueOf(update)));
    }

    private final void cancelTransition(float endValue, boolean animate) {
        if (this.isTransitionRunning && animate) {
            this.isAnimatedCancelRunning = true;
            this.springAnimation.animateToFinalPosition(endValue);
            return;
        }
        setTransitionProgress(endValue);
        this.isAnimatedCancelRunning = false;
        this.isTransitionRunning = false;
        this.springAnimation.cancel();
        Iterator<T> it = this.listeners.iterator();
        while (it.hasNext()) {
            ((UnfoldTransitionProgressProvider.TransitionProgressListener) it.next()).onTransitionFinished();
        }
        Log.d("PhysicsBasedUnfoldTransitionProgressProvider", "onTransitionFinished");
    }

    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
    public void onAnimationEnd(DynamicAnimation<? extends DynamicAnimation<?>> animation, boolean canceled, float value, float velocity) {
        Intrinsics.checkNotNullParameter(animation, "animation");
        if (this.isAnimatedCancelRunning) {
            cancelTransition(value, false);
        }
    }

    private final void onStartTransition() {
        Iterator<T> it = this.listeners.iterator();
        while (it.hasNext()) {
            ((UnfoldTransitionProgressProvider.TransitionProgressListener) it.next()).onTransitionStarted();
        }
        this.isTransitionRunning = true;
        Log.d("PhysicsBasedUnfoldTransitionProgressProvider", "onTransitionStarted");
    }

    private final void startTransition(float startValue) {
        if (!this.isTransitionRunning) {
            onStartTransition();
        }
        SpringAnimation springAnimation = this.springAnimation;
        SpringForce springForce = new SpringForce();
        springForce.setFinalPosition(startValue);
        springForce.setDampingRatio(1.0f);
        springForce.setStiffness(200.0f);
        springAnimation.setSpring(springForce);
        springAnimation.setMinimumVisibleChange(0.001f);
        springAnimation.setStartValue(startValue);
        springAnimation.setMinValue(0.0f);
        springAnimation.setMaxValue(1.0f);
        this.springAnimation.start();
    }

    /* JADX DEBUG: Method merged with bridge method: addCallback(Ljava/lang/Object;)V */
    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(UnfoldTransitionProgressProvider.TransitionProgressListener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        this.listeners.add(listener);
    }

    /* JADX DEBUG: Method merged with bridge method: removeCallback(Ljava/lang/Object;)V */
    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(UnfoldTransitionProgressProvider.TransitionProgressListener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        this.listeners.remove(listener);
    }

    /* JADX INFO: compiled from: PhysicsBasedUnfoldTransitionProgressProvider.kt */
    @Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\bÂ\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0002H\u0016J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0006\u001a\u00020\u00022\u0006\u0010\t\u001a\u00020\u0005H\u0016¨\u0006\n"}, d2 = {"Lcom/android/systemui/unfold/progress/PhysicsBasedUnfoldTransitionProgressProvider$AnimationProgressProperty;", "Landroidx/dynamicanimation/animation/FloatPropertyCompat;", "Lcom/android/systemui/unfold/progress/PhysicsBasedUnfoldTransitionProgressProvider;", "()V", "getValue", "", "provider", "setValue", "", "value", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
    private static final class AnimationProgressProperty extends FloatPropertyCompat<PhysicsBasedUnfoldTransitionProgressProvider> {
        public static final AnimationProgressProperty INSTANCE = new AnimationProgressProperty();

        private AnimationProgressProperty() {
            super("animation_progress");
        }

        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(PhysicsBasedUnfoldTransitionProgressProvider provider, float value) {
            Intrinsics.checkNotNullParameter(provider, "provider");
            provider.setTransitionProgress(value);
        }

        /* JADX DEBUG: Method merged with bridge method: getValue(Ljava/lang/Object;)F */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(PhysicsBasedUnfoldTransitionProgressProvider provider) {
            Intrinsics.checkNotNullParameter(provider, "provider");
            return provider.transitionProgress;
        }
    }
}
