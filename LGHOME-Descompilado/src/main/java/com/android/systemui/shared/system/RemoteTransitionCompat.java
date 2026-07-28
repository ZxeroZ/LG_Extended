package com.android.systemui.shared.system;

import android.annotation.NonNull;
import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.IApplicationThread;
import android.content.ComponentName;
import android.graphics.Rect;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceControl;
import android.window.IRemoteTransition;
import android.window.IRemoteTransitionFinishedCallback;
import android.window.PictureInPictureSurfaceTransaction;
import android.window.RemoteTransition;
import android.window.TaskSnapshot;
import android.window.TransitionFilter;
import android.window.TransitionInfo;
import android.window.WindowContainerToken;
import android.window.WindowContainerTransaction;
import com.android.internal.util.AnnotationValidations;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.RemoteTransitionCompat;
import java.util.ArrayList;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
public class RemoteTransitionCompat implements Parcelable {
    public static final Parcelable.Creator<RemoteTransitionCompat> CREATOR = new Parcelable.Creator<RemoteTransitionCompat>() { // from class: com.android.systemui.shared.system.RemoteTransitionCompat.3
        /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RemoteTransitionCompat[] newArray(int i) {
            return new RemoteTransitionCompat[i];
        }

        /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RemoteTransitionCompat createFromParcel(Parcel parcel) {
            return new RemoteTransitionCompat(parcel);
        }
    };
    private static final String TAG = "RemoteTransitionCompat";
    TransitionFilter mFilter;
    final RemoteTransition mTransition;

    @Deprecated
    private void __metadata() {
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    RemoteTransitionCompat(RemoteTransition remoteTransition) {
        this.mFilter = null;
        this.mTransition = remoteTransition;
    }

    /* JADX INFO: renamed from: com.android.systemui.shared.system.RemoteTransitionCompat$1, reason: invalid class name */
    class AnonymousClass1 extends IRemoteTransition.Stub {
        final /* synthetic */ Executor val$executor;
        final /* synthetic */ RemoteTransitionRunner val$runner;

        AnonymousClass1(Executor executor, RemoteTransitionRunner remoteTransitionRunner) {
            this.val$executor = executor;
            this.val$runner = remoteTransitionRunner;
        }

        public void startAnimation(final IBinder iBinder, final TransitionInfo transitionInfo, final SurfaceControl.Transaction transaction, final IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
            final Runnable runnable = new Runnable() { // from class: com.android.systemui.shared.system.-$$Lambda$RemoteTransitionCompat$1$4bGfLVj4G1PZG6H5jtGC-YBezCc
                @Override // java.lang.Runnable
                public final void run() {
                    RemoteTransitionCompat.AnonymousClass1.lambda$startAnimation$0(iRemoteTransitionFinishedCallback);
                }
            };
            Executor executor = this.val$executor;
            final RemoteTransitionRunner remoteTransitionRunner = this.val$runner;
            executor.execute(new Runnable() { // from class: com.android.systemui.shared.system.-$$Lambda$RemoteTransitionCompat$1$hsPmf7vBlVPyR7fn8VTjavmbzl0
                @Override // java.lang.Runnable
                public final void run() {
                    remoteTransitionRunner.startAnimation(iBinder, transitionInfo, transaction, runnable);
                }
            });
        }

        static /* synthetic */ void lambda$startAnimation$0(IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
            try {
                iRemoteTransitionFinishedCallback.onTransitionFinished((WindowContainerTransaction) null, (SurfaceControl.Transaction) null);
            } catch (RemoteException e) {
                Log.e(RemoteTransitionCompat.TAG, "Failed to call transition finished callback", e);
            }
        }

        public void mergeAnimation(final IBinder iBinder, final TransitionInfo transitionInfo, final SurfaceControl.Transaction transaction, final IBinder iBinder2, final IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
            final Runnable runnable = new Runnable() { // from class: com.android.systemui.shared.system.-$$Lambda$RemoteTransitionCompat$1$jwJx2V1LAn-ejviZsZXR1iMLhUc
                @Override // java.lang.Runnable
                public final void run() {
                    RemoteTransitionCompat.AnonymousClass1.lambda$mergeAnimation$2(iRemoteTransitionFinishedCallback);
                }
            };
            Executor executor = this.val$executor;
            final RemoteTransitionRunner remoteTransitionRunner = this.val$runner;
            executor.execute(new Runnable() { // from class: com.android.systemui.shared.system.-$$Lambda$RemoteTransitionCompat$1$aP60ABl4T7pZOMUM80gsj4ATUEI
                @Override // java.lang.Runnable
                public final void run() {
                    remoteTransitionRunner.mergeAnimation(iBinder, transitionInfo, transaction, iBinder2, runnable);
                }
            });
        }

        static /* synthetic */ void lambda$mergeAnimation$2(IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
            try {
                iRemoteTransitionFinishedCallback.onTransitionFinished((WindowContainerTransaction) null, (SurfaceControl.Transaction) null);
            } catch (RemoteException e) {
                Log.e(RemoteTransitionCompat.TAG, "Failed to call transition finished callback", e);
            }
        }
    }

    public RemoteTransitionCompat(RemoteTransitionRunner remoteTransitionRunner, Executor executor, IApplicationThread iApplicationThread) {
        this.mFilter = null;
        this.mTransition = new RemoteTransition(new AnonymousClass1(executor, remoteTransitionRunner), iApplicationThread);
    }

    public RemoteTransitionCompat(final RecentsAnimationListener recentsAnimationListener, final RecentsAnimationControllerCompat recentsAnimationControllerCompat, IApplicationThread iApplicationThread) {
        this.mFilter = null;
        this.mTransition = new RemoteTransition(new IRemoteTransition.Stub() { // from class: com.android.systemui.shared.system.RemoteTransitionCompat.2
            final RecentsControllerWrap mRecentsSession = new RecentsControllerWrap();
            IBinder mToken = null;

            public void startAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
                ArrayMap<SurfaceControl, SurfaceControl> arrayMap = new ArrayMap<>();
                RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArrWrap = RemoteAnimationTargetCompat.wrap(transitionInfo, false, transaction, arrayMap);
                RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArrWrap2 = RemoteAnimationTargetCompat.wrap(transitionInfo, true, transaction, arrayMap);
                this.mToken = iBinder;
                ArrayList<WindowContainerToken> arrayList = new ArrayList<>();
                WindowContainerToken windowContainerToken = null;
                WindowContainerToken windowContainerToken2 = null;
                for (int size = transitionInfo.getChanges().size() - 1; size >= 0; size--) {
                    TransitionInfo.Change change = (TransitionInfo.Change) transitionInfo.getChanges().get(size);
                    ActivityManager.RunningTaskInfo taskInfo = change.getTaskInfo();
                    if (change.getMode() == 2 || change.getMode() == 4) {
                        transaction.setLayer(arrayMap.get(change.getLeash()), (transitionInfo.getChanges().size() * 3) - size);
                        if (taskInfo != null) {
                            arrayList.add(0, taskInfo.token);
                            if (taskInfo.pictureInPictureParams != null && taskInfo.pictureInPictureParams.isAutoEnterEnabled()) {
                                windowContainerToken = taskInfo.token;
                            }
                        }
                    } else if (taskInfo != null && taskInfo.topActivityType == 3) {
                        transaction.setLayer(arrayMap.get(change.getLeash()), (transitionInfo.getChanges().size() * 3) - size);
                        windowContainerToken2 = taskInfo.token;
                    } else if (taskInfo != null && taskInfo.topActivityType == 2) {
                        windowContainerToken2 = taskInfo.token;
                    }
                }
                for (int length = remoteAnimationTargetCompatArrWrap2.length - 1; length >= 0; length--) {
                    transaction.setAlpha(remoteAnimationTargetCompatArrWrap2[length].leash, 1.0f);
                }
                transaction.apply();
                this.mRecentsSession.setup(recentsAnimationControllerCompat, transitionInfo, iRemoteTransitionFinishedCallback, arrayList, windowContainerToken, windowContainerToken2, arrayMap, this.mToken);
                recentsAnimationListener.onAnimationStart(this.mRecentsSession, remoteAnimationTargetCompatArrWrap, remoteAnimationTargetCompatArrWrap2, new Rect(0, 0, 0, 0), new Rect());
            }

            public void mergeAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, IBinder iBinder2, IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
                if (iBinder2.equals(this.mToken) && this.mRecentsSession.merge(transitionInfo, transaction, recentsAnimationListener)) {
                    try {
                        iRemoteTransitionFinishedCallback.onTransitionFinished((WindowContainerTransaction) null, (SurfaceControl.Transaction) null);
                    } catch (RemoteException e) {
                        Log.e(RemoteTransitionCompat.TAG, "Error merging transition.", e);
                    }
                }
            }
        }, iApplicationThread);
    }

    public void addHomeOpenCheck(ComponentName componentName) {
        if (this.mFilter == null) {
            this.mFilter = new TransitionFilter();
        }
        this.mFilter.mNotFlags = 256;
        this.mFilter.mRequirements = new TransitionFilter.Requirement[]{new TransitionFilter.Requirement(), new TransitionFilter.Requirement()};
        this.mFilter.mRequirements[0].mActivityType = 2;
        this.mFilter.mRequirements[0].mTopActivity = componentName;
        this.mFilter.mRequirements[0].mModes = new int[]{1, 3};
        this.mFilter.mRequirements[0].mOrder = 1;
        this.mFilter.mRequirements[1].mActivityType = 1;
        this.mFilter.mRequirements[1].mModes = new int[]{2, 4};
    }

    static class RecentsControllerWrap extends RecentsAnimationControllerCompat {
        private RecentsAnimationControllerCompat mWrapped = null;
        private IRemoteTransitionFinishedCallback mFinishCB = null;
        private ArrayList<WindowContainerToken> mPausingTasks = null;
        private WindowContainerToken mPipTask = null;
        private WindowContainerToken mRecentsTask = null;
        private TransitionInfo mInfo = null;
        private ArrayList<SurfaceControl> mOpeningLeashes = null;
        private ArrayMap<SurfaceControl, SurfaceControl> mLeashMap = null;
        private PictureInPictureSurfaceTransaction mPipTransaction = null;
        private IBinder mTransition = null;

        @Override // com.android.systemui.shared.system.RecentsAnimationControllerCompat
        public void animateNavigationBarToApp(long j) {
        }

        RecentsControllerWrap() {
        }

        void setup(RecentsAnimationControllerCompat recentsAnimationControllerCompat, TransitionInfo transitionInfo, IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback, ArrayList<WindowContainerToken> arrayList, WindowContainerToken windowContainerToken, WindowContainerToken windowContainerToken2, ArrayMap<SurfaceControl, SurfaceControl> arrayMap, IBinder iBinder) {
            if (this.mInfo != null) {
                throw new IllegalStateException("Trying to run a new recents animation while recents is already active.");
            }
            this.mWrapped = recentsAnimationControllerCompat;
            this.mInfo = transitionInfo;
            this.mFinishCB = iRemoteTransitionFinishedCallback;
            this.mPausingTasks = arrayList;
            this.mPipTask = windowContainerToken;
            this.mRecentsTask = windowContainerToken2;
            this.mLeashMap = arrayMap;
            this.mTransition = iBinder;
        }

        boolean merge(TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, RecentsAnimationListener recentsAnimationListener) {
            int i;
            SparseArray sparseArray = null;
            boolean z = false;
            boolean z2 = false;
            boolean z3 = false;
            for (int size = transitionInfo.getChanges().size() - 1; size >= 0; size--) {
                TransitionInfo.Change change = (TransitionInfo.Change) transitionInfo.getChanges().get(size);
                if (change.getMode() == 1 || change.getMode() == 3) {
                    ActivityManager.RunningTaskInfo taskInfo = change.getTaskInfo();
                    if (taskInfo != null) {
                        if (taskInfo.topActivityType == 2) {
                            z3 = true;
                        }
                        if (sparseArray == null) {
                            sparseArray = new SparseArray();
                        }
                        if (taskInfo.hasParentTask()) {
                            sparseArray.remove(taskInfo.parentTaskId);
                        }
                        sparseArray.put(taskInfo.taskId, change);
                    }
                } else if (change.getMode() == 2 || change.getMode() == 4) {
                    if (this.mRecentsTask.equals(change.getContainer())) {
                        z2 = true;
                    }
                } else if (change.getMode() == 6) {
                    z = true;
                }
            }
            if (z && z2) {
                if (!recentsAnimationListener.onSwitchToScreenshot(new Runnable() { // from class: com.android.systemui.shared.system.-$$Lambda$RemoteTransitionCompat$RecentsControllerWrap$wxddTuaOUJWN5dn62SlweFlnckA
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$merge$0$RemoteTransitionCompat$RecentsControllerWrap();
                    }
                })) {
                    Log.w(RemoteTransitionCompat.TAG, "Recents callback doesn't support support switching to screenshot, there might be a flicker.");
                    finish(true, false);
                }
                return false;
            }
            if (sparseArray == null) {
                return false;
            }
            if (z3) {
                i = 0;
            } else {
                i = 0;
                for (int i2 = 0; i2 < sparseArray.size(); i2++) {
                    if (this.mPausingTasks.contains(((TransitionInfo.Change) sparseArray.valueAt(i2)).getContainer())) {
                        i++;
                    }
                }
            }
            if (i > 0) {
                if (i == this.mPausingTasks.size()) {
                    return true;
                }
                throw new IllegalStateException("\"Concelling\" a recents transitions by unpausing " + i + " apps after pausing " + this.mPausingTasks.size() + " apps.");
            }
            int size2 = this.mInfo.getChanges().size() * 3;
            this.mOpeningLeashes = new ArrayList<>();
            RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr = new RemoteAnimationTargetCompat[sparseArray.size()];
            for (int i3 = 0; i3 < sparseArray.size(); i3++) {
                TransitionInfo.Change change2 = (TransitionInfo.Change) sparseArray.valueAt(i3);
                this.mOpeningLeashes.add(change2.getLeash());
                RemoteAnimationTargetCompat remoteAnimationTargetCompat = new RemoteAnimationTargetCompat(change2, size2, transitionInfo, transaction);
                this.mLeashMap.put(this.mOpeningLeashes.get(i3), remoteAnimationTargetCompat.leash);
                transaction.reparent(remoteAnimationTargetCompat.leash, this.mInfo.getRootLeash());
                transaction.setLayer(remoteAnimationTargetCompat.leash, size2);
                remoteAnimationTargetCompatArr[i3] = remoteAnimationTargetCompat;
            }
            transaction.apply();
            recentsAnimationListener.onTasksAppeared(remoteAnimationTargetCompatArr);
            return true;
        }

        public /* synthetic */ void lambda$merge$0$RemoteTransitionCompat$RecentsControllerWrap() {
            finish(true, false);
        }

        @Override // com.android.systemui.shared.system.RecentsAnimationControllerCompat
        public ThumbnailData screenshotTask(int i) {
            try {
                TaskSnapshot taskSnapshotTakeTaskSnapshot = ActivityTaskManager.getService().takeTaskSnapshot(i);
                if (taskSnapshotTakeTaskSnapshot != null) {
                    return new ThumbnailData(taskSnapshotTakeTaskSnapshot);
                }
                return null;
            } catch (RemoteException e) {
                Log.e(RemoteTransitionCompat.TAG, "Failed to screenshot task", e);
                return null;
            }
        }

        @Override // com.android.systemui.shared.system.RecentsAnimationControllerCompat
        public void setInputConsumerEnabled(boolean z) {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.setInputConsumerEnabled(z);
            }
        }

        @Override // com.android.systemui.shared.system.RecentsAnimationControllerCompat
        public void setAnimationTargetsBehindSystemBars(boolean z) {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.setAnimationTargetsBehindSystemBars(z);
            }
        }

        @Override // com.android.systemui.shared.system.RecentsAnimationControllerCompat
        public void hideCurrentInputMethod() {
            this.mWrapped.hideCurrentInputMethod();
        }

        @Override // com.android.systemui.shared.system.RecentsAnimationControllerCompat
        public void setFinishTaskTransaction(int i, PictureInPictureSurfaceTransaction pictureInPictureSurfaceTransaction, SurfaceControl surfaceControl) {
            this.mPipTransaction = pictureInPictureSurfaceTransaction;
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.setFinishTaskTransaction(i, pictureInPictureSurfaceTransaction, surfaceControl);
            }
        }

        @Override // com.android.systemui.shared.system.RecentsAnimationControllerCompat
        public void finish(boolean z, boolean z2) {
            ArrayList<WindowContainerToken> arrayList;
            if (this.mFinishCB == null) {
                Log.e(RemoteTransitionCompat.TAG, "Duplicate call to finish", new RuntimeException());
                return;
            }
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.finish(z, z2);
            }
            SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
            WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
            if (!z && (arrayList = this.mPausingTasks) != null && this.mOpeningLeashes == null) {
                for (int size = arrayList.size() - 1; size >= 0; size--) {
                    windowContainerTransaction.reorder(this.mPausingTasks.get(size), true);
                    transaction.show(this.mInfo.getChange(this.mPausingTasks.get(size)).getLeash());
                }
                WindowContainerToken windowContainerToken = this.mRecentsTask;
                if (windowContainerToken != null) {
                    windowContainerTransaction.restoreTransientOrder(windowContainerToken);
                }
            } else {
                if (!z2) {
                    for (int i = 0; i < this.mPausingTasks.size(); i++) {
                        windowContainerTransaction.setDoNotPip(this.mPausingTasks.get(i));
                    }
                }
                WindowContainerToken windowContainerToken2 = this.mPipTask;
                if (windowContainerToken2 != null && this.mPipTransaction != null && z2) {
                    transaction.show(this.mInfo.getChange(windowContainerToken2).getLeash());
                    PictureInPictureSurfaceTransaction.apply(this.mPipTransaction, this.mInfo.getChange(this.mPipTask).getLeash(), transaction);
                    this.mPipTask = null;
                    this.mPipTransaction = null;
                }
            }
            for (int i2 = 0; i2 < this.mLeashMap.size(); i2++) {
                if (this.mLeashMap.keyAt(i2) != this.mLeashMap.valueAt(i2)) {
                    transaction.remove(this.mLeashMap.valueAt(i2));
                }
            }
            try {
                IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback = this.mFinishCB;
                if (windowContainerTransaction.isEmpty()) {
                    windowContainerTransaction = null;
                }
                iRemoteTransitionFinishedCallback.onTransitionFinished(windowContainerTransaction, transaction);
            } catch (RemoteException e) {
                Log.e(RemoteTransitionCompat.TAG, "Failed to call animation finish callback", e);
                transaction.apply();
            }
            for (int i3 = 0; i3 < this.mInfo.getChanges().size(); i3++) {
                ((TransitionInfo.Change) this.mInfo.getChanges().get(i3)).getLeash().release();
            }
            this.mWrapped = null;
            this.mFinishCB = null;
            this.mPausingTasks = null;
            this.mInfo = null;
            this.mOpeningLeashes = null;
            this.mLeashMap = null;
            this.mTransition = null;
        }

        @Override // com.android.systemui.shared.system.RecentsAnimationControllerCompat
        public void setDeferCancelUntilNextTransition(boolean z, boolean z2) {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.setDeferCancelUntilNextTransition(z, z2);
            }
        }

        @Override // com.android.systemui.shared.system.RecentsAnimationControllerCompat
        public void cleanupScreenshot() {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.cleanupScreenshot();
            }
        }

        @Override // com.android.systemui.shared.system.RecentsAnimationControllerCompat
        public void setWillFinishToHome(boolean z) {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.setWillFinishToHome(z);
            }
        }

        @Override // com.android.systemui.shared.system.RecentsAnimationControllerCompat
        public boolean removeTask(int i) {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                return recentsAnimationControllerCompat.removeTask(i);
            }
            return false;
        }

        @Override // com.android.systemui.shared.system.RecentsAnimationControllerCompat
        public void detachNavigationBarFromApp(boolean z) {
            try {
                ActivityTaskManager.getService().detachNavigationBarFromApp(this.mTransition);
            } catch (RemoteException e) {
                Log.e(RemoteTransitionCompat.TAG, "Failed to detach the navigation bar from app", e);
            }
        }
    }

    RemoteTransitionCompat(RemoteTransition remoteTransition, TransitionFilter transitionFilter) {
        this.mFilter = null;
        this.mTransition = remoteTransition;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, remoteTransition);
        this.mFilter = transitionFilter;
    }

    public RemoteTransition getTransition() {
        return this.mTransition;
    }

    public TransitionFilter getFilter() {
        return this.mFilter;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte(this.mFilter != null ? (byte) 2 : (byte) 0);
        parcel.writeTypedObject(this.mTransition, i);
        TransitionFilter transitionFilter = this.mFilter;
        if (transitionFilter != null) {
            parcel.writeTypedObject(transitionFilter, i);
        }
    }

    protected RemoteTransitionCompat(Parcel parcel) {
        this.mFilter = null;
        byte b = parcel.readByte();
        RemoteTransition remoteTransition = (RemoteTransition) parcel.readTypedObject(RemoteTransition.CREATOR);
        TransitionFilter transitionFilter = (b & 2) == 0 ? null : (TransitionFilter) parcel.readTypedObject(TransitionFilter.CREATOR);
        this.mTransition = remoteTransition;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, remoteTransition);
        this.mFilter = transitionFilter;
    }

    public static class Builder {
        private long mBuilderFieldsSet = 0;
        private TransitionFilter mFilter;
        private RemoteTransition mTransition;

        public Builder(RemoteTransition remoteTransition) {
            this.mTransition = remoteTransition;
            AnnotationValidations.validate(NonNull.class, (NonNull) null, remoteTransition);
        }

        public Builder setTransition(RemoteTransition remoteTransition) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mTransition = remoteTransition;
            return this;
        }

        public Builder setFilter(TransitionFilter transitionFilter) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mFilter = transitionFilter;
            return this;
        }

        public RemoteTransitionCompat build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 4;
            this.mBuilderFieldsSet = j;
            if ((j & 2) == 0) {
                this.mFilter = null;
            }
            return new RemoteTransitionCompat(this.mTransition, this.mFilter);
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 4) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }
}
