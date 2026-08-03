package com.zxerox.lg_extended.hooks;

import android.view.View;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XSharedPreferences;

public class ScrimHook {
    public void hook(LoadPackageParam lpparam) {
        if (!lpparam.packageName.equals("com.android.systemui") && !lpparam.packageName.equals("com.lge.systemui")) return;

        try {
            Class<?> scrimControllerClass = XposedHelpers.findClass("com.android.systemui.statusbar.phone.ScrimController", lpparam.classLoader);
            Class<?> scrimViewClass = XposedHelpers.findClass("com.android.systemui.scrim.ScrimView", lpparam.classLoader);
            
            XposedHelpers.findAndHookMethod(scrimControllerClass, "attachViews", 
                    scrimViewClass, scrimViewClass, scrimViewClass, 
                    new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XSharedPreferences prefs = new XSharedPreferences("com.zxerox.lg_extended", "lg_extended_prefs");
                    prefs.makeWorldReadable();
                    boolean enabled = prefs.getBoolean("pref_enable_miui_qs", false);
                    
                    if (!enabled) {
                        try {
                            java.io.File f = new java.io.File("/data/local/tmp/lg_ext_miui_qs");
                            if (f.exists()) {
                                java.util.Scanner s = new java.util.Scanner(f);
                                enabled = s.nextBoolean();
                                s.close();
                            }
                        } catch (Exception ignored) {}
                    }
                    if (!enabled) return;

                    View mScrimBehind = (View) param.args[0];
                    if (mScrimBehind != null) {
                        try {
                            XposedHelpers.callMethod(mScrimBehind, "setBackgroundBlurRadius", 150);
                        } catch (Throwable t) {
                            XposedBridge.log("LG_Extended: setBackgroundBlurRadius not available on ScrimView.");
                        }
                        
                        XposedHelpers.setFloatField(param.thisObject, "mDefaultScrimAlpha", 0.4f);
                        XposedHelpers.setFloatField(param.thisObject, "mScrimBehindUnblurAlpha", 0.4f);
                    }
                }
            });
            
            XposedHelpers.findAndHookMethod(scrimControllerClass, "updateScrims", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XSharedPreferences prefs = new XSharedPreferences("com.zxerox.lg_extended", "lg_extended_prefs");
                    prefs.makeWorldReadable();
                    boolean enabled = prefs.getBoolean("pref_enable_miui_qs", false);
                    
                    if (!enabled) {
                        try {
                            java.io.File f = new java.io.File("/data/local/tmp/lg_ext_miui_qs");
                            if (f.exists()) {
                                java.util.Scanner s = new java.util.Scanner(f);
                                enabled = s.nextBoolean();
                                s.close();
                            }
                        } catch (Exception ignored) {}
                    }
                    if (!enabled) return;
                    
                    float currentAlpha = XposedHelpers.getFloatField(param.thisObject, "mBehindAlpha");
                    if (currentAlpha > 0.4f) {
                        XposedHelpers.setFloatField(param.thisObject, "mBehindAlpha", 0.4f);
                    }
                }
            });

        } catch (Throwable t) {
            XposedBridge.log("LG_Extended: Error in ScrimHook - " + t.getMessage());
        }
    }
}
