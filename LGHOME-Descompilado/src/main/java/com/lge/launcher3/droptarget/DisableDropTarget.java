package com.lge.launcher3.droptarget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.AttributeSet;
import android.util.Pair;
import com.android.launcher3.ButtonDropTarget;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.UninstallDropTarget;
import com.android.launcher3.allapps.AllAppsList;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsItemInfo;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.util.UserUtils;
import com.lge.launcher3.util.VplApps;

/* JADX INFO: loaded from: classes.dex */
public class DisableDropTarget extends ButtonDropTarget {
    private int mControlType;

    public DisableDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DisableDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mControlType = 0;
    }

    @Override // com.android.launcher3.ButtonDropTarget, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mHoverColor = getResources().getColor(R.color.cancel_target_hover_tint);
        setDrawable(R.drawable.ic_homescreen_uninstall);
    }

    @Override // com.android.launcher3.ButtonDropTarget
    protected boolean supportsDrop(DragSource source, Object info) {
        return !isWidgetTypeItemInfo(info) && supportsDrop(getContext(), info);
    }

    public static boolean supportsDrop(Context context, Object info) {
        Bundle userRestrictions = ((UserManager) context.getSystemService("user")).getUserRestrictions();
        if (userRestrictions.getBoolean("no_control_apps", false) || userRestrictions.getBoolean("no_uninstall_apps", false)) {
            return false;
        }
        Pair<ComponentName, Integer> appInfoFlags = getAppInfoFlags(info);
        return isVplApp(appInfoFlags) || isSystemDualApp(context, info, appInfoFlags);
    }

    private static Pair<ComponentName, Integer> getAppInfoFlags(Object item) {
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

    private static boolean isVplApp(Pair<ComponentName, Integer> componentInfo) {
        if (componentInfo != null) {
            return VplApps.contains(((ComponentName) componentInfo.first).getPackageName());
        }
        return false;
    }

    private static boolean isSystemDualApp(Context context, Object info, Pair<ComponentName, Integer> componentInfo) {
        if (info == null || componentInfo == null) {
            return false;
        }
        UserHandle userHandle = info instanceof ItemInfo ? ((ItemInfo) info).user : null;
        return (userHandle != null && UserUtils.isSecondApplication(context, userHandle.getIdentifier())) && ((((Integer) componentInfo.second).intValue() & 1) == 0);
    }

    @Override // com.android.launcher3.ButtonDropTarget, com.android.launcher3.DropTarget
    public void onDrop(DropTarget.DragObject d) {
        if (d.dragSource instanceof UninstallDropTarget.UninstallSource) {
            ((UninstallDropTarget.UninstallSource) d.dragSource).deferCompleteDropAfterUninstallActivity();
        }
        super.onDrop(d);
    }

    @Override // com.android.launcher3.ButtonDropTarget
    protected void completeDrop(final DropTarget.DragObject d) {
        final Pair<ComponentName, Integer> appInfoFlags = getAppInfoFlags(d.dragInfo);
        final UserHandle userHandle = ((ItemInfo) d.dragInfo).user;
        if (startDisableActivity(this.mLauncher, d.dragInfo)) {
            this.mLauncher.addOnResumeCallback(new Runnable() { // from class: com.lge.launcher3.droptarget.-$$Lambda$DisableDropTarget$0l5eQfbl4UWOZPJHFcRHnKMNoX4
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$completeDrop$0$DisableDropTarget(appInfoFlags, userHandle, d);
                }
            });
        } else {
            sendDisableResult(d.dragSource, false);
        }
    }

    public /* synthetic */ void lambda$completeDrop$0$DisableDropTarget(Pair pair, UserHandle userHandle, DropTarget.DragObject dragObject) {
        sendDisableResult(dragObject.dragSource, !AllAppsList.packageHasActivities(getContext(), ((ComponentName) pair.first).getPackageName(), userHandle));
    }

    @Override // com.android.launcher3.ButtonDropTarget
    public LauncherLogProto.Target getDropTargetForLogging() {
        LauncherLogProto.Target targetNewTarget = LoggerUtils.newTarget(2);
        targetNewTarget.controlType = this.mControlType;
        return targetNewTarget;
    }

    public static boolean startDisableActivity(Launcher launcher, Object info) {
        Pair<ComponentName, Integer> appInfoFlags = getAppInfoFlags(info);
        return startApplicationDisableActivity(launcher, (ComponentName) appInfoFlags.first, ((Integer) appInfoFlags.second).intValue(), ((ItemInfo) info).user);
    }

    private static boolean startApplicationDisableActivity(Context context, ComponentName componentName, int flags, UserHandle user) {
        if ((flags & 1) != 0) {
            return false;
        }
        Intent intent = new Intent("com.lge.launcher3.intent.action.SHOW_DISABLE_DIALOG", Uri.fromParts(AppNotifierManager.ExtraSpec.USAGE_PACKAGE, componentName.getPackageName(), componentName.getClassName()));
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setFlags(545259520);
        if (user != null) {
            intent.putExtra("android.intent.extra.USER", user);
        }
        context.startActivity(intent);
        return true;
    }

    void sendDisableResult(DragSource target, boolean result) {
        if (target instanceof UninstallDropTarget.UninstallSource) {
            ((UninstallDropTarget.UninstallSource) target).onUninstallActivityReturned(result);
        }
    }
}
