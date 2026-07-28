package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: compiled from: PinnedAppListViewModel.java */
/* JADX INFO: loaded from: classes.dex */
class PinnedAppListLiveData extends LiveData<List<AppEntry>> {
    private static final String TAG = "PinnedAppListLiveData";
    private final SharedPreferences.OnSharedPreferenceChangeListener mChangeListener;
    private final Context mContext;
    private int mCurrentDataVersion;
    private final PackageManager mPackageManager;

    public PinnedAppListLiveData(Context context) {
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        SharedPreferences sharedPreferences = context.getSharedPreferences("pinned_apps", 0);
        SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: com.android.launcher3.-$$Lambda$PinnedAppListLiveData$3fbMeg5nNT2omOi3AxiSR0lOBL4
            @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
            public final void onSharedPreferenceChanged(SharedPreferences sharedPreferences2, String str) {
                this.f$0.lambda$new$0$PinnedAppListLiveData(sharedPreferences2, str);
            }
        };
        this.mChangeListener = onSharedPreferenceChangeListener;
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        loadData();
    }

    public /* synthetic */ void lambda$new$0$PinnedAppListLiveData(SharedPreferences sharedPreferences, String str) {
        loadData();
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [com.android.launcher3.PinnedAppListLiveData$1] */
    private void loadData() {
        LGLog.i(TAG, "loadData ");
        final int i = this.mCurrentDataVersion + 1;
        this.mCurrentDataVersion = i;
        new AsyncTask<Void, Void, List<AppEntry>>() { // from class: com.android.launcher3.PinnedAppListLiveData.1
            /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public List<AppEntry> doInBackground(Void... voids) {
                LGLog.i(PinnedAppListLiveData.TAG, "doInBackground");
                ArrayList arrayList = new ArrayList();
                SharedPreferences sharedPreferences = PinnedAppListLiveData.this.mContext.getSharedPreferences("pinned_apps", 0);
                String string = sharedPreferences.getString("pinned_apps", null);
                LGLog.i(PinnedAppListLiveData.TAG, "doInBackground - sp = " + sharedPreferences);
                LGLog.i(PinnedAppListLiveData.TAG, "doInBackground - data = " + string);
                if (string == null) {
                    LGLog.i(PinnedAppListLiveData.TAG, "doInBackground - data is null");
                    return null;
                }
                List<String> list = (List) new Gson().fromJson(string, new TypeToken<List<String>>() { // from class: com.android.launcher3.PinnedAppListLiveData.1.1
                }.getType());
                LGLog.i(PinnedAppListLiveData.TAG, "doInBackground - pinnedAppsComponents = " + list);
                if (list != null) {
                    LGLog.i(PinnedAppListLiveData.TAG, "doInBackground - pinnedAppsComponents.size() = " + list.size());
                }
                for (String str : list) {
                    LGLog.i(PinnedAppListLiveData.TAG, "doInBackground - componentString = " + str);
                    Intent intent = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN, (Uri) null);
                    intent.setComponent(ComponentName.unflattenFromString(str));
                    intent.addCategory("android.intent.category.LAUNCHER");
                    List<ResolveInfo> listQueryIntentActivities = PinnedAppListLiveData.this.mPackageManager.queryIntentActivities(intent, 128);
                    LGLog.i(PinnedAppListLiveData.TAG, "doInBackground - apps = " + listQueryIntentActivities);
                    if (listQueryIntentActivities != null) {
                        LGLog.i(PinnedAppListLiveData.TAG, "doInBackground - apps.size() = " + listQueryIntentActivities.size());
                        for (ResolveInfo resolveInfo : listQueryIntentActivities) {
                            LGLog.i(PinnedAppListLiveData.TAG, "doInBackground - new AppEntry.  mPackageManager = " + PinnedAppListLiveData.this.mPackageManager);
                            arrayList.add(new AppEntry(resolveInfo, PinnedAppListLiveData.this.mPackageManager));
                        }
                    }
                }
                return arrayList;
            }

            /* JADX DEBUG: Method merged with bridge method: onPostExecute(Ljava/lang/Object;)V */
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(List<AppEntry> data) {
                if (PinnedAppListLiveData.this.mCurrentDataVersion == i) {
                    PinnedAppListLiveData.this.setValue(data);
                }
            }
        }.execute(new Void[0]);
    }
}
