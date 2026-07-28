package com.android.launcher3.widget;

import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.model.WidgetItem;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class WidgetListRowEntry {
    public final PackageItemInfo pkgItem;
    public String titleSectionName;
    public final ArrayList<WidgetItem> widgets;

    public WidgetListRowEntry(PackageItemInfo pkgItem, ArrayList<WidgetItem> items) {
        this.pkgItem = pkgItem;
        this.widgets = items;
    }
}
