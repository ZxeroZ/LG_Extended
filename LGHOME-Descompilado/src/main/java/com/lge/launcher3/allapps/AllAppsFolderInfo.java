package com.lge.launcher3.allapps;

import android.content.Context;
import com.android.launcher3.model.data.FolderInfo;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsFolderInfo extends FolderInfo {
    @Override // com.android.launcher3.model.data.FolderInfo
    public void setOption(int option, boolean isEnabled, Context context) {
        int i = this.options;
        if (isEnabled) {
            this.options = option | this.options;
        } else {
            this.options = (~option) & this.options;
        }
        if (context == null || i == this.options) {
            return;
        }
        AllAppsItemFactory.getInstance().updateFolderOption(context, this);
    }
}
