package com.lge.launcher3.homesettings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWindowAllocationException;
import android.database.sqlite.SQLiteException;
import com.android.launcher3.FullScreenItemInfo;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherSettings;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.config.QMemoPanelConst;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import com.lge.launcher3.util.PackageUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class SBHomeDataBaseUtil {
    public static final String SMARTBULLETIN_CONTAINER_NAME = "android.resource://com.android.launcher3/layout/smartbulletin_container";
    private static final String TAG = "SBHomeDataBaseUtil";
    private static int sFullScreenItemStatus = -1;

    public static boolean turnOnSmartBulletin(Context context) {
        if (existSmartBulletinItemInDataBase(context)) {
            return true;
        }
        turnOnFullScreenItemInfo(context, SMARTBULLETIN_CONTAINER_NAME);
        return true;
    }

    public static boolean turnOffSmartBulletin(Context context) {
        turnOffFullScreenItemInfo(context, SMARTBULLETIN_CONTAINER_NAME);
        return true;
    }

    public static boolean existSmartBulletinItemInDataBase(Context context) {
        if (LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN.getValue()) {
            return existFullScreenItemInfoInDB(context, SMARTBULLETIN_CONTAINER_NAME);
        }
        return false;
    }

    public static boolean turnOnQMemoPanel(Context context) {
        if (existQmemoPanelItemInDataBase(context)) {
            return true;
        }
        LGLog.d(TAG, "turnOnQMemoPanel");
        turnOnFullScreenItemInfo(context, QMemoPanelConst.QMEMOPANEL_CONTAINER_NAME);
        return true;
    }

    public static boolean turnOffQMemoPanel(Context context) {
        LGLog.d(TAG, "turnOffQMemoPanel");
        turnOffFullScreenItemInfo(context, QMemoPanelConst.QMEMOPANEL_CONTAINER_NAME);
        return true;
    }

    public static boolean existQmemoPanelItemInDataBase(Context context) {
        if (isEnabledQmemoPanel(context)) {
            return existFullScreenItemInfoInDB(context, QMemoPanelConst.QMEMOPANEL_CONTAINER_NAME);
        }
        return false;
    }

    public static boolean isEnabledQmemoPanel(Context context) {
        return LGHomeFeature.Config.FEATURE_USE_QMEMOPLUS_PANEL.getValue() && !PackageUtils.isPackageUninstalled(context, QMemoPanelConst.QMEMOPANEL_PACKAGE_NAME);
    }

    private static void requestCustomContentReload(Context context) {
        context.sendBroadcast(new Intent(IntentConst.Action.ACTION_RELOAD_CUSTOMCONTENT.getValue(context)));
    }

    private static void turnOnFullScreenItemInfo(Context context, String curItem) {
        deleteFullScreenItemInfo(context, curItem);
        insertFullScreenItemInfo(context, curItem);
        if (SMARTBULLETIN_CONTAINER_NAME.equals(curItem)) {
            ActionManagerUserLog.sendBoardEnabled(context);
            SmartBulletinAction.sendEnabled(context);
            SettingsSearchUtils.updateSmartBulletinOnOff(context, true);
            LGUserLog.send(context, LGUserLog.FEATURENAME_ENABLESMARTBULLETIN, 1);
        } else if (QMemoPanelConst.QMEMOPANEL_CONTAINER_NAME.equals(curItem)) {
            SettingsSearchUtils.updateCheckValue(context, HomeSettingsConstant.KEY_HOMESETTINGS_QMEMOPLUS_PANEL, true);
            LGUserLog.send(context, LGUserLog.FEATURENAME_ENABLEQMEMO, 1);
        }
        requestCustomContentReload(context);
    }

    private static void turnOffFullScreenItemInfo(Context context, String curItem) {
        deleteFullScreenItemInfo(context, curItem);
        if (SMARTBULLETIN_CONTAINER_NAME.equals(curItem)) {
            ActionManagerUserLog.sendBoardDisabled(context);
            SmartBulletinAction.sendDisabled(context);
            SettingsSearchUtils.updateSmartBulletinOnOff(context, false);
            LGUserLog.send(context, LGUserLog.FEATURENAME_ENABLESMARTBULLETIN, 0);
        } else if (QMemoPanelConst.QMEMOPANEL_CONTAINER_NAME.equals(curItem)) {
            SettingsSearchUtils.updateCheckValue(context, HomeSettingsConstant.KEY_HOMESETTINGS_QMEMOPLUS_PANEL, false);
            LGUserLog.send(context, LGUserLog.FEATURENAME_ENABLEQMEMO, 0);
        }
        requestCustomContentReload(context);
    }

    private static void deleteFullScreenItemInfo(Context context, String curItem) {
        ArrayList<FullScreenItemInfo> fullScreenItem = readFullScreenItem(context);
        if (fullScreenItem == null || !contain(fullScreenItem, curItem)) {
            return;
        }
        for (FullScreenItemInfo fullScreenItemInfo : fullScreenItem) {
            if (fullScreenItemInfo.resUri.equals(curItem)) {
                LauncherModel.deleteItemFromDatabase(context, fullScreenItemInfo);
                sFullScreenItemStatus = 0;
            }
        }
    }

    private static void insertFullScreenItemInfo(Context context, String curItem) {
        FullScreenItemInfo fullScreenItemInfo = new FullScreenItemInfo(context);
        fullScreenItemInfo.screenId = -301L;
        fullScreenItemInfo.resUri = curItem;
        LauncherModel.addItemToDatabase(context, fullScreenItemInfo, -100L, fullScreenItemInfo.screenId, fullScreenItemInfo.cellX, fullScreenItemInfo.cellY);
        sFullScreenItemStatus = 1;
    }

    private static boolean existFullScreenItemInfoInDB(Context context, String curItem) {
        ArrayList<FullScreenItemInfo> fullScreenItem;
        if (sFullScreenItemStatus == -1 && (fullScreenItem = readFullScreenItem(context)) != null) {
            if (fullScreenItem.size() == 1 && contain(fullScreenItem, curItem)) {
                sFullScreenItemStatus = 1;
            } else if (fullScreenItem.size() > 1 && contain(fullScreenItem, curItem)) {
                LGLog.e(TAG, "existFullScreenItemInfoInDB item count: " + fullScreenItem.size() + " body: " + fullScreenItem);
                sFullScreenItemStatus = 1;
            } else if (fullScreenItem.size() == 0) {
                LGLog.i(TAG, "existFullScreenItemInfoInDB item count is zero, sFullScreenItemStatus = 0 ");
                sFullScreenItemStatus = 0;
            }
        }
        return sFullScreenItemStatus == 1;
    }

    private static boolean contain(ArrayList<FullScreenItemInfo> itemInfoList, String curItem) {
        for (FullScreenItemInfo fullScreenItemInfo : itemInfoList) {
            if (fullScreenItemInfo != null && fullScreenItemInfo.resUri.equals(curItem)) {
                return true;
            }
        }
        return false;
    }

    private static ArrayList<FullScreenItemInfo> readFullScreenItem(Context context) {
        Cursor cursorQuery;
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null) {
            LGLog.e(TAG, "The contentResolver is null");
            return null;
        }
        try {
            cursorQuery = contentResolver.query(LauncherSettings.Favorites.CONTENT_URI, null, "itemType=8", null, null);
        } catch (CursorWindowAllocationException e) {
            e.printStackTrace();
            cursorQuery = null;
        } catch (SQLiteException e2) {
            LGLog.e(TAG, String.format("SQLiteException : %s", e2.toString()));
            cursorQuery = null;
        }
        if (cursorQuery == null) {
            LGLog.e(TAG, "The Cursor is null");
            return null;
        }
        return getItemInfoFromCursor(context, cursorQuery);
    }

    private static ArrayList<FullScreenItemInfo> getItemInfoFromCursor(Context context, final Cursor c) {
        ArrayList<FullScreenItemInfo> arrayList = new ArrayList<>();
        arrayList.clear();
        try {
            int columnIndexOrThrow = c.getColumnIndexOrThrow("_id");
            int columnIndexOrThrow2 = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CONTAINER);
            int columnIndexOrThrow3 = c.getColumnIndexOrThrow("screen");
            int columnIndexOrThrow4 = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.INTENT);
            int columnIndexOrThrow5 = c.getColumnIndexOrThrow("appWidgetId");
            while (c.moveToNext()) {
                FullScreenItemInfo fullScreenItemInfo = new FullScreenItemInfo(context);
                fullScreenItemInfo.id = c.getLong(columnIndexOrThrow);
                fullScreenItemInfo.container = c.getInt(columnIndexOrThrow2);
                fullScreenItemInfo.screenId = c.getInt(columnIndexOrThrow3);
                fullScreenItemInfo.resUri = c.getString(columnIndexOrThrow4);
                fullScreenItemInfo.widgetId = c.getInt(columnIndexOrThrow5);
                arrayList.add(fullScreenItemInfo);
            }
            return arrayList;
        } catch (Exception e) {
            LGLog.w(TAG, "Desktop items loading interrupted:", e, new int[0]);
            return null;
        } finally {
            c.close();
        }
    }
}
