package androidx.core.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;

/* JADX INFO: loaded from: classes.dex */
public interface TintableImageSourceView {
    ColorStateList getSupportImageTintList();

    PorterDuff.Mode getSupportImageTintMode();

    void setSupportImageTintList(ColorStateList tint);

    void setSupportImageTintMode(PorterDuff.Mode tintMode);
}
