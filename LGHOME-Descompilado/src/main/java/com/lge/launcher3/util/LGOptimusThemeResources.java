package com.lge.launcher3.util;

import android.content.Context;
import android.content.pm.PackageManager;

/* JADX INFO: loaded from: classes.dex */
public class LGOptimusThemeResources extends LGResourceManager {
    private static LGOptimusThemeResources sLGOptimusThemeResource;

    public static final LGOptimusThemeResources getInstance(Context context) {
        if (sLGOptimusThemeResource == null) {
            sLGOptimusThemeResource = new LGOptimusThemeResources(context);
        }
        return sLGOptimusThemeResource;
    }

    public LGOptimusThemeResources(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            this.mPackageName = "com.lge.launcher2.theme.optimus";
            this.mResources = packageManager.getResourcesForApplication(this.mPackageName);
        } catch (PackageManager.NameNotFoundException unused) {
            LGLog.d("LGOptimusThemeResource", "Optimus Theme Resource Exception.");
        }
    }
}
