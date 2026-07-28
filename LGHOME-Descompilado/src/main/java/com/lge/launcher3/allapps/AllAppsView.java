package com.lge.launcher3.allapps;

import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.model.data.AppInfo;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public interface AllAppsView {
    void addApps(ArrayList<AppInfo> list, int op);

    void dumpState();

    boolean isAnimating();

    boolean isVisible();

    void removeApps(ArrayList<AppInfo> list);

    void reset();

    void setApps(ArrayList<AppInfo> list);

    void setup(DragController dragController);

    void surrender();

    void updateApps(ArrayList<AppInfo> list);

    void zoom(float zoom, boolean animate);
}
