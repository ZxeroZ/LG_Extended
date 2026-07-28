package com.android.launcher3.model;

import android.os.UserHandle;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.MultiHashMap;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public abstract class ExtendedModelTask extends LauncherModel.BaseModelUpdateTask {
    public void bindUpdatedShortcuts(ArrayList<ShortcutInfo> updatedShortcuts, UserHandle user) {
        bindUpdatedShortcuts(updatedShortcuts, new ArrayList<>(), user);
    }

    public void bindUpdatedShortcuts(final ArrayList<ShortcutInfo> updatedShortcuts, final ArrayList<ShortcutInfo> removedShortcuts, final UserHandle user) {
        if (updatedShortcuts.isEmpty() && removedShortcuts.isEmpty()) {
            return;
        }
        scheduleCallbackTask(new LauncherModel.CallbackTask() { // from class: com.android.launcher3.model.ExtendedModelTask.1
            @Override // com.android.launcher3.LauncherModel.CallbackTask
            public void execute(LauncherModel.Callbacks callbacks) {
                callbacks.bindShortcutsChanged(updatedShortcuts, removedShortcuts, user);
            }
        });
    }

    public void bindDeepShortcuts(BgDataModel dataModel) {
        final MultiHashMap<ComponentKey, String> multiHashMapClone = dataModel.deepShortcutMap.clone();
        scheduleCallbackTask(new LauncherModel.CallbackTask() { // from class: com.android.launcher3.model.ExtendedModelTask.2
            @Override // com.android.launcher3.LauncherModel.CallbackTask
            public void execute(LauncherModel.Callbacks callbacks) {
                callbacks.bindDeepShortcutMap(multiHashMapClone);
            }
        });
    }
}
