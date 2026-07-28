package com.lge.contextenginelibrary;

import android.net.Uri;
import com.lge.launcher3.config.QMemoPanelConst;

/* JADX INFO: loaded from: classes.dex */
public class LibConstant {
    public static final String ACTION_START_FREQUENT_APP = "com.lge.ia.apprecommend.action.start_frequent_app";
    public static final String APP_AUTHORITY = "com.lge.ia.apprecommend";
    public static final int APP_RECOMMEND = 1;
    public static final String COMPONENT = "componentName";
    public static final String CONTENT = "content://";
    public static final String DATE = "_date";
    public static final String DIAL_ACTIVITY_NAME = ".activities.DialtactsActivity";
    public static final String EXTEND_INTEGER = "_extend_integer";
    public static final String EXTRA_PACKAGE_NAME = "extra_package_name";
    public static final String EXTRA_TYPE = "extra_type";
    public static final String FAVORITE_SCORE = "_favorite_score";
    public static final Uri HIDE_APPS_CONTENT_URI;
    public static final String HIDE_APPS_TABLE_NAME = "hideapps";
    public static final int INVALID_VALUE = -1;
    public static final String LGSETTINGS_AUTHORITY;
    public static final int MAX_RECOMMEND = 5;
    public static final String PACKAGE_CONTACTS = "com.android.contacts";
    public static final String PACKAGE_NAME = "_package_name";
    public static final String PROXIMITY = "_proximity";
    public static final String RUNNING_TIME = "_running_time";
    public static final String TAG_PREFIX = "[ContextEngineLib]";
    public static final String TB_FREQUENT_FAVORITE_APP_METRIC = "FREQUENT_FAVORITE_APP_METRIC";
    public static final String TYPE = "_type";
    public static final String USER = "profileId";
    public static final long USER_OWNER = 0;
    public static final int searchDateMax = 30;
    public static String[] DEFAULT_APPS = new String[5];
    public static final String[] DEFAULT_APPS_OLD = {"com.android.settings", "com.android.gallery3d", QMemoPanelConst.QMEMOPANEL_PACKAGE_NAME, "com.lge.clock", "com.android.calendar"};
    public static final String[] DEFAULT_APPS_NEW = {"com.android.settings", "com.android.gallery3d", QMemoPanelConst.QMEMOPANEL_PACKAGE_NAME, "com.lge.clock", "com.google.android.calendar"};
    public static final Uri FREQUENT_RECOMMENDED_APP_TABLE_URI = Uri.parse("content://com.lge.ia.apprecommend/FREQUENT_FAVORITE_APP_METRIC");

    static {
        String strIntern = "com.lge.launcher3.settings".intern();
        LGSETTINGS_AUTHORITY = strIntern;
        HIDE_APPS_CONTENT_URI = Uri.parse("content://" + strIntern + "/hideapps");
    }
}
