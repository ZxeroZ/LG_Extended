package com.android.systemui.unfold.util;

import android.content.Context;
import android.os.RemoteException;
import android.view.IRotationWatcher;
import android.view.IWindowManager;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: NaturalRotationUnfoldProgressProvider.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0003\u0018\u00002\u00020\u0001:\u0001\u0018B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0001¢\u0006\u0002\u0010\u0007J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J\b\u0010\u0012\u001a\u00020\u000fH\u0016J\u0006\u0010\u0013\u001a\u00020\u000fJ\u0010\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0010\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\n\u001a\u00060\u000bR\u00020\u0000X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019"}, d2 = {"Lcom/android/systemui/unfold/util/NaturalRotationUnfoldProgressProvider;", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;", "context", "Landroid/content/Context;", "windowManagerInterface", "Landroid/view/IWindowManager;", "unfoldTransitionProgressProvider", "(Landroid/content/Context;Landroid/view/IWindowManager;Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;)V", "isNaturalRotation", "", "rotationWatcher", "Lcom/android/systemui/unfold/util/NaturalRotationUnfoldProgressProvider$RotationWatcher;", "scopedUnfoldTransitionProgressProvider", "Lcom/android/systemui/unfold/util/ScopedUnfoldTransitionProgressProvider;", "addCallback", "", "listener", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider$TransitionProgressListener;", "destroy", "init", "onRotationChanged", "rotation", "", "removeCallback", "RotationWatcher", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class NaturalRotationUnfoldProgressProvider implements UnfoldTransitionProgressProvider {
    private final Context context;
    private boolean isNaturalRotation;
    private final RotationWatcher rotationWatcher;
    private final ScopedUnfoldTransitionProgressProvider scopedUnfoldTransitionProgressProvider;
    private final IWindowManager windowManagerInterface;

    public NaturalRotationUnfoldProgressProvider(Context context, IWindowManager windowManagerInterface, UnfoldTransitionProgressProvider unfoldTransitionProgressProvider) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(windowManagerInterface, "windowManagerInterface");
        Intrinsics.checkNotNullParameter(unfoldTransitionProgressProvider, "unfoldTransitionProgressProvider");
        this.context = context;
        this.windowManagerInterface = windowManagerInterface;
        this.scopedUnfoldTransitionProgressProvider = new ScopedUnfoldTransitionProgressProvider(unfoldTransitionProgressProvider);
        this.rotationWatcher = new RotationWatcher(this);
    }

    public final void init() {
        try {
            this.windowManagerInterface.watchRotation(this.rotationWatcher, this.context.getDisplay().getDisplayId());
            onRotationChanged(this.context.getDisplay().getRotation());
        } catch (RemoteException e) {
            RuntimeException runtimeExceptionRethrowFromSystemServer = e.rethrowFromSystemServer();
            Intrinsics.checkNotNullExpressionValue(runtimeExceptionRethrowFromSystemServer, "e.rethrowFromSystemServer()");
            throw runtimeExceptionRethrowFromSystemServer;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void onRotationChanged(int rotation) {
        boolean z = rotation == 0 || rotation == 2;
        if (this.isNaturalRotation != z) {
            this.isNaturalRotation = z;
            this.scopedUnfoldTransitionProgressProvider.setReadyToHandleTransition(z);
        }
    }

    @Override // com.android.systemui.unfold.UnfoldTransitionProgressProvider
    public void destroy() {
        try {
            this.windowManagerInterface.removeRotationWatcher(this.rotationWatcher);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
        this.scopedUnfoldTransitionProgressProvider.destroy();
    }

    /* JADX DEBUG: Method merged with bridge method: addCallback(Ljava/lang/Object;)V */
    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(UnfoldTransitionProgressProvider.TransitionProgressListener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        this.scopedUnfoldTransitionProgressProvider.addCallback(listener);
    }

    /* JADX DEBUG: Method merged with bridge method: removeCallback(Ljava/lang/Object;)V */
    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(UnfoldTransitionProgressProvider.TransitionProgressListener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        this.scopedUnfoldTransitionProgressProvider.removeCallback(listener);
    }

    /* JADX INFO: compiled from: NaturalRotationUnfoldProgressProvider.kt */
    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016¨\u0006\u0007"}, d2 = {"Lcom/android/systemui/unfold/util/NaturalRotationUnfoldProgressProvider$RotationWatcher;", "Landroid/view/IRotationWatcher$Stub;", "(Lcom/android/systemui/unfold/util/NaturalRotationUnfoldProgressProvider;)V", "onRotationChanged", "", "rotation", "", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
    private final class RotationWatcher extends IRotationWatcher.Stub {
        final /* synthetic */ NaturalRotationUnfoldProgressProvider this$0;

        /* JADX DEBUG: Incorrect args count in method signature: ()V */
        public RotationWatcher(NaturalRotationUnfoldProgressProvider this$0) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            this.this$0 = this$0;
        }

        public void onRotationChanged(int rotation) {
            this.this$0.onRotationChanged(rotation);
        }
    }
}
