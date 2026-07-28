package com.android.systemui.unfold.util;

import android.animation.ValueAnimator;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.provider.Settings;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: ScaleAwareTransitionProgressProvider.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000/\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005*\u0001\u0007\u0018\u00002\u00020\u0001:\u0001\u0012B\u0019\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016J\b\u0010\u000f\u001a\u00020\fH\u0016J\b\u0010\u0010\u001a\u00020\fH\u0002J\u0010\u0010\u0011\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016R\u0010\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0013"}, d2 = {"Lcom/android/systemui/unfold/util/ScaleAwareTransitionProgressProvider;", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;", "progressProviderToWrap", "contentResolver", "Landroid/content/ContentResolver;", "(Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;Landroid/content/ContentResolver;)V", "animatorDurationScaleObserver", "com/android/systemui/unfold/util/ScaleAwareTransitionProgressProvider$animatorDurationScaleObserver$1", "Lcom/android/systemui/unfold/util/ScaleAwareTransitionProgressProvider$animatorDurationScaleObserver$1;", "scopedUnfoldTransitionProgressProvider", "Lcom/android/systemui/unfold/util/ScopedUnfoldTransitionProgressProvider;", "addCallback", "", "listener", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider$TransitionProgressListener;", "destroy", "onAnimatorScaleChanged", "removeCallback", "Factory", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class ScaleAwareTransitionProgressProvider implements UnfoldTransitionProgressProvider {
    private final ScaleAwareTransitionProgressProvider$animatorDurationScaleObserver$1 animatorDurationScaleObserver;
    private final ContentResolver contentResolver;
    private final ScopedUnfoldTransitionProgressProvider scopedUnfoldTransitionProgressProvider;

    /* JADX INFO: compiled from: ScaleAwareTransitionProgressProvider.kt */
    @AssistedFactory
    @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bg\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&¨\u0006\u0006"}, d2 = {"Lcom/android/systemui/unfold/util/ScaleAwareTransitionProgressProvider$Factory;", "", "wrap", "Lcom/android/systemui/unfold/util/ScaleAwareTransitionProgressProvider;", "progressProvider", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
    public interface Factory {
        ScaleAwareTransitionProgressProvider wrap(UnfoldTransitionProgressProvider progressProvider);
    }

    /* JADX WARN: Type inference failed for: r3v1, types: [com.android.systemui.unfold.util.ScaleAwareTransitionProgressProvider$animatorDurationScaleObserver$1] */
    @AssistedInject
    public ScaleAwareTransitionProgressProvider(@Assisted UnfoldTransitionProgressProvider progressProviderToWrap, ContentResolver contentResolver) {
        Intrinsics.checkNotNullParameter(progressProviderToWrap, "progressProviderToWrap");
        Intrinsics.checkNotNullParameter(contentResolver, "contentResolver");
        this.contentResolver = contentResolver;
        this.scopedUnfoldTransitionProgressProvider = new ScopedUnfoldTransitionProgressProvider(progressProviderToWrap);
        ?? r3 = new ContentObserver() { // from class: com.android.systemui.unfold.util.ScaleAwareTransitionProgressProvider$animatorDurationScaleObserver$1
            {
                super(null);
            }

            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                this.this$0.onAnimatorScaleChanged();
            }
        };
        this.animatorDurationScaleObserver = r3;
        contentResolver.registerContentObserver(Settings.Global.getUriFor("animator_duration_scale"), false, (ContentObserver) r3);
        onAnimatorScaleChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void onAnimatorScaleChanged() {
        this.scopedUnfoldTransitionProgressProvider.setReadyToHandleTransition(ValueAnimator.areAnimatorsEnabled());
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

    @Override // com.android.systemui.unfold.UnfoldTransitionProgressProvider
    public void destroy() {
        this.contentResolver.unregisterContentObserver(this.animatorDurationScaleObserver);
        this.scopedUnfoldTransitionProgressProvider.destroy();
    }
}
