package com.android.launcher3.util;

import android.content.SharedPreferences;
import android.util.ArrayMap;
import com.android.launcher3.Launcher;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class OnboardingPrefs<T extends Launcher> {
    public static final String ALL_APPS_COUNT = "launcher.all_apps_count";
    public static final String HOME_BOUNCE_COUNT = "launcher.home_bounce_count";
    public static final String HOME_BOUNCE_SEEN = "launcher.apps_view_shown";
    public static final String HOTSEAT_DISCOVERY_TIP_COUNT = "launcher.hotseat_discovery_tip_count";
    public static final String HOTSEAT_LONGPRESS_TIP_SEEN = "launcher.hotseat_longpress_tip_seen";
    private static final Map<String, Integer> MAX_COUNTS;
    public static final String SHELF_BOUNCE_COUNT = "launcher.shelf_bounce_count";
    public static final String SHELF_BOUNCE_SEEN = "launcher.shelf_bounce_seen";
    protected final T mLauncher;
    protected final SharedPreferences mSharedPrefs;

    @Retention(RetentionPolicy.SOURCE)
    public @interface EventBoolKey {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface EventCountKey {
    }

    static {
        ArrayMap arrayMap = new ArrayMap(3);
        arrayMap.put("launcher.home_bounce_count", 3);
        arrayMap.put("launcher.shelf_bounce_count", 3);
        arrayMap.put(ALL_APPS_COUNT, 5);
        arrayMap.put(HOTSEAT_DISCOVERY_TIP_COUNT, 5);
        MAX_COUNTS = Collections.unmodifiableMap(arrayMap);
    }

    public OnboardingPrefs(T launcher, SharedPreferences sharedPrefs) {
        this.mLauncher = launcher;
        this.mSharedPrefs = sharedPrefs;
    }

    public int getCount(String key) {
        return this.mSharedPrefs.getInt(key, 0);
    }

    public boolean hasReachedMaxCount(String eventKey) {
        return hasReachedMaxCount(getCount(eventKey), eventKey);
    }

    private boolean hasReachedMaxCount(int count, String eventKey) {
        return count >= MAX_COUNTS.get(eventKey).intValue();
    }

    public boolean getBoolean(String key) {
        return this.mSharedPrefs.getBoolean(key, false);
    }

    public void markChecked(String flag) {
        this.mSharedPrefs.edit().putBoolean(flag, true).apply();
    }

    public boolean incrementEventCount(String eventKey) {
        int count = getCount(eventKey);
        if (hasReachedMaxCount(count, eventKey)) {
            return true;
        }
        int i = count + 1;
        this.mSharedPrefs.edit().putInt(eventKey, i).apply();
        return hasReachedMaxCount(i, eventKey);
    }
}
