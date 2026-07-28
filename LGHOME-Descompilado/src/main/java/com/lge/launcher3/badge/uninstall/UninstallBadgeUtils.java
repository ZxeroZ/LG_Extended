package com.lge.launcher3.badge.uninstall;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import com.android.launcher3.Launcher;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class UninstallBadgeUtils {
    public static int sDefaultRangeOfUninstallBadge = 0;
    public static final int sDurationOfUninstallBadgeAnimation = 600;
    private static int sRangeOfUninstallBadge = -1;
    public static final float sRangeRatio = 1.17f;
    private static BitmapDrawable sSwivelUninstallBadgeDrawable = null;
    private static int sTempRange = -1;
    private static BitmapDrawable sUninstallBadgeDrawable;
    private static Rect sUninstallBadgeRect = new Rect();

    public enum UninstallType {
        UNINSTALL,
        DELETE,
        DISABLE
    }

    public static int getRangeOfUninstallBadge() {
        return sRangeOfUninstallBadge;
    }

    public static boolean setRangeOfUninstallBadge(int range) {
        int i = sRangeOfUninstallBadge;
        sTempRange = i;
        sRangeOfUninstallBadge = range;
        return range == -1 || i != range;
    }

    public static BitmapDrawable createUninstallBadgeDrawable(Context context) {
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && !Launcher.getLauncher(context).isAllAppsVisible()) {
            BitmapDrawable bitmapDrawable = sSwivelUninstallBadgeDrawable;
            return bitmapDrawable != null ? bitmapDrawable : createSwivelUninstallBadgeDrawable(context);
        }
        BitmapDrawable bitmapDrawable2 = sUninstallBadgeDrawable;
        if (bitmapDrawable2 != null) {
            return bitmapDrawable2;
        }
        Resources resources = context.getResources();
        int dimension = (int) resources.getDimension(R.dimen.appnotifier_badge_size);
        BitmapDrawable bitmapDrawable3 = (BitmapDrawable) resources.getDrawable(R.drawable.btn_homescreen_close_normal, null);
        if (bitmapDrawable3 == null) {
            return null;
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(dimension, dimension, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        bitmapDrawable3.setFilterBitmap(true);
        bitmapDrawable3.setDither(true);
        bitmapDrawable3.setBounds(0, 0, dimension, dimension);
        bitmapDrawable3.draw(canvas);
        if (sDefaultRangeOfUninstallBadge == 0) {
            int iRound = Math.round(dimension * (-0.08499998f));
            sDefaultRangeOfUninstallBadge = iRound;
            LGLog.d("UninstallBadgeUtils", "UninstallBadge width = " + dimension + ", range = " + iRound);
        }
        BitmapDrawable bitmapDrawable4 = new BitmapDrawable(resources, bitmapCreateBitmap);
        sUninstallBadgeDrawable = bitmapDrawable4;
        return bitmapDrawable4;
    }

    public static BitmapDrawable createSwivelUninstallBadgeDrawable(Context context) {
        Resources resources = context.getResources();
        int dimension = (int) resources.getDimension(R.dimen.swivel_unistall_button_size);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) resources.getDrawable(R.drawable.btn_homescreen_close_normal_swivel, null);
        if (bitmapDrawable == null) {
            return null;
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(dimension, dimension, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        bitmapDrawable.setFilterBitmap(true);
        bitmapDrawable.setDither(true);
        bitmapDrawable.setBounds(0, 0, dimension, dimension);
        bitmapDrawable.draw(canvas);
        if (sDefaultRangeOfUninstallBadge == 0) {
            int iRound = Math.round(dimension * (-0.08499998f));
            sDefaultRangeOfUninstallBadge = iRound;
            LGLog.d("UninstallBadgeUtils", "UninstallBadge width = " + dimension + ", range = " + iRound);
        }
        BitmapDrawable bitmapDrawable2 = new BitmapDrawable(resources, bitmapCreateBitmap);
        sSwivelUninstallBadgeDrawable = bitmapDrawable2;
        return bitmapDrawable2;
    }

    public static void initUninstallBadge() {
        sUninstallBadgeDrawable = null;
        sSwivelUninstallBadgeDrawable = null;
        sDefaultRangeOfUninstallBadge = 0;
        sRangeOfUninstallBadge = -1;
        sTempRange = -1;
        sUninstallBadgeRect.set(0, 0, 0, 0);
    }

    public static boolean getUninstallBadgeTouched(MotionEvent event, Context context, Drawable drawable, Rect rect) {
        return getUninstallBadgeTouched(event, context, drawable, rect, 0);
    }

    public static boolean getUninstallBadgeTouched(MotionEvent event, Context context, Drawable drawable, Rect rect, int scrollX) {
        if (event.getAction() != 1) {
            return false;
        }
        float f = context.getResources().getFloat(R.dimen.lg_badge_clickable_ratio);
        if (drawable == null || rect == null) {
            return false;
        }
        int i = (int) ((rect.left - scrollX) / f);
        int i2 = (int) ((rect.right - scrollX) * f);
        int i3 = (int) (rect.top / f);
        int i4 = (int) (rect.bottom * f);
        LGLog.d("UninstallBadeUtils", "UninstallBadge Rect = " + rect);
        return event.getX() > ((float) i) && event.getX() < ((float) i2) && event.getY() > ((float) i3) && event.getY() < ((float) i4);
    }

    public static Rect getUninstallBadgeRect() {
        return sUninstallBadgeRect;
    }

    public static void setUninstallBadgeRect(Rect rect) {
        if (rect != null) {
            sUninstallBadgeRect.set(rect);
        }
    }
}
