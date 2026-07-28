package com.lge.launcher3.receiver;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.os.Build;
import android.os.Process;
import android.os.SystemProperties;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.LongArrayMap;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class FiSimMode {
    public static final String PROPERTY_PERSIST_SYS_SIM_OPERATOR_GOOGLEFI;
    public static final String TAG = "FiSimMode";
    public static final ComponentName mFiDialCN = new ComponentName("com.google.android.dialer", "com.google.android.dialer.extensions.GoogleDialtactsActivity");
    public static final ComponentName mLGDialCN = new ComponentName("com.android.contacts", LauncherConst.ANDROID_PHONE_CLASS_NAME);
    public static final ComponentName mGoogleContactCN = new ComponentName("com.google.android.contacts", "com.android.contacts.activities.PeopleActivity");
    public static final ComponentName mLGContactCN = new ComponentName("com.android.contacts", "com.android.contacts.activities.PeopleActivity");

    static {
        PROPERTY_PERSIST_SYS_SIM_OPERATOR_GOOGLEFI = Build.VERSION.SDK_INT < 28 ? "persist.sys.sim.operator.googlefi" : "persist.product.lge.sim.operator.googlefi";
    }

    public static void changeFiSimMode(Context context) {
        Boolean bool;
        String str = SystemProperties.get(PROPERTY_PERSIST_SYS_SIM_OPERATOR_GOOGLEFI, "0");
        if (str.equals("0")) {
            LGLog.i(TAG, "mode = TO_NOT_FI_SIM_MODE");
            bool = false;
        } else if (str.equals("1")) {
            LGLog.i(TAG, "mode = TO_FI_SIM_MODE");
            bool = true;
        } else {
            LGLog.i(TAG, "SystemProperties are not both 0 and 1");
            return;
        }
        LongArrayMap<ItemInfo> longArrayMapClone = LauncherModel.sBgDataModel.itemsIdMap.clone();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        ShortcutInfo shortcutInfoCopyShortcutInfo = null;
        ShortcutInfo shortcutInfoCopyShortcutInfo2 = null;
        ShortcutInfo shortcutInfoCopyShortcutInfo3 = null;
        ShortcutInfo shortcutInfoCopyShortcutInfo4 = null;
        for (ItemInfo itemInfo : longArrayMapClone) {
            if (itemInfo instanceof ShortcutInfo) {
                ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                ComponentName component = itemInfo.getIntent().getComponent();
                if (component != null && shortcutInfo.itemType != 6) {
                    if (component.flattenToString().equals(mFiDialCN.flattenToString())) {
                        shortcutInfoCopyShortcutInfo = copyShortcutInfo(shortcutInfo, arrayList);
                    } else if (component.flattenToString().equals(mLGDialCN.flattenToString()) && itemInfo.user.equals(Process.myUserHandle())) {
                        shortcutInfoCopyShortcutInfo2 = copyShortcutInfo(shortcutInfo, arrayList2);
                    } else if (component.flattenToString().equals(mGoogleContactCN.flattenToString())) {
                        shortcutInfoCopyShortcutInfo3 = copyShortcutInfo(shortcutInfo, arrayList3);
                    } else if (component.flattenToString().equals(mLGContactCN.flattenToString())) {
                        shortcutInfoCopyShortcutInfo4 = copyShortcutInfo(shortcutInfo, arrayList4);
                    }
                }
            }
        }
        if (LGHomeFeature.isEnableDefaultHome()) {
            if (swapFiSimModeIcon(context, bool, shortcutInfoCopyShortcutInfo, shortcutInfoCopyShortcutInfo2) && swapFiSimModeIcon(context, bool, shortcutInfoCopyShortcutInfo3, shortcutInfoCopyShortcutInfo4)) {
                LauncherAppState.getInstance(context).getModel().forceReload();
                return;
            }
            return;
        }
        if (bool.booleanValue() && arrayList2.size() > 0) {
            shortcutIconAllChangeMode(context, mFiDialCN, arrayList2);
        } else if (!bool.booleanValue() && arrayList.size() > 0) {
            shortcutIconAllChangeMode(context, mLGDialCN, arrayList);
        }
        if (bool.booleanValue() && arrayList4.size() > 0) {
            shortcutIconAllChangeMode(context, mGoogleContactCN, arrayList4);
        } else {
            if (bool.booleanValue() || arrayList3.size() <= 0) {
                return;
            }
            shortcutIconAllChangeMode(context, mLGContactCN, arrayList3);
        }
    }

    private static ShortcutInfo copyShortcutInfo(ShortcutInfo si, ArrayList<ShortcutInfo> infoArray) {
        ShortcutInfo shortcutInfo = new ShortcutInfo();
        shortcutInfo.copyFrom(si);
        infoArray.add(new ShortcutInfo(si));
        return shortcutInfo;
    }

    private static boolean swapFiSimModeIcon(Context context, Boolean mode, ShortcutInfo mFiSimDialItem, ShortcutInfo mLGDialItem) {
        if (mFiSimDialItem == null || mLGDialItem == null) {
            LGLog.d(TAG, "mode = " + mode + ", Can't find Dialer");
            return false;
        }
        if (mode.booleanValue()) {
            updateDBFiSimModeIcon(context, mLGDialItem, mFiSimDialItem);
            return true;
        }
        updateDBFiSimModeIcon(context, mFiSimDialItem, mLGDialItem);
        return true;
    }

    private static void updateDBFiSimModeIcon(Context context, ShortcutInfo disableItem, ShortcutInfo enableItem) {
        ShortcutInfo shortcutInfo = new ShortcutInfo();
        shortcutInfo.copyFrom(enableItem);
        shortcutInfo.container = disableItem.container;
        shortcutInfo.screenId = disableItem.screenId;
        shortcutInfo.cellX = disableItem.cellX;
        shortcutInfo.cellY = disableItem.cellY;
        if (LauncherModel.getParentFolder(disableItem) != null) {
            shortcutInfo.rank = disableItem.rank;
        }
        LauncherModel.deleteItemFromDatabase(context, enableItem);
        LauncherModel.addItemToDatabase(context, shortcutInfo, shortcutInfo.container, shortcutInfo.screenId, shortcutInfo.cellX, shortcutInfo.cellY);
    }

    private static void shortcutIconAllChangeMode(Context context, ComponentName phoneModePkg, ArrayList<ShortcutInfo> toBeChangeArray) {
        AppInfo appInfo = getAppInfo(context, phoneModePkg);
        String str = TAG;
        LGLog.i(str, "To Be Change Array size = " + toBeChangeArray.size());
        if (appInfo != null) {
            for (int i = 0; i < toBeChangeArray.size(); i++) {
                ShortcutInfo shortcutInfo = new ShortcutInfo(appInfo);
                shortcutInfo.id = toBeChangeArray.get(i).id;
                shortcutInfo.container = toBeChangeArray.get(i).container;
                shortcutInfo.screenId = toBeChangeArray.get(i).screenId;
                shortcutInfo.cellX = toBeChangeArray.get(i).cellX;
                shortcutInfo.cellY = toBeChangeArray.get(i).cellY;
                if (LauncherModel.getParentFolder(toBeChangeArray.get(i)) != null) {
                    shortcutInfo.rank = toBeChangeArray.get(i).rank;
                }
                LauncherModel.deleteItemFromDatabase(context, toBeChangeArray.get(i));
                LauncherModel.addItemToDatabase(context, shortcutInfo, shortcutInfo.container, shortcutInfo.screenId, shortcutInfo.cellX, shortcutInfo.cellY);
            }
            LauncherAppState.getInstance(context).getModel().forceReload();
            return;
        }
        LGLog.i(str, "cannot find phoneModeInfo appinfo");
    }

    private static AppInfo getAppInfo(Context context, ComponentName componentName) {
        for (LauncherActivityInfo launcherActivityInfo : LauncherAppsCompat.getInstance(context).getActivityList(componentName.getPackageName(), Process.myUserHandle())) {
            if (componentName.equals(launcherActivityInfo.getComponentName())) {
                return new AppInfo(context, launcherActivityInfo, Process.myUserHandle(), LauncherAppState.getInstance(context).getIconCache());
            }
        }
        return null;
    }
}
