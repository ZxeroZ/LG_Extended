package com.android.systemui.unfold.util;

import androidx.core.app.NotificationCompat;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: ScopedUnfoldTransitionProgressProvider.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010!\n\u0000\n\u0002\u0010\u0002\n\u0002\b\f\b\u0016\u0018\u0000 \u00182\u00020\u00012\u00020\u0002:\u0001\u0018B\u0013\b\u0007\u0012\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00010\u0001¢\u0006\u0002\u0010\u0004J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0002H\u0016J\b\u0010\u000f\u001a\u00020\rH\u0016J\b\u0010\u0010\u001a\u00020\rH\u0016J\u0010\u0010\u0011\u001a\u00020\r2\u0006\u0010\u0012\u001a\u00020\tH\u0016J\b\u0010\u0013\u001a\u00020\rH\u0016J\u0010\u0010\u0014\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0002H\u0016J\u000e\u0010\u0015\u001a\u00020\r2\u0006\u0010\u0005\u001a\u00020\u0006J\u0010\u0010\u0016\u001a\u00020\r2\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0001X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0019"}, d2 = {"Lcom/android/systemui/unfold/util/ScopedUnfoldTransitionProgressProvider;", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider$TransitionProgressListener;", "source", "(Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;)V", "isReadyToHandleTransition", "", "isTransitionRunning", "lastTransitionProgress", "", "listeners", "", "addCallback", "", "listener", "destroy", "onTransitionFinished", "onTransitionProgress", NotificationCompat.CATEGORY_PROGRESS, "onTransitionStarted", "removeCallback", "setReadyToHandleTransition", "setSourceProvider", "provider", "Companion", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public class ScopedUnfoldTransitionProgressProvider implements UnfoldTransitionProgressProvider, UnfoldTransitionProgressProvider.TransitionProgressListener {
    private static final float PROGRESS_UNSET = -1.0f;
    private boolean isReadyToHandleTransition;
    private boolean isTransitionRunning;
    private float lastTransitionProgress;
    private final List<UnfoldTransitionProgressProvider.TransitionProgressListener> listeners;
    private UnfoldTransitionProgressProvider source;

    public ScopedUnfoldTransitionProgressProvider() {
        this(null, 1, 0 == true ? 1 : 0);
    }

    public ScopedUnfoldTransitionProgressProvider(UnfoldTransitionProgressProvider unfoldTransitionProgressProvider) {
        this.listeners = new ArrayList();
        this.lastTransitionProgress = PROGRESS_UNSET;
        setSourceProvider(unfoldTransitionProgressProvider);
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0005: CONSTRUCTOR 
      (wrap:com.android.systemui.unfold.UnfoldTransitionProgressProvider:?: TERNARY null = ((wrap:int:0x0000: ARITH (r2v0 int) & (1 int) A[WRAPPED]) != (0 int)) ? (null com.android.systemui.unfold.UnfoldTransitionProgressProvider) : (r1v0 com.android.systemui.unfold.UnfoldTransitionProgressProvider))
     A[MD:(com.android.systemui.unfold.UnfoldTransitionProgressProvider):void (m)] (LINE:33) call: com.android.systemui.unfold.util.ScopedUnfoldTransitionProgressProvider.<init>(com.android.systemui.unfold.UnfoldTransitionProgressProvider):void type: THIS */
    public /* synthetic */ ScopedUnfoldTransitionProgressProvider(UnfoldTransitionProgressProvider unfoldTransitionProgressProvider, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : unfoldTransitionProgressProvider);
    }

    public final void setSourceProvider(UnfoldTransitionProgressProvider provider) {
        UnfoldTransitionProgressProvider unfoldTransitionProgressProvider = this.source;
        if (unfoldTransitionProgressProvider != null) {
            unfoldTransitionProgressProvider.removeCallback(this);
        }
        if (provider != null) {
            this.source = provider;
            provider.addCallback(this);
        } else {
            this.source = null;
        }
    }

    public final void setReadyToHandleTransition(boolean isReadyToHandleTransition) {
        if (this.isTransitionRunning) {
            if (!isReadyToHandleTransition) {
                this.isTransitionRunning = false;
                Iterator<T> it = this.listeners.iterator();
                while (it.hasNext()) {
                    ((UnfoldTransitionProgressProvider.TransitionProgressListener) it.next()).onTransitionFinished();
                }
            } else {
                Iterator<T> it2 = this.listeners.iterator();
                while (it2.hasNext()) {
                    ((UnfoldTransitionProgressProvider.TransitionProgressListener) it2.next()).onTransitionStarted();
                }
                if (!(this.lastTransitionProgress == PROGRESS_UNSET)) {
                    Iterator<T> it3 = this.listeners.iterator();
                    while (it3.hasNext()) {
                        ((UnfoldTransitionProgressProvider.TransitionProgressListener) it3.next()).onTransitionProgress(this.lastTransitionProgress);
                    }
                }
            }
        }
        this.isReadyToHandleTransition = isReadyToHandleTransition;
    }

    /* JADX DEBUG: Method merged with bridge method: addCallback(Ljava/lang/Object;)V */
    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(UnfoldTransitionProgressProvider.TransitionProgressListener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        this.listeners.add(listener);
    }

    /* JADX DEBUG: Method merged with bridge method: removeCallback(Ljava/lang/Object;)V */
    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(UnfoldTransitionProgressProvider.TransitionProgressListener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        this.listeners.remove(listener);
    }

    @Override // com.android.systemui.unfold.UnfoldTransitionProgressProvider
    public void destroy() {
        UnfoldTransitionProgressProvider unfoldTransitionProgressProvider = this.source;
        if (unfoldTransitionProgressProvider != null) {
            unfoldTransitionProgressProvider.removeCallback(this);
        }
        UnfoldTransitionProgressProvider unfoldTransitionProgressProvider2 = this.source;
        if (unfoldTransitionProgressProvider2 == null) {
            return;
        }
        unfoldTransitionProgressProvider2.destroy();
    }

    @Override // com.android.systemui.unfold.UnfoldTransitionProgressProvider.TransitionProgressListener
    public void onTransitionStarted() {
        this.isTransitionRunning = true;
        if (this.isReadyToHandleTransition) {
            Iterator<T> it = this.listeners.iterator();
            while (it.hasNext()) {
                ((UnfoldTransitionProgressProvider.TransitionProgressListener) it.next()).onTransitionStarted();
            }
        }
    }

    @Override // com.android.systemui.unfold.UnfoldTransitionProgressProvider.TransitionProgressListener
    public void onTransitionProgress(float progress) {
        if (this.isReadyToHandleTransition) {
            Iterator<T> it = this.listeners.iterator();
            while (it.hasNext()) {
                ((UnfoldTransitionProgressProvider.TransitionProgressListener) it.next()).onTransitionProgress(progress);
            }
        }
        this.lastTransitionProgress = progress;
    }

    @Override // com.android.systemui.unfold.UnfoldTransitionProgressProvider.TransitionProgressListener
    public void onTransitionFinished() {
        if (this.isReadyToHandleTransition) {
            Iterator<T> it = this.listeners.iterator();
            while (it.hasNext()) {
                ((UnfoldTransitionProgressProvider.TransitionProgressListener) it.next()).onTransitionFinished();
            }
        }
        this.isTransitionRunning = false;
        this.lastTransitionProgress = PROGRESS_UNSET;
    }
}
