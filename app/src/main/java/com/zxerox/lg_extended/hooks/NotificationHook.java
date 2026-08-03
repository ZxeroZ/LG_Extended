package com.zxerox.lg_extended.hooks;

import android.view.View;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XSharedPreferences;

public class NotificationHook {
    public void hook(LoadPackageParam lpparam) {
        if (!lpparam.packageName.equals("com.android.systemui") && !lpparam.packageName.equals("com.lge.systemui")) return;

        try {
            Class<?> notificationBgViewClass = XposedHelpers.findClass("com.android.systemui.statusbar.notification.row.NotificationBackgroundView", lpparam.classLoader);
            
            XposedBridge.hookAllConstructors(notificationBgViewClass, new XC_MethodHook() {
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

                    View bgView = (View) param.thisObject;
                    try {
                        XposedHelpers.callMethod(bgView, "setBackgroundBlurRadius", 50);
                    } catch (Throwable t) {
                    }
                }
            });

            XposedBridge.hookAllMethods(notificationBgViewClass, "setTint", new XC_MethodHook() {
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
                    if (enabled) {
                        int color = (int) param.args[0];
                        View view = (View) param.thisObject;
                        android.graphics.drawable.Drawable bg = (android.graphics.drawable.Drawable) XposedHelpers.getObjectField(view, "mBackground");
                        if (bg != null) {
                            if (color != 0) {
                                bg.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC);
                            } else {
                                bg.clearColorFilter();
                            }
                        }
                        param.setResult(null);
                    }
                }
            });

            Class<?> expandableRowClass = XposedHelpers.findClass("com.android.systemui.statusbar.notification.row.ExpandableNotificationRow", lpparam.classLoader);
            XposedBridge.hookAllMethods(expandableRowClass, "setPinned", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    boolean pinned = (boolean) param.args[0];
                    View row = (View) param.thisObject;
                    try {
                        Object bgNormal = XposedHelpers.getObjectField(row, "mBackgroundNormal");
                        if (bgNormal != null) {
                            android.graphics.drawable.Drawable bg = (android.graphics.drawable.Drawable) XposedHelpers.getObjectField(bgNormal, "mBackground");
                            if (bg != null) {
                                if (pinned) {
                                    int currentTint = 0;
                                    try {
                                        currentTint = (int) XposedHelpers.getObjectField(bgNormal, "mTintColor");
                                    } catch (Throwable ignored) {}
                                    if (currentTint != 0) {
                                        int opaqueColor = currentTint | 0xFF000000;
                                        bg.setColorFilter(opaqueColor, android.graphics.PorterDuff.Mode.SRC);
                                    } else {
                                        bg.setColorFilter(0xFFFFFFFF, android.graphics.PorterDuff.Mode.SRC);
                                    }
                                } else {
                                    int currentTint = 0;
                                    try {
                                        currentTint = (int) XposedHelpers.getObjectField(bgNormal, "mTintColor");
                                    } catch (Throwable ignored) {}
                                    if (currentTint != 0) {
                                        bg.setColorFilter(currentTint, android.graphics.PorterDuff.Mode.SRC);
                                    } else {
                                        bg.clearColorFilter();
                                    }
                                }
                            }
                        }
                    } catch (Throwable t) {}
                }
            });

            Class<?> expandButtonClass = XposedHelpers.findClass("com.android.internal.widget.NotificationExpandButton", lpparam.classLoader);

            XC_MethodHook removeBgHook = new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        XSharedPreferences prefs = new XSharedPreferences("com.zxerox.lg_extended", "lg_extended_prefs");
                        prefs.makeWorldReadable();
                        boolean hideArrow = prefs.getBoolean("pref_hide_notification_arrow", false);
                        
                        if (!hideArrow) {
                            try {
                                java.io.File f = new java.io.File("/data/local/tmp/lg_ext_hide_arrow");
                                if (f.exists()) {
                                    java.util.Scanner s = new java.util.Scanner(f);
                                    hideArrow = s.nextBoolean();
                                    s.close();
                                }
                            } catch (Exception ignored) {}
                        }
                        
                        if (!hideArrow) return;

                        View btn = (View) param.thisObject;
                        if (btn instanceof android.view.ViewGroup) {
                            android.view.ViewGroup group = (android.view.ViewGroup) btn;
                            int pillId = group.getResources().getIdentifier("expand_button_pill", "id", "android");
                            if (pillId != 0) {
                                View pill = group.findViewById(pillId);
                                if (pill != null) {
                                    pill.setBackground(null);
                                    pill.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    } catch (Throwable t) {}
                }
            };
            
            XposedBridge.hookAllMethods(expandButtonClass, "onFinishInflate", removeBgHook);
            try {
                XposedBridge.hookAllMethods(expandButtonClass, "updateColors", removeBgHook);
            } catch (Throwable t) {}

            Class<?> stackScrollAlgorithmClass = XposedHelpers.findClass("com.android.systemui.statusbar.notification.stack.StackScrollAlgorithm", lpparam.classLoader);
            XposedBridge.hookAllMethods(stackScrollAlgorithmClass, "updateViewWithShelf", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        Object viewState = param.args[1];
                        boolean alreadyHidden = XposedHelpers.getBooleanField(viewState, "hidden");
                        if (alreadyHidden) return;

                        float yTranslation = XposedHelpers.getFloatField(viewState, "yTranslation");
                        float shelfY = (float) param.args[2];

                        Object expandableView = param.args[0];
                        int intrinsicHeight = (int) XposedHelpers.callMethod(expandableView, "getIntrinsicHeight");

                        if (intrinsicHeight > 0) {
                            float visibleHeight = shelfY - yTranslation;
                            if (visibleHeight > 0 && visibleHeight < intrinsicHeight * 0.3f) {
                                XposedHelpers.setBooleanField(viewState, "hidden", true);
                                XposedHelpers.setBooleanField(viewState, "inShelf", true);
                            }
                        }
                    } catch (Throwable t) {}
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("LG_Extended: Error in NotificationHook - " + t.getMessage());
        }
    }
}
