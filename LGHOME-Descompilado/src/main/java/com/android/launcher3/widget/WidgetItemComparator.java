package com.android.launcher3.widget;

import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.model.WidgetItem;
import java.text.Collator;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class WidgetItemComparator implements Comparator<WidgetItem> {
    private final UserHandle mMyUserHandle = Process.myUserHandle();
    private final Collator mCollator = Collator.getInstance();

    /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
    @Override // java.util.Comparator
    public int compare(WidgetItem a, WidgetItem b) {
        boolean z = !this.mMyUserHandle.equals(a.user);
        if ((!this.mMyUserHandle.equals(b.user)) ^ z) {
            return z ? 1 : -1;
        }
        int iCompare = this.mCollator.compare(a.label, b.label);
        if (iCompare != 0) {
            return iCompare;
        }
        int i = a.spanX * a.spanY;
        int i2 = b.spanX * b.spanY;
        if (i == i2) {
            return Integer.compare(a.spanY, b.spanY);
        }
        return Integer.compare(i, i2);
    }
}
