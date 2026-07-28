package com.lge.contextenginelibrary;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import com.lge.contextenginelibrary.model.EventType;
import com.lge.contextenginelibrary.model.FavoriteAppType;
import com.lge.contextenginelibrary.model.FrequentAppsInfo;
import com.lge.contextenginelibrary.util.Utility;
import java.util.ArrayList;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class FrequentAppsLib {
    public static final String TAG = LibConstant.TAG_PREFIX + FrequentAppsLib.class.getSimpleName();
    private Context mBaseContext;
    private Context mContext;
    private boolean mShowIcon;

    public FrequentAppsLib(Context context) {
        this(context, true);
    }

    public FrequentAppsLib(Context context, boolean z) {
        this.mShowIcon = true;
        this.mContext = context;
        LibConstant.DEFAULT_APPS = existLGCalendar(context) ? LibConstant.DEFAULT_APPS_OLD : LibConstant.DEFAULT_APPS_NEW;
        this.mShowIcon = z;
        try {
            this.mBaseContext = this.mContext.createApplicationContext(this.mContext.getPackageManager().getApplicationInfoAsUser(this.mContext.getBasePackageName(), 8192, this.mContext.getUserId()), 4);
        } catch (Exception e) {
            Log.e(TAG, "Cannot create Base context : " + e);
            this.mBaseContext = this.mContext;
            e.printStackTrace();
        }
    }

    public static boolean supportFrequestApps(Context context) {
        try {
            Log.d(TAG, "supportFrequestApps = " + context.getPackageManager().getPackageInfo("com.lge.ia.task.smartsetting", 128).applicationInfo.enabled);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.d(TAG, "supportFrequestApps NameNotFoundException");
            return false;
        }
    }

    public FrequentAppsInfo getBasicFrequentApps() throws Throwable {
        ArrayList arrayList = new ArrayList();
        FrequentAppsInfo lastUpdatedAppList = getLastUpdatedAppList(FavoriteAppType.BASIC);
        ArrayList<String> hidedApps = getHidedApps();
        if (lastUpdatedAppList != null) {
            ArrayList<FrequentAppsInfo.AppInfo> arrayList2 = lastUpdatedAppList.appInfos;
            if (arrayList2 != null && arrayList2.size() != 0) {
                int i = 0;
                for (int i2 = 0; i2 < arrayList2.size() && i < 5; i2++) {
                    FrequentAppsInfo.AppInfo appInfo = arrayList2.get(i2);
                    if (appInfo != null && !isAppHided(hidedApps, appInfo.packageName) && getAppIntent(this.mBaseContext, appInfo.packageName, FavoriteAppType.BASIC) != null) {
                        arrayList.add(appInfo.packageName);
                        i++;
                    }
                }
            }
            if (arrayList.size() != 0) {
                return new FrequentAppsInfo(this.mBaseContext, lastUpdatedAppList.date, FavoriteAppType.BASIC, arrayList, this.mShowIcon);
            }
        }
        return null;
    }

    public FrequentAppsInfo getEventFrequentApps(FavoriteAppType favoriteAppType) throws Throwable {
        boolean z;
        FrequentAppsInfo basicFrequentApps = getBasicFrequentApps();
        ArrayList arrayList = new ArrayList();
        FrequentAppsInfo lastUpdatedAppList = getLastUpdatedAppList(favoriteAppType);
        ArrayList<String> hidedApps = getHidedApps();
        if (lastUpdatedAppList != null) {
            ArrayList<FrequentAppsInfo.AppInfo> arrayList2 = lastUpdatedAppList.appInfos;
            if (arrayList2 != null && arrayList2.size() != 0) {
                int i = 0;
                for (int i2 = 0; i2 < arrayList2.size() && i < 5; i2++) {
                    FrequentAppsInfo.AppInfo appInfo = arrayList2.get(i2);
                    if (basicFrequentApps != null) {
                        for (int i3 = 0; i3 < basicFrequentApps.appInfos.size() && i3 < 5; i3++) {
                            if (basicFrequentApps.appInfos.get(i3).packageName.equals(appInfo.packageName)) {
                                Log.i(TAG, "[" + favoriteAppType + "] Duplicated with Basic apps. Hide it : " + appInfo.packageName);
                                z = true;
                                break;
                            }
                        }
                        z = false;
                    } else {
                        z = false;
                    }
                    if (appInfo != null && !isAppHided(hidedApps, appInfo.packageName) && !z && getAppIntent(this.mBaseContext, appInfo.packageName, favoriteAppType) != null) {
                        arrayList.add(appInfo.packageName);
                        i++;
                    }
                }
            }
            if (arrayList.size() != 0) {
                return new FrequentAppsInfo(this.mBaseContext, lastUpdatedAppList.date, favoriteAppType, arrayList, this.mShowIcon);
            }
        }
        return null;
    }

    public EventType getCurrentEventType() {
        return EventType.NO_EVENT;
    }

    private ArrayList<String> getHidedApps() {
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cursorQuery = null;
        try {
            try {
                try {
                    cursorQuery = this.mContext.getContentResolver().query(LibConstant.HIDE_APPS_CONTENT_URI, null, "profileId == '0'", null, null);
                    while (cursorQuery.moveToNext()) {
                        arrayList.add(cursorQuery.getString(cursorQuery.getColumnIndex("componentName")));
                    }
                } catch (Exception e) {
                    Log.w(TAG, "isAppHided Exception : " + e);
                    if (cursorQuery != null) {
                    }
                }
            } catch (SQLException e2) {
                Log.w(TAG, "isAppHided SQLException : " + e2);
                if (cursorQuery != null) {
                }
            }
            return arrayList;
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    private boolean isAppHided(ArrayList<String> arrayList, String str) {
        for (String str2 : arrayList) {
            if (str2 != null && str2.startsWith(str)) {
                Log.i(TAG, str + " is Hided");
                return true;
            }
        }
        return false;
    }

    private PendingIntent getAppIntent(Context context, String str, FavoriteAppType favoriteAppType) {
        try {
            Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(str);
            launchIntentForPackage.setFlags(872415232);
            if (PendingIntent.getActivity(context, 0, launchIntentForPackage, 134217728) == null) {
                return null;
            }
            Intent intent = new Intent(LibConstant.ACTION_START_FREQUENT_APP);
            intent.putExtra(LibConstant.EXTRA_PACKAGE_NAME, str);
            intent.putExtra(LibConstant.EXTRA_TYPE, favoriteAppType.ordinal());
            intent.setPackage(context.getPackageName());
            return PendingIntent.getBroadcast(context, intent.hashCode(), intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "There's no available launch intent : " + str);
            return null;
        }
    }

    private FrequentAppsInfo getLastUpdatedAppList(FavoriteAppType favoriteAppType) throws Throwable {
        FrequentAppsInfo frequentAppsInfo;
        ContentResolver contentResolver = this.mContext.getContentResolver();
        String lastUpdatedDate = getLastUpdatedDate(favoriteAppType);
        int i = 0;
        Cursor cursor = null;
        FrequentAppsInfo frequentAppsInfo2 = null;
        Cursor cursor2 = null;
        cursor = null;
        if (lastUpdatedDate == null) {
            if (favoriteAppType != FavoriteAppType.BASIC) {
                return null;
            }
            Log.i(TAG, "There is no getLastUpdatedAppList, (BASIC) Set to default apps : " + ((Object) null));
            ArrayList arrayList = new ArrayList();
            String[] strArr = LibConstant.DEFAULT_APPS;
            int length = strArr.length;
            while (i < length) {
                arrayList.add(strArr[i]);
                i++;
            }
            return new FrequentAppsInfo(this.mBaseContext, Utility.getTimeString(System.currentTimeMillis()), favoriteAppType, arrayList, false);
        }
        try {
            try {
                Cursor cursorQuery = contentResolver.query(LibConstant.FREQUENT_RECOMMENDED_APP_TABLE_URI, null, "_date == '" + lastUpdatedDate + "' and " + LibConstant.TYPE + " == '" + favoriteAppType.ordinal() + "'", null, null);
                try {
                    try {
                        if (cursorQuery.getCount() > 0) {
                            ArrayList arrayList2 = new ArrayList();
                            while (cursorQuery.moveToNext()) {
                                arrayList2.add(cursorQuery.getString(cursorQuery.getColumnIndex(LibConstant.PACKAGE_NAME)));
                            }
                            frequentAppsInfo = new FrequentAppsInfo(this.mBaseContext, lastUpdatedDate, favoriteAppType, arrayList2, false);
                            try {
                                Log.i(TAG, "success to get getLastUpdatedAppList : " + lastUpdatedDate + ", size : " + frequentAppsInfo.appInfos.size() + ", " + favoriteAppType);
                                frequentAppsInfo2 = frequentAppsInfo;
                            } catch (SQLiteException e) {
                                e = e;
                                cursor = cursorQuery;
                                Log.e(TAG, "Cannot get getLastUpdatedAppList(" + favoriteAppType + ") : " + e);
                                e.printStackTrace();
                                if (cursor != null) {
                                    cursor.close();
                                }
                                return frequentAppsInfo;
                            } catch (Exception e2) {
                                e = e2;
                                cursor = cursorQuery;
                                Log.e(TAG, "Cannot get getLastUpdatedAppList(" + favoriteAppType + ") : " + e);
                                e.printStackTrace();
                                if (cursor != null) {
                                    cursor.close();
                                }
                                return frequentAppsInfo;
                            }
                        } else if (favoriteAppType == FavoriteAppType.BASIC) {
                            Log.i(TAG, "There is no getLastUpdatedAppList, Set to default apps " + ((Object) null) + "(" + favoriteAppType + ")");
                            ArrayList arrayList3 = new ArrayList();
                            String[] strArr2 = LibConstant.DEFAULT_APPS;
                            int length2 = strArr2.length;
                            while (i < length2) {
                                arrayList3.add(strArr2[i]);
                                i++;
                            }
                            frequentAppsInfo2 = new FrequentAppsInfo(this.mBaseContext, Utility.getTimeString(System.currentTimeMillis()), favoriteAppType, arrayList3, false);
                        } else {
                            Log.i(TAG, "There is no getLastUpdatedAppList (" + favoriteAppType + ")");
                        }
                        if (cursorQuery == null) {
                            return frequentAppsInfo2;
                        }
                        cursorQuery.close();
                        return frequentAppsInfo2;
                    } catch (SQLiteException e3) {
                        e = e3;
                        frequentAppsInfo = null;
                    } catch (Exception e4) {
                        e = e4;
                        frequentAppsInfo = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    cursor2 = cursorQuery;
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                    throw th;
                }
            } catch (SQLiteException e5) {
                e = e5;
                frequentAppsInfo = null;
            } catch (Exception e6) {
                e = e6;
                frequentAppsInfo = null;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private String getLastUpdatedDate(FavoriteAppType favoriteAppType) throws Throwable {
        String str;
        Cursor cursorQuery;
        Throwable th;
        ContentResolver contentResolver = this.mContext.getContentResolver();
        String str2 = "_date > '" + Utility.getTimeString(Utility.resetTime(Calendar.getInstance(), -30)) + "' and " + LibConstant.DATE + " <= '" + Utility.getTimeString(Utility.resetTime(Calendar.getInstance(), 0)) + "' and " + LibConstant.TYPE + " == '" + favoriteAppType.ordinal() + "' and " + LibConstant.EXTEND_INTEGER + " == '1'";
        Cursor cursor = null;
        String string = null;
        cursor = null;
        try {
            try {
                cursorQuery = contentResolver.query(LibConstant.FREQUENT_RECOMMENDED_APP_TABLE_URI, null, str2, null, null);
                try {
                    if (cursorQuery.getCount() > 0) {
                        cursorQuery.moveToLast();
                        string = cursorQuery.getString(cursorQuery.getColumnIndex(LibConstant.DATE));
                        Log.i(TAG, "success to get LastUpdatedDate : " + string);
                    } else {
                        Log.i(TAG, "There is no LastUpdatedDate");
                    }
                    if (cursorQuery == null) {
                        return string;
                    }
                    cursorQuery.close();
                    return string;
                } catch (SQLiteException e) {
                    e = e;
                    str = null;
                    cursor = cursorQuery;
                    Log.e(TAG, "Cannot get LastUpdatedDate : " + e);
                    e.printStackTrace();
                    if (cursor != null) {
                        cursor.close();
                    }
                    return str;
                } catch (Exception e2) {
                    e = e2;
                    str = null;
                    cursor = cursorQuery;
                    Log.e(TAG, "Cannot get LastUpdatedDate : " + e);
                    e.printStackTrace();
                    if (cursor != null) {
                        cursor.close();
                    }
                    return str;
                } catch (Throwable th2) {
                    th = th2;
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    throw th;
                }
            } catch (SQLiteException e3) {
                e = e3;
                str = null;
            } catch (Exception e4) {
                e = e4;
                str = null;
            }
        } catch (Throwable th3) {
            cursorQuery = null;
            th = th3;
        }
    }

    public boolean existLGCalendar(Context context) {
        try {
            Log.d(TAG, "existLGCalendar = " + context.getPackageManager().getPackageInfo("com.android.calendar", 128).applicationInfo.enabled);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.d(TAG, "existLGCalendar NameNotFoundException");
            return false;
        }
    }
}
