package com.lge.launcher3.sharedpreferences;

/* JADX INFO: loaded from: classes.dex */
public class SharedPreferencesConst {
    public static final String PREFERENCES_FILE_NAME = "com.lge.launcher3.prefs";

    public enum ABBASearchKey implements PreferenceKey {
        IS_ENABLED
    }

    public enum AdaptiveTextKey implements PreferenceKey {
        TEXT_COLOR,
        STATUS_BAR_COLOR,
        SWIVEL_WEATHER_COLOR
    }

    public enum AppBoxBootInstallKey implements PreferenceKey {
        RECEIVED,
        SUCCESS
    }

    public enum BackupRestoreKey implements PreferenceKey {
        RESTORED,
        ISRESTORING
    }

    public enum DDTKey implements PreferenceKey {
        CONFIG_THEME_PACKAGE_NAME
    }

    public enum DoNotShowAgainPopUpkey implements PreferenceKey {
        IS_DISABLED
    }

    public enum DynamicGridKey implements PreferenceKey {
        CURRENT_WORKSAPACE_ROWS,
        CURRENT_WORKSAPACE_COLUMNS
    }

    public enum GoogleInAppsKey implements PreferenceKey {
        IS_ENABLED
    }

    public enum GoogleNowKey implements PreferenceKey {
        IS_ENABLED
    }

    public enum HomescreeenLockKey implements PreferenceKey {
        IS_ENABLED
    }

    public enum InitialGuideKey implements PreferenceKey {
        ALREADY_SHOWN,
        FIRST_SHOWN_TIME
    }

    public enum MultiWindowGuideKey implements PreferenceKey {
        ALREADY_SHOWN,
        FIRST_SHOWN_TIME
    }

    public enum NEED_TO_GO_WORK_TAB implements PreferenceKey {
        GO_WORK_TAB
    }

    public enum OverviewGuideKey implements PreferenceKey {
        IS_ENABLED,
        COUNT
    }

    public interface PreferenceKey {
    }

    public enum QMemoWidgetKey implements PreferenceKey {
        WIDGETID
    }

    public enum RecentViewGuideKey implements PreferenceKey {
        ALREADY_SHOWN
    }

    public enum ScreenZoomKey implements PreferenceKey {
        DENSITY
    }

    public enum SecondrayLauncherKey implements PreferenceKey {
        ALREADY_SHOWN
    }

    public enum SwingHomescreenLockKey implements PreferenceKey {
        IS_ENABLED
    }

    public enum SwipeUpKey implements PreferenceKey {
        IS_ENABLED,
        SWIPE_UP_COUNT
    }

    public enum SwivelHomeGuideFromSettingsKey implements PreferenceKey {
        ALREADY_SHOWN_FROM_SETTINGS
    }

    public enum SwivelHomeGuideKey implements PreferenceKey {
        ALREADY_SHOWN,
        FIRST_SHOWN_TIME
    }

    public enum TPhoneMode implements PreferenceKey {
        T_PHONE_MODE,
        T_PHONE_MODE_APPDRAWER,
        T_PHONE_MODE_EASYHOME
    }

    public enum ThemedIcon implements PreferenceKey {
        IS_ENABLED
    }

    public enum VZWSideScreen implements PreferenceKey {
        IS_ENABLED
    }

    public enum WorkGuideViewKey implements PreferenceKey {
        PERSONAL_ALREADY_SHOWN,
        WORK_ALREADY_SHOW
    }

    public enum WorkspaceCAKey implements PreferenceKey {
        ISLOADING
    }
}
