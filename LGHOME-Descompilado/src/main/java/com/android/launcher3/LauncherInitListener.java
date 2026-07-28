package com.android.launcher3;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.os.CancellationSignal;
import android.os.Handler;
import com.android.quickstep.util.ActivityInitListener;
import com.android.quickstep.util.RemoteAnimationProvider;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.util.function.BiPredicate;

/* JADX INFO: loaded from: classes.dex */
public class LauncherInitListener extends ActivityInitListener<Launcher> {
    private RemoteAnimationProvider mRemoteAnimationProvider;

    public LauncherInitListener(BiPredicate<Launcher, Boolean> onInitListener) {
        super(onInitListener, Launcher.ACTIVITY_TRACKER);
    }

    /* JADX DEBUG: Method merged with bridge method: handleInit(Lcom/android/launcher3/BaseActivity;Z)Z */
    @Override // com.android.quickstep.util.ActivityInitListener
    public boolean handleInit(final Launcher launcher, boolean alreadyOnHome) {
        if (this.mRemoteAnimationProvider != null) {
            QuickstepTransitionManager appTransitionManager = ((BaseQuickstepLauncher) launcher).getAppTransitionManager();
            final CancellationSignal cancellationSignal = new CancellationSignal();
            appTransitionManager.setRemoteAnimationProvider(new RemoteAnimationProvider() { // from class: com.android.launcher3.LauncherInitListener.1
                @Override // com.android.quickstep.util.RemoteAnimationProvider
                public AnimatorSet createWindowAnimation(RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets) {
                    cancellationSignal.cancel();
                    RemoteAnimationProvider remoteAnimationProvider = LauncherInitListener.this.mRemoteAnimationProvider;
                    LauncherInitListener.this.mRemoteAnimationProvider = null;
                    if (remoteAnimationProvider == null || !((LauncherState) launcher.getStateManager().getState()).overviewUi) {
                        return null;
                    }
                    return remoteAnimationProvider.createWindowAnimation(appTargets, wallpaperTargets);
                }
            }, cancellationSignal);
        }
        launcher.deferOverlayCallbacksUntilNextResumeOrStop();
        return super.handleInit(launcher, alreadyOnHome);
    }

    @Override // com.android.quickstep.util.ActivityInitListener
    public void unregister() {
        this.mRemoteAnimationProvider = null;
        super.unregister();
    }

    @Override // com.android.quickstep.util.ActivityInitListener
    public void registerAndStartActivity(Intent intent, RemoteAnimationProvider animProvider, Context context, Handler handler, long duration) {
        this.mRemoteAnimationProvider = animProvider;
        super.registerAndStartActivity(intent, animProvider, context, handler, duration);
    }
}
