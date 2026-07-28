package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Rect;

/* JADX INFO: loaded from: classes.dex */
public abstract class RotationMode {
    public static RotationMode NORMAL = new RotationMode(0.0f) { // from class: com.android.launcher3.graphics.RotationMode.1
    };
    public final boolean isTransposed;
    public final float surfaceRotation;

    public int toNaturalGravity(int absoluteGravity) {
        return absoluteGravity;
    }

    public RotationMode(float surfaceRotation) {
        this.surfaceRotation = surfaceRotation;
        this.isTransposed = surfaceRotation != 0.0f;
    }

    public final void mapRect(Rect rect, Rect out) {
        mapRect(rect.left, rect.top, rect.right, rect.bottom, out);
    }

    public void mapRect(int left, int top, int right, int bottom, Rect out) {
        out.set(left, top, right, bottom);
    }

    public void mapInsets(Context context, Rect insets, Rect out) {
        out.set(insets);
    }
}
