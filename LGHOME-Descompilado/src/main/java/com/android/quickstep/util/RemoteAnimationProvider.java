package com.android.quickstep.util;

import android.animation.AnimatorSet;
import android.app.ActivityOptions;
import android.content.Context;
import android.os.Handler;
import com.android.launcher3.LauncherAnimationRunner;
import com.android.systemui.shared.system.ActivityOptionsCompat;
import com.android.systemui.shared.system.RemoteAnimationAdapterCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;

/* JADX INFO: loaded from: classes.dex */
public abstract class RemoteAnimationProvider {
    static final int Z_BOOST_BASE = 800570000;
    LauncherAnimationRunner.RemoteAnimationFactory mAnimationRunner;

    public abstract AnimatorSet createWindowAnimation(RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets);

    ActivityOptions toActivityOptions(Handler handler, long duration, final Context context) {
        this.mAnimationRunner = new LauncherAnimationRunner.RemoteAnimationFactory() { // from class: com.android.quickstep.util.-$$Lambda$RemoteAnimationProvider$OvR3gNT6ye9fEA05-csW4RqfdLo
            @Override // com.android.launcher3.LauncherAnimationRunner.RemoteAnimationFactory
            public final void onCreateAnimation(int i, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, LauncherAnimationRunner.AnimationResult animationResult) {
                this.f$0.lambda$toActivityOptions$0$RemoteAnimationProvider(context, i, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, animationResult);
            }
        };
        return ActivityOptionsCompat.makeRemoteAnimation(new RemoteAnimationAdapterCompat(new LauncherAnimationRunner(handler, this.mAnimationRunner, false), duration, 0L, context.getIApplicationThread()));
    }

    public /* synthetic */ void lambda$toActivityOptions$0$RemoteAnimationProvider(Context context, int i, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, LauncherAnimationRunner.AnimationResult animationResult) {
        animationResult.setAnimation(createWindowAnimation(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2), context);
    }

    public static RemoteAnimationTargetCompat findLowestOpaqueLayerTarget(RemoteAnimationTargetCompat[] appTargets, int mode) {
        int i;
        int i2 = Integer.MAX_VALUE;
        int i3 = -1;
        for (int length = appTargets.length - 1; length >= 0; length--) {
            RemoteAnimationTargetCompat remoteAnimationTargetCompat = appTargets[length];
            if (remoteAnimationTargetCompat.mode == mode && !remoteAnimationTargetCompat.isTranslucent && (i = remoteAnimationTargetCompat.prefixOrderIndex) < i2) {
                i3 = length;
                i2 = i;
            }
        }
        if (i3 != -1) {
            return appTargets[i3];
        }
        return null;
    }

    public static int getLayer(RemoteAnimationTargetCompat target, int boostModeTarget) {
        if (target.mode == boostModeTarget) {
            return target.prefixOrderIndex + Z_BOOST_BASE;
        }
        return target.prefixOrderIndex;
    }
}
