package com.android.quickstep;

import android.graphics.Rect;
import android.os.Handler;
import android.util.ArraySet;
import android.view.RemoteAnimationTarget;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.Preconditions;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.RecentsAnimationControllerCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public class RecentsAnimationCallbacks implements com.android.systemui.shared.system.RecentsAnimationListener {
    private final boolean mAllowMinimizeSplitScreen;
    private boolean mCancelled;
    private RecentsAnimationController mController;
    private final Set<RecentsAnimationListener> mListeners = new ArraySet();
    private final SystemUiProxy mSystemUiProxy;

    public interface RecentsAnimationListener {
        default void onRecentsAnimationCanceled(HashMap<Integer, ThumbnailData> thumbnailDatas) {
        }

        default void onRecentsAnimationFinished(RecentsAnimationController controller) {
        }

        default void onRecentsAnimationStart(RecentsAnimationController controller, RecentsAnimationTargets targets) {
        }

        default boolean onSwitchToScreenshot(Runnable onFinished) {
            return false;
        }

        default void onTasksAppeared(RemoteAnimationTargetCompat[] appearedTaskTarget) {
        }
    }

    public RecentsAnimationCallbacks(SystemUiProxy systemUiProxy, boolean allowMinimizeSplitScreen) {
        this.mSystemUiProxy = systemUiProxy;
        this.mAllowMinimizeSplitScreen = allowMinimizeSplitScreen;
    }

    public void addListener(RecentsAnimationListener listener) {
        Preconditions.assertUIThread();
        this.mListeners.add(listener);
    }

    public void removeListener(RecentsAnimationListener listener) {
        Preconditions.assertUIThread();
        this.mListeners.remove(listener);
    }

    public void removeAllListeners() {
        Preconditions.assertUIThread();
        this.mListeners.clear();
    }

    public void notifyAnimationCanceled() {
        this.mCancelled = true;
        onAnimationCanceled(new HashMap<>());
    }

    @Deprecated
    public final void onAnimationStart(RecentsAnimationControllerCompat controller, RemoteAnimationTargetCompat[] appTargets, Rect homeContentInsets, Rect minimizedHomeBounds) {
        onAnimationStart(controller, appTargets, new RemoteAnimationTargetCompat[0], homeContentInsets, minimizedHomeBounds);
    }

    @Override // com.android.systemui.shared.system.RecentsAnimationListener
    public final void onAnimationStart(RecentsAnimationControllerCompat animationController, RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets, Rect homeContentInsets, Rect minimizedHomeBounds) {
        onAnimationStart(animationController, appTargets, wallpaperTargets, homeContentInsets, minimizedHomeBounds, 0);
    }

    @Override // com.android.systemui.shared.system.RecentsAnimationListener
    public void onAnimationStart(RecentsAnimationControllerCompat animationController, RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets, Rect homeContentInsets, Rect minimizedHomeBounds, int displayId) {
        final RecentsAnimationTargets recentsAnimationTargets = new RecentsAnimationTargets(appTargets, wallpaperTargets, RemoteAnimationTargetCompat.wrap(this.mSystemUiProxy.onGoingToRecentsLegacy((RemoteAnimationTarget[]) Arrays.stream(appTargets).filter(new Predicate() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationCallbacks$REuHXvcNqLxk-8w_4Zsf25kobF8
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return RecentsAnimationCallbacks.lambda$onAnimationStart$0((RemoteAnimationTargetCompat) obj);
            }
        }).map(new Function() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationCallbacks$tAyCrZ2lAkHHAGjbVm8XNYk3trc
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((RemoteAnimationTargetCompat) obj).unwrap();
            }
        }).toArray(new IntFunction() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationCallbacks$pSvoROGUC4Wje4RnmgtEZBG0DLU
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                return RecentsAnimationCallbacks.lambda$onAnimationStart$1(i);
            }
        }), displayId)), homeContentInsets, minimizedHomeBounds);
        this.mController = new RecentsAnimationController(animationController, this.mAllowMinimizeSplitScreen, new Consumer() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationCallbacks$7SPbGm-pyEXsSFBUpvVoukbXoII
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.onAnimationFinished((RecentsAnimationController) obj);
            }
        });
        if (this.mCancelled) {
            Handler handler = Executors.MAIN_EXECUTOR.getHandler();
            RecentsAnimationController recentsAnimationController = this.mController;
            Objects.requireNonNull(recentsAnimationController);
            Utilities.postAsyncCallback(handler, new $$Lambda$jo5SItwDnSScytyEwiKrw2tm4Q(recentsAnimationController));
            return;
        }
        Utilities.postAsyncCallback(Executors.MAIN_EXECUTOR.getHandler(), new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationCallbacks$28elLTyGMmzH2tmrg1dNYehMS3A
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onAnimationStart$2$RecentsAnimationCallbacks(recentsAnimationTargets);
            }
        });
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: ?: TERNARY null = ((wrap:int:0x0000: IGET (r1v0 com.android.systemui.shared.system.RemoteAnimationTargetCompat) A[WRAPPED] (LINE:115) com.android.systemui.shared.system.RemoteAnimationTargetCompat.activityType int) != (2 int)) ? true : false */
    static /* synthetic */ boolean lambda$onAnimationStart$0(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        return remoteAnimationTargetCompat.activityType != 2;
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: NEW_ARRAY (r0v0 int A[IMMUTABLE_TYPE]) (LINE:117) type: android.view.RemoteAnimationTarget[] */
    static /* synthetic */ RemoteAnimationTarget[] lambda$onAnimationStart$1(int i) {
        return new RemoteAnimationTarget[i];
    }

    public /* synthetic */ void lambda$onAnimationStart$2$RecentsAnimationCallbacks(RecentsAnimationTargets recentsAnimationTargets) {
        for (RecentsAnimationListener recentsAnimationListener : getListeners()) {
            recentsAnimationListener.onRecentsAnimationStart(this.mController, recentsAnimationTargets);
        }
    }

    @Override // com.android.systemui.shared.system.RecentsAnimationListener
    public final void onAnimationCanceled(final HashMap<Integer, ThumbnailData> thumbnailDatas) {
        SystemUiProxy systemUiProxy = this.mSystemUiProxy;
        if (systemUiProxy != null) {
            systemUiProxy.onFinishGoingToRecentsLegacy();
        }
        Utilities.postAsyncCallback(Executors.MAIN_EXECUTOR.getHandler(), new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationCallbacks$UqbC9pka92CvbBiDyXJb2ovoy34
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onAnimationCanceled$3$RecentsAnimationCallbacks(thumbnailDatas);
            }
        });
    }

    public /* synthetic */ void lambda$onAnimationCanceled$3$RecentsAnimationCallbacks(HashMap map) {
        for (RecentsAnimationListener recentsAnimationListener : getListeners()) {
            recentsAnimationListener.onRecentsAnimationCanceled(map);
        }
    }

    @Override // com.android.systemui.shared.system.RecentsAnimationListener
    public void onTasksAppeared(final RemoteAnimationTargetCompat[] apps) {
        Utilities.postAsyncCallback(Executors.MAIN_EXECUTOR.getHandler(), new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationCallbacks$nI0xLJFAK3FdU0D1RhCDIiN3Po8
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onTasksAppeared$4$RecentsAnimationCallbacks(apps);
            }
        });
    }

    public /* synthetic */ void lambda$onTasksAppeared$4$RecentsAnimationCallbacks(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr) {
        for (RecentsAnimationListener recentsAnimationListener : getListeners()) {
            recentsAnimationListener.onTasksAppeared(remoteAnimationTargetCompatArr);
        }
    }

    @Override // com.android.systemui.shared.system.RecentsAnimationListener
    public boolean onSwitchToScreenshot(final Runnable onFinished) {
        Utilities.postAsyncCallback(Executors.MAIN_EXECUTOR.getHandler(), new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationCallbacks$y0B8x47byZmSF3sKGuu5ptmFmgI
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onSwitchToScreenshot$5$RecentsAnimationCallbacks(onFinished);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$onSwitchToScreenshot$5$RecentsAnimationCallbacks(Runnable runnable) {
        for (RecentsAnimationListener recentsAnimationListener : getListeners()) {
            if (recentsAnimationListener.onSwitchToScreenshot(runnable)) {
                return;
            }
        }
        runnable.run();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void onAnimationFinished(final RecentsAnimationController controller) {
        SystemUiProxy systemUiProxy = this.mSystemUiProxy;
        if (systemUiProxy != null) {
            systemUiProxy.onFinishGoingToRecentsLegacy();
        }
        Utilities.postAsyncCallback(Executors.MAIN_EXECUTOR.getHandler(), new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationCallbacks$UUBrRHY3IKKpv_eHUlWuii7-Osg
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onAnimationFinished$6$RecentsAnimationCallbacks(controller);
            }
        });
    }

    public /* synthetic */ void lambda$onAnimationFinished$6$RecentsAnimationCallbacks(RecentsAnimationController recentsAnimationController) {
        for (RecentsAnimationListener recentsAnimationListener : getListeners()) {
            recentsAnimationListener.onRecentsAnimationFinished(recentsAnimationController);
        }
    }

    private RecentsAnimationListener[] getListeners() {
        Set<RecentsAnimationListener> set = this.mListeners;
        return (RecentsAnimationListener[]) set.toArray(new RecentsAnimationListener[set.size()]);
    }
}
