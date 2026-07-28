package com.lge.launcher3.smartbulletin.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.RemoteViews;
import com.android.launcher3.Launcher;
import com.android.launcher3.uioverrides.QuickstepLauncher;
import com.android.launcher3.util.ActivityOptionsWrapper;
import com.lge.launcher3.smartbulletin.widgetlibrary.MyAppWidgetHostView;

/* JADX INFO: loaded from: classes.dex */
class SBInteractionHandler implements RemoteViews.InteractionHandler {
    private static final String TAG = "SBInteractionHandler ";
    private QuickstepLauncher mLauncher;

    SBInteractionHandler(Context context) {
        this.mLauncher = null;
        if (context instanceof QuickstepLauncher) {
            this.mLauncher = (QuickstepLauncher) Launcher.getLauncher(context);
        }
    }

    public boolean onInteraction(View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
        MyAppWidgetHostView myAppWidgetHostViewFindHostViewAncestor = findHostViewAncestor(view);
        if (myAppWidgetHostViewFindHostViewAncestor == null || this.mLauncher == null) {
            if (myAppWidgetHostViewFindHostViewAncestor == null) {
                Log.e(TAG, "View did not have a LauncherAppWidgetHostView ancestor.");
            }
            return RemoteViews.startPendingIntent(myAppWidgetHostViewFindHostViewAncestor, pendingIntent, remoteResponse.getLaunchOptions(view));
        }
        Pair launchOptions = remoteResponse.getLaunchOptions(view);
        ActivityOptionsWrapper activityLaunchOptions = this.mLauncher.getAppTransitionManager().getActivityLaunchOptions(this.mLauncher, myAppWidgetHostViewFindHostViewAncestor);
        activityLaunchOptions.options.setPendingIntentLaunchFlags(268435456);
        activityLaunchOptions.options.setSplashScreenStyle(0);
        myAppWidgetHostViewFindHostViewAncestor.getTag();
        return RemoteViews.startPendingIntent(myAppWidgetHostViewFindHostViewAncestor, pendingIntent, Pair.create((Intent) launchOptions.first, activityLaunchOptions.options));
    }

    private MyAppWidgetHostView findHostViewAncestor(View v) {
        while (v != null) {
            if (v instanceof MyAppWidgetHostView) {
                return (MyAppWidgetHostView) v;
            }
            v = (View) v.getParent();
        }
        return null;
    }
}
