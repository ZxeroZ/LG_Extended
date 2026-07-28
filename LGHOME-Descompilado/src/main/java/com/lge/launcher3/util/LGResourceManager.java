package com.lge.launcher3.util;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.lge.launcher3.config.LauncherConst;

/* JADX INFO: loaded from: classes.dex */
public abstract class LGResourceManager {
    private static final String TAG = "LGResourceManager";
    protected String mPackageName = null;
    protected Resources mResources = null;

    public int getIdentifier(String resoureName, String resourceType) {
        try {
            return this.mResources.getIdentifier(resoureName, resourceType, this.mPackageName);
        } catch (Exception unused) {
            Log.d(TAG, "Couldn't load getIdentifier(" + resoureName + ", " + resourceType + ")" + this.mPackageName);
            return 0;
        }
    }

    public int getInteger(String resoureName, int defaultValue) {
        try {
            return this.mResources.getInteger(this.mResources.getIdentifier(resoureName, LauncherConst.RESOURCE_INTEGER_TYPE, this.mPackageName));
        } catch (Exception unused) {
            LGLog.d(TAG, "Couldn't load getInteger (" + resoureName + ")" + this.mPackageName);
            return defaultValue;
        }
    }

    public String[] getStringArray(String resoureName) {
        try {
            return this.mResources.getStringArray(this.mResources.getIdentifier(resoureName, LauncherConst.RESOURCE_ARRAY_TYPE, this.mPackageName));
        } catch (Exception unused) {
            Log.d(TAG, "Couldn't load getStringArray(" + resoureName + ")" + this.mPackageName);
            return null;
        }
    }

    public Drawable getDrawable(String resoureName) {
        try {
            return getDrawable(this.mResources.getIdentifier(resoureName, LauncherConst.RESOURCE_IMAGE_TYPE, this.mPackageName));
        } catch (Exception unused) {
            Log.d(TAG, "Couldn't load getDrawable (" + resoureName + ")" + this.mPackageName);
            return null;
        }
    }

    public Drawable getDrawable(int ResId) {
        try {
            return this.mResources.getDrawable(ResId);
        } catch (Exception unused) {
            Log.d(TAG, "Couldn't load getDrawable (id: " + ResId + ")" + this.mPackageName);
            return null;
        }
    }
}
