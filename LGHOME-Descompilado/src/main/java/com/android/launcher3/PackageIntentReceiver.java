package com.android.launcher3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: compiled from: AppListViewModel.java */
/* JADX INFO: loaded from: classes.dex */
class PackageIntentReceiver extends BroadcastReceiver {
    private static final String TAG = "PackageIntentReceiver";
    private final AppListLiveData mLiveData;

    public PackageIntentReceiver(AppListLiveData liveData, Context context) {
        this.mLiveData = liveData;
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addDataScheme(AppNotifierManager.ExtraSpec.USAGE_PACKAGE);
        context.registerReceiver(this, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
        intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        context.registerReceiver(this, intentFilter2);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String str = TAG;
        LGLog.i(str, "onReceive - intent : " + intent);
        LGLog.i(str, "onReceive - intent.getAction() : " + intent.getAction());
        this.mLiveData.loadData();
    }
}
