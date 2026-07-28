package com.android.launcher3.shortcuts;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.util.LGHomeFeature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class DeepShortcutManager {
    private static final int FLAG_GET_ALL = 11;
    public static final int FLAG_MATCH_DYNAMIC = 1;
    public static final int FLAG_MATCH_MANIFEST = 8;
    public static final int FLAG_MATCH_PINNED = 2;
    private static final String TAG = "DeepShortcutManager";
    private static DeepShortcutManager sInstance;
    private static final Object sInstanceLock = new Object();
    private final LauncherApps mLauncherApps;
    private boolean mWasLastCallSuccess;

    public void onShortcutsChanged(List<ShortcutInfoCompat> shortcuts) {
    }

    public static DeepShortcutManager getInstance(Context context) {
        DeepShortcutManager deepShortcutManager;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                sInstance = new DeepShortcutManager(context.getApplicationContext());
            }
            deepShortcutManager = sInstance;
        }
        return deepShortcutManager;
    }

    public DeepShortcutManager(Context context) {
        this.mLauncherApps = (LauncherApps) context.getSystemService("launcherapps");
    }

    public DeepShortcutManager(Context context, ShortcutCache shortcutCache) {
        this.mLauncherApps = (LauncherApps) context.getSystemService("launcherapps");
    }

    public static boolean supportsShortcuts(ItemInfo info) {
        boolean z = (info instanceof ShortcutInfo) && ((ShortcutInfo) info).isPromise();
        return LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() ? ((info.itemType != 0 && info.itemType != 6) || info.isDisabled() || z) ? false : true : (info.itemType != 0 || info.isDisabled() || z) ? false : true;
    }

    public boolean wasLastCallSuccess() {
        return this.mWasLastCallSuccess;
    }

    public List<ShortcutInfoCompat> queryForFullDetails(String packageName, List<String> shortcutIds, UserHandle user) {
        return query(11, packageName, null, shortcutIds, user);
    }

    public List<ShortcutInfoCompat> queryForShortcutsContainer(ComponentName activity, List<String> ids, UserHandle user) {
        return query(9, activity.getPackageName(), activity, ids, user);
    }

    public void unpinShortcut(final ShortcutKey key) {
        if (Utilities.isNycMR1OrAbove()) {
            String packageName = key.componentName.getPackageName();
            String id = key.getId();
            UserHandle userHandle = key.user;
            List<String> listExtractIds = extractIds(queryForPinnedShortcuts(packageName, userHandle));
            listExtractIds.remove(id);
            try {
                this.mLauncherApps.pinShortcuts(packageName, listExtractIds, userHandle);
                this.mWasLastCallSuccess = true;
            } catch (IllegalStateException | SecurityException e) {
                Log.w(TAG, "Failed to unpin shortcut", e);
                this.mWasLastCallSuccess = false;
            }
        }
    }

    public void pinShortcut(final ShortcutKey key) {
        if (Utilities.isNycMR1OrAbove()) {
            String packageName = key.componentName.getPackageName();
            String id = key.getId();
            UserHandle userHandle = key.user;
            List<String> listExtractIds = extractIds(queryForPinnedShortcuts(packageName, userHandle));
            listExtractIds.add(id);
            try {
                this.mLauncherApps.pinShortcuts(packageName, listExtractIds, userHandle);
                this.mWasLastCallSuccess = true;
            } catch (IllegalStateException | SecurityException e) {
                Log.w(TAG, "Failed to pin shortcut", e);
                this.mWasLastCallSuccess = false;
            }
        }
    }

    public void startShortcut(String packageName, String id, Rect sourceBounds, Bundle startActivityOptions, UserHandle user) {
        if (Utilities.isNycMR1OrAbove()) {
            try {
                this.mLauncherApps.startShortcut(packageName, id, sourceBounds, startActivityOptions, user);
                this.mWasLastCallSuccess = true;
            } catch (IllegalStateException | SecurityException e) {
                Log.e(TAG, "Failed to start shortcut", e);
                this.mWasLastCallSuccess = false;
            }
        }
    }

    public Drawable getShortcutIconDrawable(ShortcutInfoCompat shortcutInfo, int density) {
        if (!Utilities.isNycMR1OrAbove()) {
            return null;
        }
        try {
            Drawable shortcutIconDrawable = this.mLauncherApps.getShortcutIconDrawable(shortcutInfo.getShortcutInfo(), density);
            this.mWasLastCallSuccess = true;
            return shortcutIconDrawable;
        } catch (IllegalStateException | SecurityException e) {
            Log.e(TAG, "Failed to get shortcut icon", e);
            this.mWasLastCallSuccess = false;
            return null;
        }
    }

    public List<ShortcutInfoCompat> queryForPinnedShortcuts(String packageName, UserHandle user) {
        return query(2, packageName, null, null, user);
    }

    public List<ShortcutInfoCompat> queryForAllShortcuts(UserHandle user) {
        return query(11, null, null, null, user);
    }

    private List<String> extractIds(List<ShortcutInfoCompat> shortcuts) {
        ArrayList arrayList = new ArrayList(shortcuts.size());
        Iterator<ShortcutInfoCompat> it = shortcuts.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().getId());
        }
        return arrayList;
    }

    private List<ShortcutInfoCompat> query(int flags, String packageName, ComponentName activity, List<String> shortcutIds, UserHandle user) {
        if (Utilities.isNycMR1OrAbove()) {
            LauncherApps.ShortcutQuery shortcutQuery = new LauncherApps.ShortcutQuery();
            shortcutQuery.setQueryFlags(flags);
            if (packageName != null) {
                shortcutQuery.setPackage(packageName);
                shortcutQuery.setActivity(activity);
                shortcutQuery.setShortcutIds(shortcutIds);
            }
            List<android.content.pm.ShortcutInfo> shortcuts = null;
            try {
                shortcuts = this.mLauncherApps.getShortcuts(shortcutQuery, user);
                this.mWasLastCallSuccess = true;
            } catch (IllegalStateException | SecurityException e) {
                Log.e(TAG, "Failed to query for shortcuts", e);
                this.mWasLastCallSuccess = false;
            }
            if (shortcuts == null) {
                return Collections.EMPTY_LIST;
            }
            ArrayList arrayList = new ArrayList(shortcuts.size());
            Iterator<android.content.pm.ShortcutInfo> it = shortcuts.iterator();
            while (it.hasNext()) {
                arrayList.add(new ShortcutInfoCompat(it.next()));
            }
            return arrayList;
        }
        return Collections.EMPTY_LIST;
    }

    public boolean hasHostPermission() {
        if (!Utilities.isNycMR1OrAbove()) {
            return false;
        }
        try {
            return this.mLauncherApps.hasShortcutHostPermission();
        } catch (IllegalStateException | SecurityException e) {
            Log.e(TAG, "Failed to make shortcut manager call", e);
            return false;
        }
    }
}
