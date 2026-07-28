package com.android.launcher3.allapps;

import android.os.Handler;
import com.android.launcher3.allapps.AllAppsSearchBarController;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.util.ComponentKey;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class DefaultAppSearchAlgorithm {
    private static final Pattern SPLIT_PATTERN = Pattern.compile("[\\s|\\p{javaSpaceChar}]+");
    private final List<AppInfo> mApps;
    protected final Handler mResultHandler = new Handler();

    public DefaultAppSearchAlgorithm(List<AppInfo> apps) {
        this.mApps = apps;
    }

    public void cancel(boolean interruptActiveRequests) {
        if (interruptActiveRequests) {
            this.mResultHandler.removeCallbacksAndMessages(null);
        }
    }

    public void doSearch(final String query, final AllAppsSearchBarController.Callbacks callback) {
        final ArrayList<ComponentKey> titleMatchResult = getTitleMatchResult(query);
        this.mResultHandler.post(new Runnable() { // from class: com.android.launcher3.allapps.DefaultAppSearchAlgorithm.1
            @Override // java.lang.Runnable
            public void run() {
                callback.onSearchResult(query, titleMatchResult);
            }
        });
    }

    protected ArrayList<ComponentKey> getTitleMatchResult(String query) {
        String[] strArrSplit = SPLIT_PATTERN.split(query.toLowerCase());
        ArrayList<ComponentKey> arrayList = new ArrayList<>();
        for (AppInfo appInfo : this.mApps) {
            if (matches(appInfo, strArrSplit)) {
                arrayList.add(appInfo.toComponentKey());
            }
        }
        return arrayList;
    }

    protected boolean matches(AppInfo info, String[] queryWords) {
        String[] strArrSplit = SPLIT_PATTERN.split(info.title.toString().toLowerCase());
        int i = 0;
        while (true) {
            boolean z = true;
            if (i >= queryWords.length) {
                return true;
            }
            int i2 = 0;
            while (true) {
                if (i2 >= strArrSplit.length) {
                    z = false;
                    break;
                }
                if (strArrSplit[i2].startsWith(queryWords[i])) {
                    break;
                }
                i2++;
            }
            if (!z) {
                return false;
            }
            i++;
        }
    }
}
