package androidx.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface LayoutInflaterFactory {
    View onCreateView(View parent, String name, Context context, AttributeSet attrs);
}
