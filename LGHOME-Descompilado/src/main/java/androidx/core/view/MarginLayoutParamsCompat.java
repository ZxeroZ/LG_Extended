package androidx.core.view;

import android.os.Build;
import android.view.ViewGroup;

/* JADX INFO: loaded from: classes.dex */
public final class MarginLayoutParamsCompat {
    public static int getMarginStart(ViewGroup.MarginLayoutParams lp) {
        if (Build.VERSION.SDK_INT >= 17) {
            return lp.getMarginStart();
        }
        return lp.leftMargin;
    }

    public static int getMarginEnd(ViewGroup.MarginLayoutParams lp) {
        if (Build.VERSION.SDK_INT >= 17) {
            return lp.getMarginEnd();
        }
        return lp.rightMargin;
    }

    public static void setMarginStart(ViewGroup.MarginLayoutParams lp, int marginStart) {
        if (Build.VERSION.SDK_INT >= 17) {
            lp.setMarginStart(marginStart);
        } else {
            lp.leftMargin = marginStart;
        }
    }

    public static void setMarginEnd(ViewGroup.MarginLayoutParams lp, int marginEnd) {
        if (Build.VERSION.SDK_INT >= 17) {
            lp.setMarginEnd(marginEnd);
        } else {
            lp.rightMargin = marginEnd;
        }
    }

    public static boolean isMarginRelative(ViewGroup.MarginLayoutParams lp) {
        if (Build.VERSION.SDK_INT >= 17) {
            return lp.isMarginRelative();
        }
        return false;
    }

    public static int getLayoutDirection(ViewGroup.MarginLayoutParams lp) {
        int layoutDirection = Build.VERSION.SDK_INT >= 17 ? lp.getLayoutDirection() : 0;
        if (layoutDirection == 0 || layoutDirection == 1) {
            return layoutDirection;
        }
        return 0;
    }

    public static void setLayoutDirection(ViewGroup.MarginLayoutParams lp, int layoutDirection) {
        if (Build.VERSION.SDK_INT >= 17) {
            lp.setLayoutDirection(layoutDirection);
        }
    }

    public static void resolveLayoutDirection(ViewGroup.MarginLayoutParams lp, int layoutDirection) {
        if (Build.VERSION.SDK_INT >= 17) {
            lp.resolveLayoutDirection(layoutDirection);
        }
    }

    private MarginLayoutParamsCompat() {
    }
}
