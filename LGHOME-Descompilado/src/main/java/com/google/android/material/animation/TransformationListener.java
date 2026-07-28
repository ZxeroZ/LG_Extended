package com.google.android.material.animation;

import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public interface TransformationListener<T extends View> {
    void onScaleChanged(T t);

    void onTranslationChanged(T t);
}
