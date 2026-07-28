package com.android.launcher3.util;

import com.android.launcher3.Utilities;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.shortcuts.ShortcutKey;

/* JADX INFO: loaded from: classes.dex */
public class ShortcutUtil {
    public static boolean supportsShortcuts(ItemInfo info) {
        return isActive(info) && (isApp(info) || isPinnedShortcut(info));
    }

    public static boolean supportsDeepShortcuts(ItemInfo info) {
        return isActive(info) && isApp(info);
    }

    public static String getShortcutIdIfPinnedShortcut(ItemInfo info) {
        if (isActive(info) && isPinnedShortcut(info)) {
            return ShortcutKey.fromItemInfo(info).getId();
        }
        return null;
    }

    public static String[] getPersonKeysIfPinnedShortcut(ItemInfo info) {
        return (isActive(info) && isPinnedShortcut(info)) ? ((WorkspaceItemInfo) info).getPersonKeys() : Utilities.EMPTY_STRING_ARRAY;
    }

    public static boolean isDeepShortcut(ItemInfo info) {
        return info.itemType == 6 && (info instanceof WorkspaceItemInfo);
    }

    private static boolean isActive(ItemInfo info) {
        return (((info instanceof WorkspaceItemInfo) && ((WorkspaceItemInfo) info).hasPromiseIconUi()) || info.isDisabled()) ? false : true;
    }

    private static boolean isApp(ItemInfo info) {
        return info.itemType == 0;
    }

    private static boolean isPinnedShortcut(ItemInfo info) {
        return info.itemType == 6 && info.container != -1 && (info instanceof WorkspaceItemInfo);
    }
}
