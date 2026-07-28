package com.lge.launcher3;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import com.android.launcher3.LauncherProvider;
import com.android.launcher3.notification.NotificationListener;
import com.lge.launcher3.homesettings.SwivelHomeSettingsPrefActivity;
import com.lge.launcher3.recentuninstall.RUActivity;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class LauncherApplication extends Application {
    private static final String TAG = "LauncherApplication";
    private AppSuggestionPreferenceObserver mAppSuggestionPreferenceObserver;
    private BroadcastReceiver mUnLockReceiver = null;

    private void setAppDrawerButtonInitValue() {
    }

    private void registerUnlockReceiver() {
        if (this.mUnLockReceiver != null) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.lge.launcher3.LauncherApplication.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.intent.action.USER_UNLOCKED")) {
                    LGLog.d(LauncherApplication.TAG, "onReceive " + intent.getAction());
                    try {
                        LauncherApplication launcherApplication = LauncherApplication.this;
                        launcherApplication.unregisterReceiver(launcherApplication.mUnLockReceiver);
                        context.getContentResolver().query(Uri.parse("content://" + LauncherProvider.AUTHORITY + "/"), null, null, null);
                    } catch (Exception unused) {
                        LGLog.d(LauncherApplication.TAG, "Dummy query For LaunchProvider");
                    }
                }
            }
        };
        this.mUnLockReceiver = broadcastReceiver;
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        String str = TAG;
        LGLog.i(str, "onCreate()");
        LGHomeFeature.init(this);
        setColorResource();
        setAppDrawerButtonInitValue();
        registerUnlockReceiver();
        getResources().getBoolean(R.bool.config_home_enabled);
        getResources().getBoolean(R.bool.config_home_appdrawer_enabled);
        getResources().getBoolean(R.bool.config_home_easyhome_enabled);
        NotificationListener.getInstance().registerSystemService(this);
        AppSuggestionPreferenceObserver appSuggestionPreferenceObserver = new AppSuggestionPreferenceObserver(this, new Handler());
        this.mAppSuggestionPreferenceObserver = appSuggestionPreferenceObserver;
        appSuggestionPreferenceObserver.registerObserver(getApplicationContext());
        PackageManager packageManager = getPackageManager();
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            packageManager.setComponentEnabledSetting(new ComponentName(this, (Class<?>) SwivelHomeSettingsPrefActivity.class), 1, 1);
        }
        if (!LGHomeFeature.Config.FEATURE_USE_RECENT_UNINSTALL_APP.getValue()) {
            packageManager.setComponentEnabledSetting(new ComponentName(this, (Class<?>) RUActivity.class), 2, 1);
        }
        LGLog.i(str, "[Icon_debug] LauncherApplication with activity context : " + getResources().getDisplayMetrics().toString());
        LGLog.i(str, "[Icon_debug] LauncherApplication with application context : " + getApplicationContext().getResources().getDisplayMetrics().toString());
    }

    @Override // android.app.Application
    public void onTerminate() {
        LGLog.i(TAG, "onTerminate()");
        NotificationListener.getInstance().unregisterSystemService();
        AppSuggestionPreferenceObserver appSuggestionPreferenceObserver = this.mAppSuggestionPreferenceObserver;
        if (appSuggestionPreferenceObserver != null) {
            appSuggestionPreferenceObserver.unregisterObserver(getApplicationContext());
            this.mAppSuggestionPreferenceObserver = null;
        }
        super.onTerminate();
    }

    private void setActivityDisabled(ComponentName componentName) {
        getPackageManager().setComponentEnabledSetting(componentName, 2, 1);
    }

    private void setColorResource() {
        Utilities.sWhite = getResources().getColor(R.color.white_color);
        Utilities.sBlack = getResources().getColor(R.color.black_color);
    }

    private class AppSuggestionPreferenceObserver extends ContentObserver {
        Context mContext;

        public AppSuggestionPreferenceObserver(Context context, Handler handler) {
            super(handler);
            this.mContext = context;
        }

        public void registerObserver(Context context) {
            context.getContentResolver().registerContentObserver(Settings.System.getUriFor("app_suggestion_enabled"), true, this);
        }

        public void unregisterObserver(Context context) {
            context.getContentResolver().unregisterContentObserver(this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            LGLog.d(LauncherApplication.TAG, "AppSuggestionPreferenceObserver onChange selfChange - " + selfChange);
            LGHomeFeature.checkAppSuggestionConfig(this.mContext);
        }
    }
}
