package com.zxerox.lg_extended;

import com.zxerox.lg_extended.hooks.BatteryHook;
import com.zxerox.lg_extended.hooks.DpiHook;
import com.zxerox.lg_extended.hooks.RecentsHook;
import com.zxerox.lg_extended.hooks.FlagSecureHook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.zxerox.lg_extended")) {
            try {
                new DpiHook().hook(lpparam);
            } catch (Throwable t) {
                XposedBridge.log("LG_Extended: Error en DpiHook - " + t.getMessage());
            }
        }
        if (lpparam.packageName.equals("com.android.systemui") || lpparam.packageName.equals("com.lge.systemui")) {
            try {
                new BatteryHook().hook(lpparam);
            } catch (Throwable t) {
                XposedBridge.log("LG_Extended: Error en BatteryHook - " + t.getMessage());
            }
        }
        if (lpparam.packageName.equals("com.lge.launcher3") || lpparam.packageName.equals("com.android.launcher3")) {
            try {
                new RecentsHook().hook(lpparam);
            } catch (Throwable t) {
                XposedBridge.log("LG_Extended: Error en RecentsHook - " + t.getMessage());
            }
        }
        if (lpparam.packageName.equals("android")) {
            try {
                new FlagSecureHook().hook(lpparam);
            } catch (Throwable t) {
                XposedBridge.log("LG_Extended: Error en FlagSecureHook - " + t.getMessage());
            }
        }
    }
}