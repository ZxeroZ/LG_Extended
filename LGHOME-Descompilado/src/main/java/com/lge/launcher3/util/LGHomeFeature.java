package com.lge.launcher3.util;

import android.app.LGSharedPreferences;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import com.lge.content.pm.PackageManagerEx;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;

/* JADX INFO: loaded from: classes.dex */
public class LGHomeFeature {
    public static final String TAG = "LGHomeFeature";
    public static final String WALLPAPERPICKER_PACKAGE = "com.lge.wallpaperpicker";
    private static boolean sAppsuggestionEnabled;
    private static boolean sInitialized;
    private static LGHomeFeature sLGHomeFeature;
    private String[] FEATURE_DEFAULT_THEME;

    /* JADX INFO: Access modifiers changed from: private */
    public static int getIconFrameSupports() {
        return 33947809;
    }

    public void checkLGBlurEngineInFW() {
    }

    public enum Config {
        FEATURE_USE_KNOCK_OFF(R.bool.feature_use_knock_off, false),
        FEATURE_USE_SMARTBULLETIN(R.bool.config_feature_use_smartbulletin, false),
        FEATURE_SUPPORT_SMARTBULLETIN_DOWNLOADABLE_PROVIDER(R.bool.config_feature_support_smartbulletin_downloadable_provider, false),
        FEATURE_USE_SMARTBULLETIN_NESTED_SCROLL(R.bool.config_feature_use_smartbulletin_nested_scroll, false),
        FEATURE_USE_DATA_CONNECTION_DIALOG_VDF(R.bool.config_feature_use_data_connection_dialog_vdf, false),
        FEATURE_USE_WIDGET_MAX_SPAN(R.bool.config_feature_use_widget_max_span, false),
        FEATURE_USE_EXTRA_WIDGET_INFO(R.bool.config_feature_use_extra_widget_info, false),
        FEATURE_USE_WIDGET_PRE_DRAG_CONDITION(R.bool.config_feature_use_widget_pre_drag_condition, true),
        FEATURE_USE_WIDGET_BLUR(R.bool.config_feature_use_widget_blur, true),
        FEATURE_USE_HOMESCREEN_BLUR(R.bool.config_feature_use_homescreen_blur, true),
        FEATURE_USE_LGBLURENGINE(R.bool.config_feature_use_lgblurengine, false),
        FEATURE_USE_BACKUP_RESTORE(R.bool.config_feature_use_backup_restore, true),
        FEATURE_USE_QMEMOPLUS_PANEL(R.bool.config_feature_use_qmemoplus_panel, false),
        FEATURE_USE_DYNAMIC_GRID(R.bool.config_feature_use_dynamic_grid, false),
        FEATURE_USE_SKT_PHONE_MODE(R.bool.config_feature_use_skt_phone_mode, false),
        FEATURE_USE_CARRIER_WALLPAPER_ITEM(R.bool.config_feature_use_carrier_wallpaer_item, false),
        FEATURE_USE_RECENT_UNINSTALL_APP(R.bool.config_feature_use_editmode_unintall_app, false),
        FEATURE_USE_EDITMODE_WALLPAPER(R.bool.config_feature_use_editmode_wallpaper, false),
        FEATURE_USE_EDITMODE_DYNAMICGRID(R.bool.config_feature_use_editmode_dynamic_grid, false),
        FEATURE_USE_EDITMODE_THEME(R.bool.config_feature_use_editmode_theme, false),
        FEATURE_USE_SORT_APPS_BY_NAME_IN_MULTIUSER(R.bool.config_feature_use_sort_apps_by_name_in_multiuser, false),
        FEATURE_ADD_NEW_ITEMS_ON_LAST_PAGE(R.bool.config_feature_add_new_items_on_last_page, false),
        FEATURE_SUPPORT_UNINSTALL_MODE(R.bool.config_feature_support_uninstall_mode, false),
        FEATURE_SUPPORT_GOOGLE_NOW(R.bool.config_feature_support_google_now, false),
        FEATURE_SUPPORT_GOOGLE_NOW_INIT_VALUE(R.bool.config_feature_support_google_now_init_value, false),
        FEATURE_SUPPORT_GOOGLE_HOTWORD(R.bool.config_feature_support_google_hotword, true),
        FEATURE_SUPPORT_SETTING_DDT_THEME(R.bool.config_feature_support_setting_ddt_theme, false),
        FEATURE_SUPPORT_GOOGLE_INAPPS(R.bool.config_feature_support_google_inapps, false),
        FEATURE_SUPPORT_GOOGLE_INAPPS_PREWARM(R.bool.config_feature_support_google_inapps_prewarm, false),
        FEATURE_SUPPORT_ABBA_SEARCH(R.bool.config_feature_support_abba_search, false),
        FEATURE_SUPPORT_OVERSCROLL_SLIDE_SCREEN_EFFECT(R.bool.config_feature_support_overscroll_slide_screen_effect, false),
        FEATURE_SUPPORT_FOLDER_ENLARGE_ANIMATION(R.bool.config_feature_support_folder_enlarge_animation, false),
        FEATURE_SUPPORT_ICON_FRAMES(LGHomeFeature.getIconFrameSupports(), false),
        FEATURE_SORT_APPS_EXCEPT_DEFAULT_SCREEN(R.bool.config_feature_sort_apps_except_default_screen, true),
        FEATURE_SUPPORT_FOLDER_EDITMODE_UI(R.bool.config_feature_use_folder_editmode_ui, false),
        FEATURE_USE_WORKSPACE_BG(R.bool.config_feature_use_workspace_bg, true),
        FEATURE_USE_NATIVE_WORKSPACE_BG(R.bool.config_feature_use_native_workspace_bg, false),
        FEATURE_DISABLE_ALLAPPS(R.bool.config_feature_disable_allapps, true),
        FEATURE_DISABLE_EASYHOME(R.bool.config_feature_disable_easyhome, true),
        FEATURE_USE_DEEPSHORTCUT_IN_ALLAPPS(R.bool.config_feature_use_deepshortcut_in_allapps, false),
        FEATURE_USE_SILENT_OTA(R.bool.config_feature_use_silent_ota, false),
        FEATURE_USE_SILENT_OTA_EXTENSION(R.bool.config_feature_use_silent_ota_extension, false),
        FEATURE_USE_ROUND_SEARCH_WIDGET(R.bool.config_feature_use_round_search_widget, false),
        FEATURE_USE_WALLPAPER_MOTION(R.bool.config_feature_use_wallpaper_motion, false),
        FEATURE_USE_VZW_SIDESCREEN(R.bool.config_feature_use_vzw_sidescreen, false),
        FEATURE_USE_SKIP_INITIALGUIDE_FOR_DEVICE_OWNER(R.bool.config_feature_use_skip_initialguide_for_device_owner, true),
        FEATURE_USE_SKIP_INITIALGUIDE(R.bool.config_feature_use_skip_initialguide, false),
        FEATURE_SWIPEUP_APPDRAWER(R.bool.config_feature_swipe_up_allapps, true),
        FEATURE_LOAD_DEFAULT_WORKSPACE(R.bool.config_feature_use_default_workspace_file, false),
        FEATURE_USE_DEFAULT_560DPI(R.bool.config_feature_use_default_560dpi, false),
        FEATURE_USE_DEFAULT_LOW_DPI(R.bool.config_feature_use_default_low_dpi, false),
        FEATURE_EDITMODE_LONGPRESS_DELAY(R.bool.config_feature_editmode_longpress_delay, false),
        FEATURE_USE_SHORTCUT_CUSHION(R.bool.config_feature_use_cushion_on_shortcut, false),
        FEATURE_USE_COMMAND_MULTIPHOTO(R.bool.config_feature_use_command_multiphoto, false),
        FEATURE_USE_WIDGET_SEARCH(R.bool.config_feature_use_widget_search, false),
        FEATURE_USE_PARALLAX(R.bool.config_feature_use_parallax, false),
        FEATURE_ICON_SCALE_BY_GRID(R.bool.config_feature_icon_scale_by_grid, false),
        FEATURE_NEW_ICON_SHAPE_LIST(R.bool.config_feature_new_icon_shape_list, false),
        FEATURE_SHORTCUT_BADGE_ENABLE(R.bool.config_feature_shortcut_badge, false),
        FEATURE_USE_FI_SIM_MODE(R.bool.config_feature_use_fi_sim_mode, false),
        FEATURE_USE_DUAL_APP(R.bool.config_feature_use_dual_app, false),
        FEATURE_SUPPORT_ADAPTIVEICON_ANIMATION(R.bool.config_feature_support_adaptiveicon_animation, true),
        FEATURE_SUPPORT_ADAPTIVE_STATUS_BAR_COLOR(R.bool.config_feature_support_adaptive_status_bar_color, true),
        FEATURE_GDEC_CHANGE_DEFAULT_PAGE(R.bool.config_feature_gdec_change_default_page, false),
        FEATURE_IMPROVE_RESOLUTION_CHANGE(R.bool.config_feature_improve_resolution_change, false),
        FEATURE_MOTION_SETTINGS_NOTIFYCHANGE(R.bool.config_feature_motion_settings_notifychange, true),
        FEATURE_APPDRAWER_LOOP_ENABLE(R.bool.config_feature_appdrawer_loop_enable, true),
        FEATURE_CHECK_ITEMINFO(R.bool.config_feature_check_iteminfo, false),
        FEATURE_ADD_NEW_SHORTCUT(R.bool.config_feature_add_new_shortcut, false),
        FEATURE_SUPPORT_SUGGESTION_APP(R.bool.config_feature_support_suggestion_app, false),
        FEATURE_UX_9_21(R.bool.config_feature_ux_9_21, false),
        FEATURE_OVERVIEW_NEW_UI(R.bool.config_feature_overview_new_ui, false),
        FEATURE_OVERVIEW_NEW_UI_REACTIVE_ANIMATION(R.bool.config_feature_overview_new_ui_reactive_animation, false),
        FEATURE_USE_LAUNCH_ANIMATION(R.bool.config_feature_use_launch_animation, false),
        FEATURE_USE_LGBLUR_2_WITH_LAUNCH_ANIM(R.bool.config_feature_use_lgblur_2_with_launch_anim, false),
        FEATURE_KT_GIFTBOX_DATA_FREE(R.bool.config_feature_kt_giftbox_data_free, false),
        FEATURE_SMARTBULLETIN_SET_DEFAULT_LAYOUT(R.bool.config_feature_smartbulletin_set_default_layout, false),
        FEATURE_USE_LETTERBOX_FOR_THUMBNAIL(R.bool.config_feature_use_letterbox_for_thumbnail, false),
        FEATURE_USE_SIMPLE_TRANSITION_OF_LANDSCAPE(R.bool.config_feature_use_simple_transition_landscape, false),
        FEATURE_CUSTOM_NONE_FLOATING_ICON_ANI(R.bool.config_feature_none_floating_icon_ani, false),
        FEATURE_USE_LASTPAGE_EFFECT(R.bool.config_feature_use_lastpage_effect, false),
        FEATURE_SWIPE_DOWN_HOME(R.bool.config_feature_swipe_down_home, true),
        FEATURE_SWIPE_DOWN_SUB_SWIVEL_HOME(R.bool.config_feature_swipe_down_sub_swivel_home, false),
        FEATURE_SWIPE_UP_HOME(R.bool.config_feature_swipe_up_home, true),
        FEATURE_USE_BACKGROUND_OF_ICON_ON_EASYHOME(R.bool.config_feature_use_background_of_icon_on_easy_home, false),
        FEATURE_USE_LGHOME_LAYOUT(R.bool.config_feature_use_lghome_layout, false),
        FEATURE_USE_CHANGE_WORKSPACE_GRID(R.bool.config_feature_use_lghome_layout, false),
        FEATURE_APPDRAWER_BUTTON_INIT_VALUE(R.bool.config_feature_appdrawer_button_init_value, false),
        FEATURE_APPDRAWER_BUTTON_SECOND_SCREEN_INIT_VALUE(R.bool.config_feature_appdrawer_button_second_screen_init_value, false),
        FEATURE_USE_GO_FULL_SCREEN(R.bool.config_feature_use_go_full_screen, true),
        FEATURE_SUPPORT_LANDSCAPE(R.bool.config_feature_support_landscape, false),
        FEATURE_CAROUSEL_LAYOUT(R.bool.config_feature_carousel_layout, false),
        FEATURE_USE_SWIVEL_HOME(R.bool.config_feature_use_swivel_home, false),
        FEATURE_USE_NEW_ALLAPPS_ANIMATION(R.bool.config_feature_use_new_allapps_animation, false),
        FEATURE_USE_RTL_DIRECTION_ON_RECENT_VIEW(R.bool.config_feature_use_rtl_direction_on_recent_view, false);

        private boolean mDefaultValue;
        private int mResoureID;
        private boolean mValue;

        Config(int resoureID, boolean defaultValue) {
            this.mResoureID = resoureID;
            this.mDefaultValue = defaultValue;
            this.mValue = defaultValue;
        }

        public void init(Context context) {
            try {
                this.mValue = context.getResources().getBoolean(this.mResoureID);
            } catch (Resources.NotFoundException unused) {
                this.mValue = this.mDefaultValue;
            }
        }

        public boolean getValue() {
            if (!LGHomeFeature.sInitialized) {
                RuntimeException runtimeException = new RuntimeException("LGHomeFeature is not initialized!");
                if ("user".equals(Build.TYPE)) {
                    runtimeException.printStackTrace();
                } else {
                    throw runtimeException;
                }
            }
            return this.mValue;
        }

        public void setValue(boolean value) {
            this.mValue = value;
        }
    }

    private LGHomeFeature(Context context) {
        for (Config config : Config.values()) {
            config.init(context);
        }
        String[] stringArray = LGHomeResources.getInstance(context).getStringArray("config_feature_default_theme");
        this.FEATURE_DEFAULT_THEME = stringArray;
        if (stringArray == null) {
            this.FEATURE_DEFAULT_THEME = new String[]{"com.lge.launcher2.theme.optimus"};
        }
        checkRecentlyUninstallImplementInFW();
        updateDefaultHome(context, getDefaultHome(context));
        updateEnableSwivelHomeState(context, getSwivelHomeStateFromPreferences(context));
        checkLGWallpaperPickerConfig(context);
        checkSmartSettingPackage(context);
        checkAppSuggestionConfig(context);
    }

    public static final void init(Object object) {
        if (sLGHomeFeature == null && (object instanceof Context)) {
            sLGHomeFeature = new LGHomeFeature((Context) object);
            sInitialized = true;
        }
    }

    public static final void destroy() {
        sLGHomeFeature = null;
        sInitialized = false;
    }

    public static final LGHomeFeature getInstance() {
        return sLGHomeFeature;
    }

    public void checkRecentlyUninstallImplementInFW() {
        try {
            PackageManagerEx.getDefault().getDisabledByLGLauncherPackageList(0);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NoClassDefFoundError unused) {
            LGLog.d(TAG, "Not implement PackageManagerEX in framework");
            Config.FEATURE_USE_RECENT_UNINSTALL_APP.setValue(false);
        }
    }

    private boolean getDisableAllAppsStateFromPreferences(Context context) {
        return getDefaultHome(context) != 1;
    }

    private boolean getDisableEasyHomeStateFromPreferences(Context context) {
        return getDefaultHome(context) != 2;
    }

    public boolean getSwivelHomeStateFromPreferences(Context context) {
        LGLog.d(TAG, "getSwivelHomeStateFromPreferences() PERM_PREFERENCES_KEY_SWIVELHOME_ENABLED = " + getPermanentPreferences(context).getBoolean(LauncherConst.PERM_PREFERENCES_KEY_SWIVELHOME_ENABLED, context.getResources().getBoolean(R.bool.config_feature_carousel_layout)));
        return getPermanentPreferences(context).getBoolean(LauncherConst.PERM_PREFERENCES_KEY_SWIVELHOME_ENABLED, context.getResources().getBoolean(R.bool.config_feature_carousel_layout));
    }

    public static void updateDisableAllAppsState(Context context, boolean disabled) {
        Config.FEATURE_DISABLE_ALLAPPS.setValue(disabled);
        LGLog.i(TAG, "updateDisableAllAppsState : disabled = " + disabled);
        getPermanentPreferences(context).edit().putBoolean(LauncherConst.PERM_PREFERENCES_KEY_ALLAPPS_DISABLED, disabled).commit();
    }

    public static void updateDisableEasyHomeState(Context context, boolean disabled) {
        Config.FEATURE_DISABLE_EASYHOME.setValue(disabled);
        LGLog.i(TAG, "updateDisableEasyHomeState : disabled = " + disabled);
        getPermanentPreferences(context).edit().putBoolean(LauncherConst.PERM_PREFERENCES_KEY_EASYHOME_DISABLED, disabled).commit();
    }

    public static void updateEnableSwivelHomeState(Context context, boolean Enbled) {
        Config.FEATURE_CAROUSEL_LAYOUT.setValue(Enbled);
        LGLog.i(TAG, "updateEnableSwivelHomeState : Enbled = " + Enbled);
        getPermanentPreferences(context).edit().putBoolean(LauncherConst.PERM_PREFERENCES_KEY_SWIVELHOME_ENABLED, Enbled).commit();
    }

    public static int getDefaultHome(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), LauncherConst.LGHOME_DEFAULT_HOME, 0, Process.myUserHandle().getIdentifier());
    }

    public static void checkAppSuggestionConfig(Context context) {
        sAppsuggestionEnabled = Settings.System.getInt(context.getContentResolver(), "app_suggestion_enabled", 1) == 1;
    }

    public static boolean isAppSuggestionEnabled() {
        return sAppsuggestionEnabled;
    }

    public static void updateDefaultHome(Context context, int defaultHome) {
        Config.FEATURE_DISABLE_ALLAPPS.setValue(defaultHome != 1);
        Config.FEATURE_DISABLE_EASYHOME.setValue(defaultHome != 2);
    }

    private static LGSharedPreferences getPermanentPreferences(Context context) {
        return LGSharedPreferences.get(context, LauncherConst.PERM_PREFERENCES_FILE_NANE, 0);
    }

    public static boolean isDisableAllApps() {
        return Config.FEATURE_DISABLE_ALLAPPS.getValue();
    }

    public static boolean isDisableEasyHome() {
        return Config.FEATURE_DISABLE_EASYHOME.getValue();
    }

    public static boolean isEnableDefaultHome() {
        return isDisableAllApps() && isDisableEasyHome();
    }

    public static boolean isUseCommandMultiPhoto() {
        return Config.FEATURE_USE_COMMAND_MULTIPHOTO.getValue();
    }

    public static boolean isLoadDefaultWorkspaceFile() {
        return Config.FEATURE_LOAD_DEFAULT_WORKSPACE.getValue();
    }

    public static boolean isSwipeUpAppDrawerEnable() {
        return !isDisableAllApps() && Config.FEATURE_SWIPEUP_APPDRAWER.getValue();
    }

    public static boolean isOverviewNewUIReactiveAnimationEnable() {
        return Config.FEATURE_OVERVIEW_NEW_UI_REACTIVE_ANIMATION.getValue();
    }

    private void checkLGWallpaperPickerConfig(Context context) {
        if (Build.VERSION.SDK_INT >= 28) {
            return;
        }
        try {
            Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication(WALLPAPERPICKER_PACKAGE);
            int identifier = resourcesForApplication.getIdentifier("com.lge.wallpaperpicker:bool/config_motion_settings_notifychange", null, null);
            boolean z = identifier != 0 ? resourcesForApplication.getBoolean(identifier) : false;
            LGLog.i(TAG, "com.lge.wallpaperpicker:bool/config_motion_settings_notifychange: " + z);
            Config.FEATURE_MOTION_SETTINGS_NOTIFYCHANGE.setValue(z);
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.i(TAG, "NameNotFoundException : " + e.toString());
        }
    }

    private void checkSmartSettingPackage(Context context) {
        if (context.getResources().getBoolean(R.bool.config_feature_support_suggestion_app)) {
            try {
                context.getPackageManager().getPackageInfo("com.lge.ia.task.smartsetting", 128);
                Config.FEATURE_SUPPORT_SUGGESTION_APP.setValue(!"on".equals(SystemProperties.get("persist.sys.epsmodestate", "off")));
            } catch (PackageManager.NameNotFoundException e) {
                LGLog.i(TAG, "NameNotFoundException : " + e.toString());
                Config.FEATURE_SUPPORT_SUGGESTION_APP.setValue(false);
            }
        }
    }
}
