package com.android.launcher3.uioverrides;

import android.app.ActivityTaskManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.RemoteViews;
import com.android.launcher3.LauncherAppWidgetHostView;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.ActivityOptionsWrapper;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
class QuickstepInteractionHandler implements RemoteViews.InteractionHandler {
    private static final String TAG = "QuickstepInteractionHandler";
    private final QuickstepLauncher mLauncher;

    QuickstepInteractionHandler(QuickstepLauncher launcher) {
        this.mLauncher = launcher;
    }

    public boolean onInteraction(View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
        LauncherAppWidgetHostView launcherAppWidgetHostViewFindHostViewAncestor = findHostViewAncestor(view);
        if (launcherAppWidgetHostViewFindHostViewAncestor == null || Utilities.isOsuUpgraded()) {
            if (launcherAppWidgetHostViewFindHostViewAncestor == null) {
                Log.e(TAG, "View did not have a LauncherAppWidgetHostView ancestor.");
            }
            return RemoteViews.startPendingIntent(launcherAppWidgetHostViewFindHostViewAncestor, pendingIntent, remoteResponse.getLaunchOptions(view));
        }
        Pair launchOptions = remoteResponse.getLaunchOptions(view);
        ActivityOptionsWrapper activityLaunchOptions = this.mLauncher.getAppTransitionManager().getActivityLaunchOptions(this.mLauncher, launcherAppWidgetHostViewFindHostViewAncestor);
        if (com.android.launcher3.Utilities.ATLEAST_S && !pendingIntent.isActivity()) {
            try {
                ActivityTaskManager.getService().registerRemoteAnimationForNextActivityStart(pendingIntent.getCreatorPackage(), activityLaunchOptions.options.getRemoteAnimationAdapter(), (IBinder) null);
            } catch (RemoteException unused) {
            }
        }
        activityLaunchOptions.options.setPendingIntentLaunchFlags(268435456);
        activityLaunchOptions.options.setSplashScreenStyle(0);
        Object tag = launcherAppWidgetHostViewFindHostViewAncestor.getTag();
        boolean z = tag instanceof ItemInfo;
        Pair pairCreate = Pair.create((Intent) launchOptions.first, activityLaunchOptions.options);
        if (pendingIntent.isActivity()) {
            logAppLaunch(tag);
        }
        return RemoteViews.startPendingIntent(launcherAppWidgetHostViewFindHostViewAncestor, pendingIntent, pairCreate);
    }

    private void logAppLaunch(Object itemInfo) {
        StatsLogManager.StatsLogger statsLoggerLogger = this.mLauncher.getStatsLogManager().logger();
        if (itemInfo instanceof ItemInfo) {
            statsLoggerLogger.withItemInfo((ItemInfo) itemInfo);
        }
        statsLoggerLogger.log(StatsLogManager.LauncherEvent.LAUNCHER_APP_LAUNCH_TAP);
    }

    private LauncherAppWidgetHostView findHostViewAncestor(View v) {
        while (v != null) {
            if (v instanceof LauncherAppWidgetHostView) {
                return (LauncherAppWidgetHostView) v;
            }
            v = (View) v.getParent();
        }
        return null;
    }
}
