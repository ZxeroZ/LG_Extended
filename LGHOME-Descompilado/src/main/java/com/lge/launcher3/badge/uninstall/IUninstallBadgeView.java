package com.lge.launcher3.badge.uninstall;

import android.graphics.Rect;
import com.lge.launcher3.badge.uninstall.UninstallBadgeUtils;

/* JADX INFO: loaded from: classes.dex */
public interface IUninstallBadgeView {
    void getGlobalVisibleRectForBadge(Rect r);

    UninstallBadgeUtils.UninstallType getUninstallType();

    boolean hasUnistallBadge();

    void invalidateUninstallBadge(boolean visible, boolean enableAni);

    boolean isInFolder();

    boolean isTouchedUninstallBadge();

    boolean isUninstallable();

    boolean isUninstallableAllApps();

    void setUninstallType(UninstallBadgeUtils.UninstallType uninstallType);

    void setVisibilityForUninstallBadge(boolean visible, int delay);
}
