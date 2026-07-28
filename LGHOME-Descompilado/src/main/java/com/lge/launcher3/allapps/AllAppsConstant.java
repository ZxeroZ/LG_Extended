package com.lge.launcher3.allapps;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsConstant {
    public static final int ACTION_MAKE_FOLDER = 512;
    public static final int ACTION_MASK = 65280;
    public static final int ACTION_SWAP = 256;
    public static final String ALLAPPS_ACTION_SAVE_APP_USAGE_STATS = "com.lge.launcher2.action.SAVE_APP_USAGE_STATS";
    public static final int ALLAPPS_CELLCOUNT_TYPE = 3;
    public static final String ALLAPPS_FAVORITE_APP_STATS_PREFERENCE = "allapps.appstats";
    public static final String ALLAPPS_FAVORITE_DATE_PREFERENCE = "allapps.appstats.firstdate";
    public static final int ALLAPPS_FAVORITE_PERIOD = -2;
    public static final int ALLAPPS_FAVORITE_STORE_PERIOD = -6;
    public static final int ALLAPPS_ITEMTYPE_APP = 0;
    public static final int ALLAPPS_ITEMTYPE_FOLDER = 2;
    public static final String AUTHORITY = "com.lge.launcher3.AllAppsDBProvider";
    public static final String AUTHORITY_FOR_SWIVEL = "com.lge.launcher3.SwivelAllAppsDBProvider";
    public static final String DAY_FORMAT = "yyyy-MM-dd";
    public static final int INDEX_MASK = 255;
    public static final String PARAMETER_APP_NOTIFY = "notify";
    public static final String TABLE_APP_INFOS = "app_infos";
    public static final String TABLE_WIDGET_INFOS = "widget_infos";

    public enum AllAppsStyle {
        LARGEICON_STYLE,
        SMALLICON_STYLE,
        UNKNOWN_STYLE
    }

    public enum AppState {
        ADD,
        UPDATE,
        REMOVE
    }
}
