package com.android.launcher3.util;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Insets;
import android.graphics.Rect;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import com.android.launcher3.ResourceUtils;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.DisplayController;
import com.lge.launcher3.config.LauncherConst;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class WindowManagerCompat {
    public static final int MIN_TABLET_WIDTH = 600;

    public static Set<WindowMetrics> getDisplayProfiles(Context windowContext, Collection<DisplayController.PortraitSize> allDisplaySizes, int densityDpi, boolean consumeTaskBar) {
        Insets insetsOf;
        Insets insetsOf2;
        Insets insets;
        WindowInsets windowInsets = ((WindowManager) windowContext.getSystemService(WindowManager.class)).getMaximumWindowMetrics().getWindowInsets();
        boolean z = ResourceUtils.getIntegerByName("config_navBarInteractionMode", windowContext.getResources(), -1) != 0;
        WindowInsets.Builder builder = new WindowInsets.Builder(windowInsets);
        HashSet hashSet = new HashSet();
        for (DisplayController.PortraitSize portraitSize : allDisplaySizes) {
            int iDpiFromPx = (int) Utilities.dpiFromPx(portraitSize.width, densityDpi);
            boolean z2 = iDpiFromPx >= 600;
            if ((!z2 || consumeTaskBar) && z) {
                Insets insetsOf3 = Insets.of(0, 0, 0, getSystemResource(windowContext, ResourceUtils.NAVBAR_SIZE, iDpiFromPx));
                if (z2) {
                    insetsOf2 = Insets.of(0, 0, 0, getSystemResource(windowContext, "navigation_bar_height_landscape", iDpiFromPx));
                } else {
                    insetsOf2 = Insets.of(0, 0, getSystemResource(windowContext, ResourceUtils.NAVBAR_LANDSCAPE_LEFT_RIGHT_SIZE, iDpiFromPx), 0);
                }
                insets = insetsOf2;
                insetsOf = insetsOf3;
                hashSet.add(new WindowMetrics(new Rect(0, 0, portraitSize.width, portraitSize.height), builder.setInsets(WindowInsets.Type.navigationBars(), insetsOf).build()));
                hashSet.add(new WindowMetrics(new Rect(0, 0, portraitSize.height, portraitSize.width), builder.setInsets(WindowInsets.Type.navigationBars(), insets).build()));
            } else {
                insetsOf = Insets.of(0, 0, 0, 0);
                insets = insetsOf;
                hashSet.add(new WindowMetrics(new Rect(0, 0, portraitSize.width, portraitSize.height), builder.setInsets(WindowInsets.Type.navigationBars(), insetsOf).build()));
                hashSet.add(new WindowMetrics(new Rect(0, 0, portraitSize.height, portraitSize.width), builder.setInsets(WindowInsets.Type.navigationBars(), insets).build()));
            }
        }
        return hashSet;
    }

    private static int getSystemResource(Context context, String key, int swDp) {
        int identifier = context.getResources().getIdentifier(key, "dimen", LauncherConst.PACKAGE_NAME_NATIVE);
        if (identifier <= 0) {
            return 0;
        }
        Configuration configuration = new Configuration();
        configuration.smallestScreenWidthDp = swDp;
        return context.createConfigurationContext(configuration).getResources().getDimensionPixelSize(identifier);
    }
}
