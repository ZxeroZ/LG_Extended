package com.android.launcher3.model.data;

import android.content.Context;
import android.content.Intent;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.PackageInstallerCompat;
import com.android.launcher3.util.PackageManagerHelper;
import com.lge.launcher3.util.PackageUtils;

/* JADX INFO: loaded from: classes.dex */
public class PromiseAppInfo extends AppInfo {
    public int level = 0;

    public PromiseAppInfo(PackageInstallerCompat.PackageInstallInfo installInfo) {
        this.intent = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory("android.intent.category.LAUNCHER").setComponent(this.componentName).setFlags(270532608);
    }

    @Override // com.android.launcher3.model.data.AppInfo
    public ShortcutInfo makeShortcut() {
        ShortcutInfo shortcutInfo = new ShortcutInfo(this);
        shortcutInfo.setInstallProgress(this.level);
        shortcutInfo.status |= 2;
        shortcutInfo.status |= 8;
        return shortcutInfo;
    }

    public Intent getMarketIntent(Context context) {
        return new PackageManagerHelper(context).getMarketIntent(this.componentName.getPackageName());
    }
}
