package com.lge.launcher3.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Process;
import android.os.SystemClock;
import android.os.UserHandle;
import android.telephony.TelephonyManager;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherProvider;
import com.lge.launcher3.allapps.AllAppsDBProvider;
import com.lge.launcher3.allapps.AllAppsItemFactory;
import com.lge.launcher3.homesettings.SBHomeDataBaseUtil;
import com.lge.launcher3.phoenix.ProcessPhoenix;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.silentota.SilentOTA_Extension;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;

/* JADX INFO: loaded from: classes.dex */
public class PendingIntentObjectList {

    public static class AppBoxRemovedHandler implements IntentHandler {
        public static final String APPBOX_REMOVE_INTENT = "com.lge.appbox.promising_app_removed";
        private static final String REMOVED_APP_SHORTCUT = "removed_promising_app";

        @Override // com.lge.launcher3.receiver.IntentHandler
        public String getNameOfIntent() {
            return APPBOX_REMOVE_INTENT;
        }

        @Override // com.lge.launcher3.receiver.IntentHandler
        public void onHandle(final Context context, Intent intent) {
            context.removeStickyBroadcast(intent);
            String stringExtra = intent.getStringExtra(REMOVED_APP_SHORTCUT);
            if (PackageUtils.isPackageInstalled(context, stringExtra)) {
                LGLog.i("AppBoxRemovedHandler", "onHandle ignore since " + stringExtra + " is already installed by somewhere");
                return;
            }
            removePromisingAppShortcut(context, stringExtra);
        }

        private static void removePromisingAppShortcut(Context context, String packageName) {
            LGLog.i("AppBoxRemovedHandler", "removePromisingAppShortcut() starts. : " + packageName);
            Launcher launcher = Launcher.getLauncher(context);
            UserHandle userHandleMyUserHandle = Process.myUserHandle();
            SilentOTA_Extension.updatePromisingPackage(packageName);
            launcher.getWorkspace().removeAbandonedPromise(packageName, userHandleMyUserHandle);
        }
    }

    public static class AppBoxInstalledHandler implements IntentHandler {
        public static final String APPBOX_RELOAD_INTENT = "com.lge.appbox.bootinstall.completed";
        private static final int INSTALL_COMPLETED_MIN_DELAY = 3000;
        private static final String NEED_TO_RELOAD_WORKSPACE = "need_to_reload_workspace";

        @Override // com.lge.launcher3.receiver.IntentHandler
        public String getNameOfIntent() {
            return APPBOX_RELOAD_INTENT;
        }

        @Override // com.lge.launcher3.receiver.IntentHandler
        public void onHandle(final Context context, Intent intent) {
            context.removeStickyBroadcast(intent);
            boolean booleanExtra = intent.getBooleanExtra(NEED_TO_RELOAD_WORKSPACE, false);
            boolean zIsUpgradeDB = LauncherProvider.isUpgradeDB(context);
            if (LGHomeFeature.Config.FEATURE_USE_SILENT_OTA.getValue() && LGHomeFeature.Config.FEATURE_USE_SILENT_OTA_EXTENSION.getValue()) {
                SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.AppBoxBootInstallKey.RECEIVED, true);
                LGLog.i(AppBoxInstalledHandler.class.getSimpleName(), "[SilentOTA_Extension] call makeAddedPackage");
                SilentOTA_Extension.makeAddedPackage(context);
            }
            if (!zIsUpgradeDB && booleanExtra) {
                long jElapsedRealtime = SystemClock.elapsedRealtime();
                long longExtra = jElapsedRealtime - intent.getLongExtra("com.lge.launcher3:received_time", jElapsedRealtime);
                if (longExtra > 3000) {
                    clearAndReloadWorkspaceNotify(context);
                } else {
                    new Handler().postDelayed(new Runnable() { // from class: com.lge.launcher3.receiver.PendingIntentObjectList.AppBoxInstalledHandler.1
                        @Override // java.lang.Runnable
                        public void run() {
                            AppBoxInstalledHandler.clearAndReloadWorkspaceNotify(context);
                        }
                    }, 3000 - longExtra);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void clearAndReloadWorkspaceNotify(Context context) {
            LGLog.i("AppBoxInstalledHandler", "clearAndReloadWorkspaceNotify() starts.");
            LauncherAppState.getInstance(context).clearAndReloadWorkspaceNotify();
        }
    }

    public static class OWBReloadHandler implements IntentHandler {
        public static final String OWB_RELOAD_INTENT = "com.lge.android.owb.OWB_RELOAD";

        @Override // com.lge.launcher3.receiver.IntentHandler
        public String getNameOfIntent() {
            return OWB_RELOAD_INTENT;
        }

        @Override // com.lge.launcher3.receiver.IntentHandler
        public void onHandle(Context context, Intent intent) {
            context.removeStickyBroadcast(intent);
            if (LauncherProvider.isUpgradeDB(context)) {
                return;
            }
            LauncherAppState.getInstance(context).clearAndReloadWorkspace();
        }
    }

    public static class SimChangeHandler implements IntentHandler {
        public static final String SIMCHANGE_INTENT = "android.intent.action.SIM_STATE_CHANGED";
        protected static final String TAG = "SimChangeHandler";

        @Override // com.lge.launcher3.receiver.IntentHandler
        public String getNameOfIntent() {
            return SIMCHANGE_INTENT;
        }

        @Override // com.lge.launcher3.receiver.IntentHandler
        public void onHandle(Context context, Intent intent) {
            if (LauncherProvider.isUpgradeDB(context)) {
                return;
            }
            if (!LGHomeFeature.isDisableEasyHome()) {
                LGLog.d(TAG, "SimChangeHandler skip : Easy Home");
                return;
            }
            if (SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.WorkspaceCAKey.ISLOADING, false)) {
                return;
            }
            int simState = ((TelephonyManager) context.getSystemService("phone")).getSimState();
            String str = TAG;
            LGLog.d(str, "curSimState = " + simState);
            if (simState == 5) {
                int iDFromCAList = DefaultWorkspaceLoader.getIDFromCAList(context);
                LGLog.d(str, "returnID = " + iDFromCAList);
                if (iDFromCAList > 0) {
                    SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.WorkspaceCAKey.ISLOADING, true);
                    if (!LGHomeFeature.isEnableDefaultHome()) {
                        if (AllAppsItemFactory.getInstance() != null) {
                            AllAppsItemFactory.getInstance().resetDatabase(null);
                        }
                        AllAppsDBProvider.destroyMenuDb();
                    }
                    LauncherAppState.getInstance(context).clearAndReloadWorkspace();
                    LGLog.d(str, "DB is clear by android.intent.action.SIM_STATE_CHANGED");
                }
            }
        }
    }

    public static class QMemoPanelHandler implements IntentHandler {
        public static final String ENABLE_QMEMOPANEL_INTENT = "com.lge.launcher3.intent.action.ENABLE_QMEMOPANEL";

        @Override // com.lge.launcher3.receiver.IntentHandler
        public String getNameOfIntent() {
            return ENABLE_QMEMOPANEL_INTENT;
        }

        @Override // com.lge.launcher3.receiver.IntentHandler
        public void onHandle(Context context, Intent intent) {
            if (HomeSettingsSharedPreferences.getEnableQmemopluspanel(context)) {
                SBHomeDataBaseUtil.turnOnQMemoPanel(context);
            } else {
                SBHomeDataBaseUtil.turnOffQMemoPanel(context);
            }
        }
    }

    public static class KillProcessHandler implements IntentHandler {
        public static final String KILL_PROCESS_INTENT = "com.lge.launcher3.intent.action.KILL_PROCESS";

        @Override // com.lge.launcher3.receiver.IntentHandler
        public String getNameOfIntent() {
            return KILL_PROCESS_INTENT;
        }

        @Override // com.lge.launcher3.receiver.IntentHandler
        public void onHandle(Context context, Intent intent) {
            Intent intent2 = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN);
            intent2.addCategory(PackageUtils.ANDROID_INTENT_CATEGORY_HOME);
            ProcessPhoenix.triggerRebirth(context, intent2);
        }
    }

    public static class CotaReloadHandler implements IntentHandler {
        public static final String COTA_DEFAULT_PAGE = "defaultpage";
        public static final String COTA_EXTRA_FILEPATH = "filepath";
        public static final String COTA_EXTRA_FILEPATH_APPS = "filepath_apps";
        public static final String COTA_EXTRA_PACKAGE = "packagename";
        public static final String COTA_EXTRA_SCREEN = "screen";
        public static final String COTA_FILEPATH = "launcher.cota.filepath";
        public static final String COTA_FILEPATH_APPS = "launcher.cota.filepath_apps";
        public static final String COTA_INTENT = "com.lge.launcher2.RELOAD_WORKSPACE";
        public static final String COTA_NEED_TO_MOVE_DEFAULT_PAGE = "launcher.cota.need_to_move_default_page";
        public static final String COTA_PACKAGE = "launcher.cota.package";
        public static final String COTA_RECHECK_OPTIONALS = "recheck_optionals";
        public static final String COTA_SCREEN = "launcher.cota.screen";
        public static final String COTA_TOTAL_PAGE = "workspacenum";
        private static final String TAG = "CotaReloadHandler";

        @Override // com.lge.launcher3.receiver.IntentHandler
        public String getNameOfIntent() {
            return COTA_INTENT;
        }

        @Override // com.lge.launcher3.receiver.IntentHandler
        public void onHandle(Context context, Intent intent) {
            String stringExtra = intent.getStringExtra(COTA_EXTRA_PACKAGE);
            String stringExtra2 = intent.getStringExtra(COTA_EXTRA_FILEPATH);
            String stringExtra3 = intent.getStringExtra(COTA_EXTRA_FILEPATH_APPS);
            int intExtra = intent.getIntExtra("screen", -1);
            int intExtra2 = intent.getIntExtra(COTA_DEFAULT_PAGE, 0);
            String str = TAG;
            LGLog.i(str, "onHandle(): package = " + stringExtra + ", filepath = " + stringExtra2 + ", filepathApps = " + stringExtra3);
            if (!LGHomeFeature.isDisableEasyHome()) {
                LGLog.i(str, "EasyHome is default, skip reload workspace");
                return;
            }
            if (stringExtra != null) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0);
                sharedPreferences.edit().putString(COTA_PACKAGE, stringExtra).commit();
                sharedPreferences.edit().putString(COTA_FILEPATH, stringExtra2).commit();
                sharedPreferences.edit().putString(COTA_FILEPATH_APPS, stringExtra3).commit();
                sharedPreferences.edit().putInt(COTA_SCREEN, intExtra).commit();
                sharedPreferences.edit().putInt(COTA_DEFAULT_PAGE, intExtra2).commit();
                if (intExtra2 != 0) {
                    sharedPreferences.edit().putBoolean(COTA_NEED_TO_MOVE_DEFAULT_PAGE, true).commit();
                }
                int i = intent.getBooleanExtra(COTA_RECHECK_OPTIONALS, false) ? 17 : 1;
                if (intExtra == 0) {
                    i |= 4;
                }
                if (!LGHomeFeature.isEnableDefaultHome()) {
                    if (AllAppsItemFactory.getInstance() != null) {
                        AllAppsItemFactory.getInstance().resetDatabase(null);
                    }
                    AllAppsDBProvider.destroyMenuDb();
                }
                LauncherAppState.getInstance(context).clearAndReloadWorkspace(i);
                return;
            }
            LauncherAppState.getInstance(context).clearAndReloadWorkspace(intent.getBooleanExtra(COTA_RECHECK_OPTIONALS, false) ? 16 : 0);
        }
    }
}
