package com.android.launcher3.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.util.ComponentKey;
import com.lge.launcher3.widgettray.GroupLauncherAppWidgetProviderInfo;
import com.lge.launcher3.widgettray.GroupResolveInfo;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class WidgetsAndShortcutNameComparator implements Comparator<Object> {
    private final AppWidgetManagerCompat mManager;
    private final PackageManager mPackageManager;
    private final HashMap<ComponentKey, String> mLabelCache = new HashMap<>();
    private final Collator mCollator = Collator.getInstance();
    private final UserHandle mMainHandle = Process.myUserHandle();

    public WidgetsAndShortcutNameComparator(Context context) {
        this.mManager = AppWidgetManagerCompat.getInstance(context);
        this.mPackageManager = context.getPackageManager();
    }

    @Override // java.util.Comparator
    public final int compare(Object a, Object b) {
        String str;
        String str2;
        ComponentKey componentKey = getComponentKey(a);
        ComponentKey componentKey2 = getComponentKey(b);
        if (this.mLabelCache.containsKey(componentKey)) {
            str = this.mLabelCache.get(componentKey);
        } else {
            String strLoadLabel = loadLabel(a);
            this.mLabelCache.put(componentKey, strLoadLabel);
            str = strLoadLabel;
        }
        if (this.mLabelCache.containsKey(componentKey2)) {
            str2 = this.mLabelCache.get(componentKey2);
        } else {
            String strLoadLabel2 = loadLabel(b);
            this.mLabelCache.put(componentKey2, strLoadLabel2);
            str2 = strLoadLabel2;
        }
        boolean z = false;
        boolean z2 = (a instanceof LauncherAppWidgetProviderInfo) && !this.mMainHandle.equals(this.mManager.getUser((LauncherAppWidgetProviderInfo) a));
        if ((b instanceof LauncherAppWidgetProviderInfo) && !this.mMainHandle.equals(this.mManager.getUser((LauncherAppWidgetProviderInfo) b))) {
            z = true;
        }
        if (z2 && !z) {
            return 1;
        }
        if (z2 || !z) {
            return this.mCollator.compare(str, str2);
        }
        return -1;
    }

    public String loadLabel(Object info) {
        if (info instanceof GroupLauncherAppWidgetProviderInfo) {
            return ((GroupLauncherAppWidgetProviderInfo) info).getLabel();
        }
        if (info instanceof GroupResolveInfo) {
            return ((GroupResolveInfo) info).getLabel();
        }
        if (info instanceof LauncherAppWidgetProviderInfo) {
            return Utilities.trim(this.mManager.loadLabel((LauncherAppWidgetProviderInfo) info));
        }
        return Utilities.trim(((ResolveInfo) info).loadLabel(this.mPackageManager));
    }

    private ComponentKey getComponentKey(Object o) {
        if (o instanceof LauncherAppWidgetProviderInfo) {
            LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo = (LauncherAppWidgetProviderInfo) o;
            return new ComponentKey(launcherAppWidgetProviderInfo.provider, this.mManager.getUser(launcherAppWidgetProviderInfo));
        }
        ResolveInfo resolveInfo = (ResolveInfo) o;
        return new ComponentKey(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name), Process.myUserHandle());
    }

    public void clearCache() {
        this.mLabelCache.clear();
    }
}
