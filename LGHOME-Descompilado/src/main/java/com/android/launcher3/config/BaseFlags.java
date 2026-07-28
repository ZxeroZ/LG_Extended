package com.android.launcher3.config;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.core.util.Preconditions;
import com.android.launcher3.Utilities;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseFlags {
    public static final boolean ALL_APPS_TABS_ENABLED = true;
    public static final boolean ENABLE_CUSTOM_WIDGETS = false;
    static final String FLAGS_PREF_NAME = "featureFlags";
    public static final boolean GO_DISABLE_WIDGETS = false;
    public static final boolean IS_DOGFOOD_BUILD = false;
    public static final boolean LAUNCHER3_PROMISE_APPS_IN_ALL_APPS = false;
    public static final boolean OVERVIEW_USE_SCREENSHOT_ORIENTATION = true;
    public static final boolean PULL_DOWN_STATUS_BAR = false;
    public static final boolean QSB_ON_FIRST_SCREEN = true;
    private static final Object sLock = new Object();
    private static final List<TogglableFlag> sFlags = new ArrayList();
    public static final TogglableFlag EXAMPLE_FLAG = new TogglableFlag("EXAMPLE_FLAG", true, "An example flag that doesn't do anything. Useful for testing");
    public static final TogglableFlag APPLY_CONFIG_AT_RUNTIME = new TogglableFlag("APPLY_CONFIG_AT_RUNTIME", true, "Apply display changes dynamically");
    public static final TogglableFlag QUICKSTEP_SPRINGS = new TogglableFlag("QUICKSTEP_SPRINGS", false, "Enable springs for quickstep animations");
    public static final TogglableFlag ADAPTIVE_ICON_WINDOW_ANIM = new TogglableFlag("ADAPTIVE_ICON_WINDOW_ANIM", true, "Use adaptive icons for window animations.");
    public static final TogglableFlag ENABLE_QUICKSTEP_LIVE_TILE = new TogglableFlag("ENABLE_QUICKSTEP_LIVE_TILE", false, "Enable live tile in Quickstep overview");
    public static final TogglableFlag ENABLE_HINTS_IN_OVERVIEW = new TogglableFlag("ENABLE_HINTS_IN_OVERVIEW", false, "Show chip hints and gleams on the overview screen");

    BaseFlags() {
        throw new UnsupportedOperationException("Don't instantiate BaseFlags");
    }

    public static boolean showFlagTogglerUi(Context context) {
        return Utilities.IS_DEBUG_DEVICE && Settings.Global.getInt(context.getApplicationContext().getContentResolver(), "development_settings_enabled", 0) != 0;
    }

    public static void initialize(Context context) {
        if (Utilities.IS_DEBUG_DEVICE) {
            synchronized (sLock) {
                Iterator<TogglableFlag> it = sFlags.iterator();
                while (it.hasNext()) {
                    it.next().initialize(context);
                }
            }
        }
    }

    static List<TogglableFlag> getTogglableFlags() {
        TreeMap treeMap = new TreeMap();
        synchronized (sLock) {
            for (TogglableFlag togglableFlag : sFlags) {
                treeMap.put(togglableFlag.key, togglableFlag);
            }
        }
        return new ArrayList(treeMap.values());
    }

    public static class TogglableFlag {
        private boolean currentValue;
        private final boolean defaultValue;
        private final String description;
        private final String key;

        TogglableFlag(String key, boolean defaultValue, String description) {
            this.key = (String) Preconditions.checkNotNull(key);
            this.defaultValue = defaultValue;
            this.currentValue = defaultValue;
            this.description = (String) Preconditions.checkNotNull(description);
            synchronized (BaseFlags.sLock) {
                BaseFlags.sFlags.add(this);
            }
        }

        void setForTests(boolean value) {
            this.currentValue = value;
        }

        public String getKey() {
            return this.key;
        }

        void initialize(Context context) {
            this.currentValue = getFromStorage(context, this.defaultValue);
        }

        public void updateStorage(Context context, boolean value) {
            SharedPreferences.Editor editorEdit = context.getSharedPreferences("featureFlags", 0).edit();
            if (value == this.defaultValue) {
                editorEdit.remove(this.key).apply();
            } else {
                editorEdit.putBoolean(this.key, value).apply();
            }
        }

        boolean getFromStorage(Context context, boolean defaultValue) {
            return context.getSharedPreferences("featureFlags", 0).getBoolean(this.key, defaultValue);
        }

        boolean getDefaultValue() {
            return this.defaultValue;
        }

        public boolean get() {
            return this.currentValue;
        }

        String getDescription() {
            return this.description;
        }

        public String toString() {
            return "TogglableFlag{key=" + this.key + ", defaultValue=" + this.defaultValue + ", description=" + this.description + "}";
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof TogglableFlag)) {
                return false;
            }
            TogglableFlag togglableFlag = (TogglableFlag) o;
            return this.key.equals(togglableFlag.getKey()) && this.defaultValue == togglableFlag.getDefaultValue() && this.description.equals(togglableFlag.getDescription());
        }

        public int hashCode() {
            return ((((this.key.hashCode() ^ 1000003) * 1000003) ^ (this.defaultValue ? 1231 : 1237)) * 1000003) ^ this.description.hashCode();
        }
    }

    public static final class ToggleableGlobalSettingsFlag extends TogglableFlag {
        private ContentResolver contentResolver;

        ToggleableGlobalSettingsFlag(String key, boolean defaultValue, String description) {
            super(key, defaultValue, description);
        }

        @Override // com.android.launcher3.config.BaseFlags.TogglableFlag
        public void initialize(final Context context) {
            ContentResolver contentResolver = context.getContentResolver();
            this.contentResolver = contentResolver;
            contentResolver.registerContentObserver(Settings.Global.getUriFor(getKey()), true, new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.launcher3.config.BaseFlags.ToggleableGlobalSettingsFlag.1
                @Override // android.database.ContentObserver
                public void onChange(boolean selfChange) {
                    ToggleableGlobalSettingsFlag.this.superInitialize(context);
                }
            });
            superInitialize(context);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void superInitialize(Context context) {
            super.initialize(context);
        }

        @Override // com.android.launcher3.config.BaseFlags.TogglableFlag
        public void updateStorage(Context context, boolean z) {
            ContentResolver contentResolver = this.contentResolver;
            if (contentResolver == null) {
                return;
            }
            Settings.Global.putInt(contentResolver, getKey(), z ? 1 : 0);
        }

        @Override // com.android.launcher3.config.BaseFlags.TogglableFlag
        boolean getFromStorage(Context context, boolean z) {
            ContentResolver contentResolver = this.contentResolver;
            return contentResolver == null ? z : Settings.Global.getInt(contentResolver, getKey(), z ? 1 : 0) == 1;
        }
    }
}
