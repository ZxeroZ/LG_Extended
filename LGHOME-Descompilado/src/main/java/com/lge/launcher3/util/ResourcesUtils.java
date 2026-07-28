package com.lge.launcher3.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/* JADX INFO: loaded from: classes.dex */
public class ResourcesUtils {
    public static final String TAG = "ResourcesUtils";

    public static Drawable getDrawable(Context context, String packageName, String resourceName, int density) {
        try {
            Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication(packageName);
            if (resourcesForApplication != null) {
                return resourcesForApplication.getDrawableForDensity(resourcesForApplication.getIdentifier(resourceName, "Drawable", null), density);
            }
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.i(TAG, String.format("getDrawable() : NameNotFoundException(%s)", e.toString()));
        }
        return null;
    }
}
