package com.google.android.material.color;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import com.google.android.material.resources.MaterialAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class MaterialColors {
    public static final float ALPHA_DISABLED = 0.38f;
    public static final float ALPHA_DISABLED_LOW = 0.12f;
    public static final float ALPHA_FULL = 1.0f;
    public static final float ALPHA_LOW = 0.32f;
    public static final float ALPHA_MEDIUM = 0.54f;

    public static int getColor(View view, int i) {
        return MaterialAttributes.resolveOrThrow(view, i);
    }

    public static int getColor(Context context, int i, String str) {
        return MaterialAttributes.resolveOrThrow(context, i, str);
    }

    public static int getColor(View view, int i, int i2) {
        return getColor(view.getContext(), i, i2);
    }

    public static int getColor(Context context, int i, int i2) {
        TypedValue typedValueResolve = MaterialAttributes.resolve(context, i);
        return typedValueResolve != null ? typedValueResolve.data : i2;
    }

    public static int layer(View view, int i, int i2) {
        return layer(view, i, i2, 1.0f);
    }

    public static int layer(View view, int i, int i2, float f) {
        return layer(getColor(view, i), getColor(view, i2), f);
    }

    public static int layer(int i, int i2, float f) {
        return layer(i, ColorUtils.setAlphaComponent(i2, Math.round(Color.alpha(i2) * f)));
    }

    public static int layer(int i, int i2) {
        return ColorUtils.compositeColors(i2, i);
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:3:0x000d */
    public static ColorStateList layer(ColorStateList colorStateList, int i, ColorStateList colorStateList2, int i2, int[][] iArr) {
        int i3;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int length = iArr.length;
        while (true) {
            length--;
            if (length < 0) {
                break;
            }
            int[] iArr2 = iArr[length];
            int iLayer = layer(colorStateList.getColorForState(iArr2, i), colorStateList2.getColorForState(iArr2, i2));
            if (shouldAddColorForState(arrayList, iLayer, arrayList2, iArr2)) {
                arrayList.add(0, Integer.valueOf(iLayer));
                arrayList2.add(0, iArr2);
            }
        }
        int size = arrayList.size();
        int[] iArr3 = new int[size];
        int[][] iArr4 = new int[size][];
        for (i3 = 0; i3 < size; i3++) {
            iArr3[i3] = ((Integer) arrayList.get(i3)).intValue();
            iArr4[i3] = (int[]) arrayList2.get(i3);
        }
        return new ColorStateList(iArr4, iArr3);
    }

    private static boolean shouldAddColorForState(List<Integer> list, int i, List<int[]> list2, int[] iArr) {
        new HashSet(list);
        if (!list.contains(Integer.valueOf(i))) {
            return true;
        }
        for (int[] iArr2 : list2) {
            if (StateSet.stateSetMatches(iArr2, iArr)) {
                return list.get(list2.indexOf(iArr2)).intValue() != i;
            }
        }
        return true;
    }
}
