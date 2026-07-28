package com.lge.launcher3.homesettings;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.operator.GoogleNowManager;
import com.lge.launcher3.swipehomescreen.SwipeDownHomeDialog;
import com.lge.launcher3.swipehomescreen.SwipeDownSubSwivelHomeDialog;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import com.lge.launcher3.util.Utilities;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class SearchIndexableItem {
    private static final String CLASS_NAME_HOME_SELECTOR = "com.lge.homeselector.HomeSelector";
    private static final String INTENT_ACTION_HOMESETTING_FRAGMENT = "com.lge.setting.intent.action.SHOW_FRAGMENT_HOME_SETTING";
    private static final String INTENT_ACTION_HOMESETTING_HELP_FRAGMENT = "com.lge.setting.intent.action.SHOW_FRAGMENT_HOME_SETTING_HELP";
    private static final String PACKAGE_NAME_HOME_SELECTOR = "com.lge.homeselector";
    private static final String TAG = "SearchIndexableItem";
    private Context mContext;
    private boolean mIsPrebuiltLGHome4;
    private boolean mIsSplitView;
    private ArrayList<SearchIndexableRaw> mRawData = new ArrayList<>();
    private static final String CLASS_NAME_HOMESETTING = HomeSettingsPrefActivity.class.getName();
    private static final String CLASS_NAME_SWIVELHOMESETTING = SwivelHomeSettingsPrefActivity.class.getName();

    public SearchIndexableItem(Context context) {
        Context contextCreatePackageContext;
        this.mContext = context;
        try {
            contextCreatePackageContext = new HomeSettingContext(this.mContext).createPackageContext("com.lge.launcher3", 0);
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.i(TAG, "homesettingContext.createPackageContext :" + e);
            e.printStackTrace();
            contextCreatePackageContext = null;
        }
        LGHomeFeature.init(contextCreatePackageContext);
        this.mIsSplitView = this.mContext.getResources().getBoolean(R.bool.is_tablet) || this.mContext.getResources().getBoolean(R.bool.is_large_tablet);
        setIndexableItemList();
    }

    private void setIndexableItemList() {
        String string = this.mContext.getString(R.string.display_settings);
        String str = string + " > " + this.mContext.getString(R.string.sp_homescreen_category_NORMAL);
        String str2 = string + " > " + this.mContext.getString(R.string.sp_swivel_homescreen_category_NORMAL);
        String str3 = str + " > " + this.mContext.getString(R.string.icon_shapes);
        String str4 = str + " > " + this.mContext.getString(R.string.menu_screen_effect);
        String name = HomescreenSettingsFragment.class.getName();
        String name2 = SwivelHomeSettingsPrefActivity.class.getName();
        String packageName = this.mContext.getPackageName();
        String preferredHomePackage = getPreferredHomePackage();
        boolean zEquals = packageName.equals(preferredHomePackage);
        boolean z = "com.lge.launcher2".equals(preferredHomePackage) && getPrebuiltLGHome4();
        this.mIsPrebuiltLGHome4 = z;
        LGLog.d(TAG, "LGHome5 : " + zEquals + ", prebuiltLGHome4 : " + z);
        String str5 = this.mIsSplitView ? INTENT_ACTION_HOMESETTING_FRAGMENT : PackageUtils.ANDROID_INTENT_ACTION_MAIN;
        setHomeSelectorIndexableItem(str, name, packageName, str5);
        setSmartBulletinIndexableItem(str, name, packageName, str5, zEquals);
        setQMemoPlusIndexableItem(str, name, packageName, str5, zEquals);
        setWallpaperMotionIndexableItem(str, name, packageName, str5, zEquals);
        setTWallpaperIndexableItem(str, name, packageName, str5);
        setScreenEffectIndexableItem(str, name, packageName, str5, zEquals);
        setScreenEffectTypesIndexableItem(str4, name, packageName, str5, zEquals);
        setDDTThemeIndexableItem(str, name, packageName, str5, zEquals);
        setDynamicGridIndexableItem(str, name, packageName, str5, zEquals);
        setSortAppsByIndexableItem(str, name, packageName, str5, zEquals);
        setHelpIndexableItem(str, name, packageName, str5, zEquals);
        setHideAppsIndexableItem(str, name, packageName, str5, zEquals);
        setGoogleNowIndexableItem(str, name, packageName, str5, zEquals);
        setContinuousLoopIndexableItem(str, name, packageName, str5, zEquals);
        setVZWAppDrawerLoopIndexableItem(str, name, packageName, str5, zEquals);
        setGoogleInAppsIndexableItem(str, name, packageName, str5, zEquals);
        setIconFramesIndexableItem(str, name, packageName, str5, zEquals);
        setIconFrameTypesIndexableItem(str3, name, packageName, str5, zEquals);
        setVZWSideScreenIndexableItem(str, name, packageName, str5, zEquals);
        setLeftScreenIndexableItem(str, name, packageName, str5, zEquals);
        setHomescreenLockIndexableItem(str, name, packageName, str5, zEquals);
        setSwipeDownHomeIndexableItem(str, name, packageName, str5, zEquals);
        setSwipeUpHomeIndexableItem(str, name, packageName, str5, zEquals);
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            setSwipeDownSwivelHomeIndexableItem(str2, name2, packageName, str5, zEquals);
            setSwipeDownSubSwivelHomeIndexableItem(str2, name2, packageName, str5, zEquals);
            setMultiAppShortcutIndexableItem(str2, name2, packageName, str5, zEquals);
            setSwingHomeHelpIndexableItem(str2, name2, packageName, str5, zEquals);
            setSwingHomeScreenEffectIndexableItem(str2, name2, packageName, str5, zEquals);
            setAppDrawerSwitchIndexableItem(str2, name2, packageName, str5, zEquals);
            setSwingHomescreenLockIndexableItem(str2, name2, packageName, str5, zEquals);
        } else {
            setWallpaperIndexableItem(str, name, packageName, str5, zEquals);
        }
        setThemedIconIndexableItem(str, name, packageName, str5, zEquals);
    }

    private void setSwipeDownHomeIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_SWIPE_DOWN_HOME, SwipeDownHomeDialog.getSwipeDownHomeTextString(this.mContext), className, this.mContext.getString(R.string.menu_swipe_down_home), screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, enabled && LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue());
    }

    private void setSwipeDownSwivelHomeIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_SWIPE_DOWN_SWIVEL_HOME, SwipeDownHomeDialog.getSwipeDownHomeTextString(this.mContext), className, this.mContext.getString(R.string.menu_swipe_down_home), screenTitle, null, null, intentAction, CLASS_NAME_SWIVELHOMESETTING, packageName, enabled && LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue());
    }

    private void setSwipeDownSubSwivelHomeIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_SWIPE_DOWN_SUB_SWIVEL_SWIVEL_HOME, SwipeDownSubSwivelHomeDialog.getSwipeDownSubSwivelHomeTextString(this.mContext), className, this.mContext.getString(R.string.menu_swipe_down_sub_swivel_home), screenTitle, null, null, intentAction, CLASS_NAME_SWIVELHOMESETTING, packageName, enabled && LGHomeFeature.Config.FEATURE_SWIPE_DOWN_SUB_SWIVEL_HOME.getValue());
    }

    private void setMultiAppShortcutIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_MULTI_APP_SHORTCUT, this.mContext.getString(R.string.dual_screen_app_pair_title), className, this.mContext.getString(R.string.dual_screen_app_pair_title), screenTitle, null, null, intentAction, CLASS_NAME_SWIVELHOMESETTING, packageName, enabled && LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue());
    }

    private void setSwingHomeHelpIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_SWINGHOME_HELP, this.mContext.getString(R.string.menu_help), className, this.mContext.getString(R.string.menu_help), screenTitle, null, null, intentAction, CLASS_NAME_SWIVELHOMESETTING, packageName, enabled && LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue());
    }

    private void setSwipeUpHomeIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_SWIPE_UP_HOME, this.mContext.getString(R.string.keywords_menu_swipe_up_home), className, this.mContext.getString(R.string.menu_swipe_up_home), screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, enabled && LGHomeFeature.Config.FEATURE_SWIPE_UP_HOME.getValue() && LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue() && LGHomeFeature.isEnableDefaultHome());
    }

    private void setHomescreenLockIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_HOME_SCREEN_LOCK, className, this.mContext.getResources().getString(R.string.home_screen_lock), screenTitle, this.mContext.getResources().getString(R.string.home_screen_lock_description), this.mContext.getResources().getString(R.string.home_screen_lock_description), intentAction, CLASS_NAME_HOMESETTING, packageName, enabled);
    }

    private void setSwingHomescreenLockIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_HOME_SCREEN_LOCK_SWIVEL_HOME, className, this.mContext.getResources().getString(R.string.home_screen_lock), screenTitle, this.mContext.getResources().getString(R.string.home_screen_lock_description), this.mContext.getResources().getString(R.string.home_screen_lock_description), intentAction, CLASS_NAME_SWIVELHOMESETTING, packageName, enabled);
    }

    private void setHideAppsIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_HIDE_APPS, this.mContext.getString(R.string.keywords_hide_apps_setting_title), className, this.mContext.getString(R.string.hide_apps_setting_title), screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, enabled && LGHomeFeature.isEnableDefaultHome());
    }

    private void setHomeSettingsCategoryIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        boolean z = enabled || !(this.mIsPrebuiltLGHome4 || "com.lge.launcher2".equals(getPreferredHomePackage()));
        if (this.mIsSplitView) {
            SettingsSearchUtils.isHomescreenSettingIncludedAtDisplayOnTablet();
        }
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_CATEGORY, className, screenTitle, this.mContext.getResources().getString(R.string.display_settings), null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, z);
    }

    private void setHomeSelectorIndexableItem(String screenTitle, String className, String packageName, String intentAction) {
        boolean z = (!HomeSettingsUtils.isHomeSelectorExist(this.mContext) || this.mIsPrebuiltLGHome4 || "com.lge.launcher2".equals(getPreferredHomePackage())) ? false : true;
        if (SettingsSearchUtils.hasOOSNewSettingSearchFeature(this.mContext)) {
            setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_SELECT_LAUNCHER, this.mContext.getString(R.string.keywords_sp_select_home_NORMAL), className, this.mContext.getString(R.string.sp_select_home_NORMAL), screenTitle, null, null, intentAction, CLASS_NAME_HOME_SELECTOR, PACKAGE_NAME_HOME_SELECTOR, z);
        } else {
            setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_SELECT_LAUNCHER, this.mContext.getString(R.string.keywords_sp_select_home_NORMAL), className, this.mContext.getString(R.string.sp_select_home_NORMAL), screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, z);
        }
    }

    private void setSmartBulletinIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        if (LGHomeFeature.Config.FEATURE_USE_VZW_SIDESCREEN.getValue() || LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_NOW.getValue()) {
            return;
        }
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_SMARTBULLETIN, className, this.mContext.getString(R.string.smartbulletin), screenTitle, this.mContext.getString(R.string.smartbulletin_summary), this.mContext.getString(R.string.smartbulletin_summary), intentAction, CLASS_NAME_HOMESETTING, packageName, LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN.getValue() && enabled);
    }

    private void setQMemoPlusIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_QMEMOPLUS_PANEL, className, this.mContext.getString(R.string.qmemoplus_panel_title), screenTitle, this.mContext.getString(R.string.qmemoplus_panel_description), this.mContext.getString(R.string.qmemoplus_panel_description), intentAction, CLASS_NAME_HOMESETTING, packageName, LGHomeFeature.Config.FEATURE_USE_QMEMOPLUS_PANEL.getValue() && enabled && SBHomeDataBaseUtil.existQmemoPanelItemInDataBase(this.mContext));
    }

    private void setDDTThemeIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_DDT_THEME, className, this.mContext.getString(R.string.sp_option_theme_NORMAL), screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, LGHomeFeature.Config.FEATURE_SUPPORT_SETTING_DDT_THEME.getValue());
    }

    private void setWallpaperIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_WALLPAPER, this.mContext.getString(R.string.keywords_menu_wallpaper), className, this.mContext.getString(R.string.menu_wallpaper), screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, enabled || !(this.mIsPrebuiltLGHome4 || "com.lge.launcher2".equals(getPreferredHomePackage())));
    }

    private void setWallpaperMotionIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_WALLPAPER_MOTION, className, this.mContext.getString(R.string.wp_tilting_title), screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, LGHomeFeature.Config.FEATURE_USE_WALLPAPER_MOTION.getValue() && enabled && !Utilities.isLGUI7_0());
    }

    /* JADX WARN: Removed duplicated region for block: B:7:0x001c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void setTWallpaperIndexableItem(java.lang.String r13, java.lang.String r14, java.lang.String r15, java.lang.String r16) {
        /*
            r12 = this;
            r11 = r12
            java.lang.String r0 = com.lge.launcher3.config.LGFeatureConfig.FEATURE_OPERATOR
            java.lang.String r1 = "SKT"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L1c
            android.content.Context r0 = r11.mContext
            r1 = 2131820736(0x7f1100c0, float:1.9274195E38)
            java.lang.String r1 = r0.getString(r1)
            boolean r0 = com.lge.launcher3.util.PackageUtils.isPackageInstalled(r0, r1)
            if (r0 == 0) goto L1c
            r0 = 1
            goto L1d
        L1c:
            r0 = 0
        L1d:
            r10 = r0
            android.content.Context r0 = r11.mContext
            r1 = 2131821200(0x7f110290, float:1.9275136E38)
            java.lang.String r3 = r0.getString(r1)
            r5 = 0
            r6 = 0
            java.lang.String r8 = com.lge.launcher3.homesettings.SearchIndexableItem.CLASS_NAME_HOMESETTING
            java.lang.String r1 = "homesettingsprefs_key_t_wallpaper"
            r0 = r12
            r2 = r14
            r4 = r13
            r7 = r16
            r9 = r15
            r0.setSearchIndexData(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.homesettings.SearchIndexableItem.setTWallpaperIndexableItem(java.lang.String, java.lang.String, java.lang.String, java.lang.String):void");
    }

    private void setAppDrawerSwitchIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_APP_DRAWER_SWICH, className, this.mContext.getString(R.string.app_drawer_icons_text), screenTitle, null, null, intentAction, className, packageName, enabled);
    }

    private void setScreenEffectIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT, className, this.mContext.getString(R.string.menu_screen_effect), screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, enabled);
    }

    private void setSwingHomeScreenEffectIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT_SWIVEL_HOME, className, this.mContext.getString(R.string.menu_screen_effect), screenTitle, null, null, intentAction, CLASS_NAME_SWIVELHOMESETTING, packageName, enabled);
    }

    private void setScreenEffectTypesIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        String string = this.mContext.getString(R.string.menu_screen_effect_basic);
        String str = CLASS_NAME_HOMESETTING;
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT_SLIDE, className, string, screenTitle, null, null, intentAction, str, packageName, enabled);
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT_BREEZE, className, this.mContext.getString(R.string.menu_screen_effect_breeze), screenTitle, null, null, intentAction, str, packageName, enabled);
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT_PANORAMA, className, this.mContext.getString(R.string.menu_screen_effect_panorama), screenTitle, null, null, intentAction, str, packageName, enabled);
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT_CAROUSEL, className, this.mContext.getString(R.string.menu_screen_effect_carousel), screenTitle, null, null, intentAction, str, packageName, enabled);
    }

    private void setDynamicGridIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_DYNAMIC_GRID, className, this.mContext.getString(R.string.dynamic_gird_label), screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, LGHomeFeature.Config.FEATURE_USE_DYNAMIC_GRID.getValue() && !LGHomeFeature.Config.FEATURE_USE_EDITMODE_DYNAMICGRID.getValue() && enabled);
    }

    private void setSortAppsByIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_SORT_APPS_BY, className, this.mContext.getString(R.string.sortappsby_title), screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, enabled && LGHomeFeature.isEnableDefaultHome());
    }

    private void setHelpIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        boolean z = enabled && (LGFeatureConfig.FEATURE_OPERATOR.equals("VZW") || LGFeatureConfig.FEATURE_OPERATOR.equals("ATT"));
        if (this.mIsSplitView) {
            setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_HELP, className, this.mContext.getString(R.string.menu_help), screenTitle, null, null, INTENT_ACTION_HOMESETTING_HELP_FRAGMENT, CLASS_NAME_HOMESETTING, packageName, z);
        } else {
            setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_HELP, className, this.mContext.getString(R.string.menu_help), screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, z);
        }
    }

    private void setGoogleNowIndexableItem(String screenTitle, String className, String pakageName, String intentAction, boolean enabled) {
        if (LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_NOW.getValue()) {
            GoogleNowManager.setAppEnabled(this.mContext.getPackageManager());
        }
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_GOOGLE_NOW, className, this.mContext.getString(R.string.google_feed), screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, pakageName, LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_NOW.getValue() && enabled && GoogleNowManager.isAppEnabled() && !LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN.getValue());
    }

    private void setContinuousLoopIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_CONTINUOUS_LOOP, className, this.mContext.getString(R.string.loop_home_screen), screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, enabled);
    }

    private void setVZWAppDrawerLoopIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_VZW_APPDRAWER_LOOP, className, this.mContext.getString(R.string.loop_apps_list), screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, LGFeatureConfig.FEATURE_OPERATOR.equals("VZW") && !(LGHomeFeature.isDisableAllApps() && LGHomeFeature.isDisableEasyHome()) && enabled);
    }

    private void setGoogleInAppsIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_GOOGLE_INAPPS, this.mContext.getString(R.string.keywords_google_inapps), className, this.mContext.getString(R.string.google_inapps), screenTitle, this.mContext.getString(R.string.google_inapps_summary), this.mContext.getString(R.string.google_inapps_summary), intentAction, CLASS_NAME_HOMESETTING, packageName, LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_INAPPS.getValue() && !LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue() && enabled);
    }

    private void setLeftScreenIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        boolean z = LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN.getValue() && (LGHomeFeature.Config.FEATURE_USE_VZW_SIDESCREEN.getValue() || LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_NOW.getValue()) && enabled;
        String string = this.mContext.getString(R.string.additional_screen_name);
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_LEFT_HOME_SCREEN, className, string, screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, z);
        if (z) {
            setLeftScreenTypeIndexableItem(screenTitle + " > " + string, className, packageName, intentAction, enabled);
        }
    }

    private void setLeftScreenTypeIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        boolean value = LGHomeFeature.Config.FEATURE_USE_VZW_SIDESCREEN.getValue();
        String string = this.mContext.getString(R.string.vzw_sidescreen_name);
        String str = CLASS_NAME_HOMESETTING;
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_LEFT_HOME_SCREEN_APPFLASH, className, string, screenTitle, null, null, intentAction, str, packageName, value);
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_LEFT_HOME_SCREEN_SMARTBULLETIN, className, this.mContext.getString(R.string.smartbulletin), screenTitle, null, null, intentAction, str, packageName, LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN.getValue());
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_LEFT_HOME_SCREEN_GOOGLE_FEED, className, this.mContext.getString(R.string.google_feed), screenTitle, null, null, intentAction, str, packageName, LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_NOW.getValue());
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_LEFT_HOME_SCREEN_NONE, className, this.mContext.getString(R.string.sp_none_home_NORMAL), screenTitle, null, null, intentAction, str, packageName, true);
    }

    private void setVZWSideScreenIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_VZW_SIDESCREEN, className, this.mContext.getString(R.string.vzw_sidescreen_name), screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, LGHomeFeature.Config.FEATURE_USE_VZW_SIDESCREEN.getValue() && enabled && !LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN.getValue());
    }

    private void setIconFramesIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAMES, className, this.mContext.getString(R.string.icon_shapes), screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, LGHomeFeature.Config.FEATURE_SUPPORT_ICON_FRAMES.getValue() && !DDTUtils.isAdditionalThemeApplied(this.mContext) && !DDTUtils.isAdditionalIconThemeApplied(this.mContext) && (enabled || LauncherConst.EASYHOME_PACKAGENAME.equals(getPreferredHomePackage())));
    }

    private void setIconFrameTypesIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        boolean z = LGHomeFeature.Config.FEATURE_SUPPORT_ICON_FRAMES.getValue() && !DDTUtils.isAdditionalThemeApplied(this.mContext) && !DDTUtils.isAdditionalIconThemeApplied(this.mContext) && (enabled || LauncherConst.EASYHOME_PACKAGENAME.equals(getPreferredHomePackage()));
        String string = this.mContext.getString(R.string.icon_shape_original);
        String str = CLASS_NAME_HOMESETTING;
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAME_TYPE_ORIGINAL, className, string, screenTitle, null, null, intentAction, str, packageName, z);
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAME_TYPE_ROUNDED_SQUARE, className, this.mContext.getString(R.string.icon_shape_rounded_square), screenTitle, null, null, intentAction, str, packageName, z);
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAME_TYPE_ROUND, className, this.mContext.getString(R.string.icon_shape_round), screenTitle, null, null, intentAction, str, packageName, z);
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAME_TYPE_CYLINDER, className, this.mContext.getString(R.string.icon_shape_cylinder), screenTitle, null, null, intentAction, str, packageName, z);
    }

    private void setThemedIconIndexableItem(String screenTitle, String className, String packageName, String intentAction, boolean enabled) {
        setSearchIndexData(HomeSettingsConstant.KEY_HOMESETTINGS_THEMED_ICON, className, this.mContext.getResources().getString(R.string.themed_icon_title), screenTitle, null, null, intentAction, CLASS_NAME_HOMESETTING, packageName, (DDTUtils.isAdditionalThemeApplied(this.mContext) || DDTUtils.isAdditionalIconThemeApplied(this.mContext) || !enabled) ? false : true);
    }

    private void setSearchIndexData(String key, String className, String title, String screenTtile, String summaryOn, String summaryOff, String intentAction, String intentClass, String intentPackage, boolean visible) {
        SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw();
        searchIndexableRaw.key = key;
        searchIndexableRaw.className = className;
        searchIndexableRaw.title = title;
        searchIndexableRaw.screenTitle = screenTtile;
        searchIndexableRaw.summaryOn = summaryOn;
        searchIndexableRaw.summaryOff = summaryOff;
        searchIndexableRaw.intentAction = intentAction;
        searchIndexableRaw.intentClass = intentClass;
        searchIndexableRaw.intentPackage = intentPackage;
        searchIndexableRaw.visible = visible;
        this.mRawData.add(searchIndexableRaw);
    }

    private void setSearchIndexData(String key, String keywords, String className, String title, String screenTtile, String summaryOn, String summaryOff, String intentAction, String intentClass, String intentPackage, boolean visible) {
        SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw();
        searchIndexableRaw.key = key;
        searchIndexableRaw.keywords = keywords;
        searchIndexableRaw.className = className;
        searchIndexableRaw.title = title;
        searchIndexableRaw.screenTitle = screenTtile;
        searchIndexableRaw.summaryOn = summaryOn;
        searchIndexableRaw.summaryOff = summaryOff;
        searchIndexableRaw.intentAction = intentAction;
        searchIndexableRaw.intentClass = intentClass;
        searchIndexableRaw.intentPackage = intentPackage;
        searchIndexableRaw.visible = visible;
        this.mRawData.add(searchIndexableRaw);
    }

    public final ArrayList<SearchIndexableRaw> values() {
        return this.mRawData;
    }

    private String getPreferredHomePackage() {
        ResolveInfo defaultHomeActivityResolveInfo = PackageUtils.getDefaultHomeActivityResolveInfo(this.mContext);
        if (defaultHomeActivityResolveInfo == null || defaultHomeActivityResolveInfo.activityInfo == null || defaultHomeActivityResolveInfo.activityInfo.packageName == null) {
            return null;
        }
        LGLog.d(TAG, "preferredLauncher : " + defaultHomeActivityResolveInfo.activityInfo.packageName);
        return defaultHomeActivityResolveInfo.activityInfo.packageName;
    }

    private boolean getPrebuiltLGHome4() {
        try {
            return (this.mContext.getPackageManager().getApplicationInfo("com.lge.launcher2", 128).flags & 1) != 0;
        } catch (PackageManager.NameNotFoundException unused) {
            LGLog.d(TAG, "LGHome4 NameNotFoundException");
            return false;
        }
    }
}
