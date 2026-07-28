package com.android.launcher3.shortcuts;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.ShortcutInfo;
import android.os.UserHandle;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.util.PackageUtils;

/* JADX INFO: loaded from: classes.dex */
public class ShortcutInfoCompat {
    public static final String EXTRA_SHORTCUT_ID = "shortcut_id";
    public static final String INTENT_CATEGORY = "com.android.launcher3.DEEP_SHORTCUT";
    private ShortcutInfo mShortcutInfo;

    public ShortcutInfoCompat(ShortcutInfo shortcutInfo) {
        this.mShortcutInfo = shortcutInfo;
    }

    public Intent makeIntent(Context context) {
        return new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory(INTENT_CATEGORY).setComponent(getActivity()).setPackage(getPackage()).setFlags(270532608).putExtra(ItemInfo.EXTRA_PROFILE, UserManagerCompat.getInstance(context).getSerialNumberForUser(getUserHandle())).putExtra("shortcut_id", getId());
    }

    public ShortcutInfo getShortcutInfo() {
        return this.mShortcutInfo;
    }

    public String getPackage() {
        return this.mShortcutInfo.getPackage();
    }

    public String getId() {
        return this.mShortcutInfo.getId();
    }

    public CharSequence getShortLabel() {
        return this.mShortcutInfo.getShortLabel();
    }

    public CharSequence getLongLabel() {
        return this.mShortcutInfo.getLongLabel();
    }

    public long getLastChangedTimestamp() {
        return this.mShortcutInfo.getLastChangedTimestamp();
    }

    public ComponentName getActivity() {
        return this.mShortcutInfo.getActivity();
    }

    public UserHandle getUserHandle() {
        return this.mShortcutInfo.getUserHandle();
    }

    public boolean hasKeyFieldsOnly() {
        return this.mShortcutInfo.hasKeyFieldsOnly();
    }

    public boolean isPinned() {
        return this.mShortcutInfo.isPinned();
    }

    public boolean isDeclaredInManifest() {
        return this.mShortcutInfo.isDeclaredInManifest();
    }

    public boolean isEnabled() {
        return this.mShortcutInfo.isEnabled();
    }

    public boolean isDynamic() {
        return this.mShortcutInfo.isDynamic();
    }

    public int getRank() {
        return this.mShortcutInfo.getRank();
    }

    public CharSequence getDisabledMessage() {
        return this.mShortcutInfo.getDisabledMessage();
    }

    public String toString() {
        return this.mShortcutInfo.toString();
    }

    public LauncherActivityInfo getActivityInfo(Context context) {
        return LauncherAppsCompat.getInstance(context).resolveActivity(makeIntent(context), getUserHandle());
    }
}
