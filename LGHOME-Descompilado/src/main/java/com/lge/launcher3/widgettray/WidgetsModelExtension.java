package com.lge.launcher3.widgettray;

import android.content.Context;
import com.android.launcher3.AppFilter;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.WidgetsModel;
import com.lge.launcher3.dynamicgrid.AppWidgetSizeCalculator;
import com.lge.launcher3.profile.LGInvariantDeviceProfile;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class WidgetsModelExtension extends WidgetsModel {
    public WidgetsModelExtension(Context context, IconCache iconCache, AppFilter appFilter) {
        super(context, iconCache, appFilter);
    }

    protected WidgetsModelExtension(WidgetsModel model) {
        super(model);
    }

    @Override // com.android.launcher3.model.WidgetsModel
    public void setWidgetsAndShortcuts(Context context, ArrayList<Object> rawWidgetsShortcuts) {
        checkValidSizeWidget(context, rawWidgetsShortcuts);
        super.setWidgetsAndShortcuts(context, rawWidgetsShortcuts);
    }

    private void checkValidSizeWidget(Context context, ArrayList<Object> rawWidgetsShortcuts) {
        for (int size = rawWidgetsShortcuts.size() - 1; size >= 0; size--) {
            Object obj = rawWidgetsShortcuts.get(size);
            if (obj instanceof LauncherAppWidgetProviderInfo) {
                LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo = (LauncherAppWidgetProviderInfo) obj;
                if (!isValidSizeWidget(context, launcherAppWidgetProviderInfo) || !isAddableWidget(context, launcherAppWidgetProviderInfo)) {
                    rawWidgetsShortcuts.remove(size);
                }
            }
        }
    }

    private boolean isValidSizeWidget(Context context, LauncherAppWidgetProviderInfo info) {
        int i;
        int i2;
        int[] spanForWidget = AppWidgetSizeCalculator.getSpanForWidget(context, info, (int[]) null);
        int[] minResizeSpanForWidget = AppWidgetSizeCalculator.getMinResizeSpanForWidget(context, info, null);
        if (spanForWidget != null && ((spanForWidget == null || (spanForWidget[0] >= 0 && spanForWidget[1] >= 0)) && minResizeSpanForWidget != null && (minResizeSpanForWidget == null || (minResizeSpanForWidget[0] >= 0 && minResizeSpanForWidget[1] >= 0)))) {
            int iMin = Math.min(spanForWidget[0], minResizeSpanForWidget[0]);
            int iMin2 = Math.min(spanForWidget[1], minResizeSpanForWidget[1]);
            InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
            if (idp instanceof LGInvariantDeviceProfile) {
                LGInvariantDeviceProfile lGInvariantDeviceProfile = (LGInvariantDeviceProfile) idp;
                i2 = lGInvariantDeviceProfile.numColumns;
                i = lGInvariantDeviceProfile.numRows;
            } else {
                i = -1;
                i2 = -1;
            }
            if (i2 != -1 && i != -1 && iMin <= i2 && iMin2 <= i) {
                return true;
            }
            LGLog.w("WidgetsModel", "Widget " + info.provider + " can not fit on this device (" + info.minWidth + ", " + info.minHeight + "), default (" + i2 + "x" + i + "), span (" + iMin + "x" + iMin2 + ")", new int[0]);
        }
        return false;
    }

    /* JADX DEBUG: Method merged with bridge method: clone()Ljava/lang/Object; */
    @Override // com.android.launcher3.model.WidgetsModel
    /* JADX INFO: renamed from: clone */
    public WidgetsModel mo209clone() {
        return new WidgetsModelExtension(this);
    }

    private boolean isAddableWidget(Context context, LauncherAppWidgetProviderInfo widget) {
        if (widget.minWidth > 0 && widget.minHeight > 0) {
            return true;
        }
        LGLog.w("WidgetsModel", "Widget " + widget.provider + " has invalid dimensions (" + widget.minWidth + ", " + widget.minHeight + ")", new int[0]);
        return false;
    }
}
