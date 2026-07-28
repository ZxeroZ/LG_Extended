package com.android.systemui.shared.system;

import android.app.ActivityManager;
import android.app.WindowConfiguration;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.RemoteAnimationTarget;
import android.view.SurfaceControl;
import android.window.TransitionInfo;

/* JADX INFO: loaded from: classes.dex */
public class RemoteAnimationTargetCompat {
    public static final int ACTIVITY_TYPE_ASSISTANT = 4;
    public static final int ACTIVITY_TYPE_HOME = 2;
    public static final int ACTIVITY_TYPE_RECENTS = 3;
    public static final int ACTIVITY_TYPE_STANDARD = 1;
    public static final int ACTIVITY_TYPE_UNDEFINED = 0;
    public static final int MODE_CHANGING = 2;
    public static final int MODE_CLOSING = 1;
    public static final int MODE_OPENING = 0;
    public final int activityType;
    public final boolean allowEnterPip;
    public final Rect clipRect;
    public final Rect contentInsets;
    public final boolean isNotInRecents;
    public final boolean isTranslucent;
    public final SurfaceControl leash;
    public final Rect localBounds;
    private final SurfaceControl mStartLeash;
    public final int mode;
    public final Point position;
    public final int prefixOrderIndex;
    public final int rotationChange;
    public final Rect screenSpaceBounds;
    public final Rect sourceContainerBounds;
    private final Rect startBounds;
    public final Rect startScreenSpaceBounds;
    public int taskId;
    public ActivityManager.RunningTaskInfo taskInfo;
    public final WindowConfiguration windowConfiguration;
    public final int windowType;

    private static int newModeToLegacyMode(int i) {
        if (i == 1) {
            return 0;
        }
        if (i != 2) {
            if (i == 3) {
                return 0;
            }
            if (i != 4) {
                return 2;
            }
        }
        return 1;
    }

    public RemoteAnimationTargetCompat(RemoteAnimationTarget remoteAnimationTarget) {
        this.taskId = remoteAnimationTarget.taskId;
        this.mode = remoteAnimationTarget.mode;
        this.leash = remoteAnimationTarget.leash;
        this.isTranslucent = remoteAnimationTarget.isTranslucent;
        this.clipRect = remoteAnimationTarget.clipRect;
        this.position = remoteAnimationTarget.position;
        this.localBounds = remoteAnimationTarget.localBounds;
        this.sourceContainerBounds = remoteAnimationTarget.sourceContainerBounds;
        Rect rect = remoteAnimationTarget.screenSpaceBounds;
        this.screenSpaceBounds = rect;
        this.startScreenSpaceBounds = rect;
        this.prefixOrderIndex = remoteAnimationTarget.prefixOrderIndex;
        this.isNotInRecents = remoteAnimationTarget.isNotInRecents;
        this.contentInsets = remoteAnimationTarget.contentInsets;
        this.activityType = remoteAnimationTarget.windowConfiguration.getActivityType();
        this.taskInfo = remoteAnimationTarget.taskInfo;
        this.allowEnterPip = remoteAnimationTarget.allowEnterPip;
        this.rotationChange = 0;
        this.mStartLeash = remoteAnimationTarget.startLeash;
        this.windowType = remoteAnimationTarget.windowType;
        this.windowConfiguration = remoteAnimationTarget.windowConfiguration;
        this.startBounds = remoteAnimationTarget.startBounds;
    }

    public RemoteAnimationTarget unwrap() {
        return new RemoteAnimationTarget(this.taskId, this.mode, this.leash, this.isTranslucent, this.clipRect, this.contentInsets, this.prefixOrderIndex, this.position, this.localBounds, this.screenSpaceBounds, this.windowConfiguration, this.isNotInRecents, this.mStartLeash, this.startBounds, this.taskInfo, this.allowEnterPip, this.windowType);
    }

    private static void setupLeash(SurfaceControl surfaceControl, TransitionInfo.Change change, int i, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction) {
        boolean z = transitionInfo.getType() == 1 || transitionInfo.getType() == 3;
        int size = transitionInfo.getChanges().size();
        int mode = change.getMode();
        if (!TransitionInfo.isIndependent(change, transitionInfo)) {
            if (mode == 1 || mode == 3 || mode == 6) {
                transaction.setPosition(surfaceControl, change.getEndRelOffset().x, change.getEndRelOffset().y);
                return;
            }
            return;
        }
        if (!(change.getParent() != null)) {
            transaction.reparent(surfaceControl, transitionInfo.getRootLeash());
            transaction.setPosition(surfaceControl, change.getStartAbsBounds().left - transitionInfo.getRootOffset().x, change.getStartAbsBounds().top - transitionInfo.getRootOffset().y);
        }
        if (mode == 1 || mode == 3) {
            if (z) {
                transaction.setLayer(surfaceControl, (size + transitionInfo.getChanges().size()) - i);
                if ((change.getFlags() & 8) == 0) {
                    transaction.setAlpha(surfaceControl, 0.0f);
                    return;
                }
                return;
            }
            transaction.setLayer(surfaceControl, size - i);
            return;
        }
        if (mode != 2 && mode != 4) {
            transaction.setLayer(surfaceControl, (size + transitionInfo.getChanges().size()) - i);
        } else if (z) {
            transaction.setLayer(surfaceControl, size - i);
        } else {
            transaction.setLayer(surfaceControl, (size + transitionInfo.getChanges().size()) - i);
        }
    }

    private static SurfaceControl createLeash(TransitionInfo transitionInfo, TransitionInfo.Change change, int i, SurfaceControl.Transaction transaction) {
        if (change.getParent() != null && (change.getFlags() & 2) != 0) {
            return change.getLeash();
        }
        SurfaceControl surfaceControlBuild = new SurfaceControl.Builder().setName(change.getLeash().toString() + "_transition-leash").setContainerLayer().setHidden(false).setParent(change.getParent() == null ? transitionInfo.getRootLeash() : transitionInfo.getChange(change.getParent()).getLeash()).build();
        setupLeash(surfaceControlBuild, change, transitionInfo.getChanges().size() - i, transitionInfo, transaction);
        transaction.reparent(change.getLeash(), surfaceControlBuild);
        transaction.setAlpha(change.getLeash(), 1.0f);
        transaction.show(change.getLeash());
        transaction.setPosition(change.getLeash(), 0.0f, 0.0f);
        transaction.setLayer(change.getLeash(), 0);
        return surfaceControlBuild;
    }

    public RemoteAnimationTargetCompat(TransitionInfo.Change change, int i, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction) {
        WindowConfiguration windowConfiguration;
        this.taskId = change.getTaskInfo() != null ? change.getTaskInfo().taskId : -1;
        this.mode = newModeToLegacyMode(change.getMode());
        this.leash = createLeash(transitionInfo, change, i, transaction);
        this.isTranslucent = ((change.getFlags() & 4) == 0 && (change.getFlags() & 1) == 0) ? false : true;
        this.clipRect = null;
        this.position = null;
        Rect rect = new Rect(change.getEndAbsBounds());
        this.localBounds = rect;
        rect.offsetTo(change.getEndRelOffset().x, change.getEndRelOffset().y);
        this.sourceContainerBounds = null;
        this.screenSpaceBounds = new Rect(change.getEndAbsBounds());
        this.startScreenSpaceBounds = new Rect(change.getStartAbsBounds());
        this.prefixOrderIndex = i;
        this.contentInsets = new Rect(0, 0, 0, 0);
        if (change.getTaskInfo() != null) {
            this.isNotInRecents = !change.getTaskInfo().isRunning;
            this.activityType = change.getTaskInfo().getActivityType();
        } else {
            this.isNotInRecents = true;
            this.activityType = 0;
        }
        this.taskInfo = change.getTaskInfo();
        this.allowEnterPip = change.getAllowEnterPip();
        this.mStartLeash = null;
        this.rotationChange = change.getEndRotation() - change.getStartRotation();
        this.windowType = -1;
        if (change.getTaskInfo() != null) {
            windowConfiguration = change.getTaskInfo().configuration.windowConfiguration;
        } else {
            windowConfiguration = new WindowConfiguration();
        }
        this.windowConfiguration = windowConfiguration;
        this.startBounds = change.getStartAbsBounds();
    }

    public static RemoteAnimationTargetCompat[] wrap(RemoteAnimationTarget[] remoteAnimationTargetArr) {
        int length = remoteAnimationTargetArr != null ? remoteAnimationTargetArr.length : 0;
        RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr = new RemoteAnimationTargetCompat[length];
        for (int i = 0; i < length; i++) {
            remoteAnimationTargetCompatArr[i] = new RemoteAnimationTargetCompat(remoteAnimationTargetArr[i]);
        }
        return remoteAnimationTargetCompatArr;
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x009d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static com.android.systemui.shared.system.RemoteAnimationTargetCompat[] wrap(android.window.TransitionInfo r10, boolean r11, android.view.SurfaceControl.Transaction r12, android.util.ArrayMap<android.view.SurfaceControl, android.view.SurfaceControl> r13) {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            android.util.SparseArray r1 = new android.util.SparseArray
            r1.<init>()
            android.util.IntArray r2 = new android.util.IntArray
            r2.<init>()
            r3 = 0
            r4 = r3
        L11:
            java.util.List r5 = r10.getChanges()
            int r5 = r5.size()
            if (r4 >= r5) goto La4
            java.util.List r5 = r10.getChanges()
            java.lang.Object r5 = r5.get(r4)
            android.window.TransitionInfo$Change r5 = (android.window.TransitionInfo.Change) r5
            int r6 = r5.getFlags()
            r6 = r6 & 2
            if (r6 == 0) goto L2f
            r6 = 1
            goto L30
        L2f:
            r6 = r3
        L30:
            if (r11 == r6) goto L33
            goto La0
        L33:
            com.android.systemui.shared.system.RemoteAnimationTargetCompat r6 = new com.android.systemui.shared.system.RemoteAnimationTargetCompat
            java.util.List r7 = r10.getChanges()
            int r7 = r7.size()
            int r7 = r7 - r4
            r6.<init>(r5, r7, r10, r12)
            if (r13 == 0) goto L4c
            android.view.SurfaceControl r7 = r5.getLeash()
            android.view.SurfaceControl r8 = r6.leash
            r13.put(r7, r8)
        L4c:
            android.app.ActivityManager$RunningTaskInfo r5 = r5.getTaskInfo()
            if (r5 == 0) goto L9d
            int r7 = r5.taskId
            int r7 = r2.binarySearch(r7)
            r8 = -1
            if (r7 == r8) goto L5c
            goto La0
        L5c:
            int r7 = r5.taskId
            java.lang.Object r7 = r1.get(r7)
            com.android.systemui.shared.system.RemoteAnimationTargetCompat r7 = (com.android.systemui.shared.system.RemoteAnimationTargetCompat) r7
            if (r7 == 0) goto L73
            android.app.ActivityManager$RunningTaskInfo r9 = r7.taskInfo
            r6.taskInfo = r9
            int r7 = r7.taskId
            r6.taskId = r7
            int r7 = r5.taskId
            r1.remove(r7)
        L73:
            int r7 = r5.parentTaskId
            if (r7 == r8) goto L9d
            int r7 = r5.parentTaskId
            int r7 = r2.binarySearch(r7)
            if (r7 != r8) goto L9d
            int r7 = r5.parentTaskId
            boolean r7 = r1.contains(r7)
            if (r7 != 0) goto L8d
            int r5 = r5.parentTaskId
            r1.put(r5, r6)
            goto La0
        L8d:
            int r7 = r5.parentTaskId
            java.lang.Object r7 = r1.removeReturnOld(r7)
            com.android.systemui.shared.system.RemoteAnimationTargetCompat r7 = (com.android.systemui.shared.system.RemoteAnimationTargetCompat) r7
            r0.add(r7)
            int r5 = r5.parentTaskId
            r2.add(r5)
        L9d:
            r0.add(r6)
        La0:
            int r4 = r4 + 1
            goto L11
        La4:
            int r10 = r0.size()
            com.android.systemui.shared.system.RemoteAnimationTargetCompat[] r10 = new com.android.systemui.shared.system.RemoteAnimationTargetCompat[r10]
            java.lang.Object[] r10 = r0.toArray(r10)
            com.android.systemui.shared.system.RemoteAnimationTargetCompat[] r10 = (com.android.systemui.shared.system.RemoteAnimationTargetCompat[]) r10
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.shared.system.RemoteAnimationTargetCompat.wrap(android.window.TransitionInfo, boolean, android.view.SurfaceControl$Transaction, android.util.ArrayMap):com.android.systemui.shared.system.RemoteAnimationTargetCompat[]");
    }

    public void release() {
        SurfaceControl surfaceControl = this.leash;
        if (surfaceControl != null) {
            surfaceControl.release();
        }
        SurfaceControl surfaceControl2 = this.mStartLeash;
        if (surfaceControl2 != null) {
            surfaceControl2.release();
        }
    }
}
