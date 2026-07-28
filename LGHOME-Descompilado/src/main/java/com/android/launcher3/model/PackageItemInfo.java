package com.android.launcher3.model;

import com.android.launcher3.model.data.ItemInfoWithIcon;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class PackageItemInfo extends ItemInfoWithIcon {
    int flags = 0;
    public String packageName;
    public String titleSectionName;

    public PackageItemInfo(String packageName) {
        this.packageName = packageName;
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public String toString() {
        CharSequence charSequence = this.title;
        return "PackageItemInfo(title=" + ((Object) charSequence) + " id=" + this.id + " type=" + this.itemType + " container=" + this.container + " screen=" + this.screenId + " cellX=" + this.cellX + " cellY=" + this.cellY + " spanX=" + this.spanX + " spanY=" + this.spanY + " dropPos=" + Arrays.toString(this.dropPos) + " user=" + this.user + ")";
    }
}
