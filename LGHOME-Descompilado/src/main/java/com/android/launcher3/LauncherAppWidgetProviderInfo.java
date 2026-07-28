package com.android.launcher3;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.icons.IconCache;
import com.lge.launcher3.dynamicgrid.AppWidgetSizeCalculator;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.lgewidgetlib.LgeWidgetContext;

/* JADX INFO: loaded from: classes.dex */
public class LauncherAppWidgetProviderInfo extends AppWidgetProviderInfo {
    public static final String KEY_MAX_SPAN_X = "max_span_x";
    public static final String KEY_MAX_SPAN_Y = "max_span_y";
    private static final String TAG = "LauncherAppWidgetProviderInfo";
    public boolean isCustomWidget;
    public boolean isLgeWidget;
    public int maxSpanX;
    public int maxSpanY;
    public int minSpanX;
    public int minSpanY;
    public int spanX;
    public int spanY;

    public static LauncherAppWidgetProviderInfo fromProviderInfo(Context context, AppWidgetProviderInfo info) {
        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo;
        if (info instanceof LauncherAppWidgetProviderInfo) {
            launcherAppWidgetProviderInfo = (LauncherAppWidgetProviderInfo) info;
        } else {
            Parcel parcelObtain = Parcel.obtain();
            info.writeToParcel(parcelObtain, 0);
            parcelObtain.setDataPosition(0);
            launcherAppWidgetProviderInfo = new LauncherAppWidgetProviderInfo(parcelObtain);
            parcelObtain.recycle();
        }
        launcherAppWidgetProviderInfo.initSpans(context);
        if (LGHomeFeature.Config.FEATURE_USE_WIDGET_MAX_SPAN.getValue() || com.lge.launcher3.util.Utilities.isLGUI10_0()) {
            launcherAppWidgetProviderInfo.initMaxSpan(context);
        }
        if (launcherAppWidgetProviderInfo != null && launcherAppWidgetProviderInfo.provider != null) {
            launcherAppWidgetProviderInfo.isLgeWidget = LgeWidgetContext.isLGEWeatherWidgetPackage(launcherAppWidgetProviderInfo.provider.getPackageName());
        }
        return launcherAppWidgetProviderInfo;
    }

    public LauncherAppWidgetProviderInfo(Parcel in) {
        super(in);
        this.isCustomWidget = false;
        this.isLgeWidget = false;
        this.spanX = -1;
        this.spanY = -1;
        this.minSpanX = -1;
        this.minSpanY = -1;
        this.maxSpanX = Integer.MAX_VALUE;
        this.maxSpanY = Integer.MAX_VALUE;
    }

    public LauncherAppWidgetProviderInfo() {
        this.isCustomWidget = false;
        this.isLgeWidget = false;
        this.spanX = -1;
        this.spanY = -1;
        this.minSpanX = -1;
        this.minSpanY = -1;
        this.maxSpanX = Integer.MAX_VALUE;
        this.maxSpanY = Integer.MAX_VALUE;
    }

    public LauncherAppWidgetProviderInfo(Context context, CustomAppWidget widget) {
        this.isCustomWidget = false;
        this.isLgeWidget = false;
        this.spanX = -1;
        this.spanY = -1;
        this.minSpanX = -1;
        this.minSpanY = -1;
        this.maxSpanX = Integer.MAX_VALUE;
        this.maxSpanY = Integer.MAX_VALUE;
        this.isCustomWidget = true;
        this.provider = new ComponentName(context, widget.getClass().getName());
        this.icon = widget.getIcon();
        this.label = widget.getLabel();
        this.previewImage = widget.getPreviewImage();
        this.initialLayout = widget.getWidgetLayout();
        this.resizeMode = widget.getResizeMode();
        initSpans(context);
        if (LGHomeFeature.Config.FEATURE_USE_WIDGET_MAX_SPAN.getValue() || com.lge.launcher3.util.Utilities.isLGUI10_0()) {
            initMaxSpan(context);
        }
        this.isLgeWidget = LgeWidgetContext.isLGEAppWidgetPackage(this.provider.getPackageName());
    }

    public void initSpans(Context context) {
        lazyLoadSpans(context, false);
    }

    public void afterUpdateInitSpans(Context context) {
        lazyLoadSpans(context, true);
    }

    public String getLabel(PackageManager packageManager) {
        if (this.isCustomWidget) {
            return Utilities.trim(this.label);
        }
        return super.loadLabel(packageManager);
    }

    public Drawable getIcon(Context context, IconCache cache) {
        if (this.isCustomWidget) {
            return cache.getFullResIcon(this.provider.getPackageName(), this.icon);
        }
        return super.loadIcon(context, LauncherAppState.getIDP(context).fillResIconDpi);
    }

    public String toString(PackageManager pm) {
        if (this.isCustomWidget) {
            return "WidgetProviderInfo(" + this.provider + ")";
        }
        return String.format("WidgetProviderInfo provider:%s package:%s short:%s label:%s", this.provider.toString(), this.provider.getPackageName(), this.provider.getShortClassName(), getLabel(pm));
    }

    public Point getMinSpans(InvariantDeviceProfile idp, Context context) {
        return new Point((this.resizeMode & 1) != 0 ? this.minSpanX : -1, (this.resizeMode & 2) != 0 ? this.minSpanY : -1);
    }

    public UserHandle getUser() {
        return this.isCustomWidget ? Process.myUserHandle() : getProfile();
    }

    public int getSpanX(Context launcher) {
        lazyLoadSpans(launcher, false);
        return this.spanX;
    }

    public int getSpanY(Context launcher) {
        lazyLoadSpans(launcher, false);
        return this.spanY;
    }

    public int getMinSpanX(Context launcher) {
        lazyLoadSpans(launcher, false);
        return this.minSpanX;
    }

    public int getMinSpanY(Context launcher) {
        lazyLoadSpans(launcher, false);
        return this.minSpanY;
    }

    private void lazyLoadSpans(Context context, boolean afterUpdate) {
        if (afterUpdate || this.spanX < 0 || this.spanY < 0 || this.minSpanX < 0 || this.minSpanY < 0) {
            int[] spanForWidget = AppWidgetSizeCalculator.getSpanForWidget(context, this.provider, this.minResizeWidth, this.minResizeHeight, null);
            int[] spanForWidget2 = AppWidgetSizeCalculator.getSpanForWidget(context, this.provider, this.minWidth, this.minHeight, null);
            this.spanX = spanForWidget2[0];
            this.spanY = spanForWidget2[1];
            this.minSpanX = spanForWidget[0];
            this.minSpanY = spanForWidget[1];
        }
    }

    public int getWidgetFeatures() {
        if (Utilities.ATLEAST_P) {
            return this.widgetFeatures;
        }
        return 0;
    }

    public int getMaxSpanX() {
        return this.maxSpanX;
    }

    public int getMaxSpanY() {
        return this.maxSpanY;
    }

    public void initMaxSpan(Context context) {
        Bundle bundle;
        try {
            ActivityInfo receiverInfo = context.getPackageManager().getReceiverInfo(this.provider, 128);
            if (receiverInfo == null || (bundle = receiverInfo.metaData) == null) {
                return;
            }
            int i = bundle.getInt(KEY_MAX_SPAN_X, -1);
            int i2 = bundle.getInt(KEY_MAX_SPAN_Y, -1);
            if (i != -1) {
                this.maxSpanX = Math.max(this.minSpanX, i);
            }
            if (i2 != -1) {
                this.maxSpanY = Math.max(this.minSpanY, i2);
            }
            if (this.maxSpanX == Integer.MAX_VALUE && this.maxSpanY == Integer.MAX_VALUE) {
                return;
            }
            LGLog.d(TAG, "initMaxSpan : " + this.provider + ", maxSpan(" + this.maxSpanX + ", " + this.maxSpanY + "), minSpan(" + this.minSpanX + ", " + this.minSpanY + ")");
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.d(TAG, "initMaxSpan : failed. " + this.provider + ", exception = " + e.toString());
        }
    }
}
