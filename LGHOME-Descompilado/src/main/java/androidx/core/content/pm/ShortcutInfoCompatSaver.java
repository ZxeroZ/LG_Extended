package androidx.core.content.pm;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class ShortcutInfoCompatSaver<T> {
    public abstract T addShortcuts(List<ShortcutInfoCompat> shortcuts);

    public abstract T removeAllShortcuts();

    public abstract T removeShortcuts(List<String> shortcutIds);

    public List<ShortcutInfoCompat> getShortcuts() throws Exception {
        return new ArrayList();
    }

    public static class NoopImpl extends ShortcutInfoCompatSaver<Void> {
        @Override // androidx.core.content.pm.ShortcutInfoCompatSaver
        /* JADX INFO: renamed from: addShortcuts, reason: avoid collision after fix types in other method */
        public Void addShortcuts2(List<ShortcutInfoCompat> shortcuts) {
            return null;
        }

        /* JADX DEBUG: Method merged with bridge method: removeAllShortcuts()Ljava/lang/Object; */
        @Override // androidx.core.content.pm.ShortcutInfoCompatSaver
        public Void removeAllShortcuts() {
            return null;
        }

        @Override // androidx.core.content.pm.ShortcutInfoCompatSaver
        /* JADX INFO: renamed from: removeShortcuts, reason: avoid collision after fix types in other method */
        public Void removeShortcuts2(List<String> shortcutIds) {
            return null;
        }

        /* JADX DEBUG: Return type fixed from 'java.lang.Object' to match base method */
        @Override // androidx.core.content.pm.ShortcutInfoCompatSaver
        public /* bridge */ /* synthetic */ Void addShortcuts(List shortcuts) {
            return addShortcuts2((List<ShortcutInfoCompat>) shortcuts);
        }

        /* JADX DEBUG: Return type fixed from 'java.lang.Object' to match base method */
        @Override // androidx.core.content.pm.ShortcutInfoCompatSaver
        public /* bridge */ /* synthetic */ Void removeShortcuts(List shortcutIds) {
            return removeShortcuts2((List<String>) shortcutIds);
        }
    }
}
