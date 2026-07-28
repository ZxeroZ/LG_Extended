package com.google.android.material.elevation;

import android.content.Context;
import androidx.core.graphics.ColorUtils;
import com.google.android.material.R;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.resources.MaterialAttributes;

/* JADX INFO: loaded from: classes.dex */
public class ElevationOverlayProvider {
    private static final float FORMULA_MULTIPLIER = 4.5f;
    private static final float FORMULA_OFFSET = 2.0f;
    private final int colorSurface;
    private final float displayDensity;
    private final int elevationOverlaysColor;
    private final boolean elevationOverlaysEnabled;

    public ElevationOverlayProvider(Context context) {
        this.elevationOverlaysEnabled = MaterialAttributes.resolveBoolean(context, R.attr.elevationOverlaysEnabled, false);
        this.elevationOverlaysColor = MaterialColors.getColor(context, R.attr.elevationOverlaysColor, 0);
        this.colorSurface = MaterialColors.getColor(context, R.attr.colorSurface, 0);
        this.displayDensity = context.getResources().getDisplayMetrics().density;
    }

    public int layerOverlayIfNeeded(int i, float f) {
        return (this.elevationOverlaysEnabled && isSurfaceColor(i)) ? layerOverlay(i, f) : i;
    }

    public int layerOverlay(int i, float f) {
        return MaterialColors.layer(i, this.elevationOverlaysColor, calculateOverlayAlphaFraction(f));
    }

    public int calculateOverlayAlpha(float f) {
        return Math.round(calculateOverlayAlphaFraction(f) * 255.0f);
    }

    public float calculateOverlayAlphaFraction(float f) {
        if (this.displayDensity <= 0.0f || f <= 0.0f) {
            return 0.0f;
        }
        return Math.min(((((float) Math.log1p(f / r0)) * FORMULA_MULTIPLIER) + 2.0f) / 100.0f, 1.0f);
    }

    public boolean isOverlaysEnabled() {
        return this.elevationOverlaysEnabled;
    }

    public int getOverlaysColor() {
        return this.elevationOverlaysColor;
    }

    public int getSurfaceColor() {
        return this.colorSurface;
    }

    public int getSurfaceColorWithOverlayIfNeeded(float f) {
        return layerOverlayIfNeeded(this.colorSurface, f);
    }

    private boolean isSurfaceColor(int i) {
        return ColorUtils.setAlphaComponent(i, 255) == this.colorSurface;
    }
}
