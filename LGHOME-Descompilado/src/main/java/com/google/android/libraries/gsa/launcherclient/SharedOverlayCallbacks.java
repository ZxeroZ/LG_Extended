package com.google.android.libraries.gsa.launcherclient;

import android.graphics.Bitmap;

/* JADX INFO: loaded from: classes.dex */
public interface SharedOverlayCallbacks extends LauncherClientCallbacks {
    void onGoogleOverlayIconChanged(Bitmap bitmap);

    void onGoogleOverlayTransitionComplete();

    void onSharedOverlaySwitchInitiated();
}
