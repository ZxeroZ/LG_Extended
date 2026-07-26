package com.zxerox.lg_extended.hooks;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class FlagSecureHook {

    public void hook(LoadPackageParam lpparam) {
        try {
            Class<?> windowStateClass = XposedHelpers.findClass(
                    "com.android.server.wm.WindowState",
                    lpparam.classLoader
            );

            final XSharedPreferences prefs = new XSharedPreferences("com.zxerox.lg_extended", "lg_extended_prefs");

            XposedHelpers.findAndHookMethod(
                    windowStateClass,
                    "isSecureLocked",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            prefs.reload();
                            boolean bypassActivo = prefs.getBoolean("bypass_flag_secure", true);
                            if (bypassActivo) {
                                param.setResult(false);
                            }
                        }
                    }
            );

            XposedBridge.log("LG_Extended/FlagSecure: hook aplicado en WindowState.isSecureLocked");
        } catch (Throwable t) {
            XposedBridge.log("LG_Extended/FlagSecure: error - " + t.getMessage());
        }
    }
}