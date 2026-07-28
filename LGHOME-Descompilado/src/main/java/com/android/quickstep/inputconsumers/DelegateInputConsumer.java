package com.android.quickstep.inputconsumers;

import android.view.MotionEvent;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.quickstep.InputConsumer;
import com.android.systemui.shared.system.InputMonitorCompat;

/* JADX INFO: loaded from: classes.dex */
public abstract class DelegateInputConsumer implements InputConsumer {
    protected static final int STATE_ACTIVE = 1;
    protected static final int STATE_DELEGATE_ACTIVE = 2;
    protected static final int STATE_INACTIVE = 0;
    protected final InputConsumer mDelegate;
    protected final InputMonitorCompat mInputMonitor;
    protected int mState = 0;

    public DelegateInputConsumer(InputConsumer delegate, InputMonitorCompat inputMonitor) {
        this.mDelegate = delegate;
        this.mInputMonitor = inputMonitor;
    }

    @Override // com.android.quickstep.InputConsumer
    public InputConsumer getActiveConsumerInHierarchy() {
        return this.mState == 1 ? this : this.mDelegate.getActiveConsumerInHierarchy();
    }

    @Override // com.android.quickstep.InputConsumer
    public boolean allowInterceptByParent() {
        return this.mDelegate.allowInterceptByParent() && this.mState != 1;
    }

    @Override // com.android.quickstep.InputConsumer
    public void onConsumerAboutToBeSwitched() {
        this.mDelegate.onConsumerAboutToBeSwitched();
    }

    protected void setActive(MotionEvent ev) {
        this.mState = 1;
        TestLogging.recordEvent(TestProtocol.SEQUENCE_PILFER, "pilferPointers");
        this.mInputMonitor.pilferPointers();
        MotionEvent motionEventObtain = MotionEvent.obtain(ev);
        motionEventObtain.setAction(3);
        this.mDelegate.onMotionEvent(motionEventObtain);
        motionEventObtain.recycle();
    }
}
