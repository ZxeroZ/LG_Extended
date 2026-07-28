package com.android.systemui.shared.system;

import android.content.Context;
import com.android.internal.util.LatencyTracker;

/* JADX INFO: loaded from: classes.dex */
public class LatencyTrackerCompat {
    public static void logToggleRecents(Context context, int i) {
        LatencyTracker.getInstance(context).logAction(1, i);
    }
}
