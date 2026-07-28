package com.zxerox.lg_extended;

import android.content.ContentValues;
import android.content.Context;

import com.zxerox.lg_extended.hooks.BatteryHook;
import com.zxerox.lg_extended.hooks.DpiHook;
import com.zxerox.lg_extended.hooks.RecentsHook;
import com.zxerox.lg_extended.hooks.FlagSecureHook;
import com.zxerox.lg_extended.hooks.SettingsHook;
import com.zxerox.lg_extended.log.LogWriter;
import com.zxerox.lg_extended.prefs.ModPrefs;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    
    public static String MODULE_PATH;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
    }

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.zxerox.lg_extended")) {
            try {
                new DpiHook().hook(lpparam);
                markHookActive("dpi");
                logHook("DpiHook", lpparam.packageName, true);
            } catch (Throwable t) {
                XposedBridge.log("LG_Extended: Error en DpiHook - " + t.getMessage());
                logHook("DpiHook", lpparam.packageName, false);
            }
        }
        if (lpparam.packageName.equals("com.android.systemui") || lpparam.packageName.equals("com.lge.systemui")) {
            try {
                new BatteryHook().hook(lpparam);
                markHookActive("battery");
                logHook("BatteryHook", lpparam.packageName, true);
            } catch (Throwable t) {
                XposedBridge.log("LG_Extended: Error en BatteryHook - " + t.getMessage());
                logHook("BatteryHook", lpparam.packageName, false);
            }
        }
        if (lpparam.packageName.equals("com.lge.launcher3") || lpparam.packageName.equals("com.android.launcher3")) {
            try {
                new RecentsHook().hook(lpparam);
                markHookActive("recents");
                logHook("RecentsHook", lpparam.packageName, true);
            } catch (Throwable t) {
                XposedBridge.log("LG_Extended: Error en RecentsHook - " + t.getMessage());
                logHook("RecentsHook", lpparam.packageName, false);
            }
        }
        if (lpparam.packageName.equals("android")) {
            try {
                new FlagSecureHook().hook(lpparam);
                markHookActive("flagsecure");
                logHook("FlagSecureHook", lpparam.packageName, true);
            } catch (Throwable t) {
                XposedBridge.log("LG_Extended: Error en FlagSecureHook - " + t.getMessage());
                logHook("FlagSecureHook", lpparam.packageName, false);
            }
        }
        if (lpparam.packageName.equals("com.android.settings")) {
            try {
                new SettingsHook().hook(lpparam);
                markHookActive("settings");
                logHook("SettingsHook", lpparam.packageName, true);
            } catch (Throwable t) {
                XposedBridge.log("LG_Extended: Error en SettingsHook - " + t.getMessage());
                logHook("SettingsHook", lpparam.packageName, false);
            }
        }
    }

    private Context getModuleContext() {
        try {
            Method currentApp = Class.forName("android.app.ActivityThread")
                    .getDeclaredMethod("currentApplication");
            Context ctx = (Context) currentApp.invoke(null);
            if (ctx != null) {
                return ctx.createPackageContext("com.zxerox.lg_extended",
                        Context.CONTEXT_IGNORE_SECURITY);
            }
        } catch (Throwable ignored) {}
        return null;
    }

    private void logHook(String hookName, String packageName, boolean success) {
        try {
            Context ctx = getModuleContext();
            if (ctx != null) {
                LogWriter.write(ctx, success ? "OK" : "ERR", hookName, packageName, success);
            }
        } catch (Throwable ignored) {}
    }

    private void markHookActive(String hookName) {
        try {
            Context ctx = getModuleContext();
            if (ctx != null) {
                ContentValues values = new ContentValues();
                values.put("key", "hook_active_" + hookName);
                values.put("type", "boolean");
                values.put("value", "true");
                ctx.getContentResolver().insert(ModPrefs.CONTENT_URI, values);
            }
        } catch (Throwable ignored) {}
    }
}
