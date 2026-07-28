package com.lge.launcher3.hideapps;

import android.content.ComponentName;
import android.content.Context;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.AppFilter;
import com.android.launcher3.util.ComponentKey;
import com.lge.launcher3.R;
import com.lge.launcher3.util.UserUtils;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AppFilterImpl extends AppFilter {
    private static HashSet<ComponentKey> sAppsList;
    private static final Object sLock = new Object();
    private final Context mContext;

    public AppFilterImpl(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private void init() {
        sAppsList = new HashSet<>();
        for (String str : this.mContext.getResources().getStringArray(R.array.lg_exclude_apps)) {
            sAppsList.add(new ComponentKey(ComponentName.unflattenFromString(str), Process.myUserHandle()));
        }
        List<UserHandle> userProfiles = UserUtils.getUserManagerCompat(this.mContext).getUserProfiles();
        int size = userProfiles.size();
        for (int i = 0; i < size; i++) {
            UserHandle userHandle = userProfiles.get(i);
            Iterator<ComponentName> it = HideAppsStorage.getAllItems(this.mContext, userHandle).iterator();
            while (it.hasNext()) {
                sAppsList.add(new ComponentKey(it.next(), userHandle));
            }
        }
    }

    @Override // com.android.launcher3.AppFilter
    public boolean shouldShowApp(ComponentName app, UserHandle userHandle) {
        boolean z;
        synchronized (sLock) {
            if (sAppsList == null) {
                init();
            }
            z = !sAppsList.contains(new ComponentKey(app, userHandle));
        }
        return z;
    }

    public static void clearList() {
        synchronized (sLock) {
            HashSet<ComponentKey> hashSet = sAppsList;
            if (hashSet != null) {
                hashSet.clear();
            }
            sAppsList = null;
        }
    }
}
