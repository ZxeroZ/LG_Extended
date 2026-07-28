package com.android.quickstep;

import android.graphics.HardwareRenderer;
import android.os.Handler;
import android.view.View;
import android.view.ViewRootImpl;
import com.android.launcher3.Utilities;
import java.util.function.BooleanSupplier;

/* JADX INFO: loaded from: classes.dex */
public class ViewUtils {
    static /* synthetic */ boolean lambda$postFrameDrawn$0() {
        return false;
    }

    public static boolean postFrameDrawn(View view, Runnable onFinishRunnable) {
        return postFrameDrawn(view, onFinishRunnable, new BooleanSupplier() { // from class: com.android.quickstep.-$$Lambda$ViewUtils$4dj86orGtS8qmkpomZDMEi3mL1k
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return ViewUtils.lambda$postFrameDrawn$0();
            }
        });
    }

    public static boolean postFrameDrawn(View view, Runnable onFinishRunnable, BooleanSupplier canceled) {
        return new FrameHandler(view, onFinishRunnable, canceled).schedule();
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class FrameHandler implements HardwareRenderer.FrameDrawingCallback {
        final BooleanSupplier mCancelled;
        final Runnable mFinishCallback;
        final ViewRootImpl mViewRoot;
        int mDeferFrameCount = 1;
        final Handler mHandler = new Handler();

        FrameHandler(View view, Runnable finishCallback, BooleanSupplier cancelled) {
            this.mViewRoot = view.getViewRootImpl();
            this.mFinishCallback = finishCallback;
            this.mCancelled = cancelled;
        }

        public void onFrameDraw(long frame) {
            Utilities.postAsyncCallback(this.mHandler, new Runnable() { // from class: com.android.quickstep.-$$Lambda$ViewUtils$FrameHandler$qa7dMCz_njMc_l03GJzIAd-ojmU
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.onFrame();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onFrame() {
            if (this.mCancelled.getAsBoolean()) {
                return;
            }
            int i = this.mDeferFrameCount;
            if (i > 0) {
                this.mDeferFrameCount = i - 1;
                schedule();
            } else {
                Runnable runnable = this.mFinishCallback;
                if (runnable != null) {
                    runnable.run();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean schedule() {
            ViewRootImpl viewRootImpl = this.mViewRoot;
            if (viewRootImpl == null || viewRootImpl.getView() == null) {
                return false;
            }
            this.mViewRoot.registerRtFrameCallback(this);
            this.mViewRoot.getView().invalidate();
            return true;
        }
    }
}
