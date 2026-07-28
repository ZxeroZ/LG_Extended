package com.android.launcher3.shortcuts;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.os.UserHandle;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ShortcutRequest {
    public static final int ALL = 11;
    public static final int PINNED = 2;
    public static final int PUBLISHED = 9;
    private static final String TAG = "ShortcutRequest";
    private final Context mContext;
    private final UserHandle mUserHandle;
    private final LauncherApps.ShortcutQuery mQuery = new LauncherApps.ShortcutQuery();
    boolean mFailed = false;

    public ShortcutRequest(Context context, UserHandle userHandle) {
        this.mContext = context;
        this.mUserHandle = userHandle;
    }

    public ShortcutRequest forPackage(String packageName) {
        return forPackage(packageName, (List<String>) null);
    }

    public ShortcutRequest forPackage(String packageName, String... shortcutIds) {
        return forPackage(packageName, Arrays.asList(shortcutIds));
    }

    public ShortcutRequest forPackage(String packageName, List<String> shortcutIds) {
        if (packageName != null) {
            this.mQuery.setPackage(packageName);
            this.mQuery.setShortcutIds(shortcutIds);
        }
        return this;
    }

    public ShortcutRequest withContainer(ComponentName activity) {
        if (activity == null) {
            this.mFailed = true;
        } else {
            this.mQuery.setActivity(activity);
        }
        return this;
    }

    public QueryResult query(int flags) {
        if (this.mFailed) {
            return QueryResult.DEFAULT;
        }
        this.mQuery.setQueryFlags(flags);
        try {
            return new QueryResult(((LauncherApps) this.mContext.getSystemService(LauncherApps.class)).getShortcuts(this.mQuery, this.mUserHandle));
        } catch (IllegalStateException | SecurityException e) {
            Log.e(TAG, "Failed to query for shortcuts", e);
            return QueryResult.DEFAULT;
        }
    }

    public static class QueryResult extends ArrayList<ShortcutInfo> {
        static final QueryResult DEFAULT = new QueryResult(false);
        private final boolean mWasSuccess;

        QueryResult(List<ShortcutInfo> result) {
            super(result == null ? Collections.emptyList() : result);
            this.mWasSuccess = true;
        }

        QueryResult(boolean wasSuccess) {
            this.mWasSuccess = wasSuccess;
        }

        public boolean wasSuccess() {
            return this.mWasSuccess;
        }
    }
}
