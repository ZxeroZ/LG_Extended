package com.android.launcher3.shortcuts;

import android.content.ComponentName;
import android.content.Intent;
import android.os.UserHandle;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.ComponentKey;
import com.lge.launcher3.util.PackageUtils;

/* JADX INFO: loaded from: classes.dex */
public class ShortcutKey extends ComponentKey {
    public static final String EXTRA_SHORTCUT_ID = "shortcut_id";
    private static final String INTENT_CATEGORY = "com.android.launcher3.DEEP_SHORTCUT";

    public ShortcutKey(String packageName, UserHandle user, String id) {
        super(new ComponentName(packageName, id), user);
    }

    public String getId() {
        return this.componentName.getClassName();
    }

    public static ShortcutKey fromInfo(ShortcutInfoCompat shortcutInfo) {
        return new ShortcutKey(shortcutInfo.getPackage(), shortcutInfo.getUserHandle(), shortcutInfo.getId());
    }

    public static ShortcutKey fromIntent(Intent intent, UserHandle user) {
        return new ShortcutKey(intent.getPackage(), user, intent.getStringExtra("shortcut_id"));
    }

    public static ShortcutKey fromItemInfo(ItemInfo info) {
        return fromIntent(info.getIntent(), info.user);
    }

    public static ShortcutKey fromShortcutInfo(ShortcutInfo info) {
        return fromIntent(info.getPromisedIntent(), info.user);
    }

    public static Intent makeIntent(android.content.pm.ShortcutInfo si) {
        return new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory("com.android.launcher3.DEEP_SHORTCUT").setComponent(si.getActivity()).setPackage(si.getPackage()).setFlags(270532608).putExtra("shortcut_id", si.getId());
    }
}
