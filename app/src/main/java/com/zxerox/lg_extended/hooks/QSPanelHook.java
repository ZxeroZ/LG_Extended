package com.zxerox.lg_extended.hooks;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.content.res.Configuration;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XSharedPreferences;

public class QSPanelHook {
    public void hook(LoadPackageParam lpparam) {
        if (!lpparam.packageName.equals("com.android.systemui") && !lpparam.packageName.equals("com.lge.systemui")) return;

        try {
            Class<?> qsContainerImpl = XposedHelpers.findClass("com.android.systemui.qs.QSContainerImpl", lpparam.classLoader);
            XposedHelpers.findAndHookMethod(qsContainerImpl, "onFinishInflate", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    View container = (View) param.thisObject;
                    
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

                    int bgId = container.getResources().getIdentifier("quick_settings_background", "id", "com.android.systemui");
                    if (bgId != 0) {
                        View bg = container.findViewById(bgId);
                        if (bg != null) {
                            bg.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("LG_Extended: Error in QSPanelHook - " + t.getMessage());
        }
    }
}
