package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.AttributeSet;
import android.util.Pair;
import com.android.launcher3.DropTarget;
import com.android.launcher3.allapps.AllAppsList;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsItemInfo;
import com.lge.launcher3.droptarget.ButtonDropTargetUtils;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class UninstallDropTarget extends ButtonDropTarget {
    public static final int FROM_DROPTARGET = 2;
    public static final int FROM_UNINSTALLMODE = 1;
    private int mControlType;

    public interface UninstallSource {
        void deferCompleteDropAfterUninstallActivity();

        void onUninstallActivityReturned(boolean result);
    }

    public UninstallDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UninstallDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mControlType = 0;
    }

    @Override // com.android.launcher3.ButtonDropTarget, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mHoverColor = getResources().getColor(R.color.uninstall_target_hover_tint);
        setDrawable(R.drawable.ic_homescreen_uninstall);
    }

    @Override // com.android.launcher3.ButtonDropTarget
    protected boolean supportsDrop(DragSource source, Object info) {
        return !isWidgetTypeItemInfo(info) && supportsDrop(getContext(), info, 2);
    }

    public static boolean supportsDrop(Context context, Object info, int type) {
        if (Build.VERSION.SDK_INT >= 18) {
            Bundle userRestrictions = ((UserManager) context.getSystemService("user")).getUserRestrictions();
            if (userRestrictions.getBoolean("no_control_apps", false) || userRestrictions.getBoolean("no_uninstall_apps", false)) {
                return false;
            }
        }
        Pair<ComponentName, Integer> appInfoFlags = getAppInfoFlags(info);
        if (appInfoFlags != null) {
            ComponentName componentName = (ComponentName) appInfoFlags.first;
            if (UninstallModeManager.getInstance(context).isRestrictPackage(componentName, type)) {
                LGLog.d(DropTarget.TAG, "Admin & MDM : not be able to uninstall!. componentName = " + componentName);
                return false;
            }
        }
        return ((LGHomeFeature.isEnableDefaultHome() ? ButtonDropTargetUtils.isShortcutWithApplicationType(context, info) : false) || appInfoFlags == null || (((Integer) appInfoFlags.second).intValue() & 1) == 0) ? false : true;
    }

    public static boolean supportsDrop(Context context, Object info) {
        return supportsDrop(context, info, 1);
    }

    protected static Pair<ComponentName, Integer> getAppInfoFlags(Object item) {
        PendingAddShortcutInfo pendingAddShortcutInfo;
        ComponentName componentName;
        if (item instanceof AllAppsItemInfo) {
            if (((AllAppsItemInfo) item).getFolderInfo() != null) {
                return null;
            }
            AppInfo appInfo = (AppInfo) item;
            return Pair.create(appInfo.componentName, Integer.valueOf(appInfo.flags));
        }
        if (item instanceof AppInfo) {
            AppInfo appInfo2 = (AppInfo) item;
            return Pair.create(appInfo2.componentName, Integer.valueOf(appInfo2.flags));
        }
        if (item instanceof ShortcutInfo) {
            ShortcutInfo shortcutInfo = (ShortcutInfo) item;
            ComponentName targetComponent = shortcutInfo.getTargetComponent();
            if (shortcutInfo.itemType == 0 && targetComponent != null) {
                return Pair.create(targetComponent, Integer.valueOf(shortcutInfo.flags));
            }
        } else if (item instanceof PendingAddWidgetInfo) {
            PendingAddWidgetInfo pendingAddWidgetInfo = (PendingAddWidgetInfo) item;
            ComponentName componentName2 = pendingAddWidgetInfo.componentName;
            if (componentName2 != null) {
                return Pair.create(componentName2, Integer.valueOf(pendingAddWidgetInfo.flags));
            }
        } else if ((item instanceof PendingAddShortcutInfo) && (componentName = (pendingAddShortcutInfo = (PendingAddShortcutInfo) item).componentName) != null) {
            return Pair.create(componentName, Integer.valueOf(pendingAddShortcutInfo.flags));
        }
        return null;
    }

    @Override // com.android.launcher3.ButtonDropTarget, com.android.launcher3.DropTarget
    public void onDrop(DropTarget.DragObject d) {
        if (d.dragSource instanceof UninstallSource) {
            ((UninstallSource) d.dragSource).deferCompleteDropAfterUninstallActivity();
        }
        super.onDrop(d);
    }

    @Override // com.android.launcher3.ButtonDropTarget
    protected void completeDrop(final DropTarget.DragObject d) {
        final Pair<ComponentName, Integer> appInfoFlags = getAppInfoFlags(d.dragInfo);
        final UserHandle userHandle = ((ItemInfo) d.dragInfo).user;
        if (startUninstallActivity(this.mLauncher, d.dragInfo)) {
            this.mLauncher.addOnResumeCallback(new Runnable() { // from class: com.android.launcher3.-$$Lambda$UninstallDropTarget$-aiHBmffzhpnKdJngJ73csBAqds
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$completeDrop$0$UninstallDropTarget(appInfoFlags, userHandle, d);
                }
            });
        } else {
            sendUninstallResult(d.dragSource, false);
        }
    }

    public /* synthetic */ void lambda$completeDrop$0$UninstallDropTarget(Pair pair, UserHandle userHandle, DropTarget.DragObject dragObject) {
        sendUninstallResult(dragObject.dragSource, !AllAppsList.packageHasActivities(getContext(), ((ComponentName) pair.first).getPackageName(), userHandle));
    }

    @Override // com.android.launcher3.ButtonDropTarget
    public LauncherLogProto.Target getDropTargetForLogging() {
        LauncherLogProto.Target targetNewTarget = LoggerUtils.newTarget(2);
        targetNewTarget.controlType = this.mControlType;
        return targetNewTarget;
    }

    public static boolean startUninstallActivity(Launcher launcher, Object info) {
        Pair<ComponentName, Integer> appInfoFlags = getAppInfoFlags(info);
        return launcher.startApplicationUninstallActivity((ComponentName) appInfoFlags.first, ((Integer) appInfoFlags.second).intValue(), ((ItemInfo) info).user);
    }

    protected void sendUninstallResult(DragSource target, boolean result) {
        if (target instanceof UninstallSource) {
            ((UninstallSource) target).onUninstallActivityReturned(result);
        }
    }
}
