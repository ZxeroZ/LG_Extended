package com.zxerox.lg_extended.hooks;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class LauncherHook {

    public void hook(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.lge.launcher3") && !lpparam.packageName.equals("com.android.launcher3")) {
            return;
        }

        final XSharedPreferences prefs = new XSharedPreferences("com.zxerox.lg_extended", "lg_extended_prefs");
        prefs.makeWorldReadable();

        try {
            findAndHookMethod("com.lge.launcher3.allapps.AllAppsPagedView", lpparam.classLoader, "addApps", ArrayList.class, int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    prefs.reload();
                    if (prefs.getBoolean("hook_auto_sort_apps", false)) {
                        Class<?> sortTypeEnum = findClass("com.lge.launcher3.allapps.AllAppsSort$SortType", lpparam.classLoader);
                        Object nameSort = Enum.valueOf((Class<Enum>) sortTypeEnum, "NAME");
                        
                        XposedHelpers.callMethod(param.thisObject, "changeSortType", nameSort);
                        
                        XposedBridge.log("LG Extended: Auto-sorted apps alphabetically after installation.");
                    }
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("LG Extended LauncherHook error: " + t.getMessage());
        }
    }
}
