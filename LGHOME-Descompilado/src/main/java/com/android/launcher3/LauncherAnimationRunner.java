package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Handler;
import com.android.launcher3.LauncherAnimationRunner;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.Executors;
import com.android.systemui.shared.system.RemoteAnimationRunnerCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class LauncherAnimationRunner implements RemoteAnimationRunnerCompat {
    private static final RemoteAnimationFactory DEFAULT_FACTORY = new RemoteAnimationFactory() { // from class: com.android.launcher3.-$$Lambda$LauncherAnimationRunner$dFfFyjS74inydCsXDGK5ftFoKtQ
        @Override // com.android.launcher3.LauncherAnimationRunner.RemoteAnimationFactory
        public final void onCreateAnimation(int i, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, LauncherAnimationRunner.AnimationResult animationResult) {
            animationResult.setAnimation(null, null);
        }
    };
    private AnimationResult mAnimationResult;
    private final WeakReference<RemoteAnimationFactory> mFactory;
    private final Handler mHandler;
    private final boolean mStartAtFrontOfQueue;

    @FunctionalInterface
    public interface RemoteAnimationFactory {
        default void onAnimationCancelled() {
        }

        void onCreateAnimation(int transit, RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets, RemoteAnimationTargetCompat[] nonAppTargets, AnimationResult result);
    }

    public LauncherAnimationRunner(Handler handler, RemoteAnimationFactory factory, boolean startAtFrontOfQueue) {
        this.mHandler = handler;
        this.mFactory = new WeakReference<>(factory);
        this.mStartAtFrontOfQueue = startAtFrontOfQueue;
    }

    @Override // com.android.systemui.shared.system.RemoteAnimationRunnerCompat
    public void onAnimationStart(final int transit, final RemoteAnimationTargetCompat[] appTargets, final RemoteAnimationTargetCompat[] wallpaperTargets, final RemoteAnimationTargetCompat[] nonAppTargets, final Runnable runnable) {
        Runnable runnable2 = new Runnable() { // from class: com.android.launcher3.-$$Lambda$LauncherAnimationRunner$DvViukAuJ6Gp76ICvQgYTlagihE
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onAnimationStart$2$LauncherAnimationRunner(runnable, transit, appTargets, wallpaperTargets, nonAppTargets);
            }
        };
        if (this.mStartAtFrontOfQueue) {
            com.android.systemui.shared.recents.utilities.Utilities.postAtFrontOfQueueAsynchronously(this.mHandler, runnable2);
        } else {
            Utilities.postAsyncCallback(this.mHandler, runnable2);
        }
    }

    public /* synthetic */ void lambda$onAnimationStart$2$LauncherAnimationRunner(Runnable runnable, int i, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3) {
        finishExistingAnimation();
        this.mAnimationResult = new AnimationResult(new Runnable() { // from class: com.android.launcher3.-$$Lambda$LauncherAnimationRunner$S04C32xvSThJnAi6IhPidw6ZWqo
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onAnimationStart$1$LauncherAnimationRunner();
            }
        }, runnable);
        getFactory().onCreateAnimation(i, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, this.mAnimationResult);
    }

    public /* synthetic */ void lambda$onAnimationStart$1$LauncherAnimationRunner() {
        this.mAnimationResult = null;
    }

    public void onAnimationStart(RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets, Runnable runnable) {
        onAnimationStart(0, appTargets, wallpaperTargets, new RemoteAnimationTargetCompat[0], runnable);
    }

    @Deprecated
    public void onAnimationStart(RemoteAnimationTargetCompat[] appTargets, Runnable runnable) {
        onAnimationStart(appTargets, new RemoteAnimationTargetCompat[0], runnable);
    }

    private RemoteAnimationFactory getFactory() {
        RemoteAnimationFactory remoteAnimationFactory = this.mFactory.get();
        return remoteAnimationFactory != null ? remoteAnimationFactory : DEFAULT_FACTORY;
    }

    private void finishExistingAnimation() {
        AnimationResult animationResult = this.mAnimationResult;
        if (animationResult != null) {
            animationResult.finish();
            this.mAnimationResult = null;
        }
    }

    @Override // com.android.systemui.shared.system.RemoteAnimationRunnerCompat
    public void onAnimationCancelled() {
        Utilities.postAsyncCallback(this.mHandler, new Runnable() { // from class: com.android.launcher3.-$$Lambda$LauncherAnimationRunner$MpOE_luEjsthN8dYCSu1MYHbd74
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onAnimationCancelled$3$LauncherAnimationRunner();
            }
        });
    }

    public /* synthetic */ void lambda$onAnimationCancelled$3$LauncherAnimationRunner() {
        finishExistingAnimation();
        getFactory().onAnimationCancelled();
    }

    public static final class AnimationResult {
        private final Runnable mASyncFinishRunnable;
        private AnimatorSet mAnimator;
        private boolean mFinished;
        private boolean mInitialized;
        private Runnable mOnCompleteCallback;
        private final Runnable mSyncFinishRunnable;

        private AnimationResult(Runnable syncFinishRunnable, Runnable asyncFinishRunnable) {
            this.mFinished = false;
            this.mInitialized = false;
            this.mSyncFinishRunnable = syncFinishRunnable;
            this.mASyncFinishRunnable = asyncFinishRunnable;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void finish() {
            if (this.mFinished) {
                return;
            }
            this.mSyncFinishRunnable.run();
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.launcher3.-$$Lambda$LauncherAnimationRunner$AnimationResult$7p6a0bbXBIXwxWJSG2pbnCwLRUM
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$finish$0$LauncherAnimationRunner$AnimationResult();
                }
            });
            this.mFinished = true;
        }

        public /* synthetic */ void lambda$finish$0$LauncherAnimationRunner$AnimationResult() {
            this.mASyncFinishRunnable.run();
            if (this.mOnCompleteCallback != null) {
                Executors.MAIN_EXECUTOR.execute(this.mOnCompleteCallback);
            }
        }

        public void setAnimation(AnimatorSet animation, Context context) {
            setAnimation(animation, context, null, true);
        }

        public void setAnimation(AnimatorSet animation, Context context, Runnable onCompleteCallback, boolean skipFirstFrame) {
            if (this.mInitialized) {
                throw new IllegalStateException("Animation already initialized");
            }
            this.mInitialized = true;
            this.mAnimator = animation;
            this.mOnCompleteCallback = onCompleteCallback;
            if (animation == null) {
                finish();
                return;
            }
            if (this.mFinished) {
                animation.start();
                this.mAnimator.end();
                Runnable runnable = this.mOnCompleteCallback;
                if (runnable != null) {
                    runnable.run();
                    return;
                }
                return;
            }
            animation.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.LauncherAnimationRunner.AnimationResult.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation2) {
                    AnimationResult.this.finish();
                }
            });
            this.mAnimator.start();
            if (skipFirstFrame) {
                this.mAnimator.setCurrentPlayTime(Math.min(DisplayController.getSingleFrameMs(context), this.mAnimator.getTotalDuration()));
            }
        }
    }
}
