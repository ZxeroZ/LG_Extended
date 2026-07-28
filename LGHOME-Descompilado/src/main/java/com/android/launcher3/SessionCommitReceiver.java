package com.android.launcher3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageInstaller;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.text.TextUtils;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.lge.launcher3.util.LGHomeFeature;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SessionCommitReceiver extends BroadcastReceiver {
    public static final String ADD_ICON_PREFERENCE_INITIALIZED_KEY = "pref_add_icon_to_home_initialized";
    public static final String ADD_ICON_PREFERENCE_KEY = "pref_add_icon_to_home";
    private static final String MARKER_PROVIDER_PREFIX = ".addtohomescreen";
    private static final String TAG = "SessionCommitReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (isEnabled(context) && Utilities.ATLEAST_OREO && LGHomeFeature.Config.FEATURE_ADD_NEW_SHORTCUT.getValue()) {
            PackageInstaller.SessionInfo sessionInfo = (PackageInstaller.SessionInfo) intent.getParcelableExtra("android.content.pm.extra.SESSION");
            UserHandle userHandle = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
            if (TextUtils.isEmpty(sessionInfo.getAppPackageName())) {
                return;
            }
            if (sessionInfo.getInstallReason() == 4 || sessionInfo.getInstallReason() == 0) {
                queueAppIconAddition(context, sessionInfo.getAppPackageName(), userHandle);
            }
        }
    }

    public static void queueAppIconAddition(Context context, String packageName, UserHandle user) {
        List<LauncherActivityInfo> activityList = LauncherAppsCompat.getInstance(context).getActivityList(packageName, user);
        if (activityList == null || activityList.isEmpty()) {
            return;
        }
        InstallShortcutReceiver.queueActivityInfo(activityList.get(0), context);
    }

    public static boolean isEnabled(Context context) {
        return Utilities.getPrefs(context).getBoolean(ADD_ICON_PREFERENCE_KEY, true);
    }

    public static void applyDefaultUserPrefs(final Context context) {
        if (Utilities.ATLEAST_OREO) {
            SharedPreferences prefs = Utilities.getPrefs(context);
            if (prefs.getAll().isEmpty()) {
                prefs.edit().putBoolean(ADD_ICON_PREFERENCE_KEY, true).apply();
            } else {
                if (prefs.contains(ADD_ICON_PREFERENCE_INITIALIZED_KEY)) {
                    return;
                }
                new PrefInitTask(context).executeOnExecutor(Utilities.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        }
    }

    private static class PrefInitTask extends AsyncTask<Void, Void, Void> {
        private final Context mContext;

        PrefInitTask(Context context) {
            this.mContext = context;
        }

        /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voids) {
            Utilities.getPrefs(this.mContext).edit().putBoolean(SessionCommitReceiver.ADD_ICON_PREFERENCE_KEY, readValueFromMarketApp()).putBoolean(SessionCommitReceiver.ADD_ICON_PREFERENCE_INITIALIZED_KEY, true).apply();
            return null;
        }

        /* JADX DEBUG: Another duplicated slice has different insns count: {[IF]}, finally: {[IF, INVOKE] complete} */
        /* JADX WARN: Removed duplicated region for block: B:22:0x0074 A[PHI: r2
          0x0074: PHI (r2v6 android.database.Cursor) = (r2v5 android.database.Cursor), (r2v7 android.database.Cursor) binds: [B:21:0x0072, B:15:0x0065] A[DONT_GENERATE, DONT_INLINE]] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public boolean readValueFromMarketApp() {
            /*
                r10 = this;
                android.content.Context r0 = r10.mContext
                android.content.pm.PackageManager r0 = r0.getPackageManager()
                android.content.Intent r1 = new android.content.Intent
                java.lang.String r2 = "android.intent.action.MAIN"
                r1.<init>(r2)
                java.lang.String r2 = "android.intent.category.APP_MARKET"
                android.content.Intent r1 = r1.addCategory(r2)
                r2 = 1114112(0x110000, float:1.561203E-39)
                android.content.pm.ResolveInfo r0 = r0.resolveActivity(r1, r2)
                r1 = 1
                if (r0 != 0) goto L1d
                return r1
            L1d:
                r2 = 0
                android.content.Context r3 = r10.mContext     // Catch: java.lang.Throwable -> L68 java.lang.Exception -> L6a
                android.content.ContentResolver r4 = r3.getContentResolver()     // Catch: java.lang.Throwable -> L68 java.lang.Exception -> L6a
                android.content.pm.ActivityInfo r0 = r0.activityInfo     // Catch: java.lang.Throwable -> L68 java.lang.Exception -> L6a
                java.lang.String r0 = r0.packageName     // Catch: java.lang.Throwable -> L68 java.lang.Exception -> L6a
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L68 java.lang.Exception -> L6a
                r3.<init>()     // Catch: java.lang.Throwable -> L68 java.lang.Exception -> L6a
                java.lang.String r5 = "content://"
                r3.append(r5)     // Catch: java.lang.Throwable -> L68 java.lang.Exception -> L6a
                r3.append(r0)     // Catch: java.lang.Throwable -> L68 java.lang.Exception -> L6a
                java.lang.String r0 = ".addtohomescreen"
                r3.append(r0)     // Catch: java.lang.Throwable -> L68 java.lang.Exception -> L6a
                java.lang.String r0 = r3.toString()     // Catch: java.lang.Throwable -> L68 java.lang.Exception -> L6a
                android.net.Uri r5 = android.net.Uri.parse(r0)     // Catch: java.lang.Throwable -> L68 java.lang.Exception -> L6a
                r6 = 0
                r7 = 0
                r8 = 0
                r9 = 0
                android.database.Cursor r2 = r4.query(r5, r6, r7, r8, r9)     // Catch: java.lang.Throwable -> L68 java.lang.Exception -> L6a
                boolean r0 = r2.moveToNext()     // Catch: java.lang.Throwable -> L68 java.lang.Exception -> L6a
                if (r0 == 0) goto L65
                java.lang.String r0 = "value"
                int r0 = r2.getColumnIndexOrThrow(r0)     // Catch: java.lang.Throwable -> L68 java.lang.Exception -> L6a
                int r0 = r2.getInt(r0)     // Catch: java.lang.Throwable -> L68 java.lang.Exception -> L6a
                if (r0 == 0) goto L5e
                goto L5f
            L5e:
                r1 = 0
            L5f:
                if (r2 == 0) goto L64
                r2.close()
            L64:
                return r1
            L65:
                if (r2 == 0) goto L77
                goto L74
            L68:
                r0 = move-exception
                goto L78
            L6a:
                r0 = move-exception
                java.lang.String r3 = "SessionCommitReceiver"
                java.lang.String r4 = "Error reading add to homescreen preference"
                android.util.Log.d(r3, r4, r0)     // Catch: java.lang.Throwable -> L68
                if (r2 == 0) goto L77
            L74:
                r2.close()
            L77:
                return r1
            L78:
                if (r2 == 0) goto L7d
                r2.close()
            L7d:
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.SessionCommitReceiver.PrefInitTask.readValueFromMarketApp():boolean");
        }
    }
}
