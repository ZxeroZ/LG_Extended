package com.android.launcher3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lge.launcher3.adaptive.AdaptiveTextUtil;
import com.lge.launcher3.adaptive.WallpaperColorInfoUtil;
import com.lge.launcher3.util.LGHomeFeature;

/* JADX INFO: loaded from: classes.dex */
public class WallpaperChangedReceiver extends BroadcastReceiver {
    private static final String TAG = "WallpaperChangedReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, Intent data) {
        LauncherAppState.getInstance(context).onWallpaperChanged();
        AdaptiveTextUtil.updateAdaptiveTextColor(context);
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            WallpaperColorInfoUtil.getInstance(context).runMakeInfo(context, new Runnable() { // from class: com.android.launcher3.-$$Lambda$WallpaperChangedReceiver$D62xCPTe2q67Akpn_LkAxHSiYKc
                @Override // java.lang.Runnable
                public final void run() {
                    AdaptiveTextUtil.updateAdaptiveColorForSwivel(context);
                }
            });
        }
        if (LGHomeFeature.Config.FEATURE_SUPPORT_ADAPTIVE_STATUS_BAR_COLOR.getValue()) {
            AdaptiveTextUtil.runAdaptiveColorForStatusBar(context);
        }
    }
}
