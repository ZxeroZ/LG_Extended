package com.lge.launcher3.util;

import android.content.Context;
import android.content.pm.PackageManager;

/* JADX INFO: loaded from: classes.dex */
public class LGHomeResources extends LGResourceManager {
    private static LGHomeResources sLGHomeResource;

    public static final LGHomeResources getInstance(Context context) {
        if (sLGHomeResource == null) {
            sLGHomeResource = new LGHomeResources(context);
        }
        return sLGHomeResource;
    }

    public LGHomeResources(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            this.mPackageName = context.getPackageName();
            this.mResources = packageManager.getResourcesForApplication(this.mPackageName);
        } catch (PackageManager.NameNotFoundException unused) {
            LGLog.d("LGHomeResource", "LGHome Resources Exception.");
        }
    }
}
