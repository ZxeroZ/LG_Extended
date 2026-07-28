package com.android.quickstep;

/* JADX INFO: renamed from: com.android.quickstep.-$$Lambda$-jo5SItwDnSScytyEwiKrw2tm4Q, reason: invalid class name */
/* JADX INFO: compiled from: lambda */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class $$Lambda$jo5SItwDnSScytyEwiKrw2tm4Q implements Runnable {
    public final /* synthetic */ RecentsAnimationController f$0;

    /* JADX DEBUG: Marked for inline */
    /* JADX DEBUG: Method not inlined, still used in: [com.android.quickstep.RecentsAnimationCallbacks.onAnimationStart(com.android.systemui.shared.system.RecentsAnimationControllerCompat, com.android.systemui.shared.system.RemoteAnimationTargetCompat[], com.android.systemui.shared.system.RemoteAnimationTargetCompat[], android.graphics.Rect, android.graphics.Rect, int):void] */
    public /* synthetic */ $$Lambda$jo5SItwDnSScytyEwiKrw2tm4Q(RecentsAnimationController recentsAnimationController) {
        this.f$0 = recentsAnimationController;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f$0.finishAnimationToApp();
    }
}
