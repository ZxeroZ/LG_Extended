package com.lge.launcher3.recentuninstall.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class UninstallJob extends JobService {
    static final int JOB_ID_UNINSTALL = -1108604005;

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        LGLog.i("RUService", "UninstallJob.onStartJob()");
        UninstallPackageUtil.uninstallDisableAppsAllUsers(getApplicationContext(), 86400000L);
        return false;
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        LGLog.i("RUService", "UninstallJob.onStopJob()");
        return false;
    }
}
