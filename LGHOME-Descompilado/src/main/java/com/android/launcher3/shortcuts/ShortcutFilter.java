package com.android.launcher3.shortcuts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ShortcutFilter {
    public static final int MAX_SHORTCUTS = 5;
    static final int NUM_DYNAMIC = 4;
    private static final Comparator<ShortcutInfoCompat> RANK_COMPARATOR = new Comparator<ShortcutInfoCompat>() { // from class: com.android.launcher3.shortcuts.ShortcutFilter.1
        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public int compare(ShortcutInfoCompat a, ShortcutInfoCompat b) {
            if (a.isDeclaredInManifest() && !b.isDeclaredInManifest()) {
                return -1;
            }
            if (a.isDeclaredInManifest() || !b.isDeclaredInManifest()) {
                return Integer.compare(a.getRank(), b.getRank());
            }
            return 1;
        }
    };

    public static List<ShortcutInfoCompat> sortAndFilterShortcuts(List<ShortcutInfoCompat> shortcuts) {
        Collections.sort(shortcuts, RANK_COMPARATOR);
        if (shortcuts.size() <= 5) {
            return shortcuts;
        }
        ArrayList arrayList = new ArrayList(5);
        int size = shortcuts.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            ShortcutInfoCompat shortcutInfoCompat = shortcuts.get(i2);
            int size2 = arrayList.size();
            if (size2 < 5) {
                arrayList.add(shortcutInfoCompat);
                if (shortcutInfoCompat.isDynamic()) {
                    i++;
                }
            } else if (shortcutInfoCompat.isDynamic() && i < 4) {
                i++;
                arrayList.remove(size2 - i);
                arrayList.add(shortcutInfoCompat);
            }
        }
        return arrayList;
    }
}
