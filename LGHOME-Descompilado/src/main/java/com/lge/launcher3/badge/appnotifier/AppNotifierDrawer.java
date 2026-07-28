package com.lge.launcher3.badge.appnotifier;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import com.android.launcher3.Utilities;
import com.lge.launcher3.badge.appnotifier.AppNotifierBadgeDrawable;
import java.text.NumberFormat;

/* JADX INFO: loaded from: classes.dex */
public class AppNotifierDrawer {
    public static final AppNotifierDrawer NULL = new AppNotifierDrawer() { // from class: com.lge.launcher3.badge.appnotifier.AppNotifierDrawer.1
        @Override // com.lge.launcher3.badge.appnotifier.AppNotifierDrawer
        public BitmapDrawable createBadgeDrawable(Context context, int count) {
            return null;
        }

        @Override // com.lge.launcher3.badge.appnotifier.AppNotifierDrawer
        public boolean isRegistered() {
            return false;
        }
    };

    public boolean isRegistered() {
        return true;
    }

    AppNotifierDrawer() {
    }

    public BitmapDrawable createBadgeDrawable(Context context, int count) {
        if (count <= 0) {
            return null;
        }
        String appNotifierMaxCountString = getAppNotifierMaxCountString(count);
        if (!AppNotifierManager.getInstance(context).isShowBadgeNumber()) {
            appNotifierMaxCountString = "N";
        }
        return new AppNotifierBadgeDrawable.Builder().build(context, appNotifierMaxCountString);
    }

    public static String getAppNotifierMaxCountString(int count) {
        StringBuilder sb = new StringBuilder();
        if (count > AppNotifierConstant.DEFAULT_MAX_NUMBER) {
            if (Utilities.isArabicFarsi()) {
                sb.append("+" + NumberFormat.getInstance().format(AppNotifierConstant.DEFAULT_MAX_NUMBER));
            } else {
                sb.append(AppNotifierConstant.DEFAULT_MAX_NUMBER + "+");
            }
        } else if (Utilities.isArabicFarsi()) {
            sb.append(NumberFormat.getInstance().format(count));
        } else {
            sb.append(count);
        }
        return sb.toString();
    }
}
