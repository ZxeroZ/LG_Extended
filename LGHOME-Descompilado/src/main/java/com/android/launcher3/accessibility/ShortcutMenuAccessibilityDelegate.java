package com.android.launcher3.accessibility;

import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.shortcuts.DeepShortcutView;
import com.lge.launcher3.R;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class ShortcutMenuAccessibilityDelegate extends LauncherAccessibilityDelegate {
    public ShortcutMenuAccessibilityDelegate(Launcher launcher) {
        super(launcher);
    }

    @Override // com.android.launcher3.accessibility.LauncherAccessibilityDelegate
    protected void addActions(View host, AccessibilityNodeInfo info) {
        info.addAction(this.mActions.get(R.id.action_add_to_workspace));
    }

    @Override // com.android.launcher3.accessibility.LauncherAccessibilityDelegate
    public boolean performAction(View host, ItemInfo item, int action) {
        if (action != R.id.action_add_to_workspace || !(host.getParent() instanceof DeepShortcutView)) {
            return false;
        }
        final ShortcutInfo finalInfo = ((DeepShortcutView) host.getParent()).getFinalInfo();
        final int[] iArr = new int[2];
        final long jFindSpaceOnWorkspace = findSpaceOnWorkspace(item, iArr);
        Runnable runnable = new Runnable() { // from class: com.android.launcher3.accessibility.ShortcutMenuAccessibilityDelegate.1
            @Override // java.lang.Runnable
            public void run() {
                Launcher launcher = ShortcutMenuAccessibilityDelegate.this.mLauncher;
                ShortcutInfo shortcutInfo = finalInfo;
                long j = jFindSpaceOnWorkspace;
                int[] iArr2 = iArr;
                LauncherModel.addItemToDatabase(launcher, shortcutInfo, -100L, j, iArr2[0], iArr2[1]);
                ArrayList<ItemInfo> arrayList = new ArrayList<>();
                arrayList.add(finalInfo);
                ShortcutMenuAccessibilityDelegate.this.mLauncher.bindItems(arrayList, 0, arrayList.size(), true);
                AbstractFloatingView.closeAllOpenViews(ShortcutMenuAccessibilityDelegate.this.mLauncher);
                ShortcutMenuAccessibilityDelegate.this.announceConfirmation(R.string.item_added_to_workspace);
            }
        };
        if (!this.mLauncher.showWorkspace(true, runnable)) {
            runnable.run();
        }
        return true;
    }
}
