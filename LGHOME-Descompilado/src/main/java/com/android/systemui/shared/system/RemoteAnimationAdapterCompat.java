package com.android.systemui.shared.system;

import android.app.IApplicationThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Log;
import android.view.IRemoteAnimationFinishedCallback;
import android.view.IRemoteAnimationRunner;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationTarget;
import android.view.SurfaceControl;
import android.window.IRemoteTransition;
import android.window.IRemoteTransitionFinishedCallback;
import android.window.RemoteTransition;
import android.window.TransitionInfo;
import android.window.WindowContainerTransaction;
import com.android.wm.shell.util.CounterRotator;

/* JADX INFO: loaded from: classes.dex */
public class RemoteAnimationAdapterCompat {
    private final RemoteTransitionCompat mRemoteTransition;
    private final RemoteAnimationAdapter mWrapped;

    public RemoteAnimationAdapterCompat(RemoteAnimationRunnerCompat remoteAnimationRunnerCompat, long j, long j2, IApplicationThread iApplicationThread) {
        this.mWrapped = new RemoteAnimationAdapter(wrapRemoteAnimationRunner(remoteAnimationRunnerCompat), j, j2);
        this.mRemoteTransition = buildRemoteTransition(remoteAnimationRunnerCompat, iApplicationThread);
    }

    RemoteAnimationAdapter getWrapped() {
        return this.mWrapped;
    }

    public static RemoteTransitionCompat buildRemoteTransition(RemoteAnimationRunnerCompat remoteAnimationRunnerCompat, IApplicationThread iApplicationThread) {
        return new RemoteTransitionCompat(new RemoteTransition(wrapRemoteTransition(remoteAnimationRunnerCompat), iApplicationThread));
    }

    public RemoteTransitionCompat getRemoteTransition() {
        return this.mRemoteTransition;
    }

    public static IRemoteAnimationRunner.Stub wrapRemoteAnimationRunner(final RemoteAnimationRunnerCompat remoteAnimationRunnerCompat) {
        return new IRemoteAnimationRunner.Stub() { // from class: com.android.systemui.shared.system.RemoteAnimationAdapterCompat.1
            public void onAnimationStart(int i, RemoteAnimationTarget[] remoteAnimationTargetArr, RemoteAnimationTarget[] remoteAnimationTargetArr2, RemoteAnimationTarget[] remoteAnimationTargetArr3, final IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) {
                remoteAnimationRunnerCompat.onAnimationStart(i, RemoteAnimationTargetCompat.wrap(remoteAnimationTargetArr), RemoteAnimationTargetCompat.wrap(remoteAnimationTargetArr2), RemoteAnimationTargetCompat.wrap(remoteAnimationTargetArr3), new Runnable() { // from class: com.android.systemui.shared.system.RemoteAnimationAdapterCompat.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            iRemoteAnimationFinishedCallback.onAnimationFinished();
                        } catch (RemoteException e) {
                            Log.e("ActivityOptionsCompat", "Failed to call app controlled animation finished callback", e);
                        }
                    }
                });
            }

            public void onAnimationCancelled(boolean z) {
                remoteAnimationRunnerCompat.onAnimationCancelled();
            }
        };
    }

    private static IRemoteTransition.Stub wrapRemoteTransition(final RemoteAnimationRunnerCompat remoteAnimationRunnerCompat) {
        return new IRemoteTransition.Stub() { // from class: com.android.systemui.shared.system.RemoteAnimationAdapterCompat.2
            public void mergeAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, IBinder iBinder2, IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
            }

            public void startAnimation(IBinder iBinder, final TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, final IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
                final ArrayMap arrayMap = new ArrayMap();
                RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArrWrap = RemoteAnimationTargetCompat.wrap(transitionInfo, false, transaction, arrayMap);
                RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArrWrap2 = RemoteAnimationTargetCompat.wrap(transitionInfo, true, transaction, arrayMap);
                RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr = new RemoteAnimationTargetCompat[0];
                int size = 0;
                int endRotation = 0;
                boolean z = false;
                float f = 0.0f;
                float fHeight = 0.0f;
                TransitionInfo.Change change = null;
                TransitionInfo.Change change2 = null;
                for (int size2 = transitionInfo.getChanges().size() - 1; size2 >= 0; size2--) {
                    TransitionInfo.Change change3 = (TransitionInfo.Change) transitionInfo.getChanges().get(size2);
                    if (change3.getTaskInfo() != null && change3.getTaskInfo().getActivityType() == 2) {
                        z = change3.getMode() == 1 || change3.getMode() == 3;
                        size = transitionInfo.getChanges().size() - size2;
                        change = change3;
                    } else if ((change3.getFlags() & 2) != 0) {
                        change2 = change3;
                    }
                    if (change3.getParent() == null && change3.getEndRotation() >= 0 && change3.getEndRotation() != change3.getStartRotation()) {
                        endRotation = change3.getEndRotation() - change3.getStartRotation();
                        float fWidth = change3.getEndAbsBounds().width();
                        fHeight = change3.getEndAbsBounds().height();
                        f = fWidth;
                    }
                }
                final CounterRotator counterRotator = new CounterRotator();
                final CounterRotator counterRotator2 = new CounterRotator();
                if (change != null && endRotation != 0 && change.getParent() != null) {
                    int i = size;
                    counterRotator.setup(transaction, transitionInfo.getChange(change.getParent()).getLeash(), endRotation, f, fHeight);
                    if (counterRotator.getSurface() != null) {
                        transaction.setLayer(counterRotator.getSurface(), i);
                    }
                }
                if (z) {
                    if (counterRotator.getSurface() != null) {
                        transaction.setLayer(counterRotator.getSurface(), transitionInfo.getChanges().size() * 3);
                    }
                    for (int size3 = transitionInfo.getChanges().size() - 1; size3 >= 0; size3--) {
                        TransitionInfo.Change change4 = (TransitionInfo.Change) transitionInfo.getChanges().get(size3);
                        SurfaceControl surfaceControl = (SurfaceControl) arrayMap.get(change4.getLeash());
                        int mode = ((TransitionInfo.Change) transitionInfo.getChanges().get(size3)).getMode();
                        if (TransitionInfo.isIndependent(change4, transitionInfo) && (mode == 2 || mode == 4)) {
                            transaction.setLayer(surfaceControl, (transitionInfo.getChanges().size() * 3) - size3);
                            counterRotator.addChild(transaction, surfaceControl);
                        }
                    }
                    for (int length = remoteAnimationTargetCompatArrWrap2.length - 1; length >= 0; length--) {
                        transaction.show(remoteAnimationTargetCompatArrWrap2[length].leash);
                        transaction.setAlpha(remoteAnimationTargetCompatArrWrap2[length].leash, 1.0f);
                    }
                } else {
                    if (change != null) {
                        counterRotator.addChild(transaction, (SurfaceControl) arrayMap.get(change.getLeash()));
                    }
                    if (change2 != null && endRotation != 0 && change2.getParent() != null) {
                        counterRotator2.setup(transaction, transitionInfo.getChange(change2.getParent()).getLeash(), endRotation, f, fHeight);
                        if (counterRotator2.getSurface() != null) {
                            transaction.setLayer(counterRotator2.getSurface(), -1);
                            counterRotator2.addChild(transaction, (SurfaceControl) arrayMap.get(change2.getLeash()));
                        }
                    }
                }
                transaction.apply();
                remoteAnimationRunnerCompat.onAnimationStart(0, remoteAnimationTargetCompatArrWrap, remoteAnimationTargetCompatArrWrap2, remoteAnimationTargetCompatArr, new Runnable() { // from class: com.android.systemui.shared.system.RemoteAnimationAdapterCompat.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        SurfaceControl.Transaction transaction2 = new SurfaceControl.Transaction();
                        counterRotator.cleanUp(transaction2);
                        counterRotator2.cleanUp(transaction2);
                        for (int size4 = transitionInfo.getChanges().size() - 1; size4 >= 0; size4--) {
                            ((TransitionInfo.Change) transitionInfo.getChanges().get(size4)).getLeash().release();
                        }
                        for (int size5 = arrayMap.size() - 1; size5 >= 0; size5--) {
                            ((SurfaceControl) arrayMap.valueAt(size5)).release();
                        }
                        try {
                            iRemoteTransitionFinishedCallback.onTransitionFinished((WindowContainerTransaction) null, transaction2);
                        } catch (RemoteException e) {
                            Log.e("ActivityOptionsCompat", "Failed to call app controlled animation finished callback", e);
                        }
                    }
                });
            }
        };
    }
}
