package com.lge.launcher3.smartbulletin.util;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import com.lge.launcher3.R;
import com.lge.launcher3.smartbulletin.constant.SBConstant;

/* JADX INFO: loaded from: classes.dex */
public class WidgetUtil {
    private static int sCellHeight = -1;
    private static int sCellWidth = -1;

    public static int getCalculateWidgetHeight(Context context, int minHeight) {
        if (sCellHeight == -1) {
            sCellHeight = context.getResources().getDimensionPixelSize(R.dimen.lg_workspace_cell_height_for_widget);
        }
        int i = sCellHeight;
        return ((minHeight / i) + 1) * i;
    }

    public static int getCalculateWidgetCellWidthCount(Context context, int minWidth) {
        return calculateCellSize(context, minWidth);
    }

    public static int calculateCellSize(Context context, int length) {
        if (sCellWidth == -1) {
            sCellWidth = context.getResources().getDimensionPixelSize(R.dimen.lg_workspace_cell_width_for_widget);
        }
        if (sCellHeight == -1) {
            sCellHeight = context.getResources().getDimensionPixelSize(R.dimen.lg_workspace_cell_height_for_widget);
        }
        return (int) Math.ceil(length / Math.min(sCellWidth, sCellHeight));
    }

    public static float getDensity(Context context) {
        Resources resources = context.getResources();
        if (resources != null) {
            return resources.getDisplayMetrics().noncompatDensity;
        }
        return 1.0f;
    }

    public static int getSmartBulletinType(Context context, AppWidgetProviderInfo info) {
        try {
            Bundle bundle = context.getPackageManager().getReceiverInfo(new ComponentName(info.provider.getPackageName(), info.provider.getClassName()), 128).metaData;
            if (bundle != null) {
                return bundle.getInt(SBConstant.SMARTBULLETIN_TYPE);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
