package com.zxerox.lg_extended.hooks;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import android.content.res.XModuleResources;
import com.zxerox.lg_extended.MainHook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import java.util.HashMap;
import java.util.Map;

public class SettingsHook {

    private static final String SETTINGS_PACKAGE = "com.android.settings";
    private static final String PREFS_URI = "content://com.zxerox.lg_extended.prefs/prefs";
    private static final String CARD_KEY = "lg_extended_profile_card";

    public void hook(LoadPackageParam lpparam) {
        if (!lpparam.packageName.equals(SETTINGS_PACKAGE)) return;

        try {
            Class<?> dashboardFragmentClass = XposedHelpers.findClass(
                    "com.android.settings.dashboard.DashboardFragment", lpparam.classLoader);

            XposedHelpers.findAndHookMethod(dashboardFragmentClass, "refreshAllPreferences",
                    String.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            try {
                                Object fragment = param.thisObject;
                                String className = fragment.getClass().getName();
                                if (!className.contains("TopLevelSettings")) return;

                                Context context = (Context) XposedHelpers.callMethod(fragment, "getContext");
                                Object screen = XposedHelpers.callMethod(fragment, "getPreferenceScreen");
                                if (context == null || screen == null) return;

                                String name = readPref(context, "profile_name", "LG V60 User");
                                if (name.isEmpty()) name = "LG V60 User";
                                String phrase = readPref(context, "profile_phrase", "Stock is a suggestion");
                                if (phrase.isEmpty()) phrase = "Stock is a suggestion";
                                String base64Avatar = readPref(context, "profile_avatar_base64", "");

                                View customCard = buildCustomCardView(context, name, phrase, base64Avatar);

                                String[] possibleClasses = {
                                        "com.android.settingslib.widget.LayoutPreference",
                                        "com.android.settings.applications.LayoutPreference",
                                        "com.android.settings.widget.LayoutPreference"
                                };

                                Class<?> layoutPrefClass = null;
                                for (String pkg : possibleClasses) {
                                    try {
                                        layoutPrefClass = XposedHelpers.findClass(pkg, lpparam.classLoader);
                                        if (layoutPrefClass != null) break;
                                    } catch (Throwable ignored) {}
                                }

                                if (layoutPrefClass != null) {
                                    Object pref = XposedHelpers.newInstance(layoutPrefClass,
                                            new Class[]{Context.class, View.class}, context, customCard);
                                    
                                    XposedHelpers.callMethod(pref, "setKey", CARD_KEY);
                                    XposedHelpers.callMethod(pref, "setOrder", -999);
                                    XposedHelpers.callMethod(pref, "setSelectable", false);

                                    XposedHelpers.callMethod(screen, "addPreference", pref);
                                    XposedBridge.log("SettingsHook: LayoutPreference injected successfully");

                                    try {
                                        android.app.Activity activity = (android.app.Activity) XposedHelpers.callMethod(fragment, "getActivity");
                                        if (activity != null) {
                                            int appBarId = context.getResources().getIdentifier("app_bar", "id", "com.android.settings");
                                            if (appBarId != 0) {
                                                View appBar = activity.findViewById(appBarId);
                                                if (appBar != null) {
                                                    android.view.ViewGroup.LayoutParams lp = appBar.getLayoutParams();
                                                    if (lp != null) {
                                                        lp.height = dpToPx(context, 180);
                                                        appBar.setLayoutParams(lp);
                                                    }
                                                    
                                                    TextView titleView = appBar.findViewById(android.R.id.text1);
                                                    if (titleView != null) {
                                                        titleView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 36);
                                                        android.view.ViewGroup.MarginLayoutParams mlp = (android.view.ViewGroup.MarginLayoutParams) titleView.getLayoutParams();
                                                        if (mlp != null) {
                                                            mlp.bottomMargin = dpToPx(context, 16);
                                                            titleView.setLayoutParams(mlp);
                                                        }
                                                    }
                                                    XposedBridge.log("SettingsHook: Native AppBar modified successfully");
                                                }
                                            }
                                        }
                                    } catch (Throwable t) {
                                        XposedBridge.log("SettingsHook: Error modifying Native AppBar: " + t.getMessage());
                                    }

                                    try {
                                        Class<?> prefCategoryClass = XposedHelpers.findClass("androidx.preference.PreferenceCategory", lpparam.classLoader);
                                        Object category = XposedHelpers.newInstance(prefCategoryClass, context);
                                        XposedHelpers.callMethod(category, "setKey", CARD_KEY + "_divider");
                                        XposedHelpers.callMethod(category, "setOrder", -998);
                                        
                                        int layoutId = context.getResources().getIdentifier("lge_preference_category_empty", "layout", "com.android.settings");
                                        if (layoutId != 0) {
                                            XposedHelpers.callMethod(category, "setLayoutResource", layoutId);
                                        }
                                        
                                        XposedHelpers.callMethod(screen, "addPreference", category);
                                        XposedBridge.log("SettingsHook: Divider category injected successfully");
                                    } catch (Throwable catEx) {
                                        XposedBridge.log("SettingsHook: Failed to inject divider category: " + catEx.getMessage());
                                    }
                                } else {
                                    XposedBridge.log("SettingsHook: LayoutPreference class not found, cannot inject custom view");
                                }

                            } catch (Throwable t) {
                                XposedBridge.log("SettingsHook ERROR: " + t.getMessage());
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log("SettingsHook SETUP FAILED: " + t.getMessage());
        }

        final Map<String, String> iconMap = new HashMap<>();
        iconMap.put("top_level_network", "ic_red_e_internet");
        iconMap.put("top_level_connected_devices", "ic_bluetooth");
        iconMap.put("top_level_sound", "ic_sonido");
        iconMap.put("top_level_notification", "ic_notificaciones");
        iconMap.put("top_level_display", "ic_pantalla");
        iconMap.put("top_level_theme", "ic_fondo_pantalla_y_tema");
        iconMap.put("top_level_security", "ic_pantalla_de_bloqueo_y_seguridad");
        iconMap.put("top_level_privacy", "ic_privacidad");
        iconMap.put("top_level_location", "ic_ubicacion");
        iconMap.put("top_level_useful_features", "ic_extensiones");
        iconMap.put("top_level_apps_and_notifs", "ic_aplicaciones");
        iconMap.put("top_level_digital_wellbeing", "ic_bienestar_digital");
        iconMap.put("top_level_battery", "ic_bateria");
        iconMap.put("top_level_storage", "ic_almacenamiento");
        iconMap.put("top_level_emergency", "ic_seguridad_y_emergencia");
        iconMap.put("top_level_accounts", "ic_cuentas");
        iconMap.put("top_level_system", "ic_sistema");
        iconMap.put("top_level_accessibility", "ic_accesibilidad");

        XC_MethodHook applyIconHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.args == null || param.args.length < 2) return;
                try {
                    Object adapter = param.thisObject;
                    Object holder = param.args[0];
                    int position = (int) param.args[1];

                    Object pref = XposedHelpers.callMethod(adapter, "getItem", position);
                    if (pref == null) return;

                    Context ctx = (Context) XposedHelpers.callMethod(pref, "getContext");
                    if (ctx == null) return;

                    boolean replaceIcons = Boolean.parseBoolean(readPref(ctx, "hook_settings_icons", "false"));
                    if (!replaceIcons) return;

                    String key = (String) XposedHelpers.callMethod(pref, "getKey");
                    if (key == null) return;

                    String iconName = iconMap.get(key);
                    if (iconName == null) return;

                    if (MainHook.MODULE_PATH == null) return;
                    XModuleResources modRes = XModuleResources.createInstance(MainHook.MODULE_PATH, null);
                    int resId = modRes.getIdentifier(iconName, "drawable", "com.zxerox.lg_extended");
                    if (resId == 0) {
                        XposedBridge.log("SettingsHook: Resource not found: " + iconName);
                        return;
                    }

                    Drawable newIcon = modRes.getDrawable(resId);
                    if (newIcon == null) return;

                    View itemView = (View) XposedHelpers.getObjectField(holder, "itemView");
                    if (itemView == null) return;

                    ImageView iconView = (ImageView) itemView.findViewById(android.R.id.icon);
                    if (iconView == null) {
                        int lgIconId = ctx.getResources().getIdentifier("icon", "id", "com.android.settings");
                        if (lgIconId != 0) {
                            iconView = itemView.findViewById(lgIconId);
                        }
                    }

                    if (iconView != null) {
                        iconView.setImageDrawable(newIcon);
                        iconView.setImageTintList(null);
                        iconView.clearColorFilter();
                        iconView.setBackground(null);
                        iconView.setBackgroundTintList(null);
                        
                        android.view.ViewGroup.LayoutParams lp = iconView.getLayoutParams();
                        if (lp != null) {
                            float scale = ctx.getResources().getDisplayMetrics().density;
                            int size = (int) (40 * scale + 0.5f);
                            lp.width = size;
                            lp.height = size;
                            iconView.setLayoutParams(lp);
                        }
                        iconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    }
                } catch (Throwable t) {
                    XposedBridge.log("SettingsHook applyIconHook Error: " + t.getMessage());
                }
            }
        };

        try {
            Class<?> adapterClass = XposedHelpers.findClassIfExists("com.android.settings.widget.HighlightablePreferenceGroupAdapter", lpparam.classLoader);
            if (adapterClass != null) {
                XposedBridge.log("SettingsHook: Hooking HighlightablePreferenceGroupAdapter.onBindViewHolder");
                XposedBridge.hookAllMethods(adapterClass, "onBindViewHolder", applyIconHook);
            }
            Class<?> baseAdapterClass = XposedHelpers.findClassIfExists("androidx.preference.PreferenceGroupAdapter", lpparam.classLoader);
            if (baseAdapterClass != null) {
                XposedBridge.log("SettingsHook: Hooking PreferenceGroupAdapter.onBindViewHolder");
                XposedBridge.hookAllMethods(baseAdapterClass, "onBindViewHolder", applyIconHook);
            }
        } catch (Throwable t) {
            XposedBridge.log("SettingsHook: Error hooking adapters: " + t.getMessage());
        }

        try {
            Class<?> iconColorDecoratorClass = XposedHelpers.findClassIfExists("com.android.settings.common.dashboard.DynamicIconColorDecorator", lpparam.classLoader);
            if (iconColorDecoratorClass != null) {
                XC_MethodHook blockTintHook = new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Object pref = null;
                        if (param.args.length == 3) { // setIconTint(View, Preference, boolean)
                            pref = param.args[1];
                        } else if (param.args.length == 1) { // setDynamicIconColor(Preference)
                            pref = param.args[0];
                        }
                        if (pref != null) {
                            String key = (String) XposedHelpers.callMethod(pref, "getKey");
                            if (key != null && iconMap.containsKey(key)) {
                                param.setResult(null);
                            }
                        }
                    }
                };
                XposedBridge.log("SettingsHook: Hooking DynamicIconColorDecorator to prevent tinting");
                XposedHelpers.findAndHookMethod(iconColorDecoratorClass, "setIconTint", View.class, "androidx.preference.Preference", boolean.class, blockTintHook);
                XposedHelpers.findAndHookMethod(iconColorDecoratorClass, "setDynamicIconColor", "androidx.preference.Preference", blockTintHook);
            }
        } catch (Throwable t) {
            XposedBridge.log("SettingsHook: Error hooking DynamicIconColorDecorator: " + t.getMessage());
        }

        try {
            Class<?> prefFragmentClass = XposedHelpers.findClassIfExists("androidx.preference.PreferenceFragmentCompat", lpparam.classLoader);
            if (prefFragmentClass != null) {
                XposedBridge.log("SettingsHook: Hooking PreferenceFragmentCompat.setDivider");
                XposedBridge.hookAllMethods(prefFragmentClass, "setDivider", XC_MethodReplacement.DO_NOTHING);
                XposedBridge.hookAllMethods(prefFragmentClass, "setDividerHeight", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.args[0] = 0;
                    }
                });
            }

            Class<?> recyclerViewClass = XposedHelpers.findClassIfExists("androidx.recyclerview.widget.RecyclerView", lpparam.classLoader);
            if (recyclerViewClass != null) {
                XposedBridge.log("SettingsHook: Hooking RecyclerView.addItemDecoration");
                XposedBridge.hookAllMethods(recyclerViewClass, "addItemDecoration", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Object decor = param.args[0];
                        if (decor != null) {
                            String className = decor.getClass().getName().toLowerCase();
                            if (className.contains("divider") || className.contains("decoration")) {
                                XposedBridge.log("SettingsHook: Blocked item decoration: " + className);
                                param.setResult(null);
                            }
                        }
                    }
                });
            }
            
            Class<?> lgHigHelperClass = XposedHelpers.findClassIfExists("com.lge.settingslib.utils.HIGHelper", lpparam.classLoader);
            if (lgHigHelperClass != null) {
                XposedBridge.log("SettingsHook: Hooking HIGHelper.isShowDivider");
                XposedBridge.hookAllMethods(lgHigHelperClass, "isShowDivider", XC_MethodReplacement.returnConstant(false));
            }
        } catch (Throwable t) {
            XposedBridge.log("SettingsHook: Error removing dividers: " + t.getMessage());
        }
    }

    private View buildCustomCardView(Context context, String name, String phrase, String base64Avatar) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.HORIZONTAL);
        
        card.setPadding(dpToPx(context, 16), dpToPx(context, 20), dpToPx(context, 16), dpToPx(context, 20));
        card.setGravity(Gravity.CENTER_VERTICAL);

        ImageView avatarView = new ImageView(context);
        Bitmap avatarBmp = decodeBase64Avatar(base64Avatar, name);
        Bitmap circularBmp = createCircularBitmap(avatarBmp);
        avatarView.setImageBitmap(circularBmp);
        
        int avatarSize = dpToPx(context, 48);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(avatarSize, avatarSize);
        ivParams.gravity = Gravity.CENTER_VERTICAL;
        card.addView(avatarView, ivParams);

        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textParams.leftMargin = dpToPx(context, 16);
        textLayout.setGravity(Gravity.CENTER_VERTICAL);
        textLayout.setLayoutParams(textParams);

        TextView nameView = new TextView(context);
        nameView.setText(name);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        nameView.setTypeface(null, Typeface.BOLD);
        nameView.setTextColor(getThemeColor(context, android.R.attr.textColorPrimary, 0xFF000000));

        TextView phraseView = new TextView(context);
        phraseView.setText(phrase);
        phraseView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        phraseView.setTextColor(getThemeColor(context, android.R.attr.textColorSecondary, 0xFF555555));
        
        textLayout.addView(nameView);
        textLayout.addView(phraseView);
        
        card.addView(textLayout);

        return card;
    }

    private int dpToPx(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private int getThemeColor(Context context, int attrId, int fallbackColor) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(attrId, typedValue, true)) {
            if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                return typedValue.data;
            } else if (typedValue.resourceId != 0) {
                try {
                    return context.getColor(typedValue.resourceId);
                } catch (Exception e) {
                    return fallbackColor;
                }
            }
        }
        return fallbackColor;
    }

    private Bitmap decodeBase64Avatar(String base64, String name) {
        if (base64 != null && !base64.isEmpty()) {
            try {
                byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                if (bmp != null) return bmp;
            } catch (Throwable ignored) {}
        }
        return createDefaultAvatar(name);
    }

    private String readPref(Context context, String key, String defaultValue) {
        try {
            Uri uri = Uri.parse(PREFS_URI);
            Cursor c = context.getContentResolver().query(uri,
                    new String[]{key}, null, new String[]{defaultValue}, null);
            if (c != null && c.moveToFirst()) {
                String value = c.getString(0);
                c.close();
                return value != null ? value : defaultValue;
            }
        } catch (Throwable ignored) {}
        return defaultValue;
    }

    private Bitmap createDefaultAvatar(String name) {
        int size = 256;
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        Paint bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(0xFF6750A4);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, bgPaint);

        String initial = (name != null && !name.isEmpty()) ? name.substring(0, 1).toUpperCase() : "U";
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(0xFFFFFFFF);
        textPaint.setTextSize(size * 0.45f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        float y = (size / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f);
        canvas.drawText(initial, size / 2f, y, textPaint);

        return bmp;
    }

    private Bitmap createCircularBitmap(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        float left = (size - source.getWidth()) / 2f;
        float top = (size - source.getHeight()) / 2f;
        canvas.drawBitmap(source, left, top, paint);
        return output;
    }
}
