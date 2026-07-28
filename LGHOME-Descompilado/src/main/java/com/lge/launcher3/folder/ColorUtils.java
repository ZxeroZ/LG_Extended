package com.lge.launcher3.folder;

import android.graphics.Color;
import android.text.TextUtils;
import com.lge.launcher3.util.LGLog;
import com.lge.sui.widget.control.color.SUIColor;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class ColorUtils {
    static int[] COLORS_12 = {-1, -4095, -16726467, -3886080, -3885825, -3148800, -16731136, -3931136, -16468832, -3492854, -740337, -16470016};
    static int[] COLORS_24 = {-1, -4095, -16726467, -16470016, -3886080, -3885825, -3148800, -16731136, -3931136, -16468832, -3492854, -740337, -16470016, -1, -4095, -16726467, -3886080, -3885825, -3148800, -16731136, -3931136, -16468832, -3492854, -740337};
    static int[] COLORS_32 = {-1, -4095, -16726467, -16470016, -3886080, -3885825, -3148800, -16731136, -3931136, -16468832, -3492854, -740337, -16470016, -1, -4095, -16726467, -3886080, -3885825, -3148800, -16731136, -3931136, -16468832, -3492854, -740337, -16468832, -3492854, -740337, -16470016, -1, -16726467, -16470016, -3492854};
    static int[] COLORS_CUSTOM = {-1, -4095, -16726467, -3886080, -3885825, -3148800, 0, -16731136, -3931136, -16468832, -3492854, -740337, -16470016};
    static List<SUIColor> SUI_COLORS_12 = new ArrayList();
    static List<SUIColor> SUI_COLORS_12_GRADIENT = new ArrayList();

    ColorUtils() {
    }

    static {
        for (int i : COLORS_12) {
            SUI_COLORS_12.add(new SUIColor(i));
        }
        SUI_COLORS_12_GRADIENT.addAll(SUI_COLORS_12);
        LGLog.d("test", "Test");
        SUI_COLORS_12_GRADIENT.set(10, new SUIColor(new int[]{-46017, -15433}));
        SUI_COLORS_12_GRADIENT.set(11, new SUIColor(new int[]{-32154, -6226021, -5591809}));
    }

    static int parseColor(String strColor) {
        try {
            if (TextUtils.isEmpty(strColor)) {
                return 0;
            }
            if (!strColor.startsWith("#")) {
                strColor = "#" + strColor;
            }
            return Color.parseColor(strColor);
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
