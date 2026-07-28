package androidx.core.content.pm;

import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class ShortcutInfoChangeListener {
    public void onAllShortcutsRemoved() {
    }

    public void onShortcutAdded(List<ShortcutInfoCompat> shortcuts) {
    }

    public void onShortcutRemoved(List<String> shortcutIds) {
    }

    public void onShortcutUpdated(List<ShortcutInfoCompat> shortcuts) {
    }

    public void onShortcutUsageReported(List<String> shortcutIds) {
    }
}
