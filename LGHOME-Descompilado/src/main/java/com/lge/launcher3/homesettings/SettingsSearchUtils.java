package com.lge.launcher3.homesettings;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.SystemProperties;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.operator.VZWSideScreenManager;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class SettingsSearchUtils {
    private static final String ACTION_SETTINGS_SEARCH = "com.lge.settings.SETTINGS_SEARCH";
    private static final String SETTINGS_SEARCH_CONTENT_URI = "content://com.android.settings.search.SearchDBupdateProvider/prefs_index";
    private static final String TAG = "SettingsSearch";

    public static boolean hasSettingSearchFeature(Context context) {
        if (Utilities.ATLEAST_OOS) {
            return false;
        }
        return getBooleanResource(context, "android.resource://com.android.settings/bool/config_settings_search_enable");
    }

    public static boolean hasNewSettingSearchFeature(Context context) {
        return getBooleanResource(context, "android.resource://com.android.settings/bool/config_new_settings_search_enable");
    }

    public static boolean hasOOSNewSettingSearchFeature(Context context) {
        return getBooleanResource(context, "android.resource://com.android.settings/bool/config_oos_new_search_enable");
    }

    public static void startSettingsSearchActivity(Activity activity) {
        try {
            Intent intent = new Intent();
            intent.setAction(ACTION_SETTINGS_SEARCH);
            intent.putExtra("search", true);
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static boolean getBooleanResource(Context context, String resUri) {
        Uri uri = Uri.parse(resUri);
        String path = uri.getPath();
        String host = uri.getHost();
        String[] strArrSplit = path.split("/");
        String str = strArrSplit[1];
        String str2 = strArrSplit[2];
        if (!str.equalsIgnoreCase("bool")) {
            return false;
        }
        try {
            Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication(host);
            return resourcesForApplication.getBoolean(resourcesForApplication.getIdentifier(str2, str, host));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (Resources.NotFoundException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static void updateCheckValue(Context context, String str, boolean z) {
        ContentValues contentValues = new ContentValues();
        LGLog.d(TAG, "Update check value: key=" + str + ", value=" + z);
        contentValues.put("check_value", Integer.valueOf(z ? 1 : 0));
        updateValue(context, str, contentValues);
    }

    public static void updateEnable(Context context, String str, boolean z) {
        ContentValues contentValues = new ContentValues();
        LGLog.d(TAG, "Update enable: key=" + str + ", value=" + z);
        contentValues.put("current_enable", Integer.valueOf(z ? 1 : 0));
        updateValue(context, str, contentValues);
    }

    public static void updateVisible(Context context, String str, boolean z) {
        ContentValues contentValues = new ContentValues();
        LGLog.d(TAG, "Update visible: key=" + str + ", value=" + z);
        contentValues.put("visible", Integer.valueOf(z ? 1 : 0));
        updateValue(context, str, contentValues);
    }

    private static void updateValue(Context context, String key, ContentValues values) {
        if (hasSettingSearchFeature(context) || hasNewSettingSearchFeature(context) || hasOOSNewSettingSearchFeature(context)) {
            LGLog.d(TAG, "Update value: key=" + key + ", value=" + values);
            try {
                context.getContentResolver().update(Uri.parse(SETTINGS_SEARCH_CONTENT_URI), values, "data_key_reference=? AND class_name=?", new String[]{key, HomescreenSettingsFragment.class.getName()});
            } catch (IllegalArgumentException e) {
                LGLog.w(TAG, "updateValue IllegalArgumentException :" + e, new int[0]);
            }
        }
    }

    public static void updateSmartBulletinOnOff(Context context, boolean bOn) {
        updateCheckValue(context, HomeSettingsConstant.KEY_HOMESETTINGS_SMARTBULLETIN, bOn);
    }

    public static void updateGoogleNowEnabled(Context context, boolean enabled) {
        updateCheckValue(context, HomeSettingsConstant.KEY_HOMESETTINGS_GOOGLE_NOW, enabled);
    }

    public static void updateGoogleInAppsEnabled(Context context, boolean enabled) {
        updateCheckValue(context, HomeSettingsConstant.KEY_HOMESETTINGS_GOOGLE_INAPPS, enabled);
    }

    public static void updateABBASearchEnabled(Context context, boolean enabled) {
        updateCheckValue(context, HomeSettingsConstant.KEY_HOMESETTINGS_ABBA_SEARCH, enabled);
    }

    public static void updateIconFramesEnabled(Context context, boolean enabled) {
        updateCheckValue(context, HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAMES, enabled);
    }

    public static void updateScreenEffectVisible(Context context, boolean isLGHome) {
        updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT, isLGHome);
        updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT_BREEZE, isLGHome);
        updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT_CAROUSEL, isLGHome);
        updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT_PANORAMA, isLGHome);
        updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT_SLIDE, isLGHome);
    }

    public static void updateIconFramesVisible(Context context, boolean isLGHome5, boolean isEasyHome) {
        if (LGHomeFeature.Config.FEATURE_SUPPORT_ICON_FRAMES.getValue()) {
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAMES, ((!isLGHome5 && !isEasyHome) || DDTUtils.isAdditionalThemeApplied(context) || DDTUtils.isAdditionalIconThemeApplied(context)) ? false : true);
        }
    }

    public static void updateIconFrameTypeVisible(Context context, boolean isLGHome5, boolean isEasyHome) {
        if (LGHomeFeature.Config.FEATURE_SUPPORT_ICON_FRAMES.getValue()) {
            boolean z = ((!isLGHome5 && !isEasyHome) || DDTUtils.isAdditionalThemeApplied(context) || DDTUtils.isAdditionalIconThemeApplied(context)) ? false : true;
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAME_TYPE_ORIGINAL, z);
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAME_TYPE_ROUNDED_SQUARE, z);
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAME_TYPE_ROUND, z);
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAME_TYPE_CYLINDER, z);
        }
    }

    public static void updateThemedIconVisible(Context context, boolean isLGHome5, boolean isEasyHome) {
        updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_THEMED_ICON, ((!isLGHome5 && !isEasyHome) || DDTUtils.isAdditionalThemeApplied(context) || DDTUtils.isAdditionalIconThemeApplied(context)) ? false : true);
    }

    public static void updateSettingSearchDB(Context context, boolean isDisableAllApps, boolean isLGHome, boolean isEasyHome) {
        boolean z;
        boolean z2 = false;
        if (HomeSettingsUtils.isHomeSelectorExist(context)) {
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_SELECT_LAUNCHER, true);
        } else {
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_SELECT_LAUNCHER, false);
        }
        updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_WALLPAPER, true);
        if (LGHomeFeature.Config.FEATURE_USE_WALLPAPER_MOTION.getValue() && !Utilities.isLGUI7_0()) {
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_WALLPAPER_MOTION, isLGHome);
        }
        updateScreenEffectVisible(context, isLGHome);
        if (LGHomeFeature.Config.FEATURE_SUPPORT_SETTING_DDT_THEME.getValue()) {
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_DDT_THEME, true);
        }
        updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_SORT_APPS_BY, isLGHome && isDisableAllApps);
        if (LGHomeFeature.Config.FEATURE_USE_DYNAMIC_GRID.getValue() && !LGHomeFeature.Config.FEATURE_USE_EDITMODE_DYNAMICGRID.getValue()) {
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_DYNAMIC_GRID, isLGHome);
        }
        if (LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN.getValue()) {
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_SMARTBULLETIN, isLGHome);
        }
        if (LGHomeFeature.Config.FEATURE_USE_QMEMOPLUS_PANEL.getValue()) {
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_QMEMOPLUS_PANEL, isLGHome);
        }
        updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_HIDE_APPS, isLGHome && isDisableAllApps);
        if (LGFeatureConfig.FEATURE_OPERATOR.equals("VZW") || LGFeatureConfig.FEATURE_OPERATOR.equals("ATT")) {
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_HELP, isLGHome);
        }
        if (!LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_NOW.getValue()) {
            z = false;
        } else if (LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN.getValue()) {
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_GOOGLE_NOW, false);
            z = true;
        } else {
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_GOOGLE_NOW, isLGHome);
            z = false;
        }
        if (LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_INAPPS.getValue() && !LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue()) {
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_GOOGLE_INAPPS, isLGHome);
        }
        if (LGHomeFeature.Config.FEATURE_USE_VZW_SIDESCREEN.getValue()) {
            if (LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN.getValue()) {
                updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_VZW_SIDESCREEN, false);
                z = true;
            } else {
                updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_VZW_SIDESCREEN, isLGHome && VZWSideScreenManager.isAppEnabled());
            }
        }
        if (z) {
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_LEFT_HOME_SCREEN, isLGHome);
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_LEFT_HOME_SCREEN_NONE, isLGHome);
        }
        updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_CONTINUOUS_LOOP, isLGHome);
        if (LGFeatureConfig.FEATURE_OPERATOR.equals("VZW")) {
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_VZW_APPDRAWER_LOOP, isLGHome && !isDisableAllApps);
        }
        updateIconFramesVisible(context, isLGHome, isEasyHome);
        updateIconFrameTypeVisible(context, isLGHome, isEasyHome);
        if (LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue()) {
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_SWIPE_DOWN_HOME, isLGHome);
        }
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            if (LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue()) {
                updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_SWIPE_DOWN_SWIVEL_HOME, isLGHome);
            }
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_MULTI_APP_SHORTCUT, isLGHome);
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_SWINGHOME_HELP, isLGHome);
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_APP_DRAWER_SWICH, isLGHome);
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_HOME_SCREEN_LOCK_SWIVEL_HOME, isLGHome);
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT_SWIVEL_HOME, isLGHome);
        }
        if (LGHomeFeature.Config.FEATURE_SWIPE_DOWN_SUB_SWIVEL_HOME.getValue()) {
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_SWIPE_DOWN_SUB_SWIVEL_SWIVEL_HOME, isLGHome);
        }
        if (LGHomeFeature.Config.FEATURE_SWIPE_UP_HOME.getValue() && LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue()) {
            if (isLGHome && isDisableAllApps) {
                z2 = true;
            }
            updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_SWIPE_UP_HOME, z2);
        }
        updateVisible(context, HomeSettingsConstant.KEY_HOMESETTINGS_HOME_SCREEN_LOCK, isLGHome);
        updateThemedIconVisible(context, isLGHome, isEasyHome);
    }

    public static boolean isHomescreenSettingIncludedAtDisplayOnTablet() {
        return (LGFeatureConfig.FEATURE_OPERATOR.equals("VZW") || LGFeatureConfig.FEATURE_OPERATOR.equals("ATT") || LGFeatureConfig.FEATURE_OPERATOR.equals("AIO") || LGFeatureConfig.FEATURE_OPERATOR.equals("CRK") || SystemProperties.getBoolean("ro.product.brand_qua", false)) ? false : true;
    }
}
