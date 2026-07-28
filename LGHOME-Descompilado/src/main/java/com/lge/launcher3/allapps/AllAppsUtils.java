package com.lge.launcher3.allapps;

import android.animation.TimeInterpolator;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.compat.UserManagerCompat;
import com.lge.launcher3.util.LGActivityUtil;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.OrientationUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsUtils {
    private static final String APP_MENU_NUM_X = "app_xnumofmenu";
    private static final String APP_MENU_NUM_Y = "app_ynumofmenu";
    public static final int DEFAULT_ID = 0;
    private static final String LOG_TAG = "AllAppsUtils";
    private static final String MENU_NUM = "numofmenu";
    public static final int SWIVEL_ID = 1;
    private static ArrayList<AllAppsItemInfo> sAllAppsItemInfoList;
    private static boolean sAppOrientation;
    private static boolean sAppReload;
    private static boolean sNeedToSeriallization;

    public static class ZInterpolator implements TimeInterpolator {
        private final float focalLength;

        public ZInterpolator(float foc) {
            this.focalLength = foc;
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float input) {
            float f = this.focalLength;
            return (1.0f - (f / (input + f))) / (1.0f - (f / (f + 1.0f)));
        }
    }

    public static Drawable getCurrentWallpaper(Context context) {
        WallpaperManager wallpaperManager;
        if (context == null || (wallpaperManager = (WallpaperManager) context.getSystemService("wallpaper")) == null) {
            return null;
        }
        Drawable drawable = wallpaperManager.getDrawable();
        if (wallpaperManager.getWallpaperInfo() != null) {
            return null;
        }
        return drawable;
    }

    public static void saveLayoutNumToPreference(Context context, int numx, int numy) {
        if (context == null || LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(MENU_NUM, 0);
        boolean zIsPortrait = OrientationUtils.isPortrait(context);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editorEdit = sharedPreferences.edit();
            editorEdit.putInt(APP_MENU_NUM_X, zIsPortrait ? numx : numy);
            if (zIsPortrait) {
                numx = numy;
            }
            editorEdit.putInt(APP_MENU_NUM_Y, numx);
            editorEdit.commit();
        }
    }

    public static int[] getLayoutNumFromPreference(Context context, int[] mCountInfo) {
        return getLayoutNumFromPreference(context, mCountInfo, false);
    }

    public static int[] getLayoutNumFromPreference(Context context, int[] mCountInfo, boolean usePort) {
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            return mCountInfo;
        }
        if (context == null) {
            LGLog.i(LOG_TAG, "context is null");
            return new int[]{0, 0};
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(MENU_NUM, 0);
        int i = mCountInfo[0];
        int i2 = mCountInfo[1];
        boolean zIsPortrait = usePort ? true : OrientationUtils.isPortrait(context);
        String str = APP_MENU_NUM_X;
        int i3 = sharedPreferences.getInt(zIsPortrait ? APP_MENU_NUM_X : APP_MENU_NUM_Y, i);
        if (zIsPortrait) {
            str = APP_MENU_NUM_Y;
        }
        return new int[]{i3, sharedPreferences.getInt(str, i2)};
    }

    public static void setNeedToSeriallization(boolean value, String caller) {
        sNeedToSeriallization = value;
        LGLog.i(LOG_TAG, "[ALLAPPS_DB]setNeedToSeriallization : " + value + ", " + caller);
    }

    public static boolean needToSeriallization() {
        LGLog.i(LOG_TAG, "[ALLAPPS_DB]needToSeriallization : " + sNeedToSeriallization);
        return sNeedToSeriallization;
    }

    public static boolean getAppReload() {
        return sAppReload;
    }

    public static ArrayList<AllAppsItemInfo> getAllAppsItemInfoList() {
        if (sAllAppsItemInfoList == null) {
            sAppReload = false;
            sAllAppsItemInfoList = new ArrayList<>();
        } else {
            sAppReload = true;
        }
        return sAllAppsItemInfoList;
    }

    public static void setAllAppsItemInfoList(ArrayList<AllAppsItemInfo> list) {
        sAllAppsItemInfoList = list;
    }

    public static void setAppLastOrientation(boolean set) {
        sAppOrientation = set;
    }

    public static boolean getAppLastOrientation() {
        return sAppOrientation;
    }

    public static boolean checkExternalApp(Context context, String cpName) {
        ComponentName componentNameUnflattenFromString;
        String packageName;
        if (cpName == null || (componentNameUnflattenFromString = ComponentName.unflattenFromString(cpName)) == null || (packageName = componentNameUnflattenFromString.getPackageName()) == null) {
            return false;
        }
        return LGActivityUtil.isExternalApp(context, packageName);
    }

    public static long getSerialNumberForUser(Context context, AllAppsItemInfo itemInfo) {
        UserManagerCompat userManagerCompat = UserManagerCompat.getInstance(context);
        UserHandle userHandleMyUserHandle = Process.myUserHandle();
        if (itemInfo.itemType != 0) {
            AllAppsFolderInfo allAppsFolderInfo = itemInfo.mFolderInfo;
            if (allAppsFolderInfo != null) {
                userHandleMyUserHandle = allAppsFolderInfo.user;
            }
        } else if (itemInfo != null) {
            userHandleMyUserHandle = itemInfo.user;
        }
        return userManagerCompat.getSerialNumberForUser(userHandleMyUserHandle);
    }
}
