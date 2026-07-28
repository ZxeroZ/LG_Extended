package com.android.quickstep.util;

import android.animation.ValueAnimator;
import com.android.quickstep.RemoteAnimationTargets;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.TransactionCompat;

/* JADX INFO: loaded from: classes.dex */
public class RemoteFadeOutAnimationListener implements ValueAnimator.AnimatorUpdateListener {
    private boolean mFirstFrame = true;
    private final RemoteAnimationTargets mTarget;

    public RemoteFadeOutAnimationListener(RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets) {
        this.mTarget = new RemoteAnimationTargets(appTargets, wallpaperTargets, new RemoteAnimationTargetCompat[0], 1);
    }

    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        TransactionCompat transactionCompat = new TransactionCompat();
        if (this.mFirstFrame) {
            for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : this.mTarget.unfilteredApps) {
                transactionCompat.show(remoteAnimationTargetCompat.leash);
            }
            this.mFirstFrame = false;
        }
        float animatedFraction = 1.0f - valueAnimator.getAnimatedFraction();
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat2 : this.mTarget.apps) {
            transactionCompat.setAlpha(remoteAnimationTargetCompat2.leash, animatedFraction);
        }
        transactionCompat.apply();
    }
}
