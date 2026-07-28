package com.android.launcher3;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;
import com.android.launcher3.DropTarget;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class InfoDropTarget extends ButtonDropTarget {
    private int mControlType;

    public InfoDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mControlType = 0;
    }

    @Override // com.android.launcher3.ButtonDropTarget, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mHoverColor = getResources().getColor(R.color.info_target_hover_tint);
        setDrawable(R.drawable.ic_info_launcher);
    }

    public static boolean startDetailsActivityForInfo(ItemInfo info, Launcher launcher, Rect sourceBounds, Bundle opts) {
        ComponentName component;
        if (info instanceof AppInfo) {
            component = ((AppInfo) info).componentName;
        } else if (info instanceof ShortcutInfo) {
            component = ((ShortcutInfo) info).intent.getComponent();
        } else if (info instanceof PendingAddItemInfo) {
            component = ((PendingAddItemInfo) info).componentName;
        } else {
            component = info instanceof LauncherAppWidgetInfo ? ((LauncherAppWidgetInfo) info).providerName : null;
        }
        if (component == null) {
            return false;
        }
        try {
            LauncherAppsCompat.getInstance(launcher).showAppDetailsForProfile(component, info.user, sourceBounds, opts);
            return true;
        } catch (ActivityNotFoundException | SecurityException e) {
            Toast.makeText(launcher, R.string.activity_not_found, 0).show();
            Log.e(DropTarget.TAG, "Unable to launch settings", e);
            return false;
        }
    }

    public static void startDetailsActivityForInfo(Object info, Launcher launcher) {
        ComponentName component;
        UserHandle userHandleMyUserHandle;
        if (info instanceof AppInfo) {
            component = ((AppInfo) info).componentName;
        } else if (info instanceof ShortcutInfo) {
            component = ((ShortcutInfo) info).intent.getComponent();
        } else {
            component = info instanceof PendingAddItemInfo ? ((PendingAddItemInfo) info).componentName : null;
        }
        if (info instanceof ItemInfo) {
            userHandleMyUserHandle = ((ItemInfo) info).user;
        } else {
            userHandleMyUserHandle = Process.myUserHandle();
        }
        if (component != null) {
            launcher.startApplicationDetailsActivity(component, userHandleMyUserHandle);
        }
    }

    @Override // com.android.launcher3.ButtonDropTarget
    protected boolean supportsDrop(DragSource source, Object info) {
        return source.supportsAppInfoDropTarget() && supportsDrop(getContext(), info);
    }

    public static boolean supportsDrop(Context context, Object info) {
        return (info instanceof AppInfo) || (info instanceof PendingAddItemInfo);
    }

    @Override // com.android.launcher3.ButtonDropTarget
    protected void completeDrop(DropTarget.DragObject d) {
        startDetailsActivityForInfo(d.dragInfo, this.mLauncher);
    }

    @Override // com.android.launcher3.ButtonDropTarget
    public LauncherLogProto.Target getDropTargetForLogging() {
        LauncherLogProto.Target targetNewTarget = LoggerUtils.newTarget(2);
        targetNewTarget.controlType = this.mControlType;
        return targetNewTarget;
    }
}
