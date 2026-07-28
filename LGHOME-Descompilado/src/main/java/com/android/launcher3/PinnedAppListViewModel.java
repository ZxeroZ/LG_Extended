package com.android.launcher3;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class PinnedAppListViewModel extends AndroidViewModel {
    static final String PINNED_APPS_KEY = "pinned_apps";
    private final PinnedAppListLiveData mLiveData;

    public PinnedAppListViewModel(Application application) {
        super(application);
        this.mLiveData = new PinnedAppListLiveData(application);
    }

    public LiveData<List<AppEntry>> getPinnedAppList() {
        return this.mLiveData;
    }
}
