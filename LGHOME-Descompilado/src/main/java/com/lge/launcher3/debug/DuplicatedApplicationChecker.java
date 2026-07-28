package com.lge.launcher3.debug;

import android.content.ComponentName;
import android.util.Pair;
import android.view.View;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.model.data.FolderInfo;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class DuplicatedApplicationChecker {
    private static final boolean DEBUG = false;
    private static final boolean ENABLE = false;
    private static HashMap<String, Pair<View, Throwable>> mViewTable = new HashMap<>();

    public static void addToFolder(FolderInfo info, ShortcutInfo item) {
    }

    public static void addView(View view) {
    }

    public static void removeView(View view) {
    }

    private DuplicatedApplicationChecker() {
    }

    public static void init() {
        mViewTable.clear();
    }

    private static String getKey(ShortcutInfo item) {
        ComponentName targetComponent;
        if (item == null || (targetComponent = item.getTargetComponent()) == null) {
            return null;
        }
        return targetComponent.flattenToShortString() + ":" + item.user;
    }
}
