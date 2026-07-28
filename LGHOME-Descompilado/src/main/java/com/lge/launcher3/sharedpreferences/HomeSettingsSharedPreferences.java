package com.lge.launcher3.sharedpreferences;

import android.app.LGSharedPreferences;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.provider.Settings;
import com.lge.launcher3.R;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import com.lge.launcher3.util.ManagedProfileUtils;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class HomeSettingsSharedPreferences {
    public static final String ENABLE_APPDRAWER_BUTTON = "enable_appdrawer_button";
    public static final String ENABLE_CONTINUOUS_LOOP = "enable_continuous_loop";
    public static final String ENABLE_HOMESCREEN_LOCK = "enable_homescreen_lock";
    public static final String ENABLE_QMEMOPLUSPANEL = "enable_qmemopluspanel";
    public static final String ENABLE_SWING_HOME_SCREEN_LOCK = "enable_swing_home_screen_lock";
    public static final String ENABLE_VZW_APPDRAWER_LOOP = "enable_vzw_appdrawer_loop";
    public static final String SELECTED_SCREEN_EFFECT_INDEX = "selected_screen_effect_index";
    public static final String SELECTED_SECOND_SCREEN_EFFECT_INDEX = "selected_second_screen_effect_index";
    public static final String SELECTED_SORT_APPS_BY_INDEX = "selected_sort_apps_by_index";
    public static final String SELECTED_SWIPE_DOWN_HOME_INDEX = "selected_swipe_down_home_index";
    public static final String SELECTED_SWIPE_DOWN_SUB_SWIVEL_HOME_INDEX = "selected_swipe_down_sub_swivel_home_index";
    public static final String SELECTED_SWIPE_DOWN_SWIVEL_HOME_INDEX = "selected_swipe_down_swivel_home_index";
    public static final String SELECTED_SWIPE_UP_HOME_INDEX = "selected_swipe_up_home_index";
    public static final String SHARED_PREFERENCES_KEY = "homesettings_shared_prefs";
    private static final String TAG = "HomeSettingsSharedPreferences";
    private static SharedPreferences sSharedPref;

    public static SharedPreferences getHomeSettingsSharedPreferences(Context context, int mode) {
        if (sSharedPref == null) {
            try {
                if (Utilities.isAtLeastOriginalReleasePie()) {
                    sSharedPref = context.getSharedPreferences(SHARED_PREFERENCES_KEY, mode);
                } else {
                    Class.forName("android.app.LGSharedPreferences");
                    sSharedPref = LGSharedPreferences.get(context, SHARED_PREFERENCES_KEY, mode);
                }
            } catch (ClassNotFoundException unused) {
                sSharedPref = context.getSharedPreferences(SHARED_PREFERENCES_KEY, mode);
            }
        }
        return sSharedPref;
    }

    public static void clear(Context context, int mode) {
        SharedPreferences homeSettingsSharedPreferences = getHomeSettingsSharedPreferences(context, mode);
        if (homeSettingsSharedPreferences != null) {
            SharedPreferences.Editor editorEdit = homeSettingsSharedPreferences.edit();
            editorEdit.clear();
            editorEdit.commit();
        }
        setBasicHomeLockEnabled(context, context.getResources().getBoolean(R.bool.config_feature_home_screen_lock));
        setSwingHomeLockEnabled(context, context.getResources().getBoolean(R.bool.config_feature_home_screen_lock));
        if (Utilities.isAtLeastOriginalReleasePie()) {
            Settings.System.putInt(context.getContentResolver(), ENABLE_APPDRAWER_BUTTON, 1);
        }
    }

    public static int getSelectedScreenEffect(Context context) {
        return getHomeSettingsSharedPreferences(context, 4).getInt(SELECTED_SCREEN_EFFECT_INDEX, context.getResources().getInteger(R.integer.config_feature_default_screen_effect));
    }

    public static int getSelectedSecondScreenEffect(Context context) {
        return getHomeSettingsSharedPreferences(context, 4).getInt(SELECTED_SECOND_SCREEN_EFFECT_INDEX, context.getResources().getInteger(R.integer.config_feature_default_screen_effect));
    }

    public static void putSelectedScreenEffect(Context context, int index) {
        LGUserLog.send(context, LGUserLog.FEATURENAME_CHANGEEFFECT);
        getHomeSettingsSharedPreferences(context, 4).edit().putInt(SELECTED_SCREEN_EFFECT_INDEX, index).commit();
    }

    public static void putSelectedSecondScreenEffect(Context context, int index) {
        LGUserLog.send(context, LGUserLog.FEATURENAME_CHANGEEFFECT);
        getHomeSettingsSharedPreferences(context, 4).edit().putInt(SELECTED_SECOND_SCREEN_EFFECT_INDEX, index).commit();
    }

    public static int getSelectedSortAppsBy(Context context, int defaultValue) {
        return getHomeSettingsSharedPreferences(context, 4).getInt(SELECTED_SORT_APPS_BY_INDEX, defaultValue);
    }

    public static void putSelectedSortAppsBy(Context context, int index) {
        getHomeSettingsSharedPreferences(context, 4).edit().putInt(SELECTED_SORT_APPS_BY_INDEX, index).commit();
    }

    public static boolean getEnableQmemopluspanel(Context context) {
        return getHomeSettingsSharedPreferences(context, 4).getBoolean(ENABLE_QMEMOPLUSPANEL, false);
    }

    public static void putEnableQmemopluspanel(Context context, boolean enabled) {
        getHomeSettingsSharedPreferences(context, 4).edit().putBoolean(ENABLE_QMEMOPLUSPANEL, enabled).commit();
    }

    public static boolean getEnableAppDrawerButton(Context context) {
        return !LGHomeFeature.isDisableEasyHome() || Settings.System.getInt(context.getContentResolver(), ENABLE_APPDRAWER_BUTTON, 1) == 1;
    }

    public static boolean setGoogleNowEnabled(Context context, boolean z) {
        if (LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_NOW.getValue()) {
            LGUserLog.send(context, LGUserLog.FEATURENAME_GOOGLE_FEED, z ? 1 : 0);
            SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.GoogleNowKey.IS_ENABLED, z);
        }
        return true;
    }

    public static boolean getContinuousLoopEnabled(Context context) {
        return getHomeSettingsSharedPreferences(context, 4).getBoolean(ENABLE_CONTINUOUS_LOOP, context.getResources().getBoolean(R.bool.config_feature_default_continuous_loop));
    }

    public static void setContinuousLoopEnabled(Context context, boolean enabled) {
        getHomeSettingsSharedPreferences(context, 4).edit().putBoolean(ENABLE_CONTINUOUS_LOOP, enabled).commit();
    }

    public static boolean getVZWAppDrawerLoopEnabled(Context context) {
        return getHomeSettingsSharedPreferences(context, 4).getBoolean(ENABLE_VZW_APPDRAWER_LOOP, context.getResources().getBoolean(R.bool.config_feature_use_vzw_appdrawer_loop));
    }

    public static void setVZWAppDrawerLoopEnabled(Context context, boolean enabled) {
        getHomeSettingsSharedPreferences(context, 4).edit().putBoolean(ENABLE_VZW_APPDRAWER_LOOP, enabled).commit();
    }

    public static boolean getGoogleInAppsEnabled(Context context) {
        if (LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_INAPPS.getValue()) {
            return SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.GoogleInAppsKey.IS_ENABLED, true);
        }
        return false;
    }

    public static void setGoogleInAppsEnabled(Context context, boolean enabled) {
        if (LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_INAPPS.getValue()) {
            SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.GoogleInAppsKey.IS_ENABLED, enabled);
        }
    }

    public static boolean getABBASearchEnabled(Context context) {
        if (!LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue() || !com.android.launcher3.Utilities.existAndEnablePackage(context, com.android.launcher3.Utilities.ABBA_PACKAGE_NAME) || LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue() || ManagedProfileUtils.hasDeviceOwner(context)) {
            LGLog.i(TAG, "getABBASearchEnabled return None. ABBA feature = " + LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue() + ", ABBA Package = " + com.android.launcher3.Utilities.existAndEnablePackage(context, com.android.launcher3.Utilities.ABBA_PACKAGE_NAME) + ", swipe down home feature = " + LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue() + ", Device Owner = " + ManagedProfileUtils.hasDeviceOwner(context));
            return false;
        }
        return SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.ABBASearchKey.IS_ENABLED, true);
    }

    public static void setABBASearchEnabled(Context context, boolean enabled) {
        if (LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue()) {
            SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.ABBASearchKey.IS_ENABLED, enabled);
        }
    }

    public static void setVZWSideScreenEnabled(Context context, boolean enabled) {
        if (LGHomeFeature.Config.FEATURE_USE_VZW_SIDESCREEN.getValue()) {
            SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.VZWSideScreen.IS_ENABLED, enabled);
        }
    }

    public static boolean getVZWSideScreenEnabled(Context context) {
        if (LGHomeFeature.Config.FEATURE_USE_VZW_SIDESCREEN.getValue()) {
            return SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.VZWSideScreen.IS_ENABLED, true);
        }
        return false;
    }

    public static boolean getBasicHomeLockEnabled(Context context) {
        return SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.HomescreeenLockKey.IS_ENABLED, context.getResources().getBoolean(R.bool.config_feature_home_screen_lock));
    }

    public static boolean getHomescreenLockEnabled(Context context) {
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            return getSwingHomeLockEnabled(context);
        }
        return getBasicHomeLockEnabled(context);
    }

    public static void setBasicHomeLockEnabled(Context context, boolean enabled) {
        SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.HomescreeenLockKey.IS_ENABLED, enabled);
    }

    public static boolean getSwingHomeLockEnabled(Context context) {
        return SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.SwingHomescreenLockKey.IS_ENABLED, context.getResources().getBoolean(R.bool.config_feature_home_screen_lock));
    }

    public static void setSwingHomeLockEnabled(Context context, boolean enabled) {
        SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.SwingHomescreenLockKey.IS_ENABLED, enabled);
    }

    public static String getHomeLockDisableGuideText(Context context) {
        try {
            Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication("com.android.settings");
            int identifier = resourcesForApplication.getIdentifier("settings_label", "string", "com.android.settings");
            String string = identifier > 0 ? resourcesForApplication.getString(identifier) : null;
            int identifier2 = resourcesForApplication.getIdentifier("display_settings", "string", "com.android.settings");
            String string2 = identifier2 > 0 ? resourcesForApplication.getString(identifier2) : null;
            return LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() ? LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() ? String.format(context.getResources().getString(R.string.home_screen_lock_disable_guide_text, string, string2, context.getResources().getString(R.string.sp_swivel_homescreen_category_NORMAL)), new Object[0]) : String.format(context.getResources().getString(R.string.home_screen_lock_disable_guide_text, string, string2, context.getResources().getString(R.string.sp_basic_homescreen_category_NORMAL)), new Object[0]) : String.format(context.getResources().getString(R.string.home_screen_lock_disable_guide_text, string, string2, context.getResources().getString(R.string.sp_homescreen_category_NORMAL)), new Object[0]);
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.w(TAG, String.format("NameNotFoundException(%s)", e.getMessage()), new int[0]);
            return context.getResources().getString(R.string.config_swipe_down_on_the_sub_swivel_home_none);
        } catch (Exception e2) {
            LGLog.w(TAG, String.format("Exception(%s)", e2.getMessage()), new int[0]);
            return context.getResources().getString(R.string.config_swipe_down_on_the_sub_swivel_home_none);
        }
    }

    public static boolean getThemedIconEnabled(Context context) {
        return SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.ThemedIcon.IS_ENABLED, false);
    }

    public static void setThemedIconEnabled(Context context, boolean enabled) {
        SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.ThemedIcon.IS_ENABLED, enabled);
    }

    public static void setDeletePopupDialogDisable(Context context, boolean enabled) {
        SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.DoNotShowAgainPopUpkey.IS_DISABLED, enabled);
    }

    public static boolean getDeletePopupDialogDisable(Context context) {
        return SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.DoNotShowAgainPopUpkey.IS_DISABLED, false);
    }

    public static int getSwipeDownHome(Context context) {
        if (LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue()) {
            SharedPreferences homeSettingsSharedPreferences = getHomeSettingsSharedPreferences(context, 4);
            int integer = context.getResources().getInteger(R.integer.config_swipe_down_on_the_home_screen);
            if (!homeSettingsSharedPreferences.contains(SELECTED_SWIPE_DOWN_HOME_INDEX)) {
                initSwipeDownHome(context, integer);
            }
            return homeSettingsSharedPreferences.getInt(SELECTED_SWIPE_DOWN_HOME_INDEX, integer);
        }
        return com.android.launcher3.Utilities.SWIPE_DOWN_HOME_NONE;
    }

    private static void initSwipeDownHome(Context context, int defValue) {
        if (!com.android.launcher3.Utilities.supportIntegratedSearchOrSearchBySwipingDownHome(context)) {
            LGLog.i(TAG, "initSwipeDownHome() Swipe down home is created. not support IntegratedSearch and Search. put default value(" + defValue + ")");
            putSwipeDownHome(context, defValue);
        }
        if (LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue()) {
            Boolean boolValueOf = Boolean.valueOf(SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.ABBASearchKey.IS_ENABLED, true));
            if (boolValueOf.booleanValue()) {
                LGLog.i(TAG, "initSwipeDownHome() Swipe down home is created. put default value(" + defValue + "), abba = " + boolValueOf);
                putSwipeDownHome(context, defValue);
                return;
            }
            com.android.launcher3.Utilities.checkDefineValuesForSwipeDownHome(context, false);
            LGLog.i(TAG, "initSwipeDownHome() Swipe down home is created. put none(" + com.android.launcher3.Utilities.SWIPE_DOWN_HOME_NONE + "), abba = " + boolValueOf);
            putSwipeDownHome(context, com.android.launcher3.Utilities.SWIPE_DOWN_HOME_NONE);
            return;
        }
        if (LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_INAPPS.getValue()) {
            Boolean boolValueOf2 = Boolean.valueOf(SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.GoogleInAppsKey.IS_ENABLED, true));
            if (boolValueOf2.booleanValue()) {
                LGLog.i(TAG, "initSwipeDownHome() Swipe down home is created. put default value(" + defValue + "), inApps = " + boolValueOf2);
                putSwipeDownHome(context, defValue);
                return;
            }
            com.android.launcher3.Utilities.checkDefineValuesForSwipeDownHome(context, false);
            LGLog.i(TAG, "initSwipeDownHome() Swipe down home is created. put none(" + com.android.launcher3.Utilities.SWIPE_DOWN_HOME_NONE + "), inApps = " + boolValueOf2);
            putSwipeDownHome(context, com.android.launcher3.Utilities.SWIPE_DOWN_HOME_NONE);
        }
    }

    public static int getSwipeDownSwivelHome(Context context) {
        if (LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue()) {
            return getHomeSettingsSharedPreferences(context, 4).getInt(SELECTED_SWIPE_DOWN_SWIVEL_HOME_INDEX, context.getResources().getInteger(R.integer.config_swipe_down_on_the_home_screen));
        }
        return com.android.launcher3.Utilities.SWIPE_DOWN_HOME_NONE;
    }

    public static String getSwipeDownSubSwivelHome(Context context) {
        SharedPreferences homeSettingsSharedPreferences = getHomeSettingsSharedPreferences(context, 4);
        String string = context.getResources().getString(R.string.config_swipe_down_on_the_sub_swivel_home_default);
        if (LGHomeFeature.Config.FEATURE_SWIPE_DOWN_SUB_SWIVEL_HOME.getValue() && !LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue() && homeSettingsSharedPreferences.getString(SELECTED_SWIPE_DOWN_SUB_SWIVEL_HOME_INDEX, string).equals(context.getResources().getString(R.string.config_swipe_down_on_the_sub_swivel_home_integrated_search))) {
            putSwipeDownSubSwivelHome(context, string);
            return string;
        }
        if (LGHomeFeature.Config.FEATURE_SWIPE_DOWN_SUB_SWIVEL_HOME.getValue()) {
            return homeSettingsSharedPreferences.getString(SELECTED_SWIPE_DOWN_SUB_SWIVEL_HOME_INDEX, string);
        }
        return context.getResources().getString(R.string.config_swipe_down_on_the_sub_swivel_home_none);
    }

    public static void putSwipeDownSubSwivelHome(Context context, String name) {
        if (LGHomeFeature.Config.FEATURE_SWIPE_DOWN_SUB_SWIVEL_HOME.getValue()) {
            getHomeSettingsSharedPreferences(context, 4).edit().putString(SELECTED_SWIPE_DOWN_SUB_SWIVEL_HOME_INDEX, name).commit();
        }
    }

    public static void putSwipeDownHome(Context context, int index) {
        if (LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue()) {
            getHomeSettingsSharedPreferences(context, 4).edit().putInt(SELECTED_SWIPE_DOWN_HOME_INDEX, index).commit();
        }
    }

    public static void putSwipeDownSwivelHome(Context context, int index) {
        if (LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue()) {
            getHomeSettingsSharedPreferences(context, 4).edit().putInt(SELECTED_SWIPE_DOWN_SWIVEL_HOME_INDEX, index).commit();
        }
    }

    public static int getSwipeUpHome(Context context) {
        if (!LGHomeFeature.Config.FEATURE_SWIPE_UP_HOME.getValue() || !LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue() || !com.android.launcher3.Utilities.existAndEnablePackage(context, com.android.launcher3.Utilities.ABBA_PACKAGE_NAME) || !LGHomeFeature.isEnableDefaultHome() || ManagedProfileUtils.hasDeviceOwner(context)) {
            LGLog.i(TAG, "getSwipeUpHome return None. swipe up home feature = " + LGHomeFeature.Config.FEATURE_SWIPE_UP_HOME.getValue() + ", abba feature = " + LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue() + ", ABBA Package = " + com.android.launcher3.Utilities.existAndEnablePackage(context, com.android.launcher3.Utilities.ABBA_PACKAGE_NAME) + ", Default Home = " + LGHomeFeature.isEnableDefaultHome() + ", Device Owner = " + ManagedProfileUtils.hasDeviceOwner(context));
            return 1;
        }
        return getHomeSettingsSharedPreferences(context, 4).getInt(SELECTED_SWIPE_UP_HOME_INDEX, context.getResources().getInteger(R.integer.config_swipe_up_on_the_home_screen));
    }

    public static void putSwipeUpHome(Context context, int index) {
        if (LGHomeFeature.Config.FEATURE_SWIPE_UP_HOME.getValue() && LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue()) {
            getHomeSettingsSharedPreferences(context, 4).edit().putInt(SELECTED_SWIPE_UP_HOME_INDEX, index).commit();
        }
    }
}
