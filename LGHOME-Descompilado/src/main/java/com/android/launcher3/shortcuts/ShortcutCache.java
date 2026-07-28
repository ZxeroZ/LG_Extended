package com.android.launcher3.shortcuts;

import android.util.LruCache;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ShortcutCache {
    private static final int CACHE_SIZE = 30;
    private static final boolean LOGD = false;
    private static final String TAG = "ShortcutCache";
    private LruCache<ShortcutKey, ShortcutInfoCompat> mCachedShortcuts = new LruCache<>(30);
    private HashMap<ShortcutKey, ShortcutInfoCompat> mPinnedShortcuts = new HashMap<>();

    public void removeShortcuts(List<ShortcutInfoCompat> shortcuts) {
        Iterator<ShortcutInfoCompat> it = shortcuts.iterator();
        while (it.hasNext()) {
            ShortcutKey shortcutKeyFromInfo = ShortcutKey.fromInfo(it.next());
            this.mCachedShortcuts.remove(shortcutKeyFromInfo);
            this.mPinnedShortcuts.remove(shortcutKeyFromInfo);
        }
    }

    public ShortcutInfoCompat get(ShortcutKey key) {
        if (this.mPinnedShortcuts.containsKey(key)) {
            return this.mPinnedShortcuts.get(key);
        }
        return this.mCachedShortcuts.get(key);
    }

    public void put(ShortcutKey key, ShortcutInfoCompat shortcut) {
        if (shortcut.isPinned()) {
            this.mPinnedShortcuts.put(key, shortcut);
        } else {
            this.mCachedShortcuts.put(key, shortcut);
        }
    }
}
