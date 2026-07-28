package com.android.systemui.shared.system;

import android.app.WallpaperColors;
import android.content.Context;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.colorextraction.types.Tonal;

/* JADX INFO: loaded from: classes.dex */
public class TonalCompat {
    private final Tonal mTonal;

    public static class ExtractionInfo {
        public int mainColor;
        public int secondaryColor;
        public boolean supportsDarkText;
        public boolean supportsDarkTheme;
    }

    public TonalCompat(Context context) {
        this.mTonal = new Tonal(context);
    }

    public ExtractionInfo extractDarkColors(WallpaperColors colors) {
        ColorExtractor.GradientColors gradientColors = new ColorExtractor.GradientColors();
        this.mTonal.extractInto(colors, new ColorExtractor.GradientColors(), gradientColors, new ColorExtractor.GradientColors());
        ExtractionInfo extractionInfo = new ExtractionInfo();
        extractionInfo.mainColor = gradientColors.getMainColor();
        extractionInfo.secondaryColor = gradientColors.getSecondaryColor();
        extractionInfo.supportsDarkText = gradientColors.supportsDarkText();
        if (colors != null) {
            extractionInfo.supportsDarkTheme = (colors.getColorHints() & 2) != 0;
        }
        return extractionInfo;
    }
}
