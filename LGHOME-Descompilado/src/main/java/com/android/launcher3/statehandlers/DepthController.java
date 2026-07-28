package com.android.launcher3.statehandlers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.WallpaperManager;
import android.os.IBinder;
import android.util.FloatProperty;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewRootImpl;
import android.view.ViewTreeObserver;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.systemui.shared.system.BlurUtils;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.TransactionCompat;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class DepthController implements StateManager.StateHandler<LauncherState>, BaseActivity.MultiWindowModeChangedListener {
    public static final FloatProperty<DepthController> DEPTH = new FloatProperty<DepthController>("depth") { // from class: com.android.launcher3.statehandlers.DepthController.1
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(DepthController depthController, float depth) {
            depthController.setDepth(depth);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(DepthController depthController) {
            return Float.valueOf(depthController.mDepth);
        }
    };
    private float mDepth;
    private final Launcher mLauncher;
    private int mMaxBlurRadius;
    private View.OnAttachStateChangeListener mOnAttachListener;
    private SurfaceControl mSurface;
    private WallpaperManager mWallpaperManager;
    private final ViewTreeObserver.OnDrawListener mOnDrawListener = new AnonymousClass2();
    private boolean mIgnoreStateChangesDuringMultiWindowAnimation = false;

    public static class ClampedDepthProperty extends FloatProperty<DepthController> {
        private final float mMaxValue;
        private final float mMinValue;

        public ClampedDepthProperty(float minValue, float maxValue) {
            super("depthClamped");
            this.mMinValue = minValue;
            this.mMaxValue = maxValue;
        }

        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(DepthController depthController, float depth) {
            depthController.setDepth(Utilities.boundToRange(depth, this.mMinValue, this.mMaxValue));
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(DepthController depthController) {
            return Float.valueOf(depthController.mDepth);
        }
    }

    /* JADX INFO: renamed from: com.android.launcher3.statehandlers.DepthController$2, reason: invalid class name */
    class AnonymousClass2 implements ViewTreeObserver.OnDrawListener {
        AnonymousClass2() {
        }

        @Override // android.view.ViewTreeObserver.OnDrawListener
        public void onDraw() {
            final DragLayer dragLayer = DepthController.this.mLauncher.getDragLayer();
            ViewRootImpl viewRootImpl = dragLayer.getViewRootImpl();
            if (!DepthController.this.setSurface(viewRootImpl != null ? viewRootImpl.getSurfaceControl() : null)) {
                DepthController depthController = DepthController.this;
                depthController.setDepth(depthController.mDepth);
            }
            dragLayer.post(new Runnable() { // from class: com.android.launcher3.statehandlers.-$$Lambda$DepthController$2$UMyI_UhV-dHqi5c9S7U5f6DgMt0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onDraw$0$DepthController$2(dragLayer);
                }
            });
        }

        public /* synthetic */ void lambda$onDraw$0$DepthController$2(View view) {
            view.getViewTreeObserver().removeOnDrawListener(this);
        }
    }

    public DepthController(Launcher l) {
        this.mLauncher = l;
    }

    private void ensureDependencies() {
        if (this.mWallpaperManager == null) {
            this.mMaxBlurRadius = this.mLauncher.getResources().getInteger(R.integer.max_depth_blur_radius);
            this.mWallpaperManager = (WallpaperManager) this.mLauncher.getSystemService(WallpaperManager.class);
        }
        if (this.mLauncher.getRootView() == null || this.mOnAttachListener != null) {
            return;
        }
        this.mOnAttachListener = new View.OnAttachStateChangeListener() { // from class: com.android.launcher3.statehandlers.DepthController.3
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View view) {
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View view) {
                IBinder windowToken = DepthController.this.mLauncher.getRootView().getWindowToken();
                if (windowToken != null) {
                    DepthController.this.mWallpaperManager.setWallpaperZoomOut(windowToken, DepthController.this.mDepth);
                }
            }
        };
        this.mLauncher.getRootView().addOnAttachStateChangeListener(this.mOnAttachListener);
    }

    public void setActivityStarted(boolean isStarted) {
        if (isStarted) {
            this.mLauncher.getDragLayer().getViewTreeObserver().addOnDrawListener(this.mOnDrawListener);
        } else {
            this.mLauncher.getDragLayer().getViewTreeObserver().removeOnDrawListener(this.mOnDrawListener);
            setSurface(null);
        }
    }

    public void setSurfaceToApp(RemoteAnimationTargetCompat target) {
        if (target != null) {
            setSurface(target.leash);
        } else {
            setActivityStarted(this.mLauncher.isStarted());
        }
    }

    public boolean setSurface(SurfaceControl surface) {
        if (surface == null) {
            ViewRootImpl viewRootImpl = this.mLauncher.getDragLayer().getViewRootImpl();
            surface = viewRootImpl != null ? viewRootImpl.getSurfaceControl() : null;
        }
        if (this.mSurface == surface) {
            return false;
        }
        this.mSurface = surface;
        if (surface == null) {
            return false;
        }
        setDepth(this.mDepth);
        return true;
    }

    /* JADX DEBUG: Method merged with bridge method: setState(Ljava/lang/Object;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateHandler
    public void setState(LauncherState toState) {
        if (this.mSurface == null || this.mIgnoreStateChangesDuringMultiWindowAnimation) {
            return;
        }
        float depth = toState.getDepth(this.mLauncher);
        if (Float.compare(this.mDepth, depth) != 0) {
            setDepth(depth);
        }
    }

    /* JADX DEBUG: Method merged with bridge method: setStateWithAnimation(Ljava/lang/Object;Lcom/android/launcher3/states/StateAnimationConfig;Lcom/android/launcher3/anim/PendingAnimation;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateHandler
    public void setStateWithAnimation(LauncherState toState, StateAnimationConfig config, PendingAnimation animation) {
        if (this.mSurface == null || config.onlyPlayAtomicComponent() || config.hasAnimationFlag(16) || this.mIgnoreStateChangesDuringMultiWindowAnimation) {
            return;
        }
        float depth = toState.getDepth(this.mLauncher);
        if (Float.compare(this.mDepth, depth) != 0) {
            animation.setFloat(this, DEPTH, depth, config.getInterpolator(14, Interpolators.LINEAR));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDepth(float depth) {
        SurfaceControl surfaceControl;
        float fBoundToRange = ((int) (Utilities.boundToRange(depth, 0.0f, 1.0f) * 256.0f)) / 256.0f;
        if (Float.compare(this.mDepth, fBoundToRange) == 0) {
            return;
        }
        boolean zSupportsBlursOnWindows = BlurUtils.supportsBlursOnWindows();
        if ((!zSupportsBlursOnWindows || ((surfaceControl = this.mSurface) != null && surfaceControl.isValid())) && !this.mLauncher.isInState(LauncherState.BACKGROUND_APP)) {
            this.mDepth = fBoundToRange;
            ensureDependencies();
            IBinder windowToken = this.mLauncher.getRootView().getWindowToken();
            if (windowToken != null) {
                this.mWallpaperManager.setWallpaperZoomOut(windowToken, this.mDepth);
            }
            if (zSupportsBlursOnWindows) {
                new TransactionCompat().setBackgroundBlurRadius(this.mSurface, (this.mLauncher.isInState(LauncherState.ALL_APPS) && this.mDepth == 1.0f) ? 0 : (int) (this.mDepth * this.mMaxBlurRadius)).apply();
            }
        }
    }

    @Override // com.android.launcher3.BaseActivity.MultiWindowModeChangedListener
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        this.mIgnoreStateChangesDuringMultiWindowAnimation = true;
        ObjectAnimator duration = ObjectAnimator.ofFloat(this, DEPTH, ((LauncherState) this.mLauncher.getStateManager().getState()).getDepth(this.mLauncher, isInMultiWindowMode)).setDuration(300L);
        duration.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.statehandlers.DepthController.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                DepthController.this.mIgnoreStateChangesDuringMultiWindowAnimation = false;
            }
        });
        duration.setAutoCancel(true);
        duration.start();
    }
}
