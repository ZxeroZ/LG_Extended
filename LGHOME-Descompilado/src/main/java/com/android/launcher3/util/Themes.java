package com.android.launcher3.util;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;

/* JADX INFO: loaded from: classes.dex */
public class Themes {
    public static String getDefaultBodyFont(Context context) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(R.style.TextAppearance.DeviceDefault, new int[]{R.attr.fontFamily});
        String string = typedArrayObtainStyledAttributes.getString(0);
        typedArrayObtainStyledAttributes.recycle();
        return string;
    }

    public static float getDialogCornerRadius(Context context) {
        return getDimension(context, R.attr.dialogCornerRadius, context.getResources().getDimension(com.lge.launcher3.R.dimen.default_dialog_corner_radius));
    }

    public static float getDimension(Context context, int attr, float defaultValue) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(new int[]{attr});
        float dimension = typedArrayObtainStyledAttributes.getDimension(0, defaultValue);
        typedArrayObtainStyledAttributes.recycle();
        return dimension;
    }

    public static int getColorAccent(Context context) {
        return getAttrColor(context, R.attr.colorAccent);
    }

    public static int getAttrColor(Context context, int attr) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(new int[]{attr});
        int color = typedArrayObtainStyledAttributes.getColor(0, 0);
        typedArrayObtainStyledAttributes.recycle();
        return color;
    }

    public static boolean getAttrBoolean(Context context, int attr) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(new int[]{attr});
        boolean z = typedArrayObtainStyledAttributes.getBoolean(0, false);
        typedArrayObtainStyledAttributes.recycle();
        return z;
    }

    public static Drawable getAttrDrawable(Context context, int attr) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(new int[]{attr});
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(0);
        typedArrayObtainStyledAttributes.recycle();
        return drawable;
    }

    public static int getAttrInteger(Context context, int attr) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(new int[]{attr});
        int integer = typedArrayObtainStyledAttributes.getInteger(0, 0);
        typedArrayObtainStyledAttributes.recycle();
        return integer;
    }

    public static int getAlpha(Context context, int attr) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(new int[]{attr});
        float f = typedArrayObtainStyledAttributes.getFloat(0, 0.0f);
        typedArrayObtainStyledAttributes.recycle();
        return (int) ((f * 255.0f) + 0.5f);
    }

    public static float getFloat(Context context, int attr, float defValue) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(new int[]{attr});
        float f = typedArrayObtainStyledAttributes.getFloat(0, defValue);
        typedArrayObtainStyledAttributes.recycle();
        return f;
    }

    public static void setColorScaleOnMatrix(int color, ColorMatrix target) {
        target.setScale(Color.red(color) / 255.0f, Color.green(color) / 255.0f, Color.blue(color) / 255.0f, Color.alpha(color) / 255.0f);
    }

    public static void setColorChangeOnMatrix(int srcColor, int dstColor, ColorMatrix target) {
        target.reset();
        target.getArray()[4] = Color.red(dstColor) - Color.red(srcColor);
        target.getArray()[9] = Color.green(dstColor) - Color.green(srcColor);
        target.getArray()[14] = Color.blue(dstColor) - Color.blue(srcColor);
        target.getArray()[19] = Color.alpha(dstColor) - Color.alpha(srcColor);
    }

    public static SparseArray<TypedValue> createValueMap(Context context, AttributeSet attrSet, IntArray keysToIgnore) {
        int attributeCount = attrSet.getAttributeCount();
        IntArray intArray = new IntArray(attributeCount);
        for (int i = 0; i < attributeCount; i++) {
            intArray.add(attrSet.getAttributeNameResource(i));
        }
        intArray.removeAllValues(keysToIgnore);
        int[] array = intArray.toArray();
        SparseArray<TypedValue> sparseArray = new SparseArray<>(array.length);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attrSet, array);
        for (int i2 = 0; i2 < array.length; i2++) {
            TypedValue typedValue = new TypedValue();
            typedArrayObtainStyledAttributes.getValue(i2, typedValue);
            sparseArray.put(array[i2], typedValue);
        }
        return sparseArray;
    }
}
