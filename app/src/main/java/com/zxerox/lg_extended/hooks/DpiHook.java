package com.zxerox.lg_extended.hooks;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.util.DisplayMetrics;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class DpiHook {

    private int dpiCache = -1;

    public void hook(final LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.zxerox.lg_extended")) {
            return;
        }

        XposedHelpers.findAndHookMethod(
                "android.content.res.ResourcesImpl",
                lpparam.classLoader,
                "updateConfiguration",
                Configuration.class,
                DisplayMetrics.class,
                "android.content.res.CompatibilityInfo",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (dpiCache == -1) {
                            Application app = AndroidAppHelper.currentApplication();
                            if (app != null) {
                                try {
                                    Uri uri = Uri.parse("content://com.zxerox.lg_extended.prefs/prefs");
                                    Cursor cursor = app.getContentResolver().query(
                                            uri,
                                            new String[]{lpparam.packageName},
                                            "int",
                                            new String[]{"0"},
                                            null
                                    );

                                    if (cursor != null && cursor.moveToFirst()) {
                                        String valorStr = cursor.getString(0);
                                        dpiCache = (valorStr != null && !valorStr.isEmpty()) ? Integer.parseInt(valorStr) : 0;
                                        cursor.close();
                                    } else {
                                        dpiCache = 0;
                                    }
                                } catch (Exception e) {
                                    dpiCache = 0;
                                }
                            } else {
                                return;
                            }
                        }

                        if (dpiCache <= 0) return;

                        Configuration config = (Configuration) param.args[0];
                        if (config != null) {
                            config.densityDpi = dpiCache;
                        }

                        DisplayMetrics metrics = (DisplayMetrics) param.args[1];
                        if (metrics != null) {
                            metrics.densityDpi = dpiCache;
                            metrics.density = dpiCache * 0.00625f;
                        }
                    }
                }
        );
    }
}