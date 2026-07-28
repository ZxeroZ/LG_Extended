package com.lge.launcher3.allapps;

import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.model.data.FolderInfo;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public interface IAllAppsFolderListener {
    void addItemsInAllApps(ArrayList<ShortcutInfo> shortcutInfos);

    void bindAppsMoved(ArrayList<ShortcutInfo> workspaceShortcuts, FolderInfo target);

    void removeVacantPage();
}
