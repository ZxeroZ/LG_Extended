package com.lge.launcher3.recentuninstall.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;

/* JADX INFO: loaded from: classes.dex */
public class ScheduleManager extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        IntentFilter intentFilter;
        if (intent == null) {
            return;
        }
        LGLog.d("RUService", "ScheduleManager.onReceive(): intent = " + intent);
        if (!LGHomeFeature.Config.FEATURE_USE_RECENT_UNINSTALL_APP.getValue()) {
            LGLog.d("RUService", "FEATURE_USE_RECENT_UNINSTALL_APP disabled. Skip!");
            return;
        }
        if (intent.getAction().equals(IntentConst.Action.ACTION_SCHEDULE_UNINSTALL_JOB.getValue(context))) {
            scheduleJobOrCancel(context);
        } else if (intent.getAction().equals("com.lge.android.intent.action.PREFERRED_ACTIVITY_CHANGED") && (intentFilter = (IntentFilter) intent.getExtras().get("intentFilter")) != null && intentFilter.hasCategory(PackageUtils.ANDROID_INTENT_CATEGORY_HOME)) {
            scheduleJobOrCancel(context);
        }
    }

    private void scheduleJobOrCancel(Context context) {
        if (isPreferredHomePackage(context, context.getPackageName())) {
            setJobSchedule(context);
        } else {
            cancelJobSchedule(context);
            UninstallPackageUtil.uninstallDisableAppsAllUsers(context, 0L);
        }
    }

    static boolean isPreferredHomePackage(Context context, String packageName) {
        ResolveInfo defaultHomeActivityResolveInfo = PackageUtils.getDefaultHomeActivityResolveInfo(context);
        if (defaultHomeActivityResolveInfo != null) {
            return !(defaultHomeActivityResolveInfo == null || defaultHomeActivityResolveInfo.activityInfo == null || defaultHomeActivityResolveInfo.activityInfo.packageName == null || !defaultHomeActivityResolveInfo.activityInfo.packageName.equals(packageName)) || PackageUtils.isResolverActivity(defaultHomeActivityResolveInfo);
        }
        LGLog.w("RUService", "isPreferredHomePackage() : resolveInfo is null", new int[0]);
        return false;
    }

    private void setJobSchedule(Context context) {
        ((JobScheduler) context.getSystemService("jobscheduler")).schedule(new JobInfo.Builder(-1108604005, new ComponentName(context, (Class<?>) UninstallJob.class)).setMinimumLatency(25200000L).setOverrideDeadline(28800000L).setRequiresDeviceIdle(true).setPersisted(true).build());
        LGLog.i("RUService", "setJobSchedule(): Scheduled from 420 minutes to 480minutes");
    }

    public void cancelJobSchedule(Context context) {
        LGLog.i("RUService", "cancelJobSchedule()");
        ((JobScheduler) context.getSystemService("jobscheduler")).cancel(-1108604005);
    }
}
