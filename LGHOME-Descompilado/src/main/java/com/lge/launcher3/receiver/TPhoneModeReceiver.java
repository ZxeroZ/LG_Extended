package com.lge.launcher3.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.LauncherActivityInfo;
import android.os.Process;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.LongArrayMap;
import com.lge.launcher3.R;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.profile.LGInvariantDeviceProfile;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.TPhoneModeUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class TPhoneModeReceiver extends BroadcastReceiver {
    public static final String TAG = "TPhoneModeReceiver";
    public static final ComponentName mTDialCN = new ComponentName(LauncherConst.SKT_PHONE_PACKAGE_NAME, LauncherConst.SKT_PHONE_CLASS_NAME);
    public static final ComponentName mTContactCN = new ComponentName(LauncherConst.SKT_PHONE_CONTACT_PACKAGE_NAME, LauncherConst.SKT_PHONE_CONTACT_CLASS_NAME);
    public static final ComponentName mLGDialCN = new ComponentName("com.android.contacts", LauncherConst.ANDROID_PHONE_CLASS_NAME);

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (IntentConst.Action.ACTION_RELOAD_TPHONEMODE.getValue(context).equals(action)) {
            LGLog.d(TAG, "IntentConst.Action.ACTION_RELOAD_TPHONEMODE is received");
            changeTPhoneMode(context, intent.getIntExtra("modeAfter", 2));
            return;
        }
        if ("android.telecom.action.DEFAULT_DIALER_CHANGED".equals(action)) {
            String stringExtra = intent.getStringExtra("android.telecom.extra.CHANGE_DEFAULT_DIALER_PACKAGE_NAME");
            int phoneMode = TPhoneModeUtils.getPhoneMode(stringExtra);
            LGLog.d(TAG, "ACTION_CHANGE_DEFAULT_DIALER is received : packageName = " + stringExtra + ", modeAfter = " + phoneMode);
            changeTPhoneMode(context, phoneMode);
        }
    }

    public void registerReceiver(Context context) {
        LGLog.d(TAG, "Register TPhoneModeReceiver");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IntentConst.Action.ACTION_RELOAD_TPHONEMODE.getValue(context));
        intentFilter.addAction("android.telecom.action.DEFAULT_DIALER_CHANGED");
        context.registerReceiver(this, intentFilter);
    }

    public void unregisterReceiver(Context context) {
        LGLog.d(TAG, "Unregister TPhoneModeReceiver");
        try {
            context.unregisterReceiver(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void changeTPhoneMode(Context context, int modeAfter) {
        LongArrayMap<ItemInfo> longArrayMapClone = LauncherModel.sBgDataModel.itemsIdMap.clone();
        ArrayList<ShortcutInfo> arrayList = new ArrayList<>();
        ArrayList<ShortcutInfo> arrayList2 = new ArrayList<>();
        ShortcutInfo shortcutInfo = null;
        ShortcutInfo shortcutInfo2 = null;
        for (ItemInfo itemInfo : longArrayMapClone) {
            if (itemInfo instanceof ShortcutInfo) {
                ShortcutInfo shortcutInfo3 = (ShortcutInfo) itemInfo;
                ComponentName component = itemInfo.getIntent().getComponent();
                if (component != null && shortcutInfo3.itemType != 6) {
                    if (component.flattenToString().equals(mTDialCN.flattenToString())) {
                        shortcutInfo = new ShortcutInfo();
                        shortcutInfo.copyFrom(shortcutInfo3);
                        arrayList.add(new ShortcutInfo(shortcutInfo3));
                    } else if (component.flattenToString().equals(mTContactCN.flattenToString())) {
                        new ShortcutInfo().copyFrom(shortcutInfo3);
                    } else if (component.flattenToString().equals(mLGDialCN.flattenToString()) && itemInfo.user.equals(Process.myUserHandle())) {
                        shortcutInfo2 = new ShortcutInfo();
                        shortcutInfo2.copyFrom(shortcutInfo3);
                        arrayList2.add(new ShortcutInfo(shortcutInfo3));
                    }
                }
            }
        }
        if (LGHomeFeature.isEnableDefaultHome()) {
            int i = SharedPreferencesManager.getInt(context, 0, SharedPreferencesConst.TPhoneMode.T_PHONE_MODE, -1);
            SharedPreferencesManager.putInt(context, 0, SharedPreferencesConst.TPhoneMode.T_PHONE_MODE, modeAfter);
            if (i == modeAfter || !swapTPhoneModeIcon(context, modeAfter, shortcutInfo, shortcutInfo2)) {
                return;
            }
            LauncherAppState.getInstance(context).getModel().forceReload();
            return;
        }
        if (!LGHomeFeature.isDisableEasyHome()) {
            if (SharedPreferencesManager.getInt(context, 0, SharedPreferencesConst.TPhoneMode.T_PHONE_MODE_EASYHOME, -1) != modeAfter) {
                SharedPreferencesManager.putInt(context, 0, SharedPreferencesConst.TPhoneMode.T_PHONE_MODE_EASYHOME, modeAfter);
                if (modeAfter == 0 && arrayList.size() > 0) {
                    allChangeTPhoneModeIcon(context, mLGDialCN, arrayList, modeAfter);
                    return;
                } else {
                    if (modeAfter != 1 || arrayList2.size() <= 0) {
                        return;
                    }
                    allChangeTPhoneModeIcon(context, mTDialCN, arrayList2, modeAfter);
                    return;
                }
            }
            return;
        }
        if (SharedPreferencesManager.getInt(context, 0, SharedPreferencesConst.TPhoneMode.T_PHONE_MODE_APPDRAWER, -1) != modeAfter) {
            SharedPreferencesManager.putInt(context, 0, SharedPreferencesConst.TPhoneMode.T_PHONE_MODE_APPDRAWER, modeAfter);
            if (modeAfter == 0 && arrayList.size() > 0) {
                allChangeTPhoneModeIcon(context, mLGDialCN, arrayList, modeAfter);
            } else {
                if (modeAfter != 1 || arrayList2.size() <= 0) {
                    return;
                }
                allChangeTPhoneModeIcon(context, mTDialCN, arrayList2, modeAfter);
            }
        }
    }

    private void allChangeTPhoneModeIcon(Context context, ComponentName phoneModePkg, ArrayList<ShortcutInfo> toBeChangeArray, int modeAfter) {
        AppInfo appInfo = getAppInfo(context, phoneModePkg);
        String str = TAG;
        LGLog.i(str, "To Be Chagne Array size = " + toBeChangeArray.size());
        if (appInfo != null) {
            for (int i = 0; i < toBeChangeArray.size(); i++) {
                ShortcutInfo shortcutInfo = new ShortcutInfo(appInfo);
                shortcutInfo.id = toBeChangeArray.get(i).id;
                shortcutInfo.container = toBeChangeArray.get(i).container;
                shortcutInfo.screenId = toBeChangeArray.get(i).screenId;
                shortcutInfo.cellX = toBeChangeArray.get(i).cellX;
                shortcutInfo.cellY = toBeChangeArray.get(i).cellY;
                LauncherModel.updateItemInDatabase(context, shortcutInfo);
            }
            LauncherAppState.getInstance(context).getModel().forceReload();
            return;
        }
        LGLog.i(str, "cannot find phoneModeInfo appinfo");
    }

    private boolean swapTPhoneModeIcon(Context context, int modeAfter, ShortcutInfo mTDialItem, ShortcutInfo mLGDialItem) {
        int size;
        boolean z;
        int size2;
        boolean z2;
        if (mTDialItem == null || mLGDialItem == null) {
            LGLog.i(TAG, "modeAfter = " + modeAfter + ", illegal parameter");
            return false;
        }
        if (modeAfter == 0) {
            String str = TAG;
            LGLog.i(str, "modeAfter = SKT_PHONE_MODE_OEM");
            FolderInfo parentFolder = LauncherModel.getParentFolder(mTDialItem);
            if (parentFolder == null || parentFolder.container != -101) {
                size2 = 0;
                z2 = false;
            } else {
                size2 = parentFolder.contents.size();
                z2 = true;
            }
            if (mTDialItem.container != -101 && !z2) {
                LGLog.i(str, "modeAfter = " + modeAfter + ", swapTPhoneModeIcon is not processed");
                return false;
            }
            FolderInfo parentFolder2 = LauncherModel.getParentFolder(mLGDialItem);
            if (mLGDialItem.container == -101 || (parentFolder2 != null && parentFolder2.container == -101)) {
                LGLog.i(str, "modeAfter = " + modeAfter + ", Already in HotSeat, swapTPhoneModeIcon is not processed");
                return false;
            }
            updateDBTPhoneModeIcon(context, mTDialItem, mLGDialItem, modeAfter, size2);
            return true;
        }
        if (modeAfter == 1) {
            String str2 = TAG;
            LGLog.i(str2, "modeAfter = SKT_PHONE_MODE_T_PHONE");
            FolderInfo parentFolder3 = LauncherModel.getParentFolder(mLGDialItem);
            if (parentFolder3 == null || parentFolder3.container != -101) {
                size = 0;
                z = false;
            } else {
                size = parentFolder3.contents.size();
                z = true;
            }
            if (mLGDialItem.container != -101 && !z) {
                LGLog.i(str2, "modeAfter = " + modeAfter + ", swapTPhoneModeIcon is not processed");
                return false;
            }
            FolderInfo parentFolder4 = LauncherModel.getParentFolder(mTDialItem);
            if (mTDialItem.container == -101 || (parentFolder4 != null && parentFolder4.container == -101)) {
                LGLog.i(str2, "modeAfter = " + modeAfter + ", Already in HotSeat, swapTPhoneModeIcon is not processed");
                return false;
            }
            updateDBTPhoneModeIcon(context, mLGDialItem, mTDialItem, modeAfter, size);
            return true;
        }
        LGLog.i(TAG, "modeAfter = " + modeAfter);
        return false;
    }

    private void updateDBTPhoneModeIcon(Context context, ShortcutInfo mToWSItem, ShortcutInfo mToHSItem, int modeAfter, int itemCountInFolder) {
        ShortcutInfo shortcutInfo = new ShortcutInfo();
        shortcutInfo.copyFrom(mToHSItem);
        shortcutInfo.container = mToWSItem.container;
        shortcutInfo.screenId = mToWSItem.screenId;
        shortcutInfo.cellX = mToWSItem.cellX;
        shortcutInfo.cellY = mToWSItem.cellY;
        shortcutInfo.rank = itemCountInFolder;
        ShortcutInfo shortcutInfo2 = new ShortcutInfo();
        shortcutInfo2.copyFrom(mToWSItem);
        shortcutInfo2.container = mToHSItem.container;
        shortcutInfo2.screenId = mToHSItem.screenId;
        shortcutInfo2.cellX = mToHSItem.cellX;
        shortcutInfo2.cellY = mToHSItem.cellY;
        ItemInfo lastItemInWorkspace = LauncherModel.getLastItemInWorkspace();
        if (lastItemInWorkspace == null) {
            return;
        }
        LauncherModel.deleteItemFromDatabase(context, mToHSItem);
        LauncherModel.deleteItemFromDatabase(context, mToWSItem);
        LauncherModel.addItemToDatabase(context, shortcutInfo, shortcutInfo.container, shortcutInfo.screenId, shortcutInfo.cellX, shortcutInfo.cellY);
        if (modeAfter == 0) {
            moveItemToLastPos(context, shortcutInfo2, lastItemInWorkspace);
            return;
        }
        if (modeAfter == 1) {
            FolderInfo lGFolderInWorkspace = LauncherModel.getLGFolderInWorkspace((String) context.getText(R.string.lg_folder_name));
            if (lGFolderInWorkspace == null) {
                moveItemToLastPos(context, shortcutInfo2, lastItemInWorkspace);
            } else {
                shortcutInfo2.rank = lGFolderInWorkspace.contents.size();
                LauncherModel.addItemToDatabase(context, shortcutInfo2, lGFolderInWorkspace.id, 0L, 0, 0);
            }
        }
    }

    private void moveItemToLastPos(Context context, ShortcutInfo mTemp2, ItemInfo lastItemInfo) {
        mTemp2.container = -100L;
        int sharedPrefValue = LGInvariantDeviceProfile.getSharedPrefValue(context, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS, 0);
        int sharedPrefValue2 = LGInvariantDeviceProfile.getSharedPrefValue(context, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS, 0);
        if (mTemp2.screenId == lastItemInfo.screenId && mTemp2.cellX == lastItemInfo.cellX && mTemp2.cellY == lastItemInfo.cellY) {
            LGLog.d(TAG, mTemp2 + " is the last item");
        } else {
            int i = sharedPrefValue2 - 1;
            if (lastItemInfo.cellY < i) {
                if (lastItemInfo.cellX < sharedPrefValue - 1) {
                    mTemp2.screenId = lastItemInfo.screenId;
                    mTemp2.cellX = lastItemInfo.cellX + 1;
                    mTemp2.cellY = lastItemInfo.cellY;
                } else {
                    mTemp2.screenId = lastItemInfo.screenId;
                    mTemp2.cellX = 0;
                    mTemp2.cellY = lastItemInfo.cellY + 1;
                }
            } else if (lastItemInfo.cellY == i) {
                if (lastItemInfo.cellX < sharedPrefValue - 1) {
                    mTemp2.screenId = lastItemInfo.screenId;
                    mTemp2.cellX = lastItemInfo.cellX + 1;
                    mTemp2.cellY = lastItemInfo.cellY;
                } else {
                    ArrayList<Long> arrayListLoadWorkspaceScreensDb = LauncherModel.loadWorkspaceScreensDb(context);
                    long j = LauncherSettings.Settings.call(context.getContentResolver(), LauncherSettings.Settings.METHOD_NEW_SCREEN_ID).getLong("value");
                    arrayListLoadWorkspaceScreensDb.add(Long.valueOf(j));
                    LauncherModel.updateWorkspaceScreenOrder(context, arrayListLoadWorkspaceScreensDb);
                    mTemp2.screenId = j;
                    mTemp2.cellX = 0;
                    mTemp2.cellY = 0;
                }
            }
        }
        LauncherModel.addItemToDatabase(context, mTemp2, mTemp2.container, mTemp2.screenId, mTemp2.cellX, mTemp2.cellY);
    }

    private AppInfo getAppInfo(Context context, ComponentName componentName) {
        for (LauncherActivityInfo launcherActivityInfo : LauncherAppsCompat.getInstance(context).getActivityList(componentName.getPackageName(), Process.myUserHandle())) {
            if (componentName.equals(launcherActivityInfo.getComponentName())) {
                return new AppInfo(context, launcherActivityInfo, Process.myUserHandle(), LauncherAppState.getInstance(context).getIconCache());
            }
        }
        return null;
    }
}
