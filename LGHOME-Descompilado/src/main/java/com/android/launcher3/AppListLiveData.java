package com.android.launcher3;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: compiled from: AppListViewModel.java */
/* JADX INFO: loaded from: classes.dex */
class AppListLiveData extends LiveData<List<AppEntry>> {
    private static final String TAG = PackageIntentReceiver.class.getSimpleName();
    private int mCurrentDataVersion;
    private final PackageManager mPackageManager;

    public AppListLiveData(Context context) {
        LGLog.i(TAG, "create AppListLiveData ");
        this.mPackageManager = context.getPackageManager();
        loadData();
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [com.android.launcher3.AppListLiveData$1] */
    void loadData() {
        LGLog.i(TAG, "loadData()");
        final int i = this.mCurrentDataVersion + 1;
        this.mCurrentDataVersion = i;
        new AsyncTask<Void, Void, List<AppEntry>>() { // from class: com.android.launcher3.AppListLiveData.1
            /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public List<AppEntry> doInBackground(Void... voids) {
                LGLog.i(AppListLiveData.TAG, "doInBackground()");
                Intent intent = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN, (Uri) null);
                intent.addCategory("android.intent.category.LAUNCHER");
                List<ResolveInfo> listQueryIntentActivities = AppListLiveData.this.mPackageManager.queryIntentActivities(intent, 128);
                LGLog.i(AppListLiveData.TAG, "doInBackground() - apps : " + listQueryIntentActivities);
                ArrayList arrayList = new ArrayList();
                if (listQueryIntentActivities != null) {
                    LGLog.i(AppListLiveData.TAG, "doInBackground() - apps.size() : " + listQueryIntentActivities.size());
                    for (ResolveInfo resolveInfo : listQueryIntentActivities) {
                        LGLog.i(AppListLiveData.TAG, "doInBackground() - new AppEntry.  mPackageManager = " + AppListLiveData.this.mPackageManager);
                        arrayList.add(new AppEntry(resolveInfo, AppListLiveData.this.mPackageManager));
                    }
                }
                return arrayList;
            }

            /* JADX DEBUG: Method merged with bridge method: onPostExecute(Ljava/lang/Object;)V */
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(List<AppEntry> data) {
                if (AppListLiveData.this.mCurrentDataVersion == i) {
                    Collections.sort(data, AppListUtils.NAME_COMPARATOR);
                    AppListLiveData.this.setValue(data);
                }
            }
        }.execute(new Void[0]);
    }
}
