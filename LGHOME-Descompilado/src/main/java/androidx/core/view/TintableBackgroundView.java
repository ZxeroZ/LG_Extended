package androidx.core.view;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;

/* JADX INFO: loaded from: classes.dex */
public interface TintableBackgroundView {
    ColorStateList getSupportBackgroundTintList();

    PorterDuff.Mode getSupportBackgroundTintMode();

    void setSupportBackgroundTintList(ColorStateList tint);

    void setSupportBackgroundTintMode(PorterDuff.Mode tintMode);
}
