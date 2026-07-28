package com.android.launcher3.compat;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import com.android.launcher3.util.PackageUserKey;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class AppWidgetManagerCompatVO extends AppWidgetManagerCompatVL {
    AppWidgetManagerCompatVO(Context context) {
        super(context);
    }

    @Override // com.android.launcher3.compat.AppWidgetManagerCompatVL, com.android.launcher3.compat.AppWidgetManagerCompat
    public List<AppWidgetProviderInfo> getAllProviders(PackageUserKey packageUser) {
        if (packageUser == null) {
            return super.getAllProviders(null);
        }
        return this.mAppWidgetManager.getInstalledProvidersForPackage(packageUser.mPackageName, packageUser.mUser);
    }
}
