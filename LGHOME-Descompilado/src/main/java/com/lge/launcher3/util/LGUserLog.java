package com.lge.launcher3.util;

import android.content.Context;
import android.content.Intent;
import com.lge.launcher3.config.LauncherConst;

/* JADX INFO: loaded from: classes.dex */
public class LGUserLog {
    private static final String ACTION_APPEND_USER_LOG = "com.lge.mlt.service.intent.action.APPEND_USER_LOG";
    private static final boolean DEBUG = false;
    public static final String FEATURENAME_ADDFOLDERITEMBYPLUS = "Home_AddFolderItemByPlus";
    public static final String FEATURENAME_APPDRAWER_REARRANGE = "Home_AppDrawerRearrange";
    public static final String FEATURENAME_APPDRAWER_SORT = "Home_AppDrawerSort";
    public static final String FEATURENAME_BACKUP = "Home_Backup";
    public static final String FEATURENAME_CHANGEADDITIONANSCREEN = "Home_ChangeAdditionalScreen";
    public static final String FEATURENAME_CHANGEEFFECT = "Home_ChangeEffect";
    public static final String FEATURENAME_CHANGEFOLDERCOLOR = "Home_ChangeFolderColor";
    public static final String FEATURENAME_CHANGEGRID = "Home_ChangeGrid";
    public static final String FEATURENAME_CHANGEHIDEAPPS = "Home_ChangeHideApps";
    public static final String FEATURENAME_CHANGEICONSHAPE = "Home_ChangeIconShape";
    public static final String FEATURENAME_CHANGESORTAPPSBY = "Home_ChangeSortAppsBy";
    public static final String FEATURENAME_CLEANVIEW = "Home_CleanView";
    public static final String FEATURENAME_DEFAULTSCREEN = "Home_DefaultScreen";
    public static final String FEATURENAME_ENABLECONTINUOUSLOOP = "Home_EnableContinuousLoop";
    public static final String FEATURENAME_ENABLEQMEMO = "Home_EnableQMemo";
    public static final String FEATURENAME_ENABLESEARCH = "Home_EnableSearch";
    public static final String FEATURENAME_ENABLESMARTBULLETIN = "Home_EnableSmartBulletin";
    public static final String FEATURENAME_ENABLEVZWAPPDRAWERLOOP = "Home_EnableVZWAppDrawerLoop";
    public static final String FEATURENAME_GOOGLE_FEED = "Home_GoogleFeed";
    public static final String FEATURENAME_KNOCK_OFF = "Home_KnockOff";
    public static final String FEATURENAME_OVERVIEWMODE = "Home_OverviewMode";
    public static final String FEATURENAME_REMOVE_ITEM_BY_TRASHCAN = "Home_RemoveItemByTrashCan";
    public static final String FEATURENAME_REMOVE_ITEM_BY_UNINSTALLMODE = "Home_RemoveItemByUninstallMode";
    public static final String FEATURENAME_RESIZEWIDGET = "Home_ResizeWidget";
    public static final String FEATURENAME_RESTORE = "Home_Restore";
    public static final String FEATURENAME_SHOWADDITIONALSCREEN = "Home_ShowAdditionalScreen";
    public static final String FEATURENAME_SHOWEFFECT = "Home_ShowEffect";
    public static final String FEATURENAME_SHOWGRID = "Home_ShowGrid";
    public static final String FEATURENAME_SHOWHIDEAPPS = "Home_ShowHideApps";
    public static final String FEATURENAME_SHOWHOMESCREENSETTINGS = "Home_ShowHomeScreenSettings";
    public static final String FEATURENAME_SHOWHOMESELECTOR = "Home_ShowHomeSelector";
    public static final String FEATURENAME_SHOWICONSHAPE = "Home_ShowIconShape";
    public static final String FEATURENAME_SHOWRECENTLYUNINSTALLED = "Home_ShowRecentlyUninstalled";
    public static final String FEATURENAME_SHOWSMARTBULLETIN = "Home_ShowSmartBulletin";
    public static final String FEATURENAME_SHOWSORTAPPSBY = "Home_ShowSortAppsBy";
    public static final String FEATURENAME_SHOWTHEMESQUARE = "Home_ShowThemeSquare";
    public static final String FEATURENAME_SHOWWIDGETLIST = "Home_ShowWidgetList";
    public static final String FEATURENAME_SWIPEDOWNHOME = "Home_SwipeDownHome";
    public static final String FEATURENAME_SWIPEDOWN_SUBSWIVELHOME = "Home_SwipeDown_SubSwivelHome";
    public static final String FEATURENAME_SWIPEUPHOME = "Home_SwipeUpHome";
    public static final String FEATURENAME_UNINSTALLMODE = "Home_UninstallMode";
    public static final String FEATURENAME_WALLPAPERPICKER = "Home_WallpapaerPicker";
    private static final String MLT_PACKAGE_NAME = "com.lge.mlt";
    private static final String TAG = "LGUserLog";

    public static void send(Context context, String featureName, int extVal, String extStr) {
        Intent intent = new Intent(ACTION_APPEND_USER_LOG);
        intent.setPackage(MLT_PACKAGE_NAME);
        intent.putExtra("pkg_name", context.getPackageName());
        intent.putExtra("app_name", LauncherConst.LGHOME_APP_NAME);
        intent.putExtra("feature_name", featureName);
        intent.putExtra("extend_integer", extVal);
        intent.putExtra("extend_text", extStr);
        context.startService(intent);
    }

    public static void send(Context context, String featureName, int extVal) {
        Intent intent = new Intent(ACTION_APPEND_USER_LOG);
        intent.setPackage(MLT_PACKAGE_NAME);
        intent.putExtra("pkg_name", context.getPackageName());
        intent.putExtra("app_name", LauncherConst.LGHOME_APP_NAME);
        intent.putExtra("feature_name", featureName);
        intent.putExtra("extend_integer", extVal);
        context.startService(intent);
    }

    public static void send(Context context, String featureName, String extStr) {
        Intent intent = new Intent(ACTION_APPEND_USER_LOG);
        intent.setPackage(MLT_PACKAGE_NAME);
        intent.putExtra("pkg_name", context.getPackageName());
        intent.putExtra("app_name", LauncherConst.LGHOME_APP_NAME);
        intent.putExtra("feature_name", featureName);
        intent.putExtra("extend_text", extStr);
        context.startService(intent);
    }

    public static void send(Context context, String featureName) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(ACTION_APPEND_USER_LOG);
        intent.setPackage(MLT_PACKAGE_NAME);
        intent.putExtra("pkg_name", context.getPackageName());
        intent.putExtra("app_name", LauncherConst.LGHOME_APP_NAME);
        intent.putExtra("feature_name", featureName);
        context.startService(intent);
    }
}
