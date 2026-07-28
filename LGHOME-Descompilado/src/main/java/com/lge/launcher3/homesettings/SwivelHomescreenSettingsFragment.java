package com.lge.launcher3.homesettings;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.LGPreferenceFragment;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.lge.launcher3.R;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.dynamicgrid.DynamicGridManager;
import com.lge.launcher3.hideapps.HideAppsSettingActivity;
import com.lge.launcher3.initialguide.SwivelHomeGuideManager;
import com.lge.launcher3.operator.GVNUtils;
import com.lge.launcher3.operator.GoogleNowManager;
import com.lge.launcher3.operator.VZWSideScreenManager;
import com.lge.launcher3.screeneffect.LoopNormalModeManager;
import com.lge.launcher3.screeneffect.ScreenEffectManager;
import com.lge.launcher3.screeneffect.ScreenEffectSelectionDialog;
import com.lge.launcher3.screeneffect.ScreenEffectUtils;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.sortappsby.SortAppsByDialog;
import com.lge.launcher3.swipehomescreen.SwipeDownHomeDialog;
import com.lge.launcher3.swipehomescreen.SwipeDownSubSwivelHomeDialog;
import com.lge.launcher3.swipehomescreen.SwipeUpHomeDialog;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import com.lge.launcher3.util.PackageUtils;
import com.lge.launcher3.util.Utilities;
import com.lge.launcher3.util.dialog.LoadingProgressDialogAsyncTask;
import com.lge.launcher3.wallpapermotion.WallpaperMotionUtils;
import com.lge.lgdynamicactionbar.AppBarLayout;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class SwivelHomescreenSettingsFragment extends LGPreferenceFragment {
    private static final String EXTRA_FROM_SETTING_SEARCH = "from_setting_search";
    private static final String EXTRA_NEW_VALUE = "newValue";
    private static final String EXTRA_PERFORM = "perform";
    private static final String EXTRA_SEARCH_ITEM = "search_item";
    public static final int LGHOMESETTINGSPREF_REQUEST_FROM_SEARCH = 3;
    public static final int LGHOMESETTINGSPREF_REQUEST_ICON_FRAMES = 4;
    public static final int LGHOMESETTINGSPREF_REQUEST_ICON_FRAMES_FROM_SEARCH = 5;
    public static final int LGHOMESETTINGSPREF_REQUEST_LAUNCHER_SELECT = 0;
    public static final int LGHOMESETTINGSPREF_REQUEST_WALLPAPER_SELECT = 2;
    private static final String TAG = "SwivelHomescreenSettingsFragment";
    private boolean mSelectedLGHome = true;
    private boolean mSelectedEasyHome = false;
    private boolean mOOSNewSettingSearchFeature = true;
    public final int LIST_IDX_MIN = 0;
    public final int LIST_IDX_LAUNCHER = 0;
    public final int LIST_IDX_SMARTBULLETIN = 1;
    public final int LIST_IDX_QMEMOPLUS_PANEL = 2;
    public final int LIST_IDX_WALLPAPER = 3;
    public final int LIST_IDX_T_WALLPAPER = 4;
    public final int LIST_IDX_WALLPAPER_MOTION = 5;
    public final int LIST_IDX_SCREEN_EFFECT = 6;
    public final int LIST_IDX_DDT_THEME = 7;
    public final int LIST_IDX_DYNAMIC_GRID = 8;
    public final int LIST_IDX_SORTAPPSBY = 9;
    public final int LIST_IDX_HELP = 10;
    public final int LIST_IDS_HIDE_APPS = 11;
    public final int LIST_IDX_GOOGLE_NOW = 12;
    public final int LIST_IDX_CONTINUOUS_LOOP = 13;
    public final int LIST_IDX_VZW_APPDRAWER_LOOP = 14;
    public final int LIST_IDX_GOOGLE_INAPPS = 15;
    public final int LIST_IDX_ABBA_SEARCH = 16;
    public final int LIST_IDX_ICON_FRAMES = 17;
    public final int LIST_IDX_VZW_SIDESCREEN = 18;
    public final int LIST_IDX_LEFT_HOME_SCREEN = 19;
    public final int LIST_IDX_SWIPE_DOWN_ON_THE_HOME_SCREEN = 20;
    public final int LIST_IDX_SWIPE_UP_ON_THE_HOME_SCREEN = 21;
    public final int LIST_IDX_HOME_SCREEN_LOCK = 22;
    public final int LIST_IDX_SWIPE_DOWN_ON_THE_SUB_SWIVEL_HOME_SCREEN = 23;
    public final int LIST_IDX_MAX = 24;
    private final boolean[] mSupportItems = new boolean[24];
    private final ArrayList<Preference> mPreferenceList = new ArrayList<>();
    private boolean mNeedHighlight = true;
    private int mSelectedDynamicGrid = -1;
    private final BroadcastReceiver mLGMDMReceiver = new BroadcastReceiver() { // from class: com.lge.launcher3.homesettings.SwivelHomescreenSettingsFragment.11
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (LGMDMUtils.receiveLGMDMIntentAction(intent)) {
                SwivelHomescreenSettingsFragment.this.getActivity().finish();
            }
        }
    };
    BroadcastReceiver mWallpaperMotionReceiver = new BroadcastReceiver() { // from class: com.lge.launcher3.homesettings.SwivelHomescreenSettingsFragment.12
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (LGHomeFeature.Config.FEATURE_USE_WALLPAPER_MOTION.getValue()) {
                LGLog.i(SwivelHomescreenSettingsFragment.TAG, "onReceive(): " + intent.getAction());
                if (Utilities.ACTION_WALLPAPER_CHANGED.equals(intent.getAction()) || "android.os.action.POWER_SAVE_MODE_CHANGED".equals(intent.getAction())) {
                    SwivelHomescreenSettingsFragment.this.refreshPrefInfo();
                }
            }
        }
    };

    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onCreate(Bundle savedInstanceState) {
        boolean z;
        Activity activity = getActivity();
        View viewFindViewById = activity.findViewById(getResources().getIdentifier("headers", "id", LauncherConst.PACKAGE_NAME_NATIVE));
        if (viewFindViewById != null) {
            viewFindViewById.setVisibility(8);
        }
        Context contextCreatePackageContext = null;
        try {
            contextCreatePackageContext = new HomeSettingContext(getPackageContext()).createPackageContext("com.lge.launcher3", 0);
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.i(TAG, "homesettingContext.createPackageContext :" + e);
            e.printStackTrace();
        }
        LGHomeFeature.init(contextCreatePackageContext);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.lg_swivel_homesettings_prefs);
        setHasOptionsMenu(true);
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null && (activity instanceof SwivelHomeSettingsPrefActivity)) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (LGHomeFeature.Config.FEATURE_USE_VZW_SIDESCREEN.getValue()) {
            VZWSideScreenManager.setAppEnabled(activity.getPackageManager());
            z = true;
        } else {
            z = false;
        }
        if (LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_NOW.getValue()) {
            GoogleNowManager.setAppEnabled(activity.getPackageManager());
            z = true;
        }
        boolean[] zArr = this.mSupportItems;
        zArr[0] = true;
        zArr[1] = (!LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN.getValue() || z || GVNUtils.isGiovanna(getContext())) ? false : true;
        this.mSupportItems[2] = LGHomeFeature.Config.FEATURE_USE_QMEMOPLUS_PANEL.getValue();
        boolean[] zArr2 = this.mSupportItems;
        zArr2[3] = true;
        zArr2[4] = LGFeatureConfig.FEATURE_OPERATOR.equals("SKT") && PackageUtils.isPackageInstalled(contextCreatePackageContext, activity.getString(R.string.config_t_wallpaper_packagename));
        this.mSupportItems[5] = LGHomeFeature.Config.FEATURE_USE_WALLPAPER_MOTION.getValue() && !Utilities.isLGUI7_0();
        boolean[] zArr3 = this.mSupportItems;
        zArr3[6] = true;
        zArr3[7] = LGHomeFeature.Config.FEATURE_SUPPORT_SETTING_DDT_THEME.getValue();
        this.mSupportItems[8] = LGHomeFeature.Config.FEATURE_USE_DYNAMIC_GRID.getValue();
        this.mSupportItems[9] = LGHomeFeature.isEnableDefaultHome();
        this.mSupportItems[11] = LGHomeFeature.isEnableDefaultHome();
        this.mSupportItems[10] = LGFeatureConfig.FEATURE_OPERATOR.equals("VZW") || LGFeatureConfig.FEATURE_OPERATOR.equals("ATT");
        this.mSupportItems[12] = !LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN.getValue() && LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_NOW.getValue() && GoogleNowManager.isAppEnabled();
        this.mSupportItems[13] = !GVNUtils.isGiovanna(getContext());
        this.mSupportItems[14] = !LGHomeFeature.isDisableAllApps() && LGFeatureConfig.FEATURE_OPERATOR.equals("VZW");
        this.mSupportItems[15] = LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_INAPPS.getValue();
        this.mSupportItems[16] = LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue() && !LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue();
        this.mSupportItems[17] = LGHomeFeature.Config.FEATURE_SUPPORT_ICON_FRAMES.getValue();
        this.mSupportItems[18] = !LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN.getValue() && LGHomeFeature.Config.FEATURE_USE_VZW_SIDESCREEN.getValue() && VZWSideScreenManager.isAppEnabled();
        this.mSupportItems[19] = z && LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN.getValue();
        this.mSupportItems[20] = LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue();
        this.mSupportItems[21] = LGHomeFeature.Config.FEATURE_SWIPE_UP_HOME.getValue() && LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue() && LGHomeFeature.isEnableDefaultHome();
        boolean[] zArr4 = this.mSupportItems;
        zArr4[22] = true;
        zArr4[23] = LGHomeFeature.Config.FEATURE_SWIPE_DOWN_SUB_SWIVEL_HOME.getValue();
        if (activity instanceof SwivelHomeSettingsPrefActivity) {
            refreshPrefInfo();
        }
        String stringExtra = activity.getIntent().getStringExtra(EXTRA_SEARCH_ITEM);
        this.mOOSNewSettingSearchFeature = SettingsSearchUtils.hasOOSNewSettingSearchFeature(getActivity());
        if (stringExtra != null && !stringExtra.equals("") && this.mOOSNewSettingSearchFeature) {
            this.mNeedHighlight = false;
            onCreateFromSettingsSearch(stringExtra);
        }
        LGMDMUtils.registerLGMDMFilter(getContext(), this.mLGMDMReceiver);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Utilities.ACTION_WALLPAPER_CHANGED);
        intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        getContext().registerReceiver(this.mWallpaperMotionReceiver, intentFilter);
    }

    public void onResume() {
        super.onResume();
        refreshPrefInfo();
        onCheckFromSettingsSearch(getActivity().getIntent());
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        refreshPrefInfo();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDynamicActionBar(view);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_setting_main, container, false);
    }

    private void setDynamicActionBar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.launcher_toolbar);
        if (toolbar != null) {
            Activity activity = getActivity();
            activity.setActionBar(toolbar);
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getActionBar().setDisplayShowTitleEnabled(false);
        }
        AppBarLayout appBarLayoutFindViewById = view.findViewById(R.id.launcher_app_bar);
        if (appBarLayoutFindViewById != null) {
            appBarLayoutFindViewById.setExpanded(false);
            appBarLayoutFindViewById.setAppBarTitle(getResources().getString(R.string.sp_swivel_homescreen_category_NORMAL));
        }
    }

    public void onDestroy() {
        super.onDestroy();
        LGMDMUtils.unregisterLGMDMFilter(getContext(), this.mLGMDMReceiver);
        getContext().unregisterReceiver(this.mWallpaperMotionReceiver);
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        if (preferenceTreeClickInGeneralCategory(key) || preferenceTreeClickInStyleCategory(key) || preferenceTreeClickInLayoutCategory(key, preference) || preferenceTreeClickInTipsCategory(key)) {
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private boolean preferenceTreeClickInGeneralCategory(String key) {
        if (!HomeSettingsConstant.KEY_HOMESETTINGS_SELECT_LAUNCHER.equals(key)) {
            return false;
        }
        callSelectLauncher(false);
        return true;
    }

    private boolean preferenceTreeClickInStyleCategory(String key) {
        if (HomeSettingsConstant.KEY_HOMESETTINGS_WALLPAPER.equals(key)) {
            callSelectWallpaper(false);
            return true;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_WALLPAPER_MOTION.equals(key)) {
            callWallpaperMotionPrefActivity(false);
            return true;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_T_WALLPAPER.equals(key)) {
            callTWallpaper();
            return true;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT_SWIVEL_HOME.equals(key)) {
            callScreenEffectPrefActivity(false);
            return true;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_DDT_THEME.equals(key)) {
            callSelectDDTTheme(false);
            return true;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_LEFT_HOME_SCREEN.equals(key)) {
            callAdditionalPrefActivity(false);
            return true;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_MULTI_APP_SHORTCUT.equals(key)) {
            callMultiAppShorcut(false);
            return true;
        }
        if (!HomeSettingsConstant.KEY_HOMESETTINGS_SWINGHOME_HELP.equals(key)) {
            return false;
        }
        callSwingHomeInitialGuide(false);
        return true;
    }

    private boolean preferenceTreeClickInLayoutCategory(String key, Preference preference) {
        if (HomeSettingsConstant.KEY_HOMESETTINGS_SORT_APPS_BY.equals(key)) {
            callSelectSortAppsBy();
            return true;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_DYNAMIC_GRID.equals(key)) {
            callSelectDynamicGrid();
            return true;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_SMARTBULLETIN.equals(key)) {
            callSmartBulletinSetting(preference, false);
            return true;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_HIDE_APPS.equals(key)) {
            callHideAppsSettingActivity(false);
            return true;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_SWIPE_DOWN_HOME.equals(key)) {
            callSwipeDownOnTheHomeScreen();
            return true;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_SWIPE_DOWN_SWIVEL_HOME.equals(key)) {
            callSwipeDownOnTheHomeScreen();
            return true;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_SWIPE_DOWN_SUB_SWIVEL_SWIVEL_HOME.equals(key)) {
            callSwipeDownOnTheSubSwivelHomeScreen();
            return true;
        }
        if (!HomeSettingsConstant.KEY_HOMESETTINGS_SWIPE_UP_HOME.equals(key)) {
            return false;
        }
        callSwipeUpOnTheHomeScreen();
        return true;
    }

    private boolean preferenceTreeClickInTipsCategory(String key) {
        if (!HomeSettingsConstant.KEY_HOMESETTINGS_HELP.equals(key)) {
            return false;
        }
        boolean z = getContext().getResources().getBoolean(R.bool.is_tablet) || getContext().getResources().getBoolean(R.bool.is_large_tablet);
        boolean z2 = getResources().getBoolean(getResources().getIdentifier("preferences_prefer_dual_pane", "bool", LauncherConst.PACKAGE_NAME_NATIVE));
        if (z && z2) {
            return false;
        }
        callSelectHelp(false);
        return true;
    }

    public void refreshPrefInfo() {
        if (getActivity() == null) {
            LGLog.d(TAG, "refreshPrefInfo() activity is null");
            return;
        }
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.removeAll();
            this.mPreferenceList.clear();
        }
        addPreferencesFromResource(R.layout.lg_homesettings_prefs);
        makePreferenceItem();
        PreferenceScreen preferenceScreen2 = getPreferenceScreen();
        if (preferenceScreen2 == null) {
            LGLog.w(TAG, "root is null", new int[0]);
            return;
        }
        Iterator<Preference> it = this.mPreferenceList.iterator();
        while (it.hasNext()) {
            preferenceScreen2.addPreference(it.next());
        }
    }

    private Preference makePreferenceCategory(int titleResId, String key) {
        PreferenceCategory preferenceCategory = new PreferenceCategory(getActivity());
        preferenceCategory.setTitle(titleResId);
        preferenceCategory.setKey(key);
        this.mPreferenceList.add(preferenceCategory);
        return preferenceCategory;
    }

    private Preference makeEmptyPreferenceCategory(int titleResId, String key) {
        PreferenceCategory preferenceCategory = new PreferenceCategory(getActivity());
        preferenceCategory.setTitle(titleResId);
        preferenceCategory.setKey(key);
        preferenceCategory.setLayoutResource(33751160);
        this.mPreferenceList.add(preferenceCategory);
        return preferenceCategory;
    }

    private void makePreferenceItem() {
        getActivity();
        makePreferenceCategory(R.string.sp_swivel_homesettings_category_main_screen, HomeSettingsConstant.KEY_HOMESETTINGS_LAYOUT_CATEGORY);
        if (this.mSupportItems[20]) {
            ClearSwipeDownOnTheHomeScreen();
            makeSwipeDownOnSwivelHomeScreenPreference();
        }
        makePreferenceCategory(R.string.sp_swivel_homesettings_category_sub_screen, HomeSettingsConstant.KEY_HOMESETTINGS_LAYOUT_CATEGORY);
        makeMultiAppShorcutPreference();
        if (this.mSupportItems[23]) {
            ClearSwipeDownOnTheSubSwivelHomeScreen();
            makeSwipeDownOnSubSwivelHomeScreenPreference();
        }
        if (this.mSelectedLGHome && this.mSupportItems[6]) {
            makeScreenEffectPreference();
        }
        makeAppDrawerSwitchPreference();
        makePreferenceCategory(R.string.sp_swivel_homesettings_category_advanced, HomeSettingsConstant.KEY_HOMESETTINGS_LAYOUT_CATEGORY);
        if (this.mSupportItems[22]) {
            makeLockHomescreenEditSwitchPreference();
        }
        makePreferenceCategory(R.string.sp_hometip_tips_upper_case, HomeSettingsConstant.KEY_HOMESETTINGS_LAYOUT_CATEGORY);
        makeHelpPreference();
        makeEmptyPreferenceCategory(R.string.sp_hometip_tips_upper_case, HomeSettingsConstant.KEY_HOMESETTINGS_TIPS_CATEGORY);
    }

    private void makeMainWallpaperPreference() {
        Activity activity = getActivity();
        Preference preference = new Preference(activity);
        preference.setTitle(R.string.menu_wallpaper);
        preference.setKey(HomeSettingsConstant.KEY_HOMESETTINGS_WALLPAPER);
        preference.setLayoutResource(33751183);
        checkWallpaperDisallowed(preference, activity);
        this.mPreferenceList.add(preference);
    }

    private void makeMultiAppShorcutPreference() {
        Preference preference = new Preference(getActivity());
        preference.setTitle(R.string.dual_screen_app_pair_title);
        preference.setKey(HomeSettingsConstant.KEY_HOMESETTINGS_MULTI_APP_SHORTCUT);
        preference.setLayoutResource(33751184);
        this.mPreferenceList.add(preference);
    }

    private void makeSubWallpaperPreference() {
        Activity activity = getActivity();
        Preference preference = new Preference(activity);
        preference.setTitle(R.string.menu_wallpaper);
        preference.setKey(HomeSettingsConstant.KEY_HOMESETTINGS_WALLPAPER);
        if (this.mSelectedLGHome || this.mSupportItems[4]) {
            preference.setLayoutResource(33751184);
        } else {
            preference.setLayoutResource(33751183);
        }
        checkWallpaperDisallowed(preference, activity);
        this.mPreferenceList.add(preference);
    }

    private void checkWallpaperDisallowed(Preference pf, Context context) {
        if (Utilities.isWallapaperAllowed(context)) {
            return;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(pf.getTitle());
        spannableStringBuilder.setSpan(new ForegroundColorSpan(DDTUtils.gettextColorPrimaryType5FromTheme(context, context.getColor(R.color.disabled_text_color))), 0, spannableStringBuilder.length(), 33);
        pf.setTitle(spannableStringBuilder);
    }

    private void makeScreenEffectPreference() {
        Activity activity = getActivity();
        Preference preference = new Preference(activity);
        preference.setTitle(R.string.menu_screen_effect);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(ScreenEffectUtils.getSelectedSecondScreenEffectText(activity));
        spannableStringBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.selected_item_text_color)), 0, spannableStringBuilder.length(), 33);
        preference.setSummary(spannableStringBuilder);
        preference.setKey(HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT_SWIVEL_HOME);
        preference.setLayoutResource(33751187);
        this.mPreferenceList.add(preference);
    }

    private void makeContinuousLoopSwitchPreference() {
        final Activity activity = getActivity();
        SwitchPreference switchPreference = new SwitchPreference(activity);
        switchPreference.setPersistent(false);
        switchPreference.setTitle(R.string.loop_home_screen);
        switchPreference.setKey(HomeSettingsConstant.KEY_HOMESETTINGS_CONTINUOUS_LOOP);
        switchPreference.setLayoutResource(33751159);
        switchPreference.setChecked(HomeSettingsSharedPreferences.getContinuousLoopEnabled(activity));
        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.lge.launcher3.homesettings.SwivelHomescreenSettingsFragment.1
            @Override // android.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                boolean zBooleanValue = ((Boolean) obj).booleanValue();
                HomeSettingsSharedPreferences.setContinuousLoopEnabled(activity, zBooleanValue);
                SettingsSearchUtils.updateCheckValue(activity, HomeSettingsConstant.KEY_HOMESETTINGS_CONTINUOUS_LOOP, zBooleanValue);
                LoopNormalModeManager.getInstance(activity.getApplicationContext()).updateFeatureEnabled();
                LGUserLog.send(activity.getApplicationContext(), LGUserLog.FEATURENAME_ENABLECONTINUOUSLOOP, zBooleanValue ? 1 : 0);
                return true;
            }
        });
        if ((HomeSettingsSharedPreferences.getVZWSideScreenEnabled(getActivity()) && VZWSideScreenManager.isAppEnabled()) || (GoogleNowManager.isAvailable(getActivity()) && GoogleNowManager.isAppEnabled())) {
            switchPreference.setEnabled(false);
        } else {
            switchPreference.setEnabled(true);
        }
        this.mPreferenceList.add(switchPreference);
    }

    private void makeAppDrawerSwitchPreference() {
        final Activity activity = getActivity();
        SwitchPreference switchPreference = new SwitchPreference(activity);
        switchPreference.setPersistent(false);
        switchPreference.setTitle(R.string.app_drawer_icons_text);
        switchPreference.setKey(HomeSettingsConstant.KEY_HOMESETTINGS_APP_DRAWER_SWICH);
        switchPreference.setLayoutResource(33751159);
        switchPreference.setChecked(Settings.System.getInt(activity.getContentResolver(), "enable_second_screen_appdrawer_button", 1) == 1);
        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.lge.launcher3.homesettings.SwivelHomescreenSettingsFragment.2
            @Override // android.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                Settings.System.putInt(activity.getApplicationContext().getContentResolver(), "enable_second_screen_appdrawer_button", ((Boolean) obj).booleanValue() ? 1 : 0);
                return true;
            }
        });
        this.mPreferenceList.add(switchPreference);
    }

    private void makeLockHomescreenEditSwitchPreference() {
        final Activity activity = getActivity();
        SwitchPreference switchPreference = new SwitchPreference(activity);
        switchPreference.setPersistent(false);
        switchPreference.setTitle(R.string.home_screen_lock);
        switchPreference.setKey(HomeSettingsConstant.KEY_HOMESETTINGS_HOME_SCREEN_LOCK_SWIVEL_HOME);
        switchPreference.setSummary(R.string.home_screen_lock_description);
        switchPreference.setLayoutResource(33751183);
        switchPreference.setChecked(HomeSettingsSharedPreferences.getSwingHomeLockEnabled(activity));
        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.lge.launcher3.homesettings.SwivelHomescreenSettingsFragment.3
            @Override // android.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean zBooleanValue = ((Boolean) newValue).booleanValue();
                HomeSettingsSharedPreferences.setSwingHomeLockEnabled(activity, zBooleanValue);
                SettingsSearchUtils.updateCheckValue(activity, HomeSettingsConstant.KEY_HOMESETTINGS_HOME_SCREEN_LOCK_SWIVEL_HOME, zBooleanValue);
                return true;
            }
        });
        this.mPreferenceList.add(switchPreference);
    }

    private void makeSwipeDownOnSwivelHomeScreenPreference() {
        Activity activity = getActivity();
        Preference preference = new Preference(activity);
        preference.setTitle(R.string.menu_swipe_down_home);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(SwipeDownHomeDialog.getSwipeDownSwivelHomeText(activity));
        spannableStringBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.selected_item_text_color)), 0, spannableStringBuilder.length(), 33);
        preference.setSummary(spannableStringBuilder);
        preference.setKey(HomeSettingsConstant.KEY_HOMESETTINGS_SWIPE_DOWN_SWIVEL_HOME);
        preference.setLayoutResource(33751183);
        this.mPreferenceList.add(preference);
    }

    private void makeSwipeDownOnSubSwivelHomeScreenPreference() {
        Activity activity = getActivity();
        Preference preference = new Preference(activity);
        preference.setTitle(R.string.menu_swipe_down_sub_swivel_home);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(SwipeDownSubSwivelHomeDialog.getSwipeDownSubSwivelHomeText(activity));
        spannableStringBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.selected_item_text_color)), 0, spannableStringBuilder.length(), 33);
        preference.setSummary(spannableStringBuilder);
        preference.setKey(HomeSettingsConstant.KEY_HOMESETTINGS_SWIPE_DOWN_SUB_SWIVEL_SWIVEL_HOME);
        preference.setLayoutResource(33751187);
        this.mPreferenceList.add(preference);
    }

    private void makeHelpPreference() {
        Preference preference = new Preference(getActivity());
        preference.setTitle(R.string.menu_help);
        preference.setKey(HomeSettingsConstant.KEY_HOMESETTINGS_SWINGHOME_HELP);
        preference.setLayoutResource(33751183);
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            preference.setEnabled(true);
        } else {
            preference.setEnabled(false);
        }
        this.mPreferenceList.add(preference);
    }

    private void callSelectLauncher(boolean fromSearch) {
        Intent intent = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN, (Uri) null);
        intent.setClassName("com.lge.homeselector", "com.lge.homeselector.HomeSelector");
        intent.setFlags(276824064);
        LGUserLog.send(getPackageContext(), LGUserLog.FEATURENAME_SHOWHOMESELECTOR);
        startActivityAfterCheckFromSearch(intent, fromSearch);
    }

    private void callSelectDDTTheme(boolean fromSearch) {
        startActivityAfterCheckFromSearch(new Intent("com.lge.themesquare.action.VIEW_THEMES"), fromSearch);
    }

    private void switchSmartBulletinSetting(boolean bOn) {
        Activity activity = getActivity();
        if (bOn) {
            SBHomeDataBaseUtil.turnOnSmartBulletin(activity);
        } else {
            SBHomeDataBaseUtil.turnOffSmartBulletin(activity);
        }
        ((SwitchPreference) findPreference(HomeSettingsConstant.KEY_HOMESETTINGS_SMARTBULLETIN)).setChecked(bOn);
        SettingsSearchUtils.updateSmartBulletinOnOff(activity, bOn);
    }

    private void callSmartBulletinSetting(Preference preference, boolean fromSearch) {
        Intent intent = new Intent(IntentConst.Action.ACTION_SHOW_SBSETTING.getValue(getPackageContext()));
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setFlags(276824064);
        startActivityAfterCheckFromSearch(intent, fromSearch);
    }

    private void callQMemoplusPanelSetting(SwitchPreference preference) {
        if (preference.isChecked()) {
            SBHomeDataBaseUtil.turnOnQMemoPanel(getActivity());
        } else {
            SBHomeDataBaseUtil.turnOffQMemoPanel(getActivity());
        }
    }

    private void callQMemoplusPanelSetting(boolean checked) {
        SwitchPreference switchPreference = (SwitchPreference) findPreference(HomeSettingsConstant.KEY_HOMESETTINGS_QMEMOPLUS_PANEL);
        switchPreference.setChecked(checked);
        SettingsSearchUtils.updateCheckValue(getActivity(), HomeSettingsConstant.KEY_HOMESETTINGS_QMEMOPLUS_PANEL, checked);
        callQMemoplusPanelSetting(switchPreference);
    }

    public void callSelectWallpaper(boolean fromSearch) {
        Activity activity = getActivity();
        if (!Utilities.isWallapaperAllowed(activity)) {
            sendShowAdminSupportDetailsIntent(activity);
        } else {
            startActivityAfterCheckFromSearch(Utilities.isLGUI7_0() ? DDTUtils.getThemeIntent() : new Intent(IntentConst.Action.ACTION_SHOW_WALLPAPER_LIST_ACTIVITY.getValue(getPackageContext())), fromSearch);
        }
    }

    private void callTWallpaper() {
        Activity activity = getActivity();
        if (!Utilities.isWallapaperAllowed(activity)) {
            sendShowAdminSupportDetailsIntent(activity);
            return;
        }
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(getActivity().getString(R.string.config_t_wallpaper_uri)));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            LGLog.e(TAG, String.format("ActivityNotFoundException(%s)", e.getMessage()));
        } catch (Resources.NotFoundException e2) {
            LGLog.e(TAG, String.format("NotFoundException(%s)", e2.getMessage()));
        }
    }

    public void callMultiAppShorcut(boolean fromSearch) {
        startActivityAfterCheckFromSearch(new Intent("com.lge.intent.action.MULTI_APP_SHORTCUT"), fromSearch);
    }

    private void sendShowAdminSupportDetailsIntent(Context context) {
        try {
            Intent intent = new Intent("android.settings.SHOW_ADMIN_SUPPORT_DETAILS");
            ComponentName deviceOwnerComponentOnAnyUser = ((DevicePolicyManager) context.getSystemService("device_policy")).getDeviceOwnerComponentOnAnyUser();
            int iMyUserId = UserHandle.myUserId();
            intent.putExtra("android.app.extra.DEVICE_ADMIN", deviceOwnerComponentOnAnyUser);
            intent.putExtra("android.intent.extra.USER_ID", iMyUserId);
            context.startActivityAsUser(intent, new UserHandle(UserHandle.myUserId()));
        } catch (ActivityNotFoundException e) {
            LGLog.e(TAG, String.format("ActivityNotFoundException(%s)", e.getMessage()));
        } catch (Resources.NotFoundException e2) {
            LGLog.e(TAG, String.format("NotFoundException(%s)", e2.getMessage()));
        }
    }

    private void callSelectScreenEffect() {
        final Activity activity = getActivity();
        final Context packageContext = getPackageContext();
        LGUserLog.send(activity, LGUserLog.FEATURENAME_SHOWEFFECT);
        final int selectedScreenEffect = HomeSettingsSharedPreferences.getSelectedScreenEffect(packageContext);
        ScreenEffectSelectionDialog.show(activity, packageContext, new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.homesettings.SwivelHomescreenSettingsFragment.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                if (selectedScreenEffect != HomeSettingsSharedPreferences.getSelectedSecondScreenEffect(packageContext)) {
                    SwivelHomescreenSettingsFragment.this.refreshPrefInfo();
                    ScreenEffectManager.getInstance(activity.getApplicationContext()).updateSelectedScreenEffectType();
                }
            }
        });
    }

    private void callScreenEffectPrefActivity(boolean fromSearch) {
        try {
            LGUserLog.send(getActivity(), LGUserLog.FEATURENAME_SHOWEFFECT);
            Intent intent = new Intent(IntentConst.Action.ACTION_SHOW_SECOND_SCREEN_EFFECT_SETTINGS.getValue(getPackageContext()));
            if (fromSearch) {
                intent.putExtra(EXTRA_FROM_SETTING_SEARCH, this.mNeedHighlight);
                intent.putExtra(EXTRA_SEARCH_ITEM, getActivity().getIntent().getStringExtra(EXTRA_SEARCH_ITEM));
            }
            startActivityAfterCheckFromSearch(intent, fromSearch);
        } catch (ActivityNotFoundException e) {
            LGLog.e(TAG, String.format("ActivityNotFoundException(%s)", e.getMessage()));
        }
    }

    private void callSwipeDownOnTheHomeScreen() {
        LGLog.i(TAG, "callSwipeDownOnTheHomeScreen ");
        Activity activity = getActivity();
        final Context packageContext = getPackageContext();
        LGUserLog.send(activity, LGUserLog.FEATURENAME_SWIPEDOWNHOME);
        final int swipeDownSwivelHome = HomeSettingsSharedPreferences.getSwipeDownSwivelHome(packageContext);
        SwipeDownHomeDialog.showSelectSwipeDownSwivelHomeDialog(getActivity(), new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.homesettings.SwivelHomescreenSettingsFragment.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                if (swipeDownSwivelHome != HomeSettingsSharedPreferences.getSwipeDownSwivelHome(packageContext)) {
                    SwivelHomescreenSettingsFragment.this.refreshPrefInfo();
                }
            }
        });
    }

    private void callSwipeDownOnTheSubSwivelHomeScreen() {
        LGLog.i(TAG, "callSwipeDownOnTheSubSwivelHomeScreen ");
        Activity activity = getActivity();
        final Context packageContext = getPackageContext();
        LGUserLog.send(activity, LGUserLog.FEATURENAME_SWIPEDOWN_SUBSWIVELHOME);
        final String swipeDownSubSwivelHome = HomeSettingsSharedPreferences.getSwipeDownSubSwivelHome(packageContext);
        SwipeDownSubSwivelHomeDialog.showSelectSwipeDownSubSwivelHomeDialog(getActivity(), new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.homesettings.SwivelHomescreenSettingsFragment.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                if (swipeDownSubSwivelHome.equals(HomeSettingsSharedPreferences.getSwipeDownSubSwivelHome(packageContext))) {
                    return;
                }
                SwivelHomescreenSettingsFragment.this.refreshPrefInfo();
            }
        });
    }

    private void callSwipeUpOnTheHomeScreen() {
        LGLog.i(TAG, "callSwipeUpOnTheHomeScreen ");
        Activity activity = getActivity();
        final Context packageContext = getPackageContext();
        LGUserLog.send(activity, LGUserLog.FEATURENAME_SWIPEUPHOME);
        final int swipeUpHome = HomeSettingsSharedPreferences.getSwipeUpHome(packageContext);
        SwipeUpHomeDialog.showSelectSwipeUpHomeDialog(getActivity(), new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.homesettings.SwivelHomescreenSettingsFragment.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                if (swipeUpHome != HomeSettingsSharedPreferences.getSwipeUpHome(packageContext)) {
                    SwivelHomescreenSettingsFragment.this.refreshPrefInfo();
                }
            }
        });
    }

    private void callSelectDynamicGrid() {
        final Activity activity = getActivity();
        LGUserLog.send(activity, LGUserLog.FEATURENAME_SHOWGRID);
        final DynamicGridManager dynamicGridManager = new DynamicGridManager(activity);
        dynamicGridManager.init();
        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, 33751059, android.R.id.text1, dynamicGridManager.getPresetArray());
        this.mSelectedDynamicGrid = dynamicGridManager.getSelectedGridIndex();
        final int selectedGridIndex = dynamicGridManager.getSelectedGridIndex();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View viewInflate = getActivity().getLayoutInflater().inflate(R.layout.dialog_title_secondlines, (ViewGroup) null);
        builder.setCustomTitle(viewInflate);
        ((TextView) viewInflate.findViewById(R.id.title)).setText(R.string.dynamic_gird_label);
        ((TextView) viewInflate.findViewById(R.id.subtitle)).setText(R.string.dynamic_grid_popup_help);
        builder.setSingleChoiceItems(arrayAdapter, this.mSelectedDynamicGrid, new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.homesettings.SwivelHomescreenSettingsFragment.8
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                SwivelHomescreenSettingsFragment.this.mSelectedDynamicGrid = which;
            }
        });
        builder.setPositiveButton(R.string.rename_action, new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.homesettings.SwivelHomescreenSettingsFragment.9
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                if (selectedGridIndex != SwivelHomescreenSettingsFragment.this.mSelectedDynamicGrid) {
                    dynamicGridManager.runDynamicGrid(SwivelHomescreenSettingsFragment.this.mSelectedDynamicGrid);
                    SwivelHomescreenSettingsFragment.this.refreshPrefInfo();
                    new LoadingProgressDialogAsyncTask(activity).show(1000);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel_action, (DialogInterface.OnClickListener) null);
        builder.create().show();
    }

    private void callSelectSortAppsBy() {
        Activity activity = getActivity();
        LGUserLog.send(activity, LGUserLog.FEATURENAME_SHOWSORTAPPSBY);
        SortAppsByDialog.showSelectSortTypeDialog(activity);
    }

    /* JADX INFO: renamed from: com.lge.launcher3.homesettings.SwivelHomescreenSettingsFragment$10, reason: invalid class name */
    class AnonymousClass10 implements DialogInterface.OnClickListener {
        final /* synthetic */ int val$checkedItem;
        final /* synthetic */ Context val$context;

        AnonymousClass10(final int val$checkedItem, final Context val$context) {
            this.val$checkedItem = val$checkedItem;
            this.val$context = val$context;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            if (this.val$checkedItem != which) {
                HomeSettingsSharedPreferences.putSelectedSortAppsBy(this.val$context, which);
            }
        }
    }

    private void callSelectHelp(boolean fromeSearch) {
        startActivityAfterCheckFromSearch(new Intent("com.lge.launcher3.intent.action.SHOW_HELP"), fromeSearch);
    }

    private void callSwingHomeInitialGuide(boolean fromSearch) {
        SwivelHomeGuideManager.getInstance(getActivity().getApplicationContext()).showGuideFromSettings(getActivity(), getView().getDisplay().getDisplayId());
    }

    private void callIconFramesPrefActivity(boolean fromSearch) {
        try {
            LGUserLog.send(getActivity(), LGUserLog.FEATURENAME_SHOWICONSHAPE);
            Intent intent = new Intent(IntentConst.Action.ACTION_SHOW_ICON_FRAMES_SETTINGS.getValue(getPackageContext()));
            if (fromSearch) {
                intent.putExtra(EXTRA_FROM_SETTING_SEARCH, this.mNeedHighlight);
                intent.putExtra(EXTRA_SEARCH_ITEM, getActivity().getIntent().getStringExtra(EXTRA_SEARCH_ITEM));
                startActivityForResult(intent, 5);
            } else {
                startActivityForResult(intent, 4);
            }
        } catch (ActivityNotFoundException e) {
            LGLog.e(TAG, String.format("ActivityNotFoundException(%s)", e.getMessage()));
        }
    }

    private void callWallpaperMotionPrefActivity(boolean fromSearch) {
        boolean zIsPowerSaveMode = Utilities.isPowerSaveMode(getContext());
        boolean zIsFixedWallpaper = WallpaperMotionUtils.isFixedWallpaper(getContext());
        if (zIsPowerSaveMode || !zIsFixedWallpaper) {
            LGLog.i(TAG, "can't call wallpaper motion, isPowerSaveMode = " + zIsPowerSaveMode + ", isFixedWallpaper = " + zIsFixedWallpaper);
            return;
        }
        Intent intent = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN);
        intent.setComponent(new ComponentName(LGHomeFeature.WALLPAPERPICKER_PACKAGE, "com.lge.wallpaperpicker.MotionSettingActivity"));
        startActivityAfterCheckFromSearch(intent, fromSearch);
    }

    private void callAdditionalPrefActivity(boolean fromSearch) {
        LGUserLog.send(getActivity(), LGUserLog.FEATURENAME_SHOWADDITIONALSCREEN);
        Intent intent = new Intent(IntentConst.Action.ACTION_SHOW_ADDITIONAL_SCREEN.getValue(getPackageContext()));
        if (fromSearch) {
            intent.putExtra(EXTRA_FROM_SETTING_SEARCH, this.mNeedHighlight);
            intent.putExtra(EXTRA_SEARCH_ITEM, getActivity().getIntent().getStringExtra(EXTRA_SEARCH_ITEM));
        }
        startActivityAfterCheckFromSearch(intent, fromSearch);
    }

    private void startActivityAfterCheckFromSearch(Intent intent, boolean fromSearch) {
        try {
            if (fromSearch) {
                startActivityForResult(intent, 3);
            } else {
                startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            LGLog.e(TAG, String.format("ActivityNotFoundException(%s)", e.getMessage()));
        }
    }

    private void onCheckFromSettingsSearch(Intent intent) {
        String stringExtra = intent.getStringExtra(EXTRA_SEARCH_ITEM);
        if (stringExtra == null || stringExtra.equals("")) {
            return;
        }
        if (this.mNeedHighlight) {
            SearchUtil.highlightPreference(getContext(), stringExtra, getListView(), getPreferenceScreen());
        }
        intent.removeExtra(EXTRA_SEARCH_ITEM);
    }

    private void onCreateFromSettingsSearch(String key) {
        Intent intent = getActivity().getIntent();
        boolean booleanExtra = intent.getBooleanExtra(EXTRA_PERFORM, false);
        boolean booleanExtra2 = intent.getBooleanExtra(EXTRA_NEW_VALUE, false);
        if (HomeSettingsConstant.KEY_HOMESETTINGS_SELECT_LAUNCHER.equals(key)) {
            callSelectLauncher(true);
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_WALLPAPER.equals(key)) {
            callSelectWallpaper(true);
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_DDT_THEME.equals(key)) {
            callSelectDDTTheme(true);
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_SMARTBULLETIN.equals(key) && this.mSelectedLGHome) {
            SBSwitchPreference sBSwitchPreference = (SBSwitchPreference) findPreference(HomeSettingsConstant.KEY_HOMESETTINGS_SMARTBULLETIN);
            if (booleanExtra) {
                switchSmartBulletinSetting(booleanExtra2);
                return;
            } else {
                callSmartBulletinSetting(sBSwitchPreference, true);
                return;
            }
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_QMEMOPLUS_PANEL.equals(key) && this.mSelectedLGHome) {
            this.mNeedHighlight = true;
            if (booleanExtra) {
                callQMemoplusPanelSetting(booleanExtra2);
                return;
            }
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT_SWIVEL_HOME.equals(key) && this.mSelectedLGHome) {
            callScreenEffectPrefActivity(true);
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_DYNAMIC_GRID.equals(key) && this.mSelectedLGHome) {
            this.mNeedHighlight = true;
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_SORT_APPS_BY.equals(key) && this.mSelectedLGHome) {
            this.mNeedHighlight = true;
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_HELP.equals(key) && this.mSelectedLGHome) {
            callSelectHelp(true);
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_SWINGHOME_HELP.equals(key) && this.mSelectedLGHome) {
            this.mNeedHighlight = true;
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_APP_DRAWER_SWICH.equals(key) && this.mSelectedLGHome) {
            this.mNeedHighlight = true;
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_HIDE_APPS.equals(key) && this.mSelectedLGHome) {
            callHideAppsSettingActivity(true);
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_GOOGLE_NOW.equals(key) && this.mSelectedLGHome) {
            this.mNeedHighlight = true;
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_CONTINUOUS_LOOP.equals(key) && this.mSelectedLGHome) {
            this.mNeedHighlight = true;
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_VZW_APPDRAWER_LOOP.equals(key) && this.mSelectedLGHome) {
            this.mNeedHighlight = true;
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_GOOGLE_INAPPS.equals(key) && this.mSelectedLGHome) {
            this.mNeedHighlight = true;
            return;
        }
        if ((HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAME_TYPE_ORIGINAL.equals(key) || HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAME_TYPE_ROUNDED_SQUARE.equals(key) || HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAME_TYPE_ROUND.equals(key) || HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAME_TYPE_CYLINDER.equals(key)) && (this.mSelectedLGHome || this.mSelectedEasyHome)) {
            this.mNeedHighlight = true;
            callIconFramesPrefActivity(true);
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_WALLPAPER_MOTION.equals(key) && this.mSelectedLGHome) {
            callWallpaperMotionPrefActivity(true);
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_VZW_SIDESCREEN.equals(key) && this.mSelectedLGHome) {
            this.mNeedHighlight = true;
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_LEFT_HOME_SCREEN.equals(key) && this.mSelectedLGHome) {
            callAdditionalPrefActivity(true);
            return;
        }
        if ((HomeSettingsConstant.KEY_HOMESETTINGS_LEFT_HOME_SCREEN_APPFLASH.equals(key) || HomeSettingsConstant.KEY_HOMESETTINGS_LEFT_HOME_SCREEN_GOOGLE_FEED.equals(key) || HomeSettingsConstant.KEY_HOMESETTINGS_LEFT_HOME_SCREEN_NONE.equals(key) || HomeSettingsConstant.KEY_HOMESETTINGS_LEFT_HOME_SCREEN_SMARTBULLETIN.equals(key)) && this.mSelectedLGHome) {
            this.mNeedHighlight = true;
            callAdditionalPrefActivity(true);
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_SWIPE_DOWN_SWIVEL_HOME.equals(key) && this.mSelectedLGHome) {
            this.mNeedHighlight = true;
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_SWIPE_DOWN_SUB_SWIVEL_SWIVEL_HOME.equals(key) && this.mSelectedLGHome) {
            this.mNeedHighlight = true;
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_MULTI_APP_SHORTCUT.equals(key) && this.mSelectedLGHome) {
            this.mNeedHighlight = true;
            return;
        }
        if (HomeSettingsConstant.KEY_HOMESETTINGS_SWIPE_UP_HOME.equals(key) && this.mSelectedLGHome) {
            this.mNeedHighlight = true;
        } else if (HomeSettingsConstant.KEY_HOMESETTINGS_HOME_SCREEN_LOCK_SWIVEL_HOME.equals(key) && this.mSelectedLGHome) {
            this.mNeedHighlight = true;
        }
    }

    private void callHideAppsSettingActivity(boolean fromSearch) {
        LGUserLog.send(getActivity(), LGUserLog.FEATURENAME_SHOWHIDEAPPS);
        startActivityAfterCheckFromSearch(new Intent(getPackageContext(), (Class<?>) HideAppsSettingActivity.class), fromSearch);
    }

    public void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        if (requestCode == 2) {
            if (resultCode == -1) {
                Toast.makeText(getActivity(), R.string.sp_wallpaper_changed_NORMAL, 0).show();
            }
        } else if (requestCode == 3) {
            getActivity().finish();
        } else if ((requestCode == 4 || requestCode == 5) && requestCode == 5) {
            getActivity().finish();
        }
    }

    private void ClearSwipeDownOnTheHomeScreen() {
        SwipeDownHomeDialog.clearDialog();
    }

    private void ClearSwipeDownOnTheSubSwivelHomeScreen() {
        SwipeDownSubSwivelHomeDialog.clearDialog();
    }
}
