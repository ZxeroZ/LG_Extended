package com.android.launcher3;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.lge.launcher3.util.LGLog;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AppListViewModel extends AndroidViewModel {
    private static final String TAG = "AppListViewModel";
    private final AppListLiveData mLiveData;
    private final PackageIntentReceiver mPackageIntentReceiver;

    public AppListViewModel(Application application) {
        super(application);
        LGLog.i(TAG, "create AppListViewModel. application : " + application);
        AppListLiveData appListLiveData = new AppListLiveData(application);
        this.mLiveData = appListLiveData;
        this.mPackageIntentReceiver = new PackageIntentReceiver(appListLiveData, application);
    }

    public LiveData<List<AppEntry>> getAppList() {
        return this.mLiveData;
    }

    @Override // androidx.lifecycle.ViewModel
    protected void onCleared() {
        getApplication().unregisterReceiver(this.mPackageIntentReceiver);
    }
}
