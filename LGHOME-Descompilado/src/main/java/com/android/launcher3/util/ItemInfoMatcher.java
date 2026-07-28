package com.android.launcher3.util;

import android.content.ComponentName;
import android.os.UserHandle;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.shortcuts.ShortcutKey;
import java.util.HashSet;

/* JADX INFO: loaded from: classes.dex */
public abstract class ItemInfoMatcher {
    public abstract boolean matches(ItemInfo info, ComponentName cn);

    public final HashSet<ItemInfo> filterItemInfos(Iterable<ItemInfo> infos) {
        LauncherAppWidgetInfo launcherAppWidgetInfo;
        ComponentName componentName;
        HashSet<ItemInfo> hashSet = new HashSet<>();
        for (ItemInfo itemInfo : infos) {
            if (itemInfo instanceof ShortcutInfo) {
                ItemInfo itemInfo2 = (ShortcutInfo) itemInfo;
                ComponentName targetComponent = itemInfo2.getTargetComponent();
                if (targetComponent != null && matches(itemInfo2, targetComponent)) {
                    hashSet.add(itemInfo2);
                }
            } else if (itemInfo instanceof FolderInfo) {
                for (ItemInfo itemInfo3 : ((FolderInfo) itemInfo).contents) {
                    ComponentName targetComponent2 = itemInfo3.getTargetComponent();
                    if (targetComponent2 != null && matches(itemInfo3, targetComponent2)) {
                        hashSet.add(itemInfo3);
                    }
                }
            } else if ((itemInfo instanceof LauncherAppWidgetInfo) && (componentName = (launcherAppWidgetInfo = (LauncherAppWidgetInfo) itemInfo).providerName) != null && matches(launcherAppWidgetInfo, componentName)) {
                hashSet.add(launcherAppWidgetInfo);
            }
        }
        return hashSet;
    }

    public static ItemInfoMatcher ofUser(final UserHandle user) {
        return new ItemInfoMatcher() { // from class: com.android.launcher3.util.ItemInfoMatcher.1
            @Override // com.android.launcher3.util.ItemInfoMatcher
            public boolean matches(ItemInfo info, ComponentName cn) {
                return info.user.equals(user);
            }
        };
    }

    public static ItemInfoMatcher ofComponents(final HashSet<ComponentName> components, final UserHandle user) {
        return new ItemInfoMatcher() { // from class: com.android.launcher3.util.ItemInfoMatcher.2
            @Override // com.android.launcher3.util.ItemInfoMatcher
            public boolean matches(ItemInfo info, ComponentName cn) {
                return components.contains(cn) && info.user.equals(user);
            }
        };
    }

    public static ItemInfoMatcher ofPackages(final HashSet<String> packageNames, final UserHandle user) {
        return new ItemInfoMatcher() { // from class: com.android.launcher3.util.ItemInfoMatcher.3
            @Override // com.android.launcher3.util.ItemInfoMatcher
            public boolean matches(ItemInfo info, ComponentName cn) {
                return packageNames.contains(cn.getPackageName()) && info.user.equals(user);
            }
        };
    }

    public static ItemInfoMatcher ofShortcutKeys(final HashSet<ShortcutKey> keys) {
        return new ItemInfoMatcher() { // from class: com.android.launcher3.util.ItemInfoMatcher.4
            @Override // com.android.launcher3.util.ItemInfoMatcher
            public boolean matches(ItemInfo info, ComponentName cn) {
                return info.itemType == 6 && keys.contains(ShortcutKey.fromItemInfo(info));
            }
        };
    }
}
