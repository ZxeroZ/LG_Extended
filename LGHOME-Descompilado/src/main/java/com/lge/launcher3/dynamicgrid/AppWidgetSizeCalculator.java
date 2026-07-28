package com.lge.launcher3.dynamicgrid;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import com.lge.launcher3.R;
import com.lge.launcher3.util.ManagedProfileUtils;

/* JADX INFO: loaded from: classes.dex */
public class AppWidgetSizeCalculator {

    public static class Padding {
        public int bottom;
        public int left;
        public int right;
        public int top;

        Padding() {
            this.left = 0;
            this.right = 0;
            this.top = 0;
            this.bottom = 0;
            this.left = 0;
            this.top = 0;
            this.right = 0;
            this.bottom = 0;
        }

        public Padding(final int left, final int top, final int right, final int bottom) {
            this.left = 0;
            this.right = 0;
            this.top = 0;
            this.bottom = 0;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }

    public static int[] getMinResizeSpanForWidget(final Context context, final AppWidgetProviderInfo info, final int[] spanXY) {
        return getSpanForWidget(context, info.provider, info.minResizeWidth, info.minResizeHeight, spanXY);
    }

    public static int[] getSpanForWidget(final Context context, final AppWidgetProviderInfo info, final int[] spanXY) {
        return getSpanForWidget(context, info.provider, info.minWidth, info.minHeight, spanXY);
    }

    public static int[] getSpanForWidget(final Context context, final ComponentName component, final int minWidth, final int minHeight, int[] spanXY) {
        if (spanXY == null) {
            spanXY = new int[2];
        }
        if (!isValidComponent(context, component)) {
            spanXY[0] = -1;
            spanXY[1] = -1;
            return spanXY;
        }
        Padding padding = new Padding();
        return rectToCellForWidget(context.getResources(), minWidth + padding.left + padding.right, minHeight + padding.top + padding.bottom, spanXY);
    }

    public static boolean isValidComponent(final Context context, final ComponentName component) {
        try {
            context.getPackageManager().getApplicationInfo(component.getPackageName(), 0);
            return true;
        } catch (Exception e) {
            if (ManagedProfileUtils.hasProfileOwner(context)) {
                try {
                    context.getPackageManager().getPackageInfo(component.getPackageName(), 8192);
                    return true;
                } catch (Exception e2) {
                    e2.printStackTrace();
                    return false;
                }
            }
            e.printStackTrace();
            return false;
        }
    }

    public static AppWidgetProviderInfo getSpanForWidget(Context context, int appWidgetId, int[] spanXY) {
        AppWidgetProviderInfo appWidgetInfo = AppWidgetManager.getInstance(context).getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo != null) {
            getSpanForWidget(context, appWidgetInfo, spanXY);
        }
        return appWidgetInfo;
    }

    public static int[] rectToCellForWidget(Resources resources, int width, int height, int[] result) {
        float fMin = Math.min(resources.getDimensionPixelSize(R.dimen.lg_workspace_cell_width_for_widget), resources.getDimensionPixelSize(R.dimen.lg_workspace_cell_height_for_widget));
        int iCeil = (int) Math.ceil(width / fMin);
        int iCeil2 = (int) Math.ceil(height / fMin);
        if (result == null) {
            return new int[]{iCeil, iCeil2};
        }
        result[0] = iCeil;
        result[1] = iCeil2;
        return result;
    }
}
