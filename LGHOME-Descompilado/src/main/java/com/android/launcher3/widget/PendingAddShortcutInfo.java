package com.android.launcher3.widget;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Process;
import com.android.launcher3.Launcher;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;
import com.android.launcher3.icons.IconCache;

/* JADX INFO: loaded from: classes.dex */
public class PendingAddShortcutInfo extends PendingAddItemInfo {
    public static final int DOWNLOADED_FLAG = 1;
    public static final int UPDATED_SYSTEM_APP_FLAG = 2;
    public ShortcutConfigActivityInfo activityInfo;
    public int flags;
    public Launcher mLauncher;

    public PendingAddShortcutInfo(final ActivityInfo activityInfo, final Launcher launcher) {
        this.flags = 0;
        this.componentName = new ComponentName(activityInfo.packageName, activityInfo.name);
        this.itemType = 1;
        this.flags = initFlags(activityInfo);
        this.mLauncher = launcher;
        this.activityInfo = new ShortcutConfigActivityInfo(this.componentName, Process.myUserHandle()) { // from class: com.android.launcher3.widget.PendingAddShortcutInfo.1
            @Override // com.android.launcher3.compat.ShortcutConfigActivityInfo
            public CharSequence getLabel() {
                return activityInfo.loadLabel(PendingAddShortcutInfo.this.mLauncher.getPackageManager());
            }

            @Override // com.android.launcher3.compat.ShortcutConfigActivityInfo
            public Drawable getFullResIcon(IconCache cache) {
                return cache.getFullResIcon(activityInfo);
            }
        };
    }

    public PendingAddShortcutInfo(ShortcutConfigActivityInfo activityInfo) {
        this.flags = 0;
        this.activityInfo = activityInfo;
        this.componentName = activityInfo.getComponent();
        this.user = activityInfo.getUser();
        this.itemType = 1;
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public String toString() {
        return String.format("PendingAddShortcutInfo package=%s, name=%s", this.componentName.getPackageName(), this.componentName.getClassName());
    }

    public int initFlags(ActivityInfo info) {
        int i = info.applicationInfo.flags;
        if ((i & 1) == 0) {
            return (i & 128) != 0 ? 3 : 1;
        }
        return 0;
    }
}
