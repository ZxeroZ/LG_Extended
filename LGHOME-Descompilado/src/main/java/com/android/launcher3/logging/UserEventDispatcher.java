package com.android.launcher3.logging;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Utilities;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.userevent.LauncherLogProto;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.InstantAppResolver;
import com.android.launcher3.util.ResourceBasedOverride;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.lge.launcher3.R;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class UserEventDispatcher implements ResourceBasedOverride {
    private static final boolean IS_VERBOSE = false;
    private static final int MAXIMUM_VIEW_HIERARCHY_LEVEL = 5;
    private static final String TAG = "UserEvent";
    private static final String UUID_STORAGE = "uuid";
    private long mActionDurationMillis;
    private boolean mAppOrTaskLaunch;
    private UserEventDelegate mDelegate;
    private long mElapsedContainerMillis;
    private long mElapsedSessionMillis;
    protected InstantAppResolver mInstantAppResolver;
    private boolean mIsInLandscapeMode;
    private boolean mIsInMultiWindowMode;
    private boolean mPreviousHomeGesture;
    private boolean mSessionStarted;
    private String mUuidStr;

    public interface LogContainerProvider {
        void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent);
    }

    public interface UserEventDelegate {
        void modifyUserEvent(LauncherLogProto.LauncherEvent event);
    }

    public void logActionTip(int actionType, int viewType) {
    }

    protected void onFillInLogContainerData(ItemInfo itemInfo, LauncherLogProto.Target target, ArrayList<LauncherLogProto.Target> targets) {
    }

    public static UserEventDispatcher newInstance(Context context) {
        SharedPreferences devicePrefs = Utilities.getDevicePrefs(context);
        String string = devicePrefs.getString(UUID_STORAGE, null);
        if (string == null) {
            string = UUID.randomUUID().toString();
            devicePrefs.edit().putString(UUID_STORAGE, string).apply();
        }
        UserEventDispatcher userEventDispatcher = (UserEventDispatcher) ResourceBasedOverride.Overrides.getObject(UserEventDispatcher.class, context.getApplicationContext(), R.string.user_event_dispatcher_class);
        userEventDispatcher.mUuidStr = string;
        userEventDispatcher.mInstantAppResolver = InstantAppResolver.newInstance(context);
        return userEventDispatcher;
    }

    public static UserEventDispatcher newInstance(Context context, DeviceProfile dp, UserEventDelegate delegate) {
        SharedPreferences devicePrefs = Utilities.getDevicePrefs(context);
        String string = devicePrefs.getString(UUID_STORAGE, null);
        if (string == null) {
            string = UUID.randomUUID().toString();
            devicePrefs.edit().putString(UUID_STORAGE, string).apply();
        }
        UserEventDispatcher userEventDispatcher = (UserEventDispatcher) Utilities.getOverrideObject(UserEventDispatcher.class, context.getApplicationContext(), R.string.user_event_dispatcher_class);
        userEventDispatcher.mDelegate = delegate;
        userEventDispatcher.mIsInLandscapeMode = dp.isVerticalBarLayout();
        userEventDispatcher.mIsInMultiWindowMode = dp.isMultiWindowMode;
        userEventDispatcher.mUuidStr = string;
        userEventDispatcher.mInstantAppResolver = InstantAppResolver.newInstance(context);
        return userEventDispatcher;
    }

    public static UserEventDispatcher newInstance(Context context, DeviceProfile dp) {
        return newInstance(context, dp, null);
    }

    public static LogContainerProvider getLaunchProviderRecursive(View v) {
        if (v != null) {
            ViewParent parent = v.getParent();
            int i = 5;
            while (parent != null) {
                int i2 = i - 1;
                if (i <= 0) {
                    break;
                }
                if (parent instanceof LogContainerProvider) {
                    return (LogContainerProvider) parent;
                }
                parent = parent.getParent();
                i = i2;
            }
        }
        return null;
    }

    protected boolean fillInLogContainerData(LauncherLogProto.LauncherEvent event, View v) {
        LogContainerProvider launchProviderRecursive = getLaunchProviderRecursive(v);
        if (v == null || !(v.getTag() instanceof ItemInfo) || launchProviderRecursive == null) {
            return false;
        }
        launchProviderRecursive.fillInLogContainerData(v, (ItemInfo) v.getTag(), event.srcTarget[0], event.srcTarget[1]);
        return true;
    }

    @Deprecated
    public void logAppLaunch(View v, Intent intent) {
        LauncherLogProto.LauncherEvent launcherEventNewLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(0), LoggerUtils.newItemTarget(v, this.mInstantAppResolver), LoggerUtils.newTarget(3));
        if (fillInLogContainerData(launcherEventNewLauncherEvent, v)) {
            UserEventDelegate userEventDelegate = this.mDelegate;
            if (userEventDelegate != null) {
                userEventDelegate.modifyUserEvent(launcherEventNewLauncherEvent);
            }
            fillIntentInfo(launcherEventNewLauncherEvent.srcTarget[0], intent);
        }
        dispatchUserEvent(launcherEventNewLauncherEvent, intent);
        this.mAppOrTaskLaunch = true;
    }

    public void logTaskLaunchOrDismiss(int action, int direction, int taskIndex, ComponentKey componentKey) {
        LauncherLogProto.LauncherEvent launcherEventNewLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), LoggerUtils.newTarget(1));
        if (action == 3 || action == 4) {
            launcherEventNewLauncherEvent.action.dir = direction;
        }
        launcherEventNewLauncherEvent.srcTarget[0].itemType = 9;
        launcherEventNewLauncherEvent.srcTarget[0].pageIndex = taskIndex;
        fillComponentInfo(launcherEventNewLauncherEvent.srcTarget[0], componentKey.componentName);
        dispatchUserEvent(launcherEventNewLauncherEvent, null);
        this.mAppOrTaskLaunch = true;
    }

    protected void fillIntentInfo(LauncherLogProto.Target target, Intent intent) {
        target.intentHash = intent.hashCode();
        fillComponentInfo(target, intent.getComponent());
    }

    private void fillComponentInfo(LauncherLogProto.Target target, ComponentName cn) {
        if (cn != null) {
            target.packageNameHash = (this.mUuidStr + cn.getPackageName()).hashCode();
            target.componentHash = (this.mUuidStr + cn.flattenToString()).hashCode();
        }
    }

    public void logNotificationLaunch(View v, PendingIntent intent) {
        LauncherLogProto.LauncherEvent launcherEventNewLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(0), LoggerUtils.newItemTarget(v, this.mInstantAppResolver), LoggerUtils.newTarget(3));
        if (fillInLogContainerData(launcherEventNewLauncherEvent, v)) {
            launcherEventNewLauncherEvent.srcTarget[0].packageNameHash = (this.mUuidStr + intent.getCreatorPackage()).hashCode();
        }
        dispatchUserEvent(launcherEventNewLauncherEvent, null);
    }

    public void logActionCommand(int command, LauncherLogProto.Target srcTarget) {
        logActionCommand(command, srcTarget, (LauncherLogProto.Target) null);
    }

    public void logActionCommand(int command, int srcContainerType, int dstContainerType) {
        logActionCommand(command, LoggerUtils.newContainerTarget(srcContainerType), dstContainerType >= 0 ? LoggerUtils.newContainerTarget(dstContainerType) : null);
    }

    public void logActionCommand(int command, LauncherLogProto.Target srcTarget, LauncherLogProto.Target dstTarget) {
        LauncherLogProto.LauncherEvent launcherEventNewLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newCommandAction(command), srcTarget);
        if (command == 5 && (this.mAppOrTaskLaunch || !this.mSessionStarted)) {
            this.mSessionStarted = false;
            return;
        }
        if (dstTarget != null) {
            launcherEventNewLauncherEvent.destTarget = new LauncherLogProto.Target[1];
            launcherEventNewLauncherEvent.destTarget[0] = dstTarget;
            launcherEventNewLauncherEvent.action.isStateChange = true;
        }
        dispatchUserEvent(launcherEventNewLauncherEvent, null);
    }

    public void logActionCommand(int command, View itemView, int srcContainerType) {
        LauncherLogProto.LauncherEvent launcherEventNewLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newCommandAction(command), LoggerUtils.newItemTarget(itemView, this.mInstantAppResolver), LoggerUtils.newTarget(3));
        if (fillInLogContainerData(launcherEventNewLauncherEvent, itemView)) {
            launcherEventNewLauncherEvent.srcTarget[0].type = 3;
            launcherEventNewLauncherEvent.srcTarget[0].containerType = srcContainerType;
        }
        dispatchUserEvent(launcherEventNewLauncherEvent, null);
    }

    public void logActionOnControl(int action, int controlType) {
        logActionOnControl(action, controlType, (View) null, -1);
    }

    public void logActionOnControl(int action, int controlType, int parentContainerType) {
        logActionOnControl(action, controlType, (View) null, parentContainerType);
    }

    public void logActionOnControl(int action, int controlType, View controlInContainer) {
        logActionOnControl(action, controlType, controlInContainer, -1);
    }

    public void logActionOnControl(int action, int controlType, int parentContainer, int grandParentContainer) {
        dispatchUserEvent(LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), LoggerUtils.newControlTarget(controlType), LoggerUtils.newContainerTarget(parentContainer), LoggerUtils.newContainerTarget(grandParentContainer)), null);
    }

    public void logActionOnControl(int action, int controlType, View controlInContainer, int parentContainerType) {
        LauncherLogProto.LauncherEvent launcherEventNewLauncherEvent;
        if (controlInContainer == null && parentContainerType < 0) {
            launcherEventNewLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), LoggerUtils.newTarget(2));
        } else {
            launcherEventNewLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), LoggerUtils.newTarget(2), LoggerUtils.newTarget(3));
        }
        launcherEventNewLauncherEvent.srcTarget[0].controlType = controlType;
        if (controlInContainer != null) {
            fillInLogContainerData(launcherEventNewLauncherEvent, controlInContainer);
        }
        if (parentContainerType >= 0) {
            launcherEventNewLauncherEvent.srcTarget[1].containerType = parentContainerType;
        }
        if (action == 2) {
            launcherEventNewLauncherEvent.actionDurationMillis = SystemClock.uptimeMillis() - this.mActionDurationMillis;
        }
        dispatchUserEvent(launcherEventNewLauncherEvent, null);
    }

    public void logActionTapOutside(LauncherLogProto.Target target) {
        LauncherLogProto.LauncherEvent launcherEventNewLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(0), target);
        launcherEventNewLauncherEvent.action.isOutside = true;
        dispatchUserEvent(launcherEventNewLauncherEvent, null);
    }

    public void logActionBounceTip(int containerType) {
        LauncherLogProto.LauncherEvent launcherEventNewLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newAction(3), LoggerUtils.newContainerTarget(containerType));
        launcherEventNewLauncherEvent.srcTarget[0].tipType = 1;
        dispatchUserEvent(launcherEventNewLauncherEvent, null);
    }

    public void logActionOnContainer(int action, int dir, int containerType) {
        logActionOnContainer(action, dir, containerType, 0);
    }

    public void logActionOnContainer(int action, int dir, int containerType, int pageIndex) {
        LauncherLogProto.LauncherEvent launcherEventNewLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), LoggerUtils.newContainerTarget(containerType));
        launcherEventNewLauncherEvent.action.dir = dir;
        launcherEventNewLauncherEvent.srcTarget[0].pageIndex = pageIndex;
        dispatchUserEvent(launcherEventNewLauncherEvent, null);
    }

    public void logStateChangeAction(int action, int dir, int downX, int downY, int srcChildTargetType, int srcParentContainerType, int dstContainerType, int pageIndex) {
        LauncherLogProto.LauncherEvent launcherEventNewLauncherEvent;
        if (srcChildTargetType == 9) {
            launcherEventNewLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), LoggerUtils.newItemTarget(srcChildTargetType), LoggerUtils.newContainerTarget(srcParentContainerType));
        } else {
            launcherEventNewLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), LoggerUtils.newContainerTarget(srcChildTargetType), LoggerUtils.newContainerTarget(srcParentContainerType));
        }
        launcherEventNewLauncherEvent.destTarget = new LauncherLogProto.Target[1];
        launcherEventNewLauncherEvent.destTarget[0] = LoggerUtils.newContainerTarget(dstContainerType);
        launcherEventNewLauncherEvent.action.dir = dir;
        launcherEventNewLauncherEvent.action.isStateChange = true;
        launcherEventNewLauncherEvent.srcTarget[0].pageIndex = pageIndex;
        launcherEventNewLauncherEvent.srcTarget[0].spanX = downX;
        launcherEventNewLauncherEvent.srcTarget[0].spanY = downY;
        dispatchUserEvent(launcherEventNewLauncherEvent, null);
        resetElapsedContainerMillis("state changed");
    }

    public void logStateChangeAction(int action, int dir, int srcChildTargetType, int srcParentContainerType, int dstContainerType, int pageIndex) {
        LauncherLogProto.LauncherEvent launcherEventNewLauncherEvent;
        if (srcChildTargetType == 9) {
            launcherEventNewLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), LoggerUtils.newItemTarget(srcChildTargetType), LoggerUtils.newContainerTarget(srcParentContainerType));
        } else {
            launcherEventNewLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), LoggerUtils.newContainerTarget(srcChildTargetType), LoggerUtils.newContainerTarget(srcParentContainerType));
        }
        launcherEventNewLauncherEvent.destTarget = new LauncherLogProto.Target[1];
        launcherEventNewLauncherEvent.destTarget[0] = LoggerUtils.newContainerTarget(dstContainerType);
        launcherEventNewLauncherEvent.action.dir = dir;
        launcherEventNewLauncherEvent.action.isStateChange = true;
        launcherEventNewLauncherEvent.srcTarget[0].pageIndex = pageIndex;
        dispatchUserEvent(launcherEventNewLauncherEvent, null);
        resetElapsedContainerMillis("state changed");
    }

    public void logActionOnItem(int action, int dir, int itemType) {
        LauncherLogProto.Target targetNewTarget = LoggerUtils.newTarget(1);
        targetNewTarget.itemType = itemType;
        LauncherLogProto.LauncherEvent launcherEventNewLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), targetNewTarget);
        launcherEventNewLauncherEvent.action.dir = dir;
        dispatchUserEvent(launcherEventNewLauncherEvent, null);
    }

    public void logLauncherEvent(LauncherLogProto.LauncherEvent launcherEvent) {
        if (this.mPreviousHomeGesture) {
            this.mPreviousHomeGesture = false;
        }
        this.mAppOrTaskLaunch = false;
        launcherEvent.toBuilder().setElapsedContainerMillis(SystemClock.uptimeMillis() - this.mElapsedContainerMillis).setElapsedSessionMillis(SystemClock.uptimeMillis() - this.mElapsedSessionMillis).build();
        try {
            dispatchUserEvent(LauncherLogProto.LauncherEvent.parseFrom(launcherEvent.toByteArray()), null);
        } catch (InvalidProtocolBufferNanoException unused) {
            throw new RuntimeException("Cannot convert LauncherEvent from Lite to Nano version.");
        }
    }

    public void logDeepShortcutsOpen(View icon) {
        LogContainerProvider launchProviderRecursive = getLaunchProviderRecursive(icon);
        if (icon == null || !(icon.getTag() instanceof ItemInfo)) {
            return;
        }
        ItemInfo itemInfo = (ItemInfo) icon.getTag();
        LauncherLogProto.LauncherEvent launcherEventNewLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(1), LoggerUtils.newItemTarget(itemInfo, this.mInstantAppResolver), LoggerUtils.newTarget(3));
        launchProviderRecursive.fillInLogContainerData(icon, itemInfo, launcherEventNewLauncherEvent.srcTarget[0], launcherEventNewLauncherEvent.srcTarget[1]);
        dispatchUserEvent(launcherEventNewLauncherEvent, null);
        resetElapsedContainerMillis("deep shortcut open");
    }

    public void logOverviewReorder() {
        dispatchUserEvent(LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(2), LoggerUtils.newContainerTarget(1), LoggerUtils.newContainerTarget(6)), null);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r10v0, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    public void logDragNDrop(DropTarget.DragObject dragObj, View dropTargetAsView) {
        LauncherLogProto.LauncherEvent launcherEventNewLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(2), LoggerUtils.newItemTarget(dragObj.originalDragInfo, this.mInstantAppResolver), LoggerUtils.newTarget(3));
        launcherEventNewLauncherEvent.destTarget = new LauncherLogProto.Target[]{LoggerUtils.newItemTarget(dragObj.originalDragInfo, this.mInstantAppResolver), LoggerUtils.newDropTarget(dropTargetAsView)};
        dragObj.dragSource.fillInLogContainerData(null, dragObj.originalDragInfo, launcherEventNewLauncherEvent.srcTarget[0], launcherEventNewLauncherEvent.srcTarget[1]);
        if (dropTargetAsView instanceof LogContainerProvider) {
            ((LogContainerProvider) dropTargetAsView).fillInLogContainerData(null, (ItemInfo) dragObj.dragInfo, launcherEventNewLauncherEvent.destTarget[0], launcherEventNewLauncherEvent.destTarget[1]);
        }
        launcherEventNewLauncherEvent.actionDurationMillis = SystemClock.uptimeMillis() - this.mActionDurationMillis;
        dispatchUserEvent(launcherEventNewLauncherEvent, null);
    }

    public void logActionBack(boolean z, int i, int i2, boolean z2, boolean z3, int i3) {
        int i4 = 3;
        LauncherLogProto.Action actionNewCommandAction = LoggerUtils.newCommandAction(z2 ? 0 : 3);
        actionNewCommandAction.command = 1;
        if (z2) {
            i4 = 0;
        } else if (!z3) {
            i4 = 4;
        }
        actionNewCommandAction.dir = i4;
        LauncherLogProto.Target targetNewControlTarget = LoggerUtils.newControlTarget(z2 ? 11 : 19);
        targetNewControlTarget.spanX = i;
        targetNewControlTarget.spanY = i2;
        targetNewControlTarget.cardinality = z ? 1 : 0;
        dispatchUserEvent(LoggerUtils.newLauncherEvent(actionNewCommandAction, targetNewControlTarget, LoggerUtils.newContainerTarget(i3)), null);
    }

    public final void resetElapsedContainerMillis(String reason) {
        this.mElapsedContainerMillis = SystemClock.uptimeMillis();
        if (IS_VERBOSE) {
            Log.d("UserEvent", "resetElapsedContainerMillis reason=" + reason);
        }
    }

    public final void startSession() {
        this.mSessionStarted = true;
        this.mElapsedSessionMillis = SystemClock.uptimeMillis();
        this.mElapsedContainerMillis = SystemClock.uptimeMillis();
    }

    public final void setPreviousHomeGesture(boolean homeGesture) {
        this.mPreviousHomeGesture = homeGesture;
    }

    public final boolean isPreviousHomeGesture() {
        return this.mPreviousHomeGesture;
    }

    public final void resetActionDurationMillis() {
        this.mActionDurationMillis = SystemClock.uptimeMillis();
    }

    public void dispatchUserEvent(LauncherLogProto.LauncherEvent ev, Intent intent) {
        if (this.mPreviousHomeGesture) {
            this.mPreviousHomeGesture = false;
        }
        this.mAppOrTaskLaunch = false;
        ev.isInLandscapeMode = this.mIsInLandscapeMode;
        ev.isInMultiWindowMode = this.mIsInMultiWindowMode;
        ev.elapsedContainerMillis = SystemClock.uptimeMillis() - this.mElapsedContainerMillis;
        ev.elapsedSessionMillis = SystemClock.uptimeMillis() - this.mElapsedSessionMillis;
        if (IS_VERBOSE) {
            String str = "\n-----------------------------------------------------\naction:" + LoggerUtils.getActionStr(ev.action);
            if (ev.srcTarget != null && ev.srcTarget.length > 0) {
                str = str + "\n Source " + getTargetsStr(ev.srcTarget);
            }
            if (ev.destTarget != null && ev.destTarget.length > 0) {
                str = str + "\n Destination " + getTargetsStr(ev.destTarget);
            }
            Log.d("UserEvent", (((str + String.format(Locale.US, "\n Elapsed container %d ms, session %d ms, action %d ms", Long.valueOf(ev.elapsedContainerMillis), Long.valueOf(ev.elapsedSessionMillis), Long.valueOf(ev.actionDurationMillis))) + "\n isInLandscapeMode " + ev.isInLandscapeMode) + "\n isInMultiWindowMode " + ev.isInMultiWindowMode) + "\n\n");
        }
    }

    private static String getTargetsStr(LauncherLogProto.Target[] targets) {
        String str = "child:" + LoggerUtils.getTargetStr(targets[0]);
        for (int i = 1; i < targets.length; i++) {
            str = str + "\tparent:" + LoggerUtils.getTargetStr(targets[i]);
        }
        return str;
    }
}
