package com.android.systemui.unfold.util;

import android.content.ContentResolver;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import javax.inject.Provider;

/* JADX INFO: renamed from: com.android.systemui.unfold.util.ScaleAwareTransitionProgressProvider_Factory, reason: case insensitive filesystem */
/* JADX INFO: loaded from: classes.dex */
public final class C0022ScaleAwareTransitionProgressProvider_Factory {
    private final Provider<ContentResolver> contentResolverProvider;

    public C0022ScaleAwareTransitionProgressProvider_Factory(Provider<ContentResolver> provider) {
        this.contentResolverProvider = provider;
    }

    public ScaleAwareTransitionProgressProvider get(UnfoldTransitionProgressProvider unfoldTransitionProgressProvider) {
        return newInstance(unfoldTransitionProgressProvider, (ContentResolver) this.contentResolverProvider.get());
    }

    public static C0022ScaleAwareTransitionProgressProvider_Factory create(Provider<ContentResolver> provider) {
        return new C0022ScaleAwareTransitionProgressProvider_Factory(provider);
    }

    public static ScaleAwareTransitionProgressProvider newInstance(UnfoldTransitionProgressProvider unfoldTransitionProgressProvider, ContentResolver contentResolver) {
        return new ScaleAwareTransitionProgressProvider(unfoldTransitionProgressProvider, contentResolver);
    }
}
