package com.android.launcher3.model;

import android.content.pm.PackageManager;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;
import com.android.launcher3.util.ComponentKey;
import java.text.Collator;

/* JADX INFO: loaded from: classes.dex */
public class WidgetItem extends ComponentKey implements Comparable<WidgetItem> {
    private static Collator sCollator;
    private static UserHandle sMyUserHandle;
    public final ShortcutConfigActivityInfo activityInfo;
    public final String label;
    public final int spanX;
    public final int spanY;
    public final LauncherAppWidgetProviderInfo widgetInfo;

    public WidgetItem(LauncherAppWidgetProviderInfo info, PackageManager pm, InvariantDeviceProfile idp, Launcher launcher) {
        super(info.provider, info.getProfile());
        this.label = Utilities.trim(info.getLabel(pm));
        this.widgetInfo = info;
        this.activityInfo = null;
        this.spanX = Math.min(info.getSpanX(launcher), idp.numColumns);
        this.spanY = Math.min(info.getSpanY(launcher), idp.numRows);
    }

    public WidgetItem(ShortcutConfigActivityInfo info) {
        super(info.getComponent(), info.getUser());
        this.label = Utilities.trim(info.getLabel());
        this.widgetInfo = null;
        this.activityInfo = info;
        this.spanY = 1;
        this.spanX = 1;
    }

    /* JADX DEBUG: Method merged with bridge method: compareTo(Ljava/lang/Object;)I */
    @Override // java.lang.Comparable
    public int compareTo(WidgetItem another) {
        if (sMyUserHandle == null) {
            sMyUserHandle = Process.myUserHandle();
            sCollator = Collator.getInstance();
        }
        boolean z = !sMyUserHandle.equals(this.user);
        if ((!sMyUserHandle.equals(another.user)) ^ z) {
            return z ? 1 : -1;
        }
        int iCompare = sCollator.compare(this.label, another.label);
        if (iCompare != 0) {
            return iCompare;
        }
        int i = this.spanX;
        int i2 = this.spanY;
        int i3 = i * i2;
        int i4 = another.spanX;
        int i5 = another.spanY;
        int i6 = i4 * i5;
        if (i3 == i6) {
            return Integer.compare(i2, i5);
        }
        return Integer.compare(i3, i6);
    }
}
