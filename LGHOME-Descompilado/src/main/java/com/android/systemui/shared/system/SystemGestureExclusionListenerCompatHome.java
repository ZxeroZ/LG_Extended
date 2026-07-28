package com.android.systemui.shared.system;

import android.graphics.Region;
import android.os.RemoteException;
import android.util.Log;
import android.view.ISystemGestureExclusionListener;
import android.view.WindowManagerGlobal;

/* JADX INFO: loaded from: classes.dex */
public abstract class SystemGestureExclusionListenerCompatHome {
    private static final String TAG = "SGEListenerCompat";
    private final int mDisplayId;
    private ISystemGestureExclusionListener mGestureExclusionListener = new ISystemGestureExclusionListener.Stub() { // from class: com.android.systemui.shared.system.SystemGestureExclusionListenerCompatHome.1
        public void onSystemGestureExclusionChanged(int displayId, Region systemGestureExclusion, Region unrestrictedOrNull) {
            if (displayId == SystemGestureExclusionListenerCompatHome.this.mDisplayId) {
                if (unrestrictedOrNull == null) {
                    unrestrictedOrNull = systemGestureExclusion;
                }
                SystemGestureExclusionListenerCompatHome.this.onExclusionChanged(systemGestureExclusion, unrestrictedOrNull);
            }
        }

        public void onSystemGestureExclusionChanged(int displayId, Region systemGestureExclusion) {
            if (displayId == SystemGestureExclusionListenerCompatHome.this.mDisplayId) {
                SystemGestureExclusionListenerCompatHome.this.onExclusionChanged(systemGestureExclusion);
            }
        }
    };
    private boolean mRegistered;

    public abstract void onExclusionChanged(Region systemGestureExclusion);

    public SystemGestureExclusionListenerCompatHome(int displayId) {
        this.mDisplayId = displayId;
    }

    public void onExclusionChanged(Region systemGestureExclusion, Region systemGestureExclusionUnrestricted) {
        onExclusionChanged(systemGestureExclusion);
    }

    public void register() {
        if (this.mRegistered) {
            return;
        }
        try {
            WindowManagerGlobal.getWindowManagerService().registerSystemGestureExclusionListener(this.mGestureExclusionListener, this.mDisplayId);
            this.mRegistered = true;
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to register window manager callbacks", e);
        }
    }

    public void unregister() {
        if (this.mRegistered) {
            try {
                WindowManagerGlobal.getWindowManagerService().unregisterSystemGestureExclusionListener(this.mGestureExclusionListener, this.mDisplayId);
                this.mRegistered = false;
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to unregister window manager callbacks", e);
            }
        }
    }
}
