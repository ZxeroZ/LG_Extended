package com.android.launcher3.config;

import android.content.Context;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.uioverrides.DeviceFlag;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class FeatureFlags extends BaseFlags {
    public static final boolean ADAPTIVE_ICON_SHADOW = false;
    public static final boolean BADGE_ICONS = true;
    public static final boolean DISCOVERY_ENABLED = true;
    public static final String FLAGS_PREF_NAME = "featureFlags";
    public static final boolean IS_DOGFOOD_BUILD = false;
    public static final boolean IS_STUDIO_BUILD = false;
    public static final boolean LAUNCHER3_SPRING_ICONS = true;
    public static final boolean LEGACY_ICON_TREATMENT = true;
    public static final boolean LIGHT_STATUS_BAR = false;
    public static final boolean NO_ALL_APPS_ICON = true;
    public static final boolean OVERVIEW_USE_SCREENSHOT_ORIENTATION = true;
    public static final boolean PULLDOWN_SEARCH = false;
    public static final boolean QSB_ON_FIRST_SCREEN = true;
    private static final List<DebugFlag> sDebugFlags = new ArrayList();
    public static boolean LAUNCHER3_DISABLE_ICON_NORMALIZATION = true;
    public static boolean LAUNCHER3_LEGACY_WORKSPACE_DND = false;
    public static boolean LAUNCHER3_LEGACY_FOLDER_ICON = false;
    public static boolean LAUNCHER3_USE_SYSTEM_DRAG_DRIVER = true;
    public static boolean LAUNCHER3_DISABLE_PINCH_TO_OVERVIEW = false;
    public static boolean LAUNCHER3_ALL_APPS_PULL_UP = true;
    public static boolean LAUNCHER3_NEW_FOLDER_ANIMATION = false;
    public static boolean LAUNCHER3_DIRECT_SCROLL = true;
    public static boolean LAUNCHER3_UPDATE_SOFT_INPUT_MODE = false;
    public static final BooleanFlag PROMISE_APPS_IN_ALL_APPS = getDebugFlag("PROMISE_APPS_IN_ALL_APPS", false, "Add promise icon in all-apps");
    public static final BooleanFlag PROMISE_APPS_NEW_INSTALLS = getDebugFlag("PROMISE_APPS_NEW_INSTALLS", true, "Adds a promise icon to the home screen for new install sessions.");
    public static final BooleanFlag APPLY_CONFIG_AT_RUNTIME = getDebugFlag("APPLY_CONFIG_AT_RUNTIME", true, "Apply display changes dynamically");
    public static final BooleanFlag QUICKSTEP_SPRINGS = getDebugFlag("QUICKSTEP_SPRINGS", true, "Enable springs for quickstep animations");
    public static final BooleanFlag UNSTABLE_SPRINGS = getDebugFlag("UNSTABLE_SPRINGS", false, "Enable unstable springs for quickstep animations");
    public static final BooleanFlag KEYGUARD_ANIMATION = getDebugFlag("KEYGUARD_ANIMATION", false, "Enable animation for keyguard going away on wallpaper");
    public static final BooleanFlag ADAPTIVE_ICON_WINDOW_ANIM = getDebugFlag("ADAPTIVE_ICON_WINDOW_ANIM", true, "Use adaptive icons for window animations.");
    public static final BooleanFlag ENABLE_QUICKSTEP_LIVE_TILE = getDebugFlag("ENABLE_QUICKSTEP_LIVE_TILE", false, "Enable live tile in Quickstep overview");
    public static final BooleanFlag ENABLE_SUGGESTED_ACTIONS_OVERVIEW = new DeviceFlag("ENABLE_SUGGESTED_ACTIONS_OVERVIEW", false, "Show chip hints on the overview screen");
    public static final BooleanFlag FOLDER_NAME_SUGGEST = new DeviceFlag("FOLDER_NAME_SUGGEST", true, "Suggests folder names instead of blank text.");
    public static final BooleanFlag FOLDER_NAME_MAJORITY_RANKING = getDebugFlag("FOLDER_NAME_MAJORITY_RANKING", true, "Suggests folder names based on majority based ranking.");
    public static final BooleanFlag APP_SEARCH_IMPROVEMENTS = new DeviceFlag("APP_SEARCH_IMPROVEMENTS", true, "Adds localized title and keyword search and ranking");
    public static final BooleanFlag ENABLE_PREDICTION_DISMISS = getDebugFlag("ENABLE_PREDICTION_DISMISS", true, "Allow option to dimiss apps from predicted list");
    public static final BooleanFlag ENABLE_QUICK_CAPTURE_GESTURE = getDebugFlag("ENABLE_QUICK_CAPTURE_GESTURE", true, "Swipe from right to left to quick capture");
    public static final BooleanFlag ENABLE_QUICK_CAPTURE_WINDOW = getDebugFlag("ENABLE_QUICK_CAPTURE_WINDOW", false, "Use window to host quick capture");
    public static final BooleanFlag FORCE_LOCAL_OVERSCROLL_PLUGIN = getDebugFlag("FORCE_LOCAL_OVERSCROLL_PLUGIN", false, "Use a launcher-provided OverscrollPlugin if available");
    public static final BooleanFlag ASSISTANT_GIVES_LAUNCHER_FOCUS = getDebugFlag("ASSISTANT_GIVES_LAUNCHER_FOCUS", false, "Allow Launcher to handle nav bar gestures while Assistant is running over it");
    public static final BooleanFlag ENABLE_HYBRID_HOTSEAT = getDebugFlag("ENABLE_HYBRID_HOTSEAT", true, "Fill gaps in hotseat with predicted apps");
    public static final BooleanFlag HOTSEAT_MIGRATE_TO_FOLDER = getDebugFlag("HOTSEAT_MIGRATE_TO_FOLDER", false, "Should move hotseat items into a folder");
    public static final BooleanFlag ENABLE_DEEP_SHORTCUT_ICON_CACHE = getDebugFlag("ENABLE_DEEP_SHORTCUT_ICON_CACHE", true, "R/W deep shortcut in IconCache");
    public static final BooleanFlag MULTI_DB_GRID_MIRATION_ALGO = getDebugFlag("MULTI_DB_GRID_MIRATION_ALGO", true, "Use the multi-db grid migration algorithm");
    public static final BooleanFlag ENABLE_LAUNCHER_PREVIEW_IN_GRID_PICKER = getDebugFlag("ENABLE_LAUNCHER_PREVIEW_IN_GRID_PICKER", true, "Show launcher preview in grid picker");
    public static final BooleanFlag ENABLE_OVERVIEW_ACTIONS = getDebugFlag("ENABLE_OVERVIEW_ACTIONS", true, "Show app actions instead of the shelf in Overview. As part of this decoupling, also distinguish swipe up from nav bar vs above it.");
    public static final BooleanFlag ENABLE_OVERVIEW_SELECTIONS = new DeviceFlag("ENABLE_OVERVIEW_SELECTIONS", true, "Show Select Mode button in Overview Actions");
    public static final BooleanFlag ENABLE_OVERVIEW_SHARE = getDebugFlag("ENABLE_OVERVIEW_SHARE", false, "Show Share button in Overview Actions");
    public static final BooleanFlag ENABLE_DATABASE_RESTORE = getDebugFlag("ENABLE_DATABASE_RESTORE", true, "Enable database restore when new restore session is created");
    public static final BooleanFlag ENABLE_UNIVERSAL_SMARTSPACE = getDebugFlag("ENABLE_UNIVERSAL_SMARTSPACE", false, "Replace Smartspace with a version rendered by System UI.");
    public static final BooleanFlag ENABLE_LSQ_VELOCITY_PROVIDER = getDebugFlag("ENABLE_LSQ_VELOCITY_PROVIDER", true, "Use Least Square algorithm for motion pause detection.");
    public static final BooleanFlag ALWAYS_USE_HARDWARE_OPTIMIZATION_FOR_FOLDER_ANIMATIONS = getDebugFlag("ALWAYS_USE_HARDWARE_OPTIMIZATION_FOR_FOLDER_ANIMATIONS", false, "Always use hardware optimization for folder animations.");
    public static final BooleanFlag ENABLE_ALL_APPS_EDU = getDebugFlag("ENABLE_ALL_APPS_EDU", false, "Shows user a tutorial on how to get to All Apps after X amount of attempts.");
    public static final BooleanFlag SEPARATE_RECENTS_ACTIVITY = getDebugFlag("SEPARATE_RECENTS_ACTIVITY", false, "Uses a separate recents activity instead of using the integrated recents+Launcher UI");
    public static final BooleanFlag USER_EVENT_DISPATCHER = new DeviceFlag("USER_EVENT_DISPATCHER", true, "User event dispatcher collects logs.");
    public static final BooleanFlag ENABLE_SPLIT_SELECT = getDebugFlag("ENABLE_SPLIT_SELECT", true, "Uses new split screen selection overview UI");

    private FeatureFlags() {
    }

    public static boolean showFlagTogglerUi(Context context) {
        return Utilities.IS_DEBUG_DEVICE && Utilities.isDevelopersOptionsEnabled(context);
    }

    public static void initialize(Context context) {
        List<DebugFlag> list = sDebugFlags;
        synchronized (list) {
            Iterator<DebugFlag> it = list.iterator();
            while (it.hasNext()) {
                it.next().initialize(context);
            }
            sDebugFlags.sort(new Comparator() { // from class: com.android.launcher3.config.-$$Lambda$FeatureFlags$6bcXSl1sS2HEXuuZllPO0H4vKew
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return ((FeatureFlags.DebugFlag) obj).key.compareToIgnoreCase(((FeatureFlags.DebugFlag) obj2).key);
                }
            });
        }
    }

    static List<DebugFlag> getDebugFlags() {
        ArrayList arrayList;
        List<DebugFlag> list = sDebugFlags;
        synchronized (list) {
            arrayList = new ArrayList(list);
        }
        return arrayList;
    }

    public static void dump(PrintWriter pw) {
        pw.println("DeviceFlags:");
        List<DebugFlag> list = sDebugFlags;
        synchronized (list) {
            for (DebugFlag debugFlag : list) {
                if (debugFlag instanceof DeviceFlag) {
                    pw.println("  " + debugFlag.toString());
                }
            }
        }
        pw.println("DebugFlags:");
        List<DebugFlag> list2 = sDebugFlags;
        synchronized (list2) {
            for (DebugFlag debugFlag2 : list2) {
                if (!(debugFlag2 instanceof DeviceFlag)) {
                    pw.println("  " + debugFlag2.toString());
                }
            }
        }
    }

    public static class BooleanFlag {
        public boolean defaultValue;
        public final String key;

        public void addChangeListener(Context context, Runnable r) {
        }

        public BooleanFlag(String key, boolean defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public boolean get() {
            return this.defaultValue;
        }

        public String toString() {
            return appendProps(new StringBuilder()).toString();
        }

        protected StringBuilder appendProps(StringBuilder src) {
            return src.append(this.key).append(", defaultValue=").append(this.defaultValue);
        }
    }

    public static class DebugFlag extends BooleanFlag {
        public final String description;
        private boolean mCurrentValue;

        public DebugFlag(String key, boolean defaultValue, String description) {
            super(key, defaultValue);
            this.description = description;
            this.mCurrentValue = this.defaultValue;
            synchronized (FeatureFlags.sDebugFlags) {
                FeatureFlags.sDebugFlags.add(this);
            }
        }

        @Override // com.android.launcher3.config.FeatureFlags.BooleanFlag
        public boolean get() {
            return this.mCurrentValue;
        }

        public void initialize(Context context) {
            this.mCurrentValue = context.getSharedPreferences(FeatureFlags.FLAGS_PREF_NAME, 0).getBoolean(this.key, this.defaultValue);
        }

        @Override // com.android.launcher3.config.FeatureFlags.BooleanFlag
        protected StringBuilder appendProps(StringBuilder src) {
            return super.appendProps(src).append(", mCurrentValue=").append(this.mCurrentValue);
        }
    }

    private static BooleanFlag getDebugFlag(String key, boolean defaultValue, String description) {
        if (Utilities.IS_DEBUG_DEVICE) {
            return new DebugFlag(key, defaultValue, description);
        }
        return new BooleanFlag(key, defaultValue);
    }
}
