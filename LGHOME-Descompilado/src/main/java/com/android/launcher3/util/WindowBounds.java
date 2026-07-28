package com.android.launcher3.util;

import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class WindowBounds {
    public final Point availableSize;
    public final Rect bounds;
    public final Rect insets;
    public int rotation;

    public WindowBounds(Rect bounds, Rect insets, int rotation) {
        this.bounds = bounds;
        this.insets = insets;
        this.availableSize = new Point((bounds.width() - insets.left) - insets.right, (bounds.height() - insets.top) - insets.bottom);
        this.rotation = rotation;
    }

    public WindowBounds(int width, int height, int availableWidth, int availableHeight, int rotation) {
        this.bounds = new Rect(0, 0, width, height);
        this.availableSize = new Point(availableWidth, availableHeight);
        this.insets = new Rect(0, 0, width - availableWidth, height - availableHeight);
        this.rotation = rotation;
    }

    public int hashCode() {
        return Objects.hash(this.bounds, this.insets);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WindowBounds)) {
            return false;
        }
        WindowBounds windowBounds = (WindowBounds) obj;
        return windowBounds.bounds.equals(this.bounds) && windowBounds.insets.equals(this.insets);
    }

    public String toString() {
        return "WindowBounds{bounds=" + this.bounds + ", insets=" + this.insets + ", availableSize=" + this.availableSize + "}";
    }

    public final boolean isLandscape() {
        return this.availableSize.x > this.availableSize.y;
    }

    public static WindowBounds fromWindowMetrics(WindowMetrics wm, int rotation) {
        Insets insets = wm.getWindowInsets().getInsets(WindowInsets.Type.systemBars());
        return new WindowBounds(wm.getBounds(), new Rect(insets.left, insets.top, insets.right, insets.bottom), rotation);
    }
}
