package androidx.core.content;

import android.content.ContentProvider;
import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
public final class ContentProviderCompat {
    private ContentProviderCompat() {
    }

    public static Context requireContext(final ContentProvider provider) {
        Context context = provider.getContext();
        if (context != null) {
            return context;
        }
        throw new IllegalStateException("Cannot find context from the provider.");
    }
}
