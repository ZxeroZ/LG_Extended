package com.android.systemui.unfold.util;

import android.view.View;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import java.util.function.Supplier;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: JankMonitorTransitionProgressListener.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0013\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0002\u0010\u0005J\b\u0010\t\u001a\u00020\nH\u0016J\b\u0010\u000b\u001a\u00020\nH\u0016R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\n \b*\u0004\u0018\u00010\u00070\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\f"}, d2 = {"Lcom/android/systemui/unfold/util/JankMonitorTransitionProgressListener;", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider$TransitionProgressListener;", "attachedViewProvider", "Ljava/util/function/Supplier;", "Landroid/view/View;", "(Ljava/util/function/Supplier;)V", "interactionJankMonitor", "Lcom/android/internal/jank/InteractionJankMonitor;", "kotlin.jvm.PlatformType", "onTransitionFinished", "", "onTransitionStarted", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class JankMonitorTransitionProgressListener implements UnfoldTransitionProgressProvider.TransitionProgressListener {
    private final Supplier<View> attachedViewProvider;
    private final InteractionJankMonitor interactionJankMonitor;

    public JankMonitorTransitionProgressListener(Supplier<View> attachedViewProvider) {
        Intrinsics.checkNotNullParameter(attachedViewProvider, "attachedViewProvider");
        this.attachedViewProvider = attachedViewProvider;
        this.interactionJankMonitor = InteractionJankMonitor.getInstance();
    }

    @Override // com.android.systemui.unfold.UnfoldTransitionProgressProvider.TransitionProgressListener
    public void onTransitionProgress(float f) {
        UnfoldTransitionProgressProvider.TransitionProgressListener.DefaultImpls.onTransitionProgress(this, f);
    }

    @Override // com.android.systemui.unfold.UnfoldTransitionProgressProvider.TransitionProgressListener
    public void onTransitionStarted() {
        this.interactionJankMonitor.begin(this.attachedViewProvider.get(), 44);
    }

    @Override // com.android.systemui.unfold.UnfoldTransitionProgressProvider.TransitionProgressListener
    public void onTransitionFinished() {
        this.interactionJankMonitor.end(44);
    }
}
