package com.android.quickstep.util;

import android.graphics.HardwareRenderer;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewRootImpl;
import com.android.quickstep.RemoteAnimationTargets;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class SurfaceTransactionApplier extends RemoteAnimationTargets.ReleaseCheck {
    private static final int MSG_UPDATE_SEQUENCE_NUMBER = 0;
    private final Handler mApplyHandler;
    private final SurfaceControl mBarrierSurfaceControl;
    private int mLastSequenceNumber = 0;
    private final ViewRootImpl mTargetViewRootImpl;

    public SurfaceTransactionApplier(View targetView) {
        ViewRootImpl viewRootImpl = targetView.getViewRootImpl();
        this.mTargetViewRootImpl = viewRootImpl;
        this.mBarrierSurfaceControl = viewRootImpl.getSurfaceControl();
        this.mApplyHandler = new Handler(new Handler.Callback() { // from class: com.android.quickstep.util.-$$Lambda$iHUe-S0-7c-VRTxNgEMSNce0Bsk
            @Override // android.os.Handler.Callback
            public final boolean handleMessage(Message message) {
                return this.f$0.onApplyMessage(message);
            }
        });
        setCanRelease(true);
    }

    protected boolean onApplyMessage(Message msg) {
        if (msg.what != 0) {
            return false;
        }
        setCanRelease(msg.arg1 == this.mLastSequenceNumber);
        return true;
    }

    public void scheduleApply(final SyncRtSurfaceTransactionApplierCompat.SurfaceParams... params) {
        View view = this.mTargetViewRootImpl.getView();
        if (view == null) {
            return;
        }
        final int i = this.mLastSequenceNumber + 1;
        this.mLastSequenceNumber = i;
        setCanRelease(false);
        this.mTargetViewRootImpl.registerRtFrameCallback(new HardwareRenderer.FrameDrawingCallback() { // from class: com.android.quickstep.util.-$$Lambda$SurfaceTransactionApplier$egjlq1nGDaXKwo86oQ92Gggem98
            public final void onFrameDraw(long j) {
                this.f$0.lambda$scheduleApply$0$SurfaceTransactionApplier(i, params, j);
            }
        });
        view.invalidate();
    }

    public /* synthetic */ void lambda$scheduleApply$0$SurfaceTransactionApplier(int i, SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] surfaceParamsArr, long j) {
        SurfaceControl surfaceControl = this.mBarrierSurfaceControl;
        if (surfaceControl == null || !surfaceControl.isValid()) {
            Message.obtain(this.mApplyHandler, 0, i, 0).sendToTarget();
            return;
        }
        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
        for (int length = surfaceParamsArr.length - 1; length >= 0; length--) {
            SyncRtSurfaceTransactionApplierCompat.SurfaceParams surfaceParams = surfaceParamsArr[length];
            if (surfaceParams.surface.isValid()) {
                surfaceParams.applyTo(transaction);
            }
        }
        this.mTargetViewRootImpl.mergeWithNextTransaction(transaction, j);
        Message.obtain(this.mApplyHandler, 0, i, 0).sendToTarget();
    }

    public static void create(final View targetView, final Consumer<SurfaceTransactionApplier> callback) {
        if (targetView == null) {
            callback.accept(null);
        } else if (targetView.isAttachedToWindow()) {
            callback.accept(new SurfaceTransactionApplier(targetView));
        } else {
            targetView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: com.android.quickstep.util.SurfaceTransactionApplier.1
                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewDetachedFromWindow(View v) {
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewAttachedToWindow(View v) {
                    targetView.removeOnAttachStateChangeListener(this);
                    callback.accept(new SurfaceTransactionApplier(targetView));
                }
            });
        }
    }
}
