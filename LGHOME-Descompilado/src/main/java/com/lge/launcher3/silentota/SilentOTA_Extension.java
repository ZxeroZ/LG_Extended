package com.lge.launcher3.silentota;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import com.android.systemui.shared.system.PeopleProviderUtils;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGLog;
import java.util.HashSet;

/* JADX INFO: loaded from: classes.dex */
public class SilentOTA_Extension {
    private static String APPBOX_AUTHORITY = "com.lge.appbox.contentprovider";
    private static String APPBOX_PROMISING_URI = null;
    private static String APPBOX_URI = "content://com.lge.appbox.contentprovider/dyna_attr";
    private static String COLUMN_NAME_ATTR = "attr";
    private static String COLUMN_NAME_PACKAGE = "package";
    private static String COLUMN_NAME_PACKAGE_NAME = null;
    private static String DP_PRELOAD_SELECTION = null;
    private static String[] PROJECTION = null;
    private static String TAG = "SilentOTA_Extension";
    private static String TYPE_DP_PRELOAD = "'dpPreload'";
    private static HashSet<String> sPackageSet;
    private static HashSet<String> sPromisingPackageSet;

    static {
        String str = COLUMN_NAME_ATTR;
        PROJECTION = new String[]{COLUMN_NAME_PACKAGE, str};
        DP_PRELOAD_SELECTION = str + " = " + TYPE_DP_PRELOAD;
        sPackageSet = new HashSet<>();
        sPromisingPackageSet = new HashSet<>();
        APPBOX_PROMISING_URI = "content://" + APPBOX_AUTHORITY + "/dppreload_promising_apps";
        COLUMN_NAME_PACKAGE_NAME = PeopleProviderUtils.EXTRAS_KEY_PACKAGE_NAME;
    }

    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:24:0x0099 */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00bf  */
    /* JADX WARN: Type inference failed for: r7v0 */
    /* JADX WARN: Type inference failed for: r7v1, types: [android.database.Cursor] */
    /* JADX WARN: Type inference failed for: r7v2 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.util.HashSet<java.lang.String> makeAddedPromisingPackage(android.content.Context r9) throws java.lang.Throwable {
        /*
            java.util.HashSet<java.lang.String> r0 = com.lge.launcher3.silentota.SilentOTA_Extension.sPromisingPackageSet
            r0.clear()
            android.content.ContentResolver r1 = r9.getContentResolver()
            r0 = 1
            r7 = 0
            r8 = 0
            java.lang.String r2 = com.lge.launcher3.silentota.SilentOTA_Extension.APPBOX_PROMISING_URI     // Catch: java.lang.Throwable -> L99 android.database.sqlite.SQLiteException -> L9b
            android.net.Uri r2 = android.net.Uri.parse(r2)     // Catch: java.lang.Throwable -> L99 android.database.sqlite.SQLiteException -> L9b
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6)     // Catch: java.lang.Throwable -> L99 android.database.sqlite.SQLiteException -> L9b
            if (r1 != 0) goto L2e
            java.lang.String r2 = com.lge.launcher3.silentota.SilentOTA_Extension.TAG     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            java.lang.String r3 = "[SilentOTA_Extension] makeAddedPromisingPackage: cursor is null"
            com.lge.launcher3.util.LGLog.i(r2, r3)     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            com.lge.launcher3.sharedpreferences.SharedPreferencesConst$AppBoxBootInstallKey r2 = com.lge.launcher3.sharedpreferences.SharedPreferencesConst.AppBoxBootInstallKey.SUCCESS     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            com.lge.launcher3.sharedpreferences.SharedPreferencesManager.putBoolean(r9, r8, r2, r8)     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            if (r1 == 0) goto L2d
            r1.close()
        L2d:
            return r7
        L2e:
            java.lang.String r2 = com.lge.launcher3.silentota.SilentOTA_Extension.COLUMN_NAME_PACKAGE_NAME     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            int r2 = r1.getColumnIndex(r2)     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            r3 = -1
            r1.moveToPosition(r3)     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
        L38:
            boolean r3 = r1.moveToNext()     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            if (r3 == 0) goto L6e
            java.lang.String r3 = r1.getString(r2)     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            java.lang.String r4 = com.lge.launcher3.silentota.SilentOTA_Extension.TAG     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            r5.<init>()     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            java.lang.String r6 = "[SilentOTA_Extension] makeAddedPromisingPackage: packageName = "
            r5.append(r6)     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            r5.append(r3)     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            java.lang.String r6 = ", columnIndex = "
            r5.append(r6)     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            r5.append(r2)     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            java.lang.String r5 = r5.toString()     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            com.lge.launcher3.util.LGLog.d(r4, r5)     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            if (r3 == 0) goto L38
            boolean r4 = com.lge.launcher3.util.PackageUtils.isPackageInstalled(r9, r3)     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            if (r4 != 0) goto L38
            java.util.HashSet<java.lang.String> r4 = com.lge.launcher3.silentota.SilentOTA_Extension.sPromisingPackageSet     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            r4.add(r3)     // Catch: android.database.sqlite.SQLiteException -> L97 java.lang.Throwable -> Lbb
            goto L38
        L6e:
            if (r1 == 0) goto L73
            r1.close()
        L73:
            com.lge.launcher3.sharedpreferences.SharedPreferencesConst$AppBoxBootInstallKey r1 = com.lge.launcher3.sharedpreferences.SharedPreferencesConst.AppBoxBootInstallKey.SUCCESS
            com.lge.launcher3.sharedpreferences.SharedPreferencesManager.putBoolean(r9, r8, r1, r0)
            java.lang.String r9 = com.lge.launcher3.silentota.SilentOTA_Extension.TAG
            java.util.HashSet<java.lang.String> r0 = com.lge.launcher3.silentota.SilentOTA_Extension.sPromisingPackageSet
            int r0 = r0.size()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "[SilentOTA_Extension] makeAddedPromisingPackage: success. size = "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            com.lge.launcher3.util.LGLog.i(r9, r0)
            java.util.HashSet<java.lang.String> r9 = com.lge.launcher3.silentota.SilentOTA_Extension.sPromisingPackageSet
            return r9
        L97:
            r2 = move-exception
            goto L9d
        L99:
            r9 = move-exception
            goto Lbd
        L9b:
            r2 = move-exception
            r1 = r7
        L9d:
            com.lge.launcher3.sharedpreferences.SharedPreferencesConst$AppBoxBootInstallKey r3 = com.lge.launcher3.sharedpreferences.SharedPreferencesConst.AppBoxBootInstallKey.SUCCESS     // Catch: java.lang.Throwable -> Lbb
            com.lge.launcher3.sharedpreferences.SharedPreferencesManager.putBoolean(r9, r8, r3, r8)     // Catch: java.lang.Throwable -> Lbb
            java.lang.String r9 = com.lge.launcher3.silentota.SilentOTA_Extension.TAG     // Catch: java.lang.Throwable -> Lbb
            java.lang.String r3 = "[SilentOTA_Extension] makeAddedPromisingPackage() : SQLiteException(%s)"
            java.lang.Object[] r0 = new java.lang.Object[r0]     // Catch: java.lang.Throwable -> Lbb
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> Lbb
            r0[r8] = r2     // Catch: java.lang.Throwable -> Lbb
            java.lang.String r0 = java.lang.String.format(r3, r0)     // Catch: java.lang.Throwable -> Lbb
            com.lge.launcher3.util.LGLog.i(r9, r0)     // Catch: java.lang.Throwable -> Lbb
            if (r1 == 0) goto Lba
            r1.close()
        Lba:
            return r7
        Lbb:
            r9 = move-exception
            r7 = r1
        Lbd:
            if (r7 == 0) goto Lc2
            r7.close()
        Lc2:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.silentota.SilentOTA_Extension.makeAddedPromisingPackage(android.content.Context):java.util.HashSet");
    }

    public static void updatePromisingPackage(String packageName) {
        sPromisingPackageSet.remove(packageName);
        LGLog.i(TAG, "[SilentOTA_Extension] updatePromisingPackage: success. size = " + sPromisingPackageSet.size());
    }

    public static HashSet<String> getAddedPromisingPackage() {
        return sPromisingPackageSet;
    }

    public static void addPromisingPackageFromPAI(String packageName) {
        sPromisingPackageSet.add(packageName);
    }

    public static void makeAddedPackage(Context context) {
        sPackageSet.clear();
        Cursor cursor = null;
        try {
            try {
                Cursor cursorQuery = context.getContentResolver().query(Uri.parse(APPBOX_URI), PROJECTION, DP_PRELOAD_SELECTION, null, null);
                if (cursorQuery == null) {
                    LGLog.i(TAG, "[SilentOTA_Extension] makeAddedPackage: cursor is null");
                    SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.AppBoxBootInstallKey.SUCCESS, false);
                    if (cursorQuery != null) {
                        cursorQuery.close();
                        return;
                    }
                    return;
                }
                int columnIndex = cursorQuery.getColumnIndex(COLUMN_NAME_PACKAGE);
                cursorQuery.moveToPosition(-1);
                while (cursorQuery.moveToNext()) {
                    String string = cursorQuery.getString(columnIndex);
                    LGLog.d(TAG, "[SilentOTA_Extension] makeAddedPackage: packageName = " + string + ", columnIndex = " + columnIndex);
                    if (string != null) {
                        sPackageSet.add(string);
                    }
                }
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.AppBoxBootInstallKey.SUCCESS, true);
                LGLog.i(TAG, "[SilentOTA_Extension] makeAddedPackage: success. size = " + sPackageSet.size());
            } catch (SQLiteException e) {
                SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.AppBoxBootInstallKey.SUCCESS, false);
                LGLog.i(TAG, String.format("[SilentOTA_Extension] makeAddedPackage() : SQLiteException(%s)", e.toString()));
                if (0 != 0) {
                    cursor.close();
                }
            }
        } catch (Throwable th) {
            if (0 != 0) {
                cursor.close();
            }
            throw th;
        }
    }

    public static HashSet<String> getAddedPackage(Context context) {
        if (sPackageSet.size() == 0 && SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.AppBoxBootInstallKey.RECEIVED, false)) {
            makeAddedPackage(context);
            boolean z = SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.AppBoxBootInstallKey.SUCCESS, false);
            LGLog.i(TAG, "[SilentOTA_Extension] getAddedPackage : remake size = " + sPackageSet.size() + ", success = " + z);
        }
        return sPackageSet;
    }
}
