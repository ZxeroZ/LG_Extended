package com.android.quickstep;

import android.graphics.Rect;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;

/* JADX INFO: loaded from: classes.dex */
public class RecentsAnimationTargets extends RemoteAnimationTargets {
    public final Rect homeContentInsets;
    public final Rect minimizedHomeBounds;

    public RecentsAnimationTargets(RemoteAnimationTargetCompat[] apps, RemoteAnimationTargetCompat[] wallpapers, RemoteAnimationTargetCompat[] nonApps, Rect homeContentInsets, Rect minimizedHomeBounds) {
        super(apps, wallpapers, nonApps, 1);
        this.homeContentInsets = homeContentInsets;
        this.minimizedHomeBounds = minimizedHomeBounds;
    }

    public boolean hasTargets() {
        return this.unfilteredApps.length != 0;
    }
}
